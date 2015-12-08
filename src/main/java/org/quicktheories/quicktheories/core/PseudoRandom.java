package org.quicktheories.quicktheories.core;

import java.util.function.LongPredicate;

/**
 * Generates random ints and longs within given intervals from an initial seed
 *
 */
public interface PseudoRandom {

  /**
   * Generates a random integer within the interval
   * 
   * @param startInclusive
   *          startInclusive to be generated
   * @param endInclusive
   *          endInclusive to be generated
   * @return an integer between start and end inclusive
   */
  public int nextInt(int startInclusive, int endInclusive);

  /**
   * Returns a psuedo random long
   * 
   * @return a psuedo random long
   */
  public long nextLong();

  /**
   * Generates a random long within the interval
   * 
   * @param startInclusive
   *          startInclusive to be generated
   * @param endInclusive
   *          endInclusive to generated
   * @return a long between start and end inclusive
   */

  /**
   * Returns the seed used to generate random numbers for this instance
   * 
   * @return the seed
   */
  public long getInitialSeed();

  /**
   * Returns a pseudo random long within the interval
   * 
   * @param startInclusive
   *          - the lower bound (inclusive)
   * @param endInclusive
   *          - the upper bound (inclusive)
   * @return long between startInclusive and endInclusive
   */
  public default long generateRandomLongWithinInterval(
      final long startInclusive,
      final long endInclusive) {
    if (longRangeIsSmallerThanMax(startInclusive, endInclusive)) {
      return generateRandomLongWhereRangeLessThanMax(startInclusive,
          endInclusive);
    }
    return generateRandomLongWhereRangeGEQMax(startInclusive, endInclusive);
  }

  default long generateRandomLongWhereRangeLessThanMax(
      final long startInclusive, final long endInclusive) {
    return nextLongWithinCheckedInterval(startInclusive, endInclusive);
  }

  default boolean longRangeIsSmallerThanMax(final long x, final long y) {
    return (1 <= x && y <= Long.MAX_VALUE)
        || ((Long.MIN_VALUE) <= x && y <= -2)
        || ((Long.MIN_VALUE + 1) <= x && y <= -1) || (-Long.MAX_VALUE < x
            && x <= 0 && 0 <= y && y < Long.MAX_VALUE + x);
  }

  default long generateRandomLongWhereRangeGEQMax(final long x,
      final long y) {
    long result = nextLong();
    while (longValueUnsuitable(x, y).test(result)) {
      result = nextLong();
    }
    return result;
  }

  default LongPredicate longValueUnsuitable(final long startInclusive,
      final long endInclusive) {
    return i -> i < startInclusive || i > endInclusive;
  }

  long nextLongWithinCheckedInterval(long start, long end);

}