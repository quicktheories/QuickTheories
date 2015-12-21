package org.quicktheories.quicktheories.generators;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

final class Strings {

  private static final String LARGEST_DEFINED_BMP_CHARACTER = "\ufffd";
  private static final int LARGEST_DEFINED_BMP_CODEPOINT = 65533;

  static Source<String> boundedNumericStrings(int startInclusive,
      int endInclusive) {
    return Integers.range(startInclusive, endInclusive)
        .as(i -> i.toString(), s -> Integer.parseInt(s));
  }

  static Source<String> ofFixedNumberOfCodePointsStrings(int minCodePoint,
      int maxCodePoint, int codePoints) {
    return Source.of(
        (prng, step) -> Collections.nCopies(codePoints, prng)
            .stream()
            .map(p -> (int) CodePoints.codePoints(minCodePoint, maxCodePoint)
                .next(p, step).longValue())
            .map(i -> new StringBuilder().appendCodePoint(i).toString())
            .collect(Collectors.joining()))
        .withShrinker(
            Strings.shrinkFixedNumberOfCodePointsString(minCodePoint,
                maxCodePoint));
  }

  static Source<String> ofBoundedLengthStrings(int minCodePoint,
      int maxCodePoint,
      int minLength, int maxLength) {
    return Source.of((prng, step) -> generateFixedLengthString(
        CodePoints.codePoints(minCodePoint, maxCodePoint),
        (int) prng.generateRandomLongWithinInterval(minLength, maxLength),
        prng, step)).withShrinker(
            Strings.shrinkBoundedLengthString(minLength, minCodePoint,
                maxCodePoint));
  }

  private static Shrink<String> shrinkFixedNumberOfCodePointsString(
      int startInclusive, int endInclusive) {
    return (original, context) -> shrinkFixedSizeStringStream(original,
        c -> shrinkSingleElementStringOfFixedNumberOfCodePoints(c, context,
            startInclusive, endInclusive));
  }

  private static Stream<String> shrinkFixedSizeStringStream(
      String original, Function<Integer, String> howShrinkSingle) {
    if (original.isEmpty()) {
      return Stream.empty();
    }
    return Stream.iterate(
        shrunkenFixedSizeString(original, c -> howShrinkSingle.apply(c)),
        s -> shrunkenFixedSizeString(s, c -> howShrinkSingle.apply(c)));
  }

  private static String shrunkenFixedSizeString(String original,
      Function<Integer, String> howShrinkSingle) {
    return original.codePoints()
        .mapToObj(c -> howShrinkSingle.apply(c))
        .collect(Collectors.joining());
  }

  private static String shrinkSingleElementStringOfFixedNumberOfCodePoints(
      long original, ShrinkContext context, int startInclusive,
      int endInclusive) {
    long codePoint = CodePoints
        .codePoints(startInclusive, endInclusive).shrink(original, context)
        .findFirst()
        .orElse(original);

    return codePointToString((int) codePoint);
  }

  private static String codePointToString(Integer codePoint) {
    return new StringBuilder().appendCodePoint(codePoint).toString();
  }

  private static String shrinkSingleElementStringOfFixedLength(int original,
      ShrinkContext context, int startInclusive, int endInclusive) {
    int numberOfCharacters = Character.charCount(original);
    String shrunkenElement = shrinkSingleElementStringOfFixedNumberOfCodePoints(
        original, context, startInclusive, endInclusive);
    if (shrunkenElement.length() < numberOfCharacters) { // will only ever be
      // less by one
      return shrunkenElement + shrunkenElement;
    }
    return shrunkenElement;
  }

  private static Shrink<String> shrinkBoundedLengthString(int minimumLength,
      int startInclusive, int endInclusive) {
    return (original, context) -> {
      if (original.length() > minimumLength) {
        return shrinkBoundedLengthStringStream(original, context, minimumLength,
            startInclusive);
      }
      return shrinkFixedSizeStringStream(original,
          c -> shrinkSingleElementStringOfFixedLength(c, context,
              startInclusive, endInclusive));
    };
  }

