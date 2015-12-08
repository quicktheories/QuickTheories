package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

final class Integers {

  static Source<Integer> range(final int startInclusive,
      final int endInclusive) {
    return Longs.range(startInclusive, endInclusive)
        .as(i -> (int) i.longValue(), i -> (long) i);
  }

}
