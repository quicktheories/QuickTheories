package org.quicktheories.quicktheories.core;

import java.util.function.IntUnaryOperator;

/**
 * Specifies details necessary for the shrinking process
 *
 */
public final class ShrinkContext {

  private final int step;
  private final int remainingCyles;
  private final PseudoRandom prng;

  /**
   * Specifies details necessary for the shrinking process
   * 
   * @param step
   *          step value used in place of state
   * @param remainingCyles
   *          the number of remaining shrink cycles
   * @param prng
   *          PseudoRandom used to generate values
   */
  public ShrinkContext(int step, int remainingCyles, PseudoRandom prng) {
    this.step = step;
    this.remainingCyles = remainingCyles;
    this.prng = prng;
  }

  /**
   * Returns remaining number of shrink cycles
   * 
   * @return remaining number of shrink cycles
   */
  public int remainingCycles() {
    return remainingCyles;
  }

  /**
   * Returns the PseudoRandom used to generate values
   * 
   * @return a PseudoRandom
   */
  public PseudoRandom prng() {
    return prng;
  }

  /**
   * Returns the step value
   * 
   * @return the step value
   */
  public int step() {
    return step;
  }

  /**
   * Adjusts the step using an IntUnaryOperator
   * 
   * @param offset
   *          the IntUnaryOperator
   * @return a ShrinkContext with a different step value
   */
  public ShrinkContext adjustStep(IntUnaryOperator offset) {
    return new ShrinkContext(offset.applyAsInt(step), remainingCyles, prng);
  }

}
