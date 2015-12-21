package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Source;

final class CodePoints {

  private static final int FIRST_NON_WHITESPACE_CHARACTER_IN_BLC = 0x0021;

  static Source<Long> codePoints(int startInclusive,
      int endInclusive) {
    return codePoints(startInclusive, endInclusive,
        FIRST_NON_WHITESPACE_CHARACTER_IN_BLC);

  }

  static Source<Long> codePoints(int startInclusive, int endInclusive,
      int idealTarget) {
    long target = setTargetForCodePoints(startInclusive, endInclusive,
        idealTarget);
    return Source.of(
        (prng, step) -> generateValidCodePoint(prng, startInclusive,
            endInclusive))
        .withShrinker(
            shrinkCodePoint(target));
  }

  private static long generateValidCodePoint(PseudoRandom prng, long startInclusive,
      long endInclusive) {
    long codePoint = prng.generateRandomLongWithinInterval(
        startInclusive, endInclusive);
    while (!Character.isDefined((int) codePoint)) {
      codePoint = prng.generateRandomLongWithinInterval(
          startInclusive, endInclusive);
    }
    return codePoint;
  }

  private static long setTargetForCodePoints(int startInclusive,
      int endInclusive, int idealTarget) {
    long target = idealTarget;
    if (startInclusive > target || endInclusive < target) {
      target = startInclusive;
    }
    return target;
  }

  private static Shrink<Long> shrinkCodePoint(long target) {
    return Longs.shrinkTowardsTarget(target, i -> Character.isDefined((int) i),
        (prng, start, end) -> (long) generateValidCodePoint(prng, start, end));
  }

}
