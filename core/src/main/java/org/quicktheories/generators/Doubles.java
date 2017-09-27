package org.quicktheories.generators;

import java.util.function.Function;

import org.quicktheories.core.Gen;

final class Doubles {

  private static final long POSITIVE_INFINITY_CORRESPONDING_LONG = 0x7ff0000000000000L;
  private static final long NEGATIVE_INFINITY_CORRESPONDING_LONG = 0xfff0000000000000L;
  //fraction portion of double, last 52 bits
  private static final long FRACTION_BITS = 1L << 53;
  private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)
  private static final long NEGATIVE_ZERO_CORRESPONDING_LONG = Long.MIN_VALUE;

  static Gen<Double> fromNegativeInfinityToPositiveInfinity() {
    return negative().mix(positive());
  }

  static Gen<Double> negative() {
    return range(NEGATIVE_ZERO_CORRESPONDING_LONG,
        NEGATIVE_INFINITY_CORRESPONDING_LONG, NEGATIVE_ZERO_CORRESPONDING_LONG);
  }

  static Gen<Double> positive() {
    return range(0, POSITIVE_INFINITY_CORRESPONDING_LONG);
  }

  static Gen<Double> fromZeroToOne() {
    return range(0, FRACTION_BITS, 0,
        l -> l * DOUBLE_UNIT);
  }

  static Gen<Double> range(long startInclusive, long endInclusive) {
    return range(startInclusive, endInclusive, 0);
  }

  static Gen<Double> range(long startInclusive, long endInclusive,
      long target) {
    return range(startInclusive, endInclusive, target,
        Double::longBitsToDouble);
  }

  static Gen<Double> range(long startInclusive, long endInclusive,
      long target, Function<Long, Double> conversion) {
    return Generate.longRange(startInclusive, endInclusive)
        .map(conversion);
  }

}
