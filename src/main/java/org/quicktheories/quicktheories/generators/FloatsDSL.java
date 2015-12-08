package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating Float Sources that will produce floats within a set
 * interval and will shrink within this domain.
 */
public class FloatsDSL {

  /**
   * Generates Floats inclusively bounded below by Float.NEGATIVE_INFINITY and
   * above by Float.POSITIVE_INFINITY.
   * 
   * The Source is weighted so it is likely to generate Float.NEGATIVE_INFINITY,
   * Float.POSITIVE_INFINITY, Float.MAX_VALUE one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.weightWithValues(
        Floats.fromNegativeInfinityToPositiveInfinity(),
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.MAX_VALUE);
  }

  /**
   * Generates Floats inclusively bounded below by Float.NEGATIVE_INFINITY and
   * above by a value very close to zero on the negative side.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromNegativeInfinityToNegativeZero() {
    return Compositions.weightWithValues(
        Floats.fromNegativeInfinityToNegativeZero(),
        Float.NEGATIVE_INFINITY, -0f);
  }

  /**
   * Generates Floats inclusively bounded below by -Float.MAX_VALUE and above by
   * Float.MAX_VALUE.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromNegativeFloatMaxToPositiveFloatMax() {
    return Compositions.weightWithValues(
        Floats.fromNegativeFloatMaxToPositiveFloatMax(), -Float.MAX_VALUE,
        Float.MAX_VALUE);
  }

  /**
   * Generates Floats inclusively bounded below by -Float.MAX_VALUE and above by
   * a value very close to zero on the negative side.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromNegativeFloatMaxToNegativeZero() {
    return Compositions
        .weightWithValues(
            Floats.fromNegativeFloatMaxToNegativeZero(),
            -Float.MAX_VALUE, -0f);
  }

  /**
   * Generates Floats inclusively bounded below by a value very close to zero on
   * the positive side and above by Float.POSITIVE_INFINITY.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromZeroToPositiveInfinity() {
    return Compositions.weightWithValues(
        Floats.fromZeroToPositiveInfinity(),
        Float.POSITIVE_INFINITY, 0f);
  }

  /**
   * Generates Floats inclusively bounded below by a value very close to zero on
   * the positive side and above by Float.MAX_VALUE.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromZeroToFloatMax() {
    return Compositions.weightWithValues(
        Floats.fromZeroToFloatMax(),
        Float.MAX_VALUE, 0f);
  }

  /**
   * Generates Floats inclusively bounded below by zero and above by one.
   *
   * The Source is weighted so it is likely to generate the upper and lower
   * limits of the domain one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> fromZeroToOne() {
    return Compositions.weightWithValues(
        Floats.fromZeroToOne(),
        1f, 0f);
  }

  /**
   * Generates Floats in Java, including Float.NaN - which will only ever shrink
   * to itself.
   * 
   * The Source is weighted so it is likely to generate Float.NEGATIVE_INFINITY,
   * Float.POSITIVE_INFINITY, Float.MAX_VALUE and Float.NaN one or more times.
   * 
   * @return a Source of type Float
   */
  public Source<Float> allFloats() {
    return Compositions.combineWithValues(
        fromNegativeInfinityToPositiveInfinity(), Float.NaN);
  }

}
