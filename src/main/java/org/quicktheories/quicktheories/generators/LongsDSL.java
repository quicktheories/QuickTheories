package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Gen;

/**
 * A Class for creating Long Sources that will produces Longs within a set
 * interval and will shrink within this domain. 
 */
public class LongsDSL {

  /**
   * Constructs a LongDomainBuilder object with an inclusive lower bound
   * 
   * @param startInclusive
   *          - lower bound of domain
   * @return a LongDomainBuilder
   */
  public LongDomainBuilder from(final long startInclusive) {
    return new LongDomainBuilder(startInclusive);
  }

  /**
   * Generates all possible Longs in Java bounded below by Long.MIN_VALUE and
   * above by Long.MAX_VALUE.
   * 
   * @return a Source of type Long
   */
  public Gen<Long> all() {
    return between(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public class LongDomainBuilder {

    private final long startInclusive;

    private LongDomainBuilder(long startInclusive) {
      this.startInclusive = startInclusive;
    }

    /**
     * Generates Longs within the interval specified with an inclusive lower and
     * upper bound.
     * 
     * @param endInclusive
     *          - inclusive upper bound of domain
     * @return a Source of type Long
     */
    public Gen<Long> upToAndIncluding(final long endInclusive) {
      return between(startInclusive, endInclusive);
    }

    /**
     * Generates Longs within the interval specified with an inclusive lower
     * bound and exclusive upper bound.
     * 
     * @param endExclusive
     *          - exclusive upper bound of domain
     * @return a Source of type Long
     */
    public Gen<Long> upTo(final long endExclusive) {
      return between(startInclusive, endExclusive - 1);
    }
  }

  /**
   * Generates Longs within the interval specified with an inclusive lower and
   * upper bound.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @param startInclusive
   *          - inclusive lower bound of domain
   * @param endInclusive
   *          - inclusive upper bound of domain
   * @return a Source of type Long
   */
  public Gen<Long> between(final long startInclusive,
      final long endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Long values to be generated between (%s) and (%s)",
        startInclusive, endInclusive);
    return Generate.longRange(startInclusive, endInclusive);
  }

}
