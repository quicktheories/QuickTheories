package org.quicktheories.quicktheories.generators;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating BigInteger Sources that will produce BigIntegers
 * randomly and shrink the individual elements of the BigInteger's byte array
 */
public class BigIntegersDSL {

  /**
   * Generates BigIntegers with byte arrays of size less than or equal to the
   * parameter maxLengthByteArray
   * 
   * @param maxLengthByteArray
   *          - maximum length of byte array
   * @return a Source of type BigInteger
   */
  public Source<BigInteger> ofBytes(int maxLengthByteArray) {
    ArgumentAssertions.checkArguments(maxLengthByteArray > 0,
        "The length of this array cannot be less than one; %s is not an accepted argument",
        maxLengthByteArray);
    return BigIntegers.random(maxLengthByteArray);
  }

  final static class BigIntegers {
    static Source<BigInteger> random(int maxLengthByteArray) {
      return generateRandomByteArray(maxLengthByteArray).as(
          b -> new BigInteger(convertObjectToPrimitive(b)),
          bi -> convertPrimitiveToObject(bi.toByteArray()));
    }

    private static Source<Byte[]> generateRandomByteArray(int maxLength) {
      return generateRandomByteList(maxLength).as(
          l -> l.toArray((Byte[]) Array.newInstance(Byte.class, 0)),
          a -> java.util.Arrays.asList(a));
    }

    private static Source<List<Byte>> generateRandomByteList(int maxLength) {
      return Lists
          .alternatingBoundedListsOf(Integers.range(-128, 127), 1, maxLength)
          .withShrinker(Lists.swapBetweenShrinkMethodsForBoundedIntegerLists(
              Integers.range(-128, 127), Lists.arrayListCollector(), 1))
          .as(l -> l.stream().map(i -> i.byteValue())
              .collect(Collectors.toList()),
              l -> l.stream().map(b -> b.intValue())
                  .collect(Collectors.toList()));
    }

    private static Byte[] convertPrimitiveToObject(byte[] byteArray) {
      Byte[] array = new Byte[byteArray.length];
      for (int i = 0; i < byteArray.length; i++) {
        array[i] = byteArray[i];
      }
      return array;
    }

    private static byte[] convertObjectToPrimitive(Byte[] array) {
      byte[] byteArray = new byte[array.length];
      for (int i = 0; i < array.length; i++) {
        byteArray[i] = array[i];
      }
      return byteArray;
    }
  }

}
