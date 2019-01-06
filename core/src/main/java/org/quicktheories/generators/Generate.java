package org.quicktheories.generators;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;
import org.quicktheories.core.RandomnessSource;
import org.quicktheories.impl.Constraint;

public class Generate {

  /**
   * Generates a constant value
   * @param <T> type of value to generate
   * @param constant The constant to return
   * @return A Gen of T
   */
  public static <T> Gen<T> constant(T constant) {
    return prng -> constant;
  }

  /**
   * Generates a constant value from a supplier.
   * 
   * This method is intended to allow a single mutable value to be safely supplied. If it is abused to inject
   * randomness or values that are semantically different across multiple invocations
   * then QuickTheories will not work correctly.
   * 
   * @param <T> type of value to generate
   * @param constant The constant to return
   * @return A Gen of T
   */
  public static <T> Gen<T> constant(Supplier<T> constant) {
    return prng -> constant.get();
  }

  
  /**
   * Randomly returns one of the supplied values
   * @param <T> type of value to generate
   * @param ts Values to pick from
   * @return A Gen of T
   */
  public static <T> Gen<T> pickWithNoShrinkPoint(List<T> ts) {
    Gen<Integer> index = rangeWithNoShrinkPoint(0, ts.size() - 1);
    return prng -> ts.get(index.generate(prng));
  }
    
  /**
   * Randomly returns one of the supplied values
   * @param <T> type of value to generate   * 
   * @param ts Values to pick from
   * @return A Gen of T
   */
  public static <T> Gen<T> pick(List<T> ts) {
    Gen<Integer> index = range(0, ts.size() - 1);
    return prng -> ts.get(index.generate(prng));
  }

  /**
   * Returns a generator that provides a value from a random generator provided.
   * 
   * @param mandatory Generator to sample from  
   * @param others Other generators to sample from with equal weighting
   * @param <T> Type to generate
   * @return A gen of T
   */
  @SafeVarargs
  public static <T> Gen<T> oneOf(Gen<T> mandatory, Gen<T> ... others) {
    Gen<T>[] generators = Arrays.copyOf(others, others.length + 1);
    generators[generators.length - 1] = mandatory;
    Gen<Integer> index = range(0, generators.length - 1);
    return prng -> generators[(index.generate(prng))].generate(prng);
  }

  /**
   * Returns a generator that provides a value from a generator chosen with probability
   * in proportion to the weight supplied in the {@link Pair}. Shrinking is
   * towards the first non-zero weight in the list. At least one generator must have
   * a positive weight and non-positive generators will never be chosen.
   *
   * @param mandatory Generator to sample from
   * @param others Other generators to sample
   * @param <T> Type to generate
   * @return A gen of T
   */
  @SafeVarargs
  public static <T> Gen<T> frequency(Pair<Integer, Gen<T>> mandatory,
                                         Pair<Integer, Gen<T>> ... others) {

    return frequency(FrequencyGen.makeGeneratorList(mandatory, others));
  }

  /**
   * Returns a generator that provides a value from a generator chosen with probability
   * in proportion to the weight supplied in the {@link Pair}. Shrinking is
   * towards the first non-zero weight in the list. At least one generator must have
   * a positive weight and non-positive generators will never be chosen.
   *
   * @param weightedGens pairs of weight and generators to sample in proportion to their weighting
   * @param <T> Type to generate
   * @return A gen of T
   */
  public static <T> Gen<T> frequency(List<Pair<Integer, Gen<T>>> weightedGens) {
    return FrequencyGen.fromList(false, weightedGens);
  }

  /**
   * Returns a generator that provides a value from a generator chosen with probability
   * in proportion to the weight supplied in the {@link Pair} with it. This generator
   * does not shrink, and will always pick generators in proportion to their weight.
   * At least one generator must have a positive weight and non-positive generators will never
   * be chosen.
   *
   * @param mandatory Generator to sample from
   * @param others Other generators to sample
   * @param <T> Type to generate
   * @return A gen of T
   */
  @SafeVarargs
  public static <T> Gen<T> frequencyWithNoShrinkPoint(Pair<Integer, Gen<T>> mandatory,
                                                          Pair<Integer, Gen<T>> ... others) {
    return frequencyWithNoShrinkPoint(
        FrequencyGen.makeGeneratorList(mandatory, others));
  }

  /**
   * Returns a generator that provides a value from a generator chosen with probability
   * in proportion to the weight supplied in the {@link Pair} with it. This generator
   * does not shrink, and will always pick generators in proportion to their weight.
   * At least one generator must have a positive weight and non-positive generators will never
   * be chosen.
   *
   * @param weightedGens pairs of weight and generators to sample in proportion to their weighting
   * @param <T> Type to generate
   * @return A gen of T
   */
  public static <T> Gen<T> frequencyWithNoShrinkPoint(List<Pair<Integer, Gen<T>>> weightedGens) {
    return FrequencyGen.fromList(true, weightedGens);
  }

