package org.quicktheories.quicktheories.generators;

import java.util.stream.LongStream;

import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

final class Characters {

  static Source<Character> ofCharacters(int startCodePoint,
      int endCodePoint) {
    return Source.of((prng, step) -> (char) Longs.generateValidCodePoint(prng,
        startCodePoint, endCodePoint))
        .withShrinker(shrinkCharacter(startCodePoint));
  }

  private static Shrink<Character> shrinkCharacter(int startInclusive) {
    return (original,
        context) -> shrinkCharacterStream(original, startInclusive, context)
            .mapToObj(i -> (char) i);
  }

  private static LongStream shrinkCharacterStream(int original,
      int startInclusive,
      ShrinkContext context) {
    if (original - startInclusive <= context.remainingCycles()
        && original != startInclusive) {
      return shrinkAcrossEntireDomain(original, startInclusive);
    }
    return shrinkRandomly(original, startInclusive, context);
  }

  private static LongStream shrinkAcrossEntireDomain(int original,
      int startInclusive) {
    int limit = Math.abs(original - startInclusive);
    return LongStream.iterate(original - 1, i -> i - 1)
        .filter(i -> Character.isDefined((int) i))
        .limit(limit);
  }

  private static LongStream shrinkRandomly(int original, int startInclusive,
      ShrinkContext context) {
    if (original != startInclusive) {
      PseudoRandom prng = context.prng();
      return LongStream.generate(
          () -> Longs.generateValidCodePoint(prng, startInclusive,
              original));
    }
    return LongStream.empty();
  }

}