  private static Stream<String> shrinkBoundedLengthStringStream(String original,
      ShrinkContext context, int minimumLength, int startInclusive) {
    int limit = original.length() - minimumLength;
    String randomSubString = createRandomSubString(original, context,
        startInclusive);
    return Stream
        .iterate(randomSubString,
            s -> createRandomSubString(s, context, startInclusive))
        .limit(limit);
  }

  private static String createRandomSubString(String original,
      ShrinkContext context, int startInclusive) {
    int randomIndex = context.prng().nextInt(0, original.length() - 1);
    // The order of the following booleans is important so as not to have an
    // index out of bounds exception
    if (isNotASurrogate(original, randomIndex)
        || isHighSurrogateAtFinalIndex(original, randomIndex)
        || isLowSurrogateAtIndexZero(original, randomIndex)
        || isHighSurrogateNotPartOfSurrogatePair(original, randomIndex)
        || isLowSurrogateNotPartOfSurrogatePair(original, randomIndex)) {
      return original.substring(0, randomIndex)
          + original.substring(randomIndex + 1);
    }
    return replaceSurrogatePair(original, randomIndex, startInclusive);
  }

  private static boolean isNotASurrogate(String original, int randomIndex) {
    return !Character.isHighSurrogate(original.charAt(randomIndex))
        && !Character.isLowSurrogate(original.charAt(randomIndex));
  }

  private static boolean isHighSurrogateAtFinalIndex(String original,
      int randomIndex) {
    return Character.isHighSurrogate(original.charAt(randomIndex))
        && randomIndex == original.length() - 1;
  }

  private static boolean isLowSurrogateAtIndexZero(String original,
      int randomIndex) {
    return Character.isLowSurrogate(original.charAt(randomIndex))
        && randomIndex == 0;
  }

  private static boolean isHighSurrogateNotPartOfSurrogatePair(String original,
      int randomIndex) {
    return Character.isHighSurrogate(original.charAt(randomIndex))
        && !Character.isSurrogatePair(original.charAt(randomIndex),
            original.charAt(randomIndex + 1));
  }

  private static boolean isLowSurrogateNotPartOfSurrogatePair(String original,
      int randomIndex) {
    return Character.isLowSurrogate(original.charAt(randomIndex))
        && !Character.isSurrogatePair(original.charAt(randomIndex - 1),
            original.charAt(randomIndex));
  }

  private static String replaceSurrogatePair(String original, int randomIndex,
      int startInclusive) {
    if (startInclusive <= LARGEST_DEFINED_BMP_CODEPOINT) {
      if (Character.isHighSurrogate(original.charAt(randomIndex))) {
        return replaceSupplementaryCharacter(original, randomIndex,
            randomIndex + 2);
      }
      return replaceSupplementaryCharacter(original, randomIndex - 1,
          randomIndex + 1);
    }
    return original; // will return the same String repeatedly, rather than
                     // return a String outside of the domain
  }

  private static String replaceSupplementaryCharacter(String original,
      int startOfSupplementaryChar, int startOfRestOfString) {
    return original.substring(0, startOfSupplementaryChar)
        + LARGEST_DEFINED_BMP_CHARACTER
        + original.substring(startOfRestOfString);
  }

  private static String generateFixedLengthString(
      Source<Long> codePointGenerator, int fixedLength, PseudoRandom prng,
      int step) {
    StringBuilder sb = new StringBuilder();
    if (fixedLength == 0) {
      return "";
    }
    int charactersGenerated = 0;
    while (charactersGenerated < fixedLength - 1) {
      int codePoint = (int) codePointGenerator.next(prng, step).longValue();

      sb.appendCodePoint(codePoint);
      charactersGenerated += Character.charCount(codePoint);
    }
    if (charactersGenerated != fixedLength) {
      sb.appendCodePoint(
          finalCharNeedsToBeInBMP(codePointGenerator, prng, step));
    }
    return sb.toString();
  }

  private static int finalCharNeedsToBeInBMP(Source<Long> codePointGenerator,
      PseudoRandom prng, int step) {

    int codePoint = (int) codePointGenerator.next(prng, step).longValue();
    while (Character.charCount(codePoint) != 1) {
      codePoint = (int) codePointGenerator.next(prng, step).longValue();
    }
    return codePoint;
  }

}
