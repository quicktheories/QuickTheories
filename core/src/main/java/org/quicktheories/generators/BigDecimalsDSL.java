package org.quicktheories.generators;

import java.math.BigDecimal;

import org.quicktheories.core.Gen;
import org.quicktheories.generators.BigIntegersDSL.BigIntegers;

/**
 * A Class for creating BigDecimal Sources
 *
 */
public class BigDecimalsDSL {

  /**
   * Creates a BigDecimalsBuilder that can be used to create BigDecimals
   * 
   * @param maxNumberOfBytes
   *          the maximum number of bytes of a BigInteger from which a
   *          BigDecimal would be composed
   * @return a BigDecimalsBuilder
   */
  public BigDecimalsBuilder ofBytes(int maxNumberOfBytes) {
    return new BigDecimalsBuilder(maxNumberOfBytes);
  }

  public static class BigDecimalsBuilder {

    private final int maxLengthOfBigIntegerByteArray;

    private BigDecimalsBuilder(int maxLengthOfBigIntegerByteArray) {
      ArgumentAssertions.checkArguments(maxLengthOfBigIntegerByteArray > 0,
          "The length of this array cannot be less than one; %s is not an accepted argument",
          maxLengthOfBigIntegerByteArray);
      this.maxLengthOfBigIntegerByteArray = maxLengthOfBigIntegerByteArray;
    }

    /**
     * Generates BigDecimals of specified scale from BigIntegers constructed
     * from Byte arrays of a given maximum size
     * 
     * @param scale
     *          - the desired scale of the BigDecimal
     * @return a Source of type BigDecimal
     */
    public Gen<BigDecimal> withScale(int scale) {
      return BigDecimals.randomWithScale(maxLengthOfBigIntegerByteArray, scale);
    }
  }

  final static class BigDecimals {

    static Gen<BigDecimal> randomWithScale(
        int maxLengthOfBigIntegerByteArray, int scale) {
      return BigIntegers.random(maxLengthOfBigIntegerByteArray).map(
          i -> new BigDecimal(i, scale));
    }

  }
}
