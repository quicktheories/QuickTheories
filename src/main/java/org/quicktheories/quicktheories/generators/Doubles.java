package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Doubles {

  private static final long POSITIVE_INFINITY_CORRESPONDING_LONG = 0x7ff0000000000000L;
  private static final long NEGATIVE_INFINITY_CORRESPONDING_LONG = 0xfff0000000000000L;

  static Source<Double> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.interleave(fromNegativeInfinityToNegativeZero(),
        fromZeroToPositiveInfinity());
  }

  static Source<Double> fromNegativeDoubleMaxToDoubleMax() {
    return Compositions.interleave(fromNegativeDoubleMaxToNegativeZero(),
        fromZeroToDoubleMax());
  }

  static Source<Double> fromNegativeInfinityToNegativeZero() {
    return Longs.range(Long.MIN_VALUE, NEGATIVE_INFINITY_CORRESPONDING_LONG)
        .withShrinker(Longs.shrinkTowardsTarget(Long.MIN_VALUE))
        .as(i -> Double.longBitsToDouble(i), j -> Double.doubleToLongBits(j));
  }

  static Source<Double> fromNegativeDoubleMaxToNegativeZero() {
    return Longs.range(Long.MIN_VALUE, NEGATIVE_INFINITY_CORRESPONDING_LONG - 1)
        .withShrinker(Longs.shrinkTowardsTarget(Long.MIN_VALUE))
        .as(i -> Double.longBitsToDouble(i), j -> Double.doubleToLongBits(j));
  }

  static Source<Double> fromZeroToPositiveInfinity() {
    return Longs.range(0, POSITIVE_INFINITY_CORRESPONDING_LONG)
        .as(i -> Double.longBitsToDouble(i), j -> Double.doubleToLongBits(j));
  }

  static Source<Double> fromZeroToDoubleMax() {
    return Longs.range(0, POSITIVE_INFINITY_CORRESPONDING_LONG - 1)
        .as(i -> Double.longBitsToDouble(i), j -> Double.doubleToLongBits(j));
  }

  static Source<Double> fromZeroToOne() {
    return Longs.range(0, 1).as(i -> Double.longBitsToDouble(i),
        j -> Double.doubleToLongBits(j));
  }

}
