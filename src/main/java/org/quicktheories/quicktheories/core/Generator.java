package org.quicktheories.quicktheories.core;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.quicktheories.quicktheories.api.Function3;
import org.quicktheories.quicktheories.api.Function4;

/**
 * A Generator of values of type T
 *
 * @param <T>
 *          The type to generate
 */
@FunctionalInterface
public interface Generator<T> {

  /**
   * Produce a single value of type T
   * 
   * @param prng
   *          PseudoRandom as a source of randomness
   * @param step
   *          The step value may be used by a generator in place of internal
   *          state (which should be avoided).
   * @return A value based on prng
   */
  T next(final PseudoRandom prng, int step);

  /**
   * Produce a Generator of type V by applying the supplied function to this
   * Generator's values
   *
   * @param<V> type to generate
   * 
   * @param after
   *          function to apply
   * @return a Generator of type V
   */
  default <V> Generator<V> andThen(
      Function<? super T, ? extends V> after) {
    Objects.requireNonNull(after);
    return (prng, step) -> after.apply(next(prng, step));
  }

  /**
   * Produce a Generator of type V by applying the supplied bifunction to this
   * Generator and the supplied Generator's values
   * 
   * @param<B> type of other generator

   * @param<V> type to generate
   * 
   * @param other
   *          second Generator
   * @param combine
   *          bifunction to apply
   * @return a Generator of type V
   */
  default <B, V> Generator<V> combine(Generator<B> other,
      BiFunction<T, B, V> combine) {
    return (prng, step) -> combine.apply(this.next(prng, step),
        other.next(prng, step));
  }

  /**
   * Produce a Generator of type V by applying the supplied Function3 to this
   * Generator and the two other supplied Generators' values
   *
   * @param<A> type of first generator
   * @param<B> type of second generator
   * @param<V> type to generate   
   *
   * @param as
   *          second Generator
   * @param bs
   *          third Generator
   * @param combine
   *          Function3 to apply
   * @return a Generator of type V
   */
  default <A, B, V> Generator<V> combine(Generator<A> as, Generator<B> bs,
      Function3<T, A, B, V> combine) {
    return (prng, step) -> combine.apply(this.next(prng, step),
        as.next(prng, step), bs.next(prng, step));
  }

  /**
   * Produce a Generator of type V by applying the supplied Function4 to this
   * Generator and the three other supplied Generators' values
   * 
   * @param<A> type of first generator
   * @param<B> type of second generator
   * @param<C> type of third generator
   * @param<V> type to generate
   *  
   * 
   * @param as
   *          second Generator
   * @param bs
   *          third Generator
   * @param cs
   *          fourth Generator
   * @param combine
   *          Function4 to apply
   * @return a Generator of type V
   */
  default <A, B, C, V> Generator<V> combine(Generator<A> as, Generator<B> bs,
      Generator<C> cs, Function4<T, A, B, C, V> combine) {
    return (prng, step) -> combine.apply(this.next(prng, step),
        as.next(prng, step), bs.next(prng, step), cs.next(prng, step));
  }

}
