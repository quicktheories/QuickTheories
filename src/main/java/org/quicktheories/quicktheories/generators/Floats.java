package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Floats {

  private static final int POSITIVE_INFINITY_CORRESPONDING_INT = 0x7f800000;
  private static final int NEGATIVE_INFINITY_CORRESPONDING_INT = 0xff800000;

  static Source<Float> fromNegativeInfinityToPositiveInfinity() {
    return Compositions.interleave(fromNegativeInfinityToNegativeZero(),
        fromZeroToPositiveInfinity());
  }

  static Source<Float> fromNegativeFloatMaxToPositiveFloatMax() {
    return Compositions.interleave(fromNegativeFloatMaxToNegativeZero(),
        fromZeroToFloatMax());
  }

  static Source<Float> fromNegativeInfinityToNegativeZero() {
    return Longs.range(Integer.MIN_VALUE, NEGATIVE_INFINITY_CORRESPONDING_INT)
        .withShrinker(Longs.shrinkTowardsTarget(Integer.MIN_VALUE))
        .as(i -> Float.intBitsToFloat((int) i.longValue()),
            j -> (long) Float.floatToIntBits(j));
  }

  static Source<Float> fromNegativeFloatMaxToNegativeZero() {
    return Longs
        .range(Integer.MIN_VALUE, NEGATIVE_INFINITY_CORRESPONDING_INT - 1)
        .withShrinker(Longs.shrinkTowardsTarget(Integer.MIN_VALUE))
        .as(i -> Float.intBitsToFloat((int) i.longValue()),
            j -> (long) Float.floatToIntBits(j));
  }

  static Source<Float> fromZeroToPositiveInfinity() {
    return Integers.range(0, POSITIVE_INFINITY_CORRESPONDING_INT)
        .as(i -> Float.intBitsToFloat(i), j -> Float.floatToIntBits(j));
  }

  static Source<Float> fromZeroToFloatMax() {
    return Integers.range(0, POSITIVE_INFINITY_CORRESPONDING_INT - 1).as(
        i -> Float.intBitsToFloat(i),
        j -> Float.floatToIntBits(j));
  }

  static Source<Float> fromZeroToOne() {
    return Integers.range(0, 1).as(i -> Float.intBitsToFloat(i),
        j -> Float.floatToIntBits(j));
  }

}
