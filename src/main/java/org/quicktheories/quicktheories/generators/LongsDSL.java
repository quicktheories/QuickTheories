package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;
import static org.quicktheories.quicktheories.generators.Longs.range;

/**
 * A Class for creating Long Sources that will produces Longs within a set
 * interval and will shrink within this domain. If the distance between the
 * target and original value is greater than the number of remaining shrink
 * cycles, shrinking is random with the domain; otherwise, shrinking is
 * deterministic with Longs getting closer to the target value by increments of
 * one.
 *
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
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Long
   */
  public Source<Long> all() {
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
     * The Source is weighted so it is likely to generate the upper and lower
     * limits of the domain one or more times.
     * 
     * @param endInclusive
     *          - inclusive upper bound of domain
     * @return a Source of type Long
     */
    public Source<Long> upToAndIncluding(final long endInclusive) {
      return between(startInclusive, endInclusive);
    }

    /**
     * Generates Longs within the interval specified with an inclusive lower
     * bound and exclusive upper bound.
     * 
     * The Source is weighted so it is likely to generate the upper and lower
     * limits of the domain one or more times.
     * 
     * @param endExclusive
     *          - exclusive upper bound of domain
     * @return a Source of type Long
     */
    public Source<Long> upTo(final long endExclusive) {
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
  public Source<Long> between(final long startInclusive,
      final long endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Long values to be generated between (%s) and (%s)",
        startInclusive, endInclusive);
    return Compositions.weightWithValues(
        range(startInclusive, endInclusive), startInclusive, endInclusive);
  }

}
