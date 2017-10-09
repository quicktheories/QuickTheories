package org.quicktheories.core;

/**
 * Randomly modifies a base value
 *
 * @param <T> type to modify
 * @param <R> resulting type
 */
public interface Mod<T, R> {

  /**
   * Create one modification of a value
   * @param base start value
   * @param randomness source of randomness
   * @return An R based on a random modification of input value 
   */
  R apply(T base, RandomnessSource randomness);
  
}
