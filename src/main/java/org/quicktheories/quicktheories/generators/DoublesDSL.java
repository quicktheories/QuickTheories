package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating Double Sources that will produce doubles within a set
 * interval and will shrink within this domain.
 */
public class DoublesDSL {

  /**
   * Generates Doubles inclusively bounded below by Double.NEGATIVE_INFINITY and
   * above by Double.POSITIVE_INFINITY.
   * 
   * The Source is weighted so it is likely to generate
   * Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY and Double.MAX_VALUE one
   * or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.weightWithValues(
        Doubles.fromNegativeInfinityToPositiveInfinity(),
        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MAX_VALUE);
  }

  /**
   * Generates Doubles inclusively bounded below by Double.NEGATIVE_INFINITY and
   * above by a value very close to zero on the negative side.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromNegativeInfinityToNegativeZero() {
    return Compositions.weightWithValues(
        Doubles.fromNegativeInfinityToNegativeZero(),
        Double.NEGATIVE_INFINITY, -0d);
  }

  /**
   * Generates Doubles inclusively bounded below by -Double.MAX_VALUE and above
   * by Double.MAX_VALUE.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromNegativeDoubleMaxToPositiveDoubleMax() {
    return Compositions.weightWithValues(
        Doubles.fromNegativeDoubleMaxToDoubleMax(), -Double.MAX_VALUE,
        Double.MAX_VALUE);
  }

  /**
   * Generates Doubles inclusively bounded below by -Double.MAX_VALUE and above
   * by a value very close to zero on the negative side.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromNegativeDoubleMaxToNegativeZero() {
    return Compositions
        .weightWithValues(
            Doubles.fromNegativeDoubleMaxToNegativeZero(),
            -Double.MAX_VALUE, -0d);
  }

  /**
   * Generates Doubles inclusively bounded below by a value very close to zero
   * on the positive side and above by Double.POSITIVE_INFINITY.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromZeroToPositiveInfinity() {
    return Compositions.weightWithValues(
        Doubles.fromZeroToPositiveInfinity(),
        Double.POSITIVE_INFINITY, 0d);
  }

  /**
   * Generates Doubles inclusively bounded below by a value very close to zero
   * on the positive side and above by Double.MAX_VALUE.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromZeroToDoubleMax() {
    return Compositions.weightWithValues(
        Doubles.fromZeroToDoubleMax(),
        Double.MAX_VALUE, 0d);
  }

  /**
   * Generates Doubles inclusively bounded below by zero and above by one.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Double
   */
  public Source<Double> fromZeroToOne() {
    return Compositions.weightWithValues(
        Doubles.fromZeroToOne(),
        1d, 0d);
  }

  /**
   * Generates Doubles in Java, including Double.NaN - which will only ever
   * shrink to itself.
   * 
   * The Source is weighted so it is likely to generate Double.NaN, Double.NEGATIVE_INFINITY,
   * Double.POSITIVE_INFINITY and Double.MAX_VALUE one or more times.
   * 
   * @return Source of type Double
   */
  public Source<Double> allDoubles() {
    return Compositions.combineWithValues(
        fromNegativeInfinityToPositiveInfinity(), Double.NaN);
  }

}
