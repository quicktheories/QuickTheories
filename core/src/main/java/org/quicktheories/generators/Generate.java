package org.quicktheories.generators;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.quicktheories.api.AsString;
import org.quicktheories.core.Gen;
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
  static <T> Gen<T[]> arraysOf(Gen<T> values, Class<T> c,
      int minLength, int maxLength) {
    return Lists
        .listsOf(values, Lists.arrayList(), range(minLength, maxLength))
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
