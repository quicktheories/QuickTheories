package org.quicktheories.quicktheories.core;

/**
 * Generates random ints and longs within given intervals from an initial seed
 */
public interface PseudoRandom {

  /**
   * Returns a pseudo random long within the interval
   * 
   * @param startInclusive
   *          - the lower bound (inclusive)
   * @param endInclusive
   *          - the upper bound (inclusive)
   * @return long between startInclusive and endInclusive
   */
  long nextLong(long startInclusive, long endInclusive);
  
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
  long getInitialSeed();
  

  /**
   * Generates a random integer within the interval
   * 
   * @param startInclusive
   *          startInclusive to be generated
   * @param endInclusive
   *          endInclusive to be generated
   * @return an integer between start and end inclusive
   */
  default int nextInt(int startInclusive, int endInclusive) {
    return (int) nextLong(startInclusive, endInclusive);
  }
  

}