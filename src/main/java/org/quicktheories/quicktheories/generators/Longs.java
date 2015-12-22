package org.quicktheories.quicktheories.generators;

import java.util.function.LongPredicate;
import java.util.stream.LongStream;

import org.quicktheories.quicktheories.api.Function3;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

final class Longs {

  static Source<Long> range(final long startInclusive,
      final long endInclusive) {
    return Source.of(
        (prng, step) -> prng.generateRandomLongWithinInterval(startInclusive,
            endInclusive))
        .withShrinker(
            shrinkTowardsTarget(findTarget(startInclusive, endInclusive)));
  }

  static Shrink<Long> shrinkTowardsTarget(long target) {
    return shrinkTowardsTarget(target, s -> true,
        (prng, start, end) -> generateLongWithinInterval(prng, start, end));
  }

  static Shrink<Long> shrinkTowardsTarget(long target,
      LongPredicate filter,
      Function3<PseudoRandom, Long, Long, Long> longGenerator) {
    return (original, context) -> {
      if (shouldShrinkAcrossDomain(target, original, context.remainingCycles())
          && original != target) {
        return shrinkAcrossEntireDomain(original, target, filter).boxed();
      }
      return shrinkRandomly(original, context, target, longGenerator).boxed();
    };
  }

  private static boolean shouldShrinkAcrossDomain(long target, long original,
      long remainingCycles) {
    if ((target >= 0 && original >= 0) || (intervalLEQMax(target, original))
        || (intervalLEQMax(original, target))) {
      return Math.abs(target - original) <= remainingCycles;
    }
    if (!negativeIntervalEqualMin(target, original)) {
      return Math.abs((target + 1) - (original + 1)) <= remainingCycles;
    }
    return false;
  }

  private static boolean intervalLEQMax(long x, long y) {
    return Long.MIN_VALUE < x && x <= 0 && 0 <= y && y <= Long.MAX_VALUE + x;
  }

  private static boolean negativeIntervalEqualMin(long x, long y) {
    return x == 0 && y == Long.MIN_VALUE || x == Long.MIN_VALUE && y == 0;
  }

  private static LongStream shrinkAcrossEntireDomain(long original, long target,
      LongPredicate filter) {
    long limit = Math.abs(original - target);
    if (original > target) {
      return LongStream.iterate(original - 1, i -> i - 1).filter(filter)
          .limit(limit);
    }
    return LongStream.iterate(original + 1, i -> i + 1).filter(filter)
        .limit(limit);
  }

  private static LongStream shrinkRandomly(long original, ShrinkContext context,
      long target, Function3<PseudoRandom, Long, Long, Long> longGenerator) {
    if (original != target) {
      PseudoRandom prng = context.prng();
      if (original > target) {
        return LongStream
            .generate(() -> longGenerator.apply(prng, target, original));
      }
      return LongStream.generate(
          () -> longGenerator.apply(prng, original, target));
    }
    return LongStream.empty();
  }

  private static long generateLongWithinInterval(PseudoRandom prng,
      long startInclusive, long endInclusive) {
    return prng.generateRandomLongWithinInterval(startInclusive, endInclusive);
  }

  private static long findTarget(long startInclusive, long endInclusive) {
    if (startInclusive > 0) {
      return startInclusive;
    }
    if (endInclusive < 0) {
      return endInclusive;
    }
    return 0L;
  }

}
