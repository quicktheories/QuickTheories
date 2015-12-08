package org.quicktheories.quicktheories.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

final class Arbitrary {

  static <T> Source<T> constant(T constant) {
    return Source.of((prng, step) -> constant);
  }

  static <T> Source<T> pick(List<T> ts) {
    return Source.of((prng, step) -> ts.get(prng.nextInt(0, ts.size() - 1)))
        .withShrinker(
            new OrderedShrinker<>(reverse(ts)));
  }

  @SafeVarargs
  static <T> Source<T> reverse(T... ts) {
    List<T> values = Arrays.asList(ts);
    return sequence(reverse(Arrays.asList(ts)))
        .withShrinker(new OrderedShrinker<>(reverse(values)));
  }

  static <T> Source<T> sequence(List<T> ts) {
    return Source.of((prng, step) -> ts.get(step % ts.size())).withShrinker(
        new OrderedShrinker<>(reverse(ts)));
  }

  private static class OrderedShrinker<T> implements Shrink<T> {
    private final List<T> l;

    OrderedShrinker(List<T> l) {
      this.l = l;
    }

    @Override
    public Stream<T> shrink(T original, ShrinkContext context) {
      int even = l.indexOf(original) + 1;
      List<T> sublist = new ArrayList<>(l.subList(even, l.size()));
      return sublist.stream();
    }
  }

  private static <T> List<T> reverse(List<T> l) {
    List<T> reverseMe = new ArrayList<>(l);
    Collections.reverse(reverseMe);
    return reverseMe;
  }

}
