package org.quicktheories.generators;

import org.quicktheories.core.Gen;

final class Floats {

  private static final int POSITIVE_INFINITY_CORRESPONDING_INT = 0x7f800000;
  private static final int NEGATIVE_INFINITY_CORRESPONDING_INT = 0xff800000;
  private static final int ONE_CORRESPONDING_INT = 1065353216;
  private static final int NEGATIVE_ZERO_CORRESPONDING_INT = Integer.MIN_VALUE;

  static Gen<Float> fromNegativeInfinityToPositiveInfinity() {
    return fromNegativeInfinityToNegativeZero().mix(
        fromZeroToPositiveInfinity());
  }

  static Gen<Float> fromNegativeInfinityToNegativeZero() {
    return range(NEGATIVE_ZERO_CORRESPONDING_INT,
        NEGATIVE_INFINITY_CORRESPONDING_INT, NEGATIVE_ZERO_CORRESPONDING_INT);
  }

  static Gen<Float> fromZeroToPositiveInfinity() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_INT);
  }

  static Gen<Float> fromZeroToOne() {
    return range(0, ONE_CORRESPONDING_INT);
  }

  private static Gen<Float> range(int startInclusive, int endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  private static Gen<Float> range(int startInclusive, int endInclusive,
      int target) {
    return Generate.range(startInclusive, endInclusive, target)
        .map(i -> Float.intBitsToFloat((int) i.longValue()));
  }

}
