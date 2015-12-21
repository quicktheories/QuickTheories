package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Characters {

  static Source<Character> ofCharacters(int startCodePoint, int endCodePoint) {
    return CodePoints.codePoints(startCodePoint, endCodePoint, startCodePoint)
        .as(l -> (char) l.intValue(), c -> (long) c);
  }

}
