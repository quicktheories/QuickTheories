package org.quicktheories.quicktheories.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.assertj.core.api.AbstractAssert;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class SourceAssert<T>
    extends AbstractAssert<SourceAssert<T>, Source<T>> {

  protected SourceAssert(Source<T> actual) {
    super(actual, SourceAssert.class);
  }

  public static <T> SourceAssert<T> assertThatSource(
      Source<T> actual) {
    return new SourceAssert<T>(actual);
  }

  @SuppressWarnings("unchecked")
  public SourceAssert<T> generatesTheFirstFourValues(T... values) {
    List<T> generated = generateValues(4);
    org.assertj.core.api.Assertions.assertThat(generated)
        .containsExactly(values);
    return this;
  }

  @SuppressWarnings("unchecked")
  public SourceAssert<T> generatesTheFirstSixValues(T... values) {
    List<T> generated = generateValues(6);
    org.assertj.core.api.Assertions.assertThat(generated)
        .containsExactly(values);
    return this;
  }

  public SourceAssert<T> generatesAllOf(
      @SuppressWarnings("unchecked") T... ts) {
    List<T> generated = generateValues(1000);
    org.assertj.core.api.Assertions.assertThat(generated).contains(ts);
    return this;
  }

  private List<T> generateValues(int count) {
    isNotNull();
    PseudoRandom prng = Configuration.defaultPRNG(0);
    List<T> generated = new ArrayList<T>();
    for (int i = 0; i != count; i++) {
      generated.add((T) actual.next(prng, i));
    }
    return generated;
  }

  public SourceAssert<T> doesNotGenerate(
      @SuppressWarnings("unchecked") T... ts) {
    List<T> generated = generateValues(100);
    org.assertj.core.api.Assertions.assertThat(generated).doesNotContain(ts);
    return this;
  }

  public SourceAssert<T> shrinksConformTo(T original, Predicate<T> check,
      ShrinkContext context, Function<T, String> toString) {
    Stream<T> shrunk = actual.shrink(original, context);
    T produced = shrunk.findFirst().get();
    if (!check.test(produced)) {
      failWithMessage("Expected <%s> to be shrunk but got %s",
          toString.apply(original),
          toString.apply(produced));
    }
    return this;
  }

  public SourceAssert<T> shrinksConformTo(T original, Predicate<T> check,
      ShrinkContext context) {
    return shrinksConformTo(original, check, context, i -> i.toString());
  }

  public SourceAssert<T> arrayShrinksConformTo(T original,
      Predicate<T> check, ShrinkContext context) {
    return shrinksConformTo(original, check, context,
        i -> Arrays.deepToString((Object[]) i));
  }

  public SourceAssert<T> shrinksValueTo(T value, T expected,
      ShrinkContext context, BiPredicate<T, T> equalityCheck,
      Function<T, String> toString) {
    isNotNull();
    Stream<T> shrunk = actual.shrink(value, context);
    T first = shrunk.iterator().next();
    if (!equalityCheck.test(first, expected)) {
      failWithMessage("Expected <%s> to be shrunk to <%s> but got %s",
          toString.apply(value), toString.apply(expected),
          toString.apply(first));
    }
    return this;
  }

  public SourceAssert<T> shrinksValueTo(T value, T expected,
      ShrinkContext context) {
    return shrinksValueTo(value, expected, context,
        (i, j) -> Objects.equals(i, j), i -> i.toString());
  }

  public SourceAssert<T> shrinksArrayValueTo(T value, T expected,
      ShrinkContext context) {
    return shrinksValueTo(value, expected, context,
        (i, j) -> Arrays.deepEquals((Object[]) i, (Object[]) j),
        i -> Arrays.deepToString((Object[]) i));
  }

  public SourceAssert<T> shrinksValueTo(T value, T expected) {
    return shrinksValueTo(value, expected,
        new ShrinkContext(0, 100, Configuration.defaultPRNG(0)));
  }

  public SourceAssert<T> cannotShrink(T value,
      Function<T, String> toString) {
    isNotNull();
    ShrinkContext context = new ShrinkContext(0, 1,
        Configuration.defaultPRNG(0));
    Stream<T> shrunk = actual.shrink(value, context);
    Iterator<T> it = shrunk.iterator();
    if (it.hasNext()) {
      failWithMessage("Expected <%s> to get no smaller but was shrunk to <%s>",
          toString.apply(value), toString.apply(it.next()));
    }
    return this;
  }

  public SourceAssert<T> cannotShrink(T value) {
    return cannotShrink(value, i -> i.toString());
  }

  public SourceAssert<T> cannotShrinkArray(T value) {
    return cannotShrink(value, i -> Arrays.deepToString((Object[]) i));
  }

}
