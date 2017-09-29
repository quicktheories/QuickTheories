package org.quicktheories.generators;

import java.util.List;
import java.util.function.Supplier;

import org.quicktheories.core.Gen;

/**
 * Class for creating Sources of constant values, enum values, sequences and
 * specified items of the same type
 */
public class ArbitraryDSL {

  /**
   * Generates the same constant value
   * 
   * @param <T> type of value to generate
   * @param constant
   *          the constant value to generate
   * @return a Source of type T of the constant value
   */
  public <T> Gen<T> constant(T constant) {
    return Generate.constant(constant);
  }
  
  /**
   * Generates a constant value using the given supplier. This method is intended to
   * allow a single mutable value to be safely supplied. If it is abused to inject
   * randomness or values that are semantically different across multiple invocations
   * then QuickTheories will not work correctly.
   * 
   * @param <T> type of value to generate
   * @param constant the constant value to generate
   * @return a Source of type T of the constant value
   */
  public <T> Gen<T> constant(Supplier<T> constant) {
    return Generate.constant(constant);
  }

  /**
   * Generates enum values of type T by randomly picking one from the defined
   * constants. When shrinking, enum constants defined first will be considered
   * "smaller".
   * 
   * @param <T> type of value to generate
   * @param e the enum class to produce constants from
   * @return a Source of type T of randomly selected enum values
   */
  public <T extends Enum<T>> Gen<T> enumValues(Class<T> e) {
    return pick(e.getEnumConstants());
  }

  /**
   * Generates enum values of type T by randomly picking one from the defined
   * constants. 
   * 
   * When shrinking no order will be assumed.
   * 
   * @param <T> type of value to generate
   * @param e the enum class to produce constants from
   * @return a Source of type T of randomly selected enum values
   */
  public <T extends Enum<T>> Gen<T> enumValuesWithNoOrder(Class<T> e) {
    return Generate.pickWithNoShrinkPoint(java.util.Arrays.asList(e.getEnumConstants()));
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
  public <T> Gen<T> pick(T... ts) {
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
  public <T> Gen<T> pick(List<T> ts) {
    return Generate.pick(ts);
  }

}
