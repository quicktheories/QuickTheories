package org.quicktheories.quicktheories.generators;

import java.util.function.Function;

import org.quicktheories.quicktheories.core.Gen;

final class Strings {

  static Gen<String> boundedNumericStrings(int startInclusive,
      int endInclusive) {
    return Generate.range(startInclusive, endInclusive)
        .map(i -> i.toString());
  }

  static Gen<String> withCodePoints(int minCodePoint,
      int maxCodePoint, Gen<Integer> numberOfCodePoints) {
    
      return Generate.intArrays(numberOfCodePoints
          , CodePoints.codePoints(minCodePoint, maxCodePoint))
          .map(is -> new String(is, 0, is.length));
    
  }

  static Gen<String> ofBoundedLengthStrings(int minCodePoint,
      int maxCodePoint,
      int minLength, int maxLength) {
    
    // generate strings of fixed number of code points then modify any that exceed max length
    return withCodePoints(minCodePoint, maxCodePoint, Generate.range(minLength, maxLength))
        .map(reduceToSize(maxLength));
  }
  
  private static Function<String,String> reduceToSize(int maxLength) {
    // Reduce size of string by removing characters from start
    return s -> {
      if (s.length() <= maxLength) {
        return s;
      }
      String t = s;
      while (t.length() > maxLength) {
        t = t.substring(1);
      }
      return t;
      
    };
  }
  
}



