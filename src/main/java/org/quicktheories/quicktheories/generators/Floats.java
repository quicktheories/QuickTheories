package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Floats {

  private static final int POSITIVE_INFINITY_CORRESPONDING_INT = 0x7f800000;
  private static final int NEGATIVE_INFINITY_CORRESPONDING_INT = 0xff800000;
  private static final int ONE_CORRESPONDING_INT = 1065353216;
  private static final int NEGATIVE_ZERO_CORRESPONDING_INT = Integer.MIN_VALUE;

  static Source<Float> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.interleave(fromNegativeInfinityToNegativeZero(),
        fromZeroToPositiveInfinity());
  }

  static Source<Float> fromNegativeFloatMaxToPositiveFloatMax() {
    return Compositions.interleave(fromNegativeFloatMaxToNegativeZero(),
        fromZeroToFloatMax());
  }

  static Source<Float> fromNegativeInfinityToNegativeZero() {
    return range(NEGATIVE_ZERO_CORRESPONDING_INT,
        NEGATIVE_INFINITY_CORRESPONDING_INT, NEGATIVE_ZERO_CORRESPONDING_INT);
  }

  static Source<Float> fromNegativeFloatMaxToNegativeZero() {
    return range(NEGATIVE_ZERO_CORRESPONDING_INT,
        NEGATIVE_INFINITY_CORRESPONDING_INT - 1,
        NEGATIVE_ZERO_CORRESPONDING_INT);
  }

  static Source<Float> fromZeroToPositiveInfinity() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_INT);
  }

  static Source<Float> fromZeroToFloatMax() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_INT - 1);
  }

  static Source<Float> fromZeroToOne() {
    return range(0, ONE_CORRESPONDING_INT);
  }

  private static Source<Float> range(int startInclusive, int endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  private static Source<Float> range(int startInclusive, int endInclusive,
      int target) {
    return Longs.range(startInclusive, endInclusive)
        .withShrinker(Longs.shrinkTowardsTarget(target))
        .as(i -> Float.intBitsToFloat((int) i.longValue()),
            j -> (long) Float.floatToIntBits(j));
  }

}
