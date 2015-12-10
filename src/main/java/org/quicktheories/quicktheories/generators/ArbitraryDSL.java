package org.quicktheories.quicktheories.generators;

import java.util.List;

import org.quicktheories.quicktheories.core.Source;

/**
 * Class for creating Sources of constant values, enum values, sequences and
 * specified items of the same type
 */
public class ArbitraryDSL {

  /**
   * Generates the same constant value
   * 
   * @param <T>
   *          type of value to generate
   * @param constant
   *          the constant value to generate
   * @return a Source of type T of the constant value
   */
  public <T> Source<T> constant(T constant) {
    return Arbitrary.constant(constant);
  }

  /**
   * Generates enum values of type T by randomly picking one from the defined
   * constants. When shrinking, enum constants defined first will be considered
   * "smaller".
   * 
   * @param <T>
   *          type of value to generate
   * @param e
   *          the enum class to produce constants from
   * @return a Source of type T of randomly selected enum values
   */
  public <T extends Enum<T>> Source<T> enumValues(Class<T> e) {
    return pick(e.getEnumConstants());
  }

  /**
   * Generates a value by randomly picking one from the supplied. When
   * shrinking, values supplied earlier will be considered "smaller".
   * 
   * @param <T>
   *          type of value to generate
   * @param ts
   *          the values of T to pick from
   * @return a Source of type T of values selected randomly from ts
   */
  @SuppressWarnings("unchecked")
  public <T> Source<T> pick(T... ts) {
    return pick(java.util.Arrays.asList(ts));
  }

  /**
   * Generates a value by randomly picking one from the supplied. When
   * shrinking, values earlier in the list will be considered "smaller".
   * 
   * @param <T>
   *          type of value to generate
   * @param ts
   *          the values of T to pick from
   * @return a Source of type T of values selected randomly from ts
   */
  public <T> Source<T> pick(List<T> ts) {
    return Arbitrary.pick(ts);
  }

  /**
   * Generates a value in order deterministically from the supplied values.
   * 
   * If more examples are requested than are supplied then the sequence will be
   * repeated.
   *
   * @param <T>
   *          type of value to generate
   * @param ts
   *          values to create sequence from
   * @return a Source of type T of values selected from ts
   */
  @SuppressWarnings("unchecked")
  public <T> Source<T> sequence(T... ts) {
    return sequence(java.util.Arrays.asList(ts));
  }

  /**
   * Generates a value in order deterministically from the supplied list.
   * 
   * If more examples are requested than are present in the list then the
   * sequence will be repeated.
   *
   * @param <T>
   *          type of value to generate
   * @param ts
   *          values to create sequence from
   * @return a Source of type T of values selected from ts
   */
  public <T> Source<T> sequence(List<T> ts) {
    return Arbitrary.sequence(ts);
  }

  /**
   * Generates a value in order deterministically from the supplied values,
   * starting from the last supplied value.
   * 
   * If more examples are requested than are supplied then the last value will
   * be repeated.
   * 
   * @param <T> type to generate
   * 
   * @param ts
   *          values to create sequence from
   * @return a Source of type T of of values selected from ts
   */
  @SuppressWarnings("unchecked")
  public <T> Source<T> reverse(T... ts) {
    return Arbitrary.reverse(ts);
  }

}
