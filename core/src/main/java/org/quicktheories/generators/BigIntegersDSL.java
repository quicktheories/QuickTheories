package org.quicktheories.generators;

import java.math.BigInteger;

import org.quicktheories.core.Gen;

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
  public Gen<BigInteger> ofBytes(int maxLengthByteArray) {
    ArgumentAssertions.checkArguments(maxLengthByteArray > 0,
        "The length of this array cannot be less than one; %s is not an accepted argument",
        maxLengthByteArray);
    return BigIntegers.random(maxLengthByteArray);
  }

  final static class BigIntegers {
    static Gen<BigInteger> random(int maxLengthByteArray) {
      return prng -> {
        Gen<Integer> lengths = Generate.range(1, maxLengthByteArray);
        Gen<Byte> contents = Generate.bytes(Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 0);
        return Generate.byteArrays(lengths, contents).map(bs -> new BigInteger(bs)).generate(prng);
      };
    }
  }

}
