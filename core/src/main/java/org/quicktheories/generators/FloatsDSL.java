package org.quicktheories.generators;

import org.quicktheories.core.Gen;

/**
 * A Class for creating Float Sources that will produce floats within a set
 * interval and will shrink within this domain.
 */
public class FloatsDSL {

  /**
   * Generates Floats inclusively bounded below by Float.NEGATIVE_INFINITY and
   * above by Float.POSITIVE_INFINITY.
   * 
   * @return a Source of type Float
   */
  public Gen<Float> any() {
    return Floats.fromNegativeInfinityToPositiveInfinity();
  }

  /**
   * Generates Floats inclusively bounded below by Float.NEGATIVE_INFINITY and
   * above by a value very close to zero on the negative side.
   * 
   * @return a Source of type Float
   */
  public Gen<Float> negative() {
    return Floats.fromNegativeInfinityToNegativeZero();
  }

  /**
   * Generates Floats inclusively bounded below by a value very close to zero on
   * the positive side and above by Float.POSITIVE_INFINITY.
   * 
   * @return a Source of type Float
   */
  public Gen<Float> positive() {
    return Floats.fromZeroToPositiveInfinity();
  }

  /**
   * Generates Floats inclusively bounded below by zero and above by one.
   * 
   * @return a Source of type Float
   */
  public Gen<Float> fromZeroToOne() {
    return Floats.fromZeroToOne();
  }
  
  /**
   * Generates Floats inclusively between two bounds
   * @param minInclusive minimum value to generate
   * @param maxInclusive maximum value to generate
   * @return a Gen of Floats between minInclusive and maxInclusive
   */
  public Gen<Float> between(float minInclusive, float maxInclusive) {
    return Floats.between(minInclusive, maxInclusive);
  }
  
  /**
   * Starts a range
   * 
   * @param startInclusive
   *          - lower bound of domain
   * @return start of range
   */
  public FloatDomainBuilder from(final float startInclusive) {
    return new FloatDomainBuilder(startInclusive);
  }

  
  public class FloatDomainBuilder {

    private final float startInclusive;

    private FloatDomainBuilder(float startInclusive) {
      this.startInclusive = startInclusive;
    }

    /**
     * Generates within the interval specified with an inclusive lower
     * and upper bound.
     * 
     * @param endInclusive
     *          - inclusive upper bound of domain
     * @return a Source of type Float
     */
    public Gen<Float> upToAndIncluding(final float endInclusive) {
      return between(startInclusive, endInclusive);
    }

  }
    

}