  static class FrequencyGen<T> implements Gen<T>
  {
    private final NavigableMap<Integer, Gen<T>> weightedMap;
    private final Gen<Integer> indexGen;

    private FrequencyGen(Gen<Integer> indexGen, NavigableMap<Integer, Gen<T>> weightedMap) {
      this.weightedMap = weightedMap;
      this.indexGen = indexGen;
    }

    static <T> List<Pair<Integer, Gen<T>>> makeGeneratorList(Pair<Integer, Gen<T>> mandatory,
                                                             Pair<Integer, Gen<T>>[] others) {
      List<Pair<Integer,Gen<T>>> ts = new ArrayList<>(others.length + 1);
      ts.add(mandatory);
      Collections.addAll(ts, others);
      return ts;
    }

    /* First the generator normalizes the weights and their total by the greatest common factor
     * to keep integers small and improve the chance of moving to a new generator as the
     * index generator shrinks.
     *
     * Then assigns each generator a range of integers according to their normalized weight
     * For example, with three normalized weighted generators {3, g1}, {4, g2}, {5, g3},
     * total 12 it assigns [0, 2] to g1, [3, 6] to g2, [7, 11] to g3.
     *
     * At generation time, the generator picks an integer between [0, total weight) and finds
     * the generator responsible for the range.
     */
    @SuppressWarnings("unchecked")
    static <T> FrequencyGen<T> fromList(boolean withNoShrinkPoint, List<Pair<Integer, Gen<T>>> ts) {
      if (ts.size() < 1) {
        throw new IllegalArgumentException("List of generators must not be empty");
      }
      /* Calculate the total unadjusted weights, and the largest common factor
       * between all of the weights and the total, so they can be reduced to the smallest.
       * It ignores non-positive weights to make it easy to disable generators while developing
       * properties.
       */
      long unadjustedTotalWeights = 0;
      long commonFactor = 0;
      for (Pair<Integer,Gen<T>> pair : ts) {
        int weight = pair._1;
        if (weight <= 0)
          continue;

        if (unadjustedTotalWeights == 0) {
          commonFactor = weight;
        } else {
          commonFactor = gcd(commonFactor, weight);
        }
        unadjustedTotalWeights += weight;
      }
      if (unadjustedTotalWeights == 0) {
        throw new IllegalArgumentException("At least one generator must have a positive weight");
      }
      commonFactor = gcd(commonFactor, unadjustedTotalWeights);

      /* Build a tree map with the key as the first integer assigned in the range,
       * floorEntry will pick the 'the greatest key less than or equal to the given key',
       * which will find the right generator for the range.
       */
      NavigableMap<Integer, Gen<T>> weightedMap = new TreeMap<>();
      int nextStart = 0;
      for (Pair<Integer,Gen<T>> pair : ts) {
        int weight = pair._1;
        if (weight <= 0)
          continue;

        weightedMap.put(nextStart, pair._2);
        nextStart += weight / commonFactor;
      }
      final int upperRange = (int) (unadjustedTotalWeights / commonFactor) - 1;

      Gen<Integer> indexGen = withNoShrinkPoint ? rangeWithNoShrinkPoint(0, upperRange): range(0, upperRange);

      return new FrequencyGen(indexGen, weightedMap);
    }

    @Override
    public T generate(RandomnessSource prng) {
      return weightedMap.floorEntry(indexGen.generate(prng)).getValue().generate(prng);
    }

    @Override
    public String asString(T t) {
      return weightedMap.get(0).asString(t);
    }

    private static long gcd(long a, long b)
    {
      return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
    }
  }

