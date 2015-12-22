package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Doubles {

  private static final long POSITIVE_INFINITY_CORRESPONDING_LONG = 0x7ff0000000000000L;
  private static final long NEGATIVE_INFINITY_CORRESPONDING_LONG = 0xfff0000000000000L;
  private static final long ONE_CORRESPONDING_LONG = 4607182418800017408L;
  private static final long NEGATIVE_ZERO_CORRESPONDING_LONG = Long.MIN_VALUE;

  static Source<Double> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.interleave(fromNegativeInfinityToNegativeZero(),
        fromZeroToPositiveInfinity());
  }

  static Source<Double> fromNegativeDoubleMaxToDoubleMax() {
    return Compositions.interleave(fromNegativeDoubleMaxToNegativeZero(),
        fromZeroToDoubleMax());
  }

  static Source<Double> fromNegativeInfinityToNegativeZero() {
    return range(NEGATIVE_ZERO_CORRESPONDING_LONG,
        NEGATIVE_INFINITY_CORRESPONDING_LONG, NEGATIVE_ZERO_CORRESPONDING_LONG);
  }

  static Source<Double> fromNegativeDoubleMaxToNegativeZero() {
    return range(NEGATIVE_ZERO_CORRESPONDING_LONG,
        NEGATIVE_INFINITY_CORRESPONDING_LONG - 1,
        NEGATIVE_ZERO_CORRESPONDING_LONG);
  }

  static Source<Double> fromZeroToPositiveInfinity() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_LONG);
  }

  static Source<Double> fromZeroToDoubleMax() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_LONG - 1);
  }

  static Source<Double> fromZeroToOne() {
    return range(0, ONE_CORRESPONDING_LONG);
  }

  static Source<Double> range(long startInclusive, long endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  static Source<Double> range(long startInclusive, long endInclusive,
      long target) {
    return Longs.range(startInclusive, endInclusive)
        .withShrinker(Longs.shrinkTowardsTarget(target))
        .as(i -> Double.longBitsToDouble(i), j -> Double.doubleToLongBits(j));
  }

}
