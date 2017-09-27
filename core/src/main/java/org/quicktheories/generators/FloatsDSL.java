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

}
