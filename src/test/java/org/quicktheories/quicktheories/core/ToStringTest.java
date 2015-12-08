package org.quicktheories.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Subject1;

public class ToStringTest {

  @Test
  public void shouldPrintArrayDeepToString() {
    try {
      qt().withFixedSeed(5).forAll(
          arrays().ofStrings(strings().ascii().ofLength(1)).withLength(1))
          .withStringFormat(a -> Arrays.deepToString(a)).check(i -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new String[] { "!" }));
    }
  }

  @Test
  public void shouldPrintArrayAsToDeepString() {
    try {
      qt().withFixedSeed(5).forAll(bigIntegers().ofBytes(5))
          .as(b -> b.toByteArray()).withStringFormat(a -> Arrays.toString(a))
          .check(i -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new Integer[] { 0 }));
    }
  }

  @Test
  public void shouldPrintArraysAsWithPrecursorToDeepString() {
    try {
      qt().forAll(
          arrays().ofIntegers(integers().all()).withLengthBetween(1, 100))
          .asWithPrecursor(a -> Arrays.asList(a))
          .withStringFormat(a -> Arrays.deepToString(a), l -> l.toString())
          .check((a, l) -> integerListIsReducedByRemovingAnItem().test(l));
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new Integer[] { 0 }));
    }
  }

  @Test
  public void shouldUseCustomToStringForStringAndIntAsPerson() {
    try {
      qt().withFixedSeed(5)
          .forAll(strings().basicLatinAlphabet().ofLengthBetween(3, 12),
              integers().between(0, 140))
          .assuming(
              (s, i) -> s.codePoints().allMatch(j -> Character.isLetter(j)))
          .as((i, j) -> new Person(i, j))
          .withStringFormat(p -> p.toUseToString())
          .check(i -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("Name:");
    }
  }

  @Test
  public void shouldUseCustomToStringForStringAndIntAsWithPrecursorPerson() {
    try {
      qt().withFixedSeed(5)
          .forAll(strings().basicLatinAlphabet().ofLengthBetween(3, 12),
              integers().between(0, 140))
          .assuming(
              (s, i) -> s.codePoints().allMatch(j -> Character.isLetter(j)))
          .asWithPrecursor((i, j) -> new Person(i, j))
          .withStringFormat(s -> s.toString(), i -> i.toString(),
              p -> p.toUseToString())
          .check((i, s, p) -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("Name:");
    }
  }

  @Test
  public void canUseCustomToStringOnPersonSubject() {
    try {
      forAllPeople().withStringFormat(p -> p.toUseToString()).check(i -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("Name:");
    }
  }

  private Subject1<Person> forAllPeople() {
    return qt().withFixedSeed(5)
        .forAll(strings().basicLatinAlphabet().ofLengthBetween(3, 12),
            integers().between(0, 140))
        .assuming((s, i) -> s.codePoints().allMatch(j -> Character.isLetter(j)))
        .as((i, j) -> new Person(i, j));
  }

  static class Person {

    private final String name;
    private final int age;

    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    String getName() {
      return name;
    }

    int getAge() {
      return age;
    }

    public String toUseToString() {
      return "Name: '" + this.name + "' Age: '" + this.age + "'";
    }
  }

  private Predicate<List<Integer>> integerListIsReducedByRemovingAnItem() {
    return l -> {
      int length = l.size();
      l.remove(0);
      return l.size() == length - 1;
    };
  }

}
