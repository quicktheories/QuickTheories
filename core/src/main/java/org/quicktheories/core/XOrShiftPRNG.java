package org.quicktheories.core;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class XOrShiftPRNG implements PseudoRandom {

  private final long initialSeed;
  private long seed;

  XOrShiftPRNG(final long seed) {
    this.initialSeed = ensureSeedIsNotZero(seed);
    this.seed = ensureSeedIsNotZero(seed);
  }

  // xorshift64* generator chosen after reading texts including:
  // 1) An experimental exploration of Marsaglia's xorshift generators,
  // scrambled - Sebastiano Vigna
  // 2) On the Xorshift Random Number Generators - Francois Panneton and Pierre
  // L'Ecuyer
  public long nextLong() {
    this.seed ^= this.seed >> 12;
    this.seed ^= this.seed << 25;
    this.seed ^= this.seed >> 27;
    return this.seed * 2685821657736338717L; // Multiplication by a constant
                                             // (specified in Vigna's paper with
                                             // reference to work completed by
                                             // Panneton and L'Ecuyer) to
                                             // provide a nonlinear
                                             // transformation
  }

  @Override
  public long nextLong(long startInclusive,
      long endInclusive) {
    if (endInclusive < startInclusive) {
      throw new IllegalArgumentException(String.format("Invalid range %d to %d", startInclusive, endInclusive));
    }

    if (longRangeIsSmallerThanMax(startInclusive, endInclusive)) {
      return nextLongWithinCheckedInterval(startInclusive,
          endInclusive);
    }
    
    return generateRandomLongWhereRangeGEQMax(startInclusive, endInclusive);
  }
    
  private boolean longRangeIsSmallerThanMax(final long x, final long y) {
    return (1 <= x && y <= Long.MAX_VALUE)
        || ((Long.MIN_VALUE) <= x && y <= -2)
        || ((Long.MIN_VALUE + 1) <= x && y <= -1) || (-Long.MAX_VALUE < x
            && x <= 0 && 0 <= y && y < Long.MAX_VALUE + x);
  }

  private long generateRandomLongWhereRangeGEQMax(final long startInclusive,
      final long endInclusive) {
    long result = nextLong();
    while (result < startInclusive || result > endInclusive) {
      result = nextLong();
    }
    return result;
  }

  private long nextLongWithinCheckedInterval(long startInclusive, long endInclusive) {
    final long temp = nextLong();
    final long temp2 = temp % ((endInclusive - startInclusive) + 1);
    if (temp2 < 0) {
      long t = temp2 + endInclusive + 1;
      return t;
    }
    return (temp2 + startInclusive);
  }
  

  @Override
  public long getInitialSeed() {
    return this.initialSeed;
  }

  private static long ensureSeedIsNotZero(long requestedSeed) {
    if (requestedSeed == 0) {
      return 1;
    }
    return requestedSeed;
  }

}