  /**
   * Inclusive integer range that shrinks towards 0
   * @param startInclusive start
   * @param endInclusive end
   * @return A Gen of Integers
   */
  public static Gen<Integer> range(final int startInclusive,
      final int endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  /**
   * Inclusive integer range that shrinks towards supplied target
   * @param startInclusive start
   * @param endInclusive end
   * @param shrinkTarget shrink target
   * @return A Gen of Integers
   */
  public static Gen<Integer> range(final int startInclusive,
      final int endInclusive, final int shrinkTarget) {
    return td -> Integer.valueOf((int)td.next(Constraint.between(startInclusive, endInclusive).withShrinkPoint(shrinkTarget)));
  }
  
  /**
   * Inclusive integer range with no shrink point
   * @param startInclusive start
   * @param endInclusive end
   * @return A Gen of Integers
   */
  public static Gen<Integer> rangeWithNoShrinkPoint(final int startInclusive,
                                                     final int endInclusive) {
    return td -> Integer.valueOf((int)td.next(Constraint.between(startInclusive, endInclusive).withNoShrinkPoint()));
  }
  
  /**
   * Inclusive long range that shrinks towards 0
   * @param startInclusive start
   * @param endInclusive end
   * @return A Gen of Longs
   */
  public static Gen<Long> longRange(final long startInclusive,
      final long endInclusive) {
    return longRange(startInclusive, endInclusive, 0);
  }

  /**
   * Inclusive long range that shrinks towards supplied target
   * @param startInclusive start
   * @param endInclusive end
   * @param shrinkTarget shrink target
   * @return A Gen of Longs
   */
  public static Gen<Long> longRange(final long startInclusive,
      final long endInclusive, final long shrinkTarget) {
    return prng -> prng.next(Constraint.between(startInclusive, endInclusive).withShrinkPoint(shrinkTarget));
  }  
  
  /**
   * Inclusive byte range that shrinks towards supplied target
   * @param startInclusive start
   * @param endInclusive end
   * @param shrinkTarget shrink target
   * @return A Gen of Bytes
   */
  public static Gen<Byte> bytes(final byte startInclusive,
      final byte endInclusive, final byte shrinkTarget) {
    return prng -> (byte) prng.next(Constraint.between(startInclusive, endInclusive).withShrinkPoint(shrinkTarget));
  } 
  
  /**
   * Enum values shrinking towards first declared value
   * @param <T> type of value to generate
   * @param e Enum from which to source values
   * @return A Gen of T
   */
  public static <T extends Enum<T>> Gen<T> enumValues(Class<T> e) {
    return pick(java.util.Arrays.asList(e.getEnumConstants()));
  }
  
  /**
   * Random booleans
   * @return Gen of Boolean
   */
  public static Gen<Boolean> booleans() {
    return Generate.pick(Arrays.asList(false, true));
  }
  
  /**
   * Inclusive Range of Characters representing valid code points. Shrinks towards '!' character
   * (the first printable ascii character)
   * @param startInclusive Code point of start of range
   * @param endInclusive Code point of end of range
   * @return A Gen of Characters
   */
  public static Gen<Character> characters(int startInclusive, int endInclusive) {
    return CodePoints.codePoints(startInclusive, endInclusive, '!')
        .map(l -> (char) l.intValue());
  }
  
  /**
   * One dimensional int arrays
   * @param sizes Gen of sizes for the arrays
   * @param contents Gen of contents
   * @return A Gen of int[]
   */
  public static Gen<int[]> intArrays(Gen<Integer> sizes, Gen<Integer> contents) {
    Gen<int[]> gen = td -> {
      int size = sizes.generate(td);
      int[] is = new int[size];
      for (int i = 0; i != size; i++) {
        is[i] = contents.generate(td);
      }
      return is;
    };
    return gen.describedAs(Arrays::toString);
  }
  
  /**
   * Two dimensional int arrays
   * @param rows Gen of rows sizes for the arrays
   * @param cols Gen of cols sizes for the arrays   * 
   * @param contents Gen of contents
   * @return A Gen of int[][]
   */
  public static Gen<int[][]> intArrays(Gen<Integer> rows, Gen<Integer> cols, Gen<Integer> contents) {
    Gen<int[][]> gen = td -> {
      int w = rows.generate(td);
      int h = cols.generate(td);      
      int[][] is = new int[w][h];
      for (int i = 0; i != w; i++) {
        for (int j = 0; j != h; j++) {
          is[i][j] = contents.generate(td);          
        }
      }
      return is;
    };
    
    return gen.describedAs(a -> java.util.Arrays.stream(a).map(Arrays::toString)
        .collect(Collectors.joining(", ", "[", "]")));
  }
    
  /**
   * One dimensional bytes arrays
   * @param sizes Gen of sizes for the arrays
   * @param contents Gen of contents
   * @return A Gen of byte[]
   */
  public static Gen<byte[]> byteArrays(Gen<Integer> sizes, Gen<Byte> contents) {
    Gen<byte[]> gen = td -> {
      int size = sizes.generate(td);
      byte[] bs = new byte[size];
      for (int i = 0; i != size; i++) {
        bs[i] = contents.generate(td);
      }
      return bs;
    };
    return gen.describedAs(Arrays::toString);
  }

  @SuppressWarnings("unchecked")
  static <T> Gen<T[]> arraysOf(Gen<T> values, Class<T> c, Gen<Integer> lengths) {
    return Lists
        .listsOf(values, Lists.arrayList(), lengths)
        .map(l -> l.toArray((T[]) Array.newInstance(c, 0)) // will generate
                                                           // correct size if
                                                           // zero is less than
                                                           // the length of the
                                                           // array
            ).describedAs(arrayDescriber(values::asString));
  }
  
  private static <T> AsString<T[]> arrayDescriber(Function<T, String> valueDescriber) {
    return a -> java.util.Arrays.stream(a).map(valueDescriber).collect(Collectors.joining(", ", "[", "]"));
  }  
   
}
