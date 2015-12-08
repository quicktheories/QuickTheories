package org.quicktheories.quicktheories.generators;

import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Source;

public class Compositions {

  /**
   * Composes two Sources such that their values are produced interleaved
   * 
   * @param lhs
   *          Source from which to retrieve first value
   * @param rhs
   *          Source from which to retrieve second value
   * @return A Source of type T combining the supplied lhs and rhs
   */
  public static <T> Source<T> interleave(Source<T> lhs,
      Source<T> rhs) {
    return choose(step -> step % 2 == 0, lhsstep -> lhsstep / 2,
        rhsstep -> (rhsstep - 1) / 2, lhs, rhs);
  }

  /**
   * Composes two Sources such that the first n values are drawn from the first,
   * with all subsequent values coming from the second
   * 
   * @param n
   *          number of values to take from lhs
   * @param lhs
   *          a Source of type T
   * @param rhs
   *          a Source of type T
   * @return a composed Source of type T
   */
  public static <T> Source<T> ntimesThen(int n, Source<T> lhs,
      Source<T> rhs) {
    return choose(step -> step < n, lhsStep -> lhsStep, rhsstep -> rhsstep - n,
        lhs, rhs);
  }

  /**
   * Creates a Source that is guaranteed to produces the supplied values at
   * least once.
   * 
   * If a property is falsified by one of the supplied values then shrunk values
   * will be selected from those specifically supplied only.
   * 
   * @param source
   *          Source from which most values will be gathered
   * @param values
   *          values to be produced at least once
   * @return a Source of type T
   */
  @SafeVarargs
  public static <T> Source<T> combineWithValues(Source<T> source,
      T... values) {
    return ntimesThen(values.length,
        Arbitrary.sequence(java.util.Arrays.asList(values)), source);
  }

  /**
   * Creates a Source that will produce the supplied values about 2% of the
   * time. The supplied generator <b>must</b> be capable of shrinking the
   * supplied values.
   *
   * @param source
   *          Source from which most values will be gathered
   * @param values
   *          values to be produced at least once
   * @return a Source of type T
   */
  @SafeVarargs
  static <T> Source<T> weightWithValues(Source<T> source, T... values) {
    return pickWithWeighting(Arbitrary.pick(java.util.Arrays.asList(values)),
        source, 2)
            .withShrinker(source);
  }

  private static <T> Source<T> choose(Predicate<Integer> useLHS,
      IntUnaryOperator lhsStepOffset, IntUnaryOperator rhsStepOffset,
      Source<T> lhs, Source<T> rhs) {
    return Source.of((prng, step) -> {
      if (useLHS.test(step)) {
        return lhs.next(prng, lhsStepOffset.applyAsInt(step));
      }
      return rhs.next(prng, rhsStepOffset.applyAsInt(step));
    }).withShrinker((original, context) -> {
      if (useLHS.test(context.step())) {
        return lhs.shrink(original, context.adjustStep(lhsStepOffset));
      }
      return rhs.shrink(original, context.adjustStep(rhsStepOffset));
    });
  }

  private static <T> Source<T> pickWithWeighting(Source<T> lhs, Source<T> rhs,
      int percentOfLhs) {
    Generator<T> chooser = (prng, step) -> {
      int choice = prng.nextInt(0, 99);

      if (choice < percentOfLhs) {
        return lhs.next(prng, step);
      }
      return rhs.next(prng, step);
    };
    return Source.of(chooser);
  }

}
