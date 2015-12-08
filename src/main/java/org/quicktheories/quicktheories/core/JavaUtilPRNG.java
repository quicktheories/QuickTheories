package org.quicktheories.quicktheories.core;

import java.util.Random;

final class JavaUtilPRNG implements PseudoRandom {

  private final long seed;
  private final Random r;

  JavaUtilPRNG(final long seed) {
    this.seed = seed;
    this.r = new Random(seed);
  }

  public int nextInt(final int startInclusive, int endInclusive) {
    return r.nextInt(endInclusive - startInclusive + 1) + startInclusive;
  }

  public long nextLong() {
    return r.nextLong();
  }

  public long getInitialSeed() {
    return this.seed;
  }

  @Override
  public String toString() {
    return "PRNG [seed=" + seed + "]";
  }

  @Override
  public long nextLongWithinCheckedInterval(long startInclusive,
      long endInclusive) {
    if (endInclusive - startInclusive + 1 <= 0) {
      throw new IllegalArgumentException("bound must be positive");
    }
    return ((long) (r.nextDouble() * (endInclusive - startInclusive + 1))
        + startInclusive);
  }

}
