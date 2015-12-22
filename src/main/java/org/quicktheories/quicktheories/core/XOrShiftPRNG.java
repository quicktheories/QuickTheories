package org.quicktheories.quicktheories.core;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class XOrShiftPRNG implements PseudoRandom {

  private final long initialSeed;
  private long seed;

  XOrShiftPRNG(final long seed) {
    this.initialSeed = ensureSeedIsNotZero(seed);
    this.seed = ensureSeedIsNotZero(seed);
  }

  @Override
  public int nextInt(int startInclusive, int endInclusive) {
    return (int) nextLongWithinCheckedInterval(startInclusive, endInclusive);
  }

  // xorshift64* generator chosen after reading texts including:
  // 1) An experimental exploration of Marsaglia's xorshift generators,
  // scrambled - Sebastiano Vigna
  // 2) On the Xorshift Random Number Generators - Francois Panneton and Pierre
  // L'Ecuyer
  @Override
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
  public long nextLongWithinCheckedInterval(long startInclusive,
      long endInclusive) {
    final long temp = nextLong();
    final long temp2 = temp % ((endInclusive - startInclusive) + 1);
    if (temp2 < 0) {
      return temp2 + endInclusive + 1;
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
