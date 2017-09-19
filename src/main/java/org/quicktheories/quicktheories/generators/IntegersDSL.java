package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Gen;

/**
 * A Class for creating Integer Sources that will produce integers within a set
 * interval and will shrink within this domain. 
 * of one.
 */
public class IntegersDSL {

  /**
   * Constructs a IntegerDomainBuilder object with an inclusive lower bound
   * 
   * @param startInclusive
   *          - lower bound of domain
   * @return an IntegerDomainBuilder
   */
  public IntegerDomainBuilder from(final int startInclusive) {
    return new IntegerDomainBuilder(startInclusive);
  }

  /**
   * Generates all possible integers in Java bounded below by Integer.MIN_VALUE
   * and above by Integer.MAX_VALUE.
   * 
   * @return a Source of type Integer
   */
  public Gen<Integer> all() {
    return between(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Generates all possible positive integers in Java, bounded above by
   * Integer.MAX_VALUE.
   * 
   * @return a Source of type Integer
   */
  public Gen<Integer> allPositive() {
    return between(1, Integer.MAX_VALUE);
  }

  public class IntegerDomainBuilder {

    private final int startInclusive;

    private IntegerDomainBuilder(int startInclusive) {
      this.startInclusive = startInclusive;
    }

    /**
     * Generates integers within the interval specified with an inclusive lower
     * and upper bound.
     * 
     * @param endInclusive
     *          - inclusive upper bound of domain
     * @return a Source of type Integer
     */
    public Gen<Integer> upToAndIncluding(final int endInclusive) {
      return between(startInclusive, endInclusive);
    }

    /**
     * Generates integers within the interval specified with an inclusive lower
     * bound and exclusive upper bound.
     * 
     * @param endExclusive
     *          - exclusive upper bound of domain
     * @return a Source of type Integer
     */
    public Gen<Integer> upTo(final int endExclusive) {
      return between(startInclusive, endExclusive - 1);
    }

  }

  /**
   * Generates Integers within the interval specified with an inclusive lower
   * and upper bound.
   * 
   * @param startInclusive
   *          - inclusive lower bound of domain
   * @param endInclusive
   *          - inclusive upper bound of domain
   * @return a Source of type Integer
   */
  public Gen<Integer> between(final int startInclusive,
      final int endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Integer values to be generated between (%s) and (%s)",
        startInclusive, endInclusive);
    return Generate.range(startInclusive, endInclusive);
  }
}
