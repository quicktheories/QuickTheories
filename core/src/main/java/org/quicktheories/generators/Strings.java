package org.quicktheories.generators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;

final class Strings {

  static Gen<String> fromRanges(List<Pair<Integer, Integer>> ranges, int minLength, int maxLength) {
      ranges = new ArrayList<>(ranges);
      // Sorting by range size seems like it ought to reduce the frequency with which we have to bump the percentage up
      // from zero below
      ranges.sort(Comparator.comparing(r -> r._2 - r._1));
      Pair<Integer, Integer> first = ranges.get(0);
      Gen<Integer> gen = CodePoints.codePoints(first._1, first._2);
      int total = 1 + ranges.get(0)._2 - ranges.get(0)._1;
      for (int i = 1; i < ranges.size(); i++) {
          Pair<Integer, Integer> next = ranges.get(i);
          Gen<Integer> gen2 = CodePoints.codePoints(next._1, next._2);
          int nextSize = 1 + next._2 - next._1;
          int weight = (int) (100 * ((float) total / (total + nextSize)));
          if (weight == 0) {
              weight = 1;
          }
          weight = 100 - weight;
          total = total + nextSize;
          gen = gen.mix(gen2, weight);
      }
      return Generate.intArrays(Generate.range(minLength, maxLength)
              , gen)
              .map(is -> new String(is, 0, is.length)).map(reduceToSize(maxLength));
  }

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



