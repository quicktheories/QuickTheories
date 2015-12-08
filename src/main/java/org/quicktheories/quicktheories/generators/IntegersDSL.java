package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;
import static org.quicktheories.quicktheories.generators.Integers.range;
import static org.quicktheories.quicktheories.generators.Longs.range;

/**
 * A Class for creating Integer Sources that will produce integers within a set
 * interval and will shrink within this domain. If the distance between the
 * target and original value is greater than the number of remaining shrink
 * cycles, shrinking is random with the domain; otherwise, shrinking is
 * deterministic with integers getting closer to the target value by increments
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
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Integer
   */
  public Source<Integer> all() {
    return between(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Generates all possible positive integers in Java, bounded above by
   * Integer.MAX_VALUE.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Integer
   */
  public Source<Integer> allPositive() {
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
     * The Source is weighted so it is likely to generate the upper and lower
     * limits of the domain one or more times.
     * 
     * @param endInclusive
     *          - inclusive upper bound of domain
     * @return a Source of type Integer
     */
    public Source<Integer> upToAndIncluding(final int endInclusive) {
      return between(startInclusive, endInclusive);
    }

    /**
     * Generates integers within the interval specified with an inclusive lower
     * bound and exclusive upper bound.
     * 
     * The Source is weighted so it is likely to generate the upper and lower
     * limits of the domain one or more times.
     * 
     * @param endExclusive
     *          - exclusive upper bound of domain
     * @return a Source of type Integer
     */
    public Source<Integer> upTo(final int endExclusive) {
      return between(startInclusive, endExclusive - 1);
    }

  }

  /**
   * Generates Integers within the interval specified with an inclusive lower
   * and upper bound.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @param startInclusive
   *          - inclusive lower bound of domain
   * @param endInclusive
   *          - inclusive upper bound of domain
   * @return a Source of type Integer
   */
  public Source<Integer> between(final int startInclusive,
      final int endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Integer values to be generated between (%s) and (%s)",
        startInclusive, endInclusive);
    return Compositions.weightWithValues(
        range(startInclusive, endInclusive), startInclusive, endInclusive);
  }
}
