package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Gen;

/**
 * A Class for creating Double Sources that will produce doubles within a set
 * interval and will shrink within this domain.
 */
public class DoublesDSL {

  /**
   * Generates Doubles inclusively bounded below by Double.NEGATIVE_INFINITY and
   * above by Double.POSITIVE_INFINITY.
   * 
   * @return a Source of type Double
   */
  public Gen<Double> any() {
    return Doubles.fromNegativeInfinityToPositiveInfinity();

  }

  /**
   * Generates Doubles inclusively bounded below by Double.NEGATIVE_INFINITY and
   * above by a value very close to zero on the negative side.
   * 
   * @return a Source of type Double
   */
  public Gen<Double> negative() {
    return Doubles.negative();
  }

  /**
   * Generates Doubles inclusively bounded below by a value very close to zero
   * on the positive side and above by Double.POSITIVE_INFINITY.
   * 
   * @return a Source of type Double
   */
  public Gen<Double> positive() {
    return Doubles.positive();
  }


  /**
   * Generates Doubles inclusively bounded below by zero and above by one.
   * 
   * @return a Source of type Double
   */
  public Gen<Double> fromZeroToOne() {
    return Doubles.fromZeroToOne();
  }

}
