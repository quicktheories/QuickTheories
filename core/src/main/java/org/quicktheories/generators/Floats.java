package org.quicktheories.generators;

import org.quicktheories.core.Gen;

final class Floats {

  private static final int POSITIVE_INFINITY_CORRESPONDING_INT = 0x7f800000;
  private static final int NEGATIVE_INFINITY_CORRESPONDING_INT = 0xff800000;
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
    return Generate.range(0, 1 << 24, 0).map(i -> i / (float)(1 << 24));
  }
  
  static Gen<Float> between(float min, float max) {
    ArgumentAssertions.checkArguments(min <= max,
        "Cannot have the maximum (%s) smaller than the min (%s)",
        max, min);
    float adjustedMax = max - min;
    return fromZeroToOne().map(f -> (f * adjustedMax) + min);
  }

  private static Gen<Float> range(int startInclusive, int endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  private static Gen<Float> range(int startInclusive, int endInclusive,
      int target) {
    return Generate.range(startInclusive, endInclusive, target)
        .map(i -> Float.intBitsToFloat(i));
  }

}
