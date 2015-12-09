package org.quicktheories.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.longs;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Subject1;

public class ToStringTest {

  @Test
  public void shouldPrintSingleLongAsExpected() {
    try {
      qt().forAll(longs().all()).as(l -> l)
          .check(i -> i < i + 1);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("9223372036854775807");
    }
  }

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
      qt().withFixedSeed(5)
          .forAll(
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
  public void shouldUseCustomToStringOnPersonSubject() {
    try {
      forAllPeople().withStringFormat(p -> p.toUseToString()).check(i -> false);
      throw (new AssertionError("Test didn't fail"));
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("Name:");
    }
  }

  @Test
  public void shouldPrintArrayFromAsOfThreeElementsToDeepString() {
    try {
      qt().withFixedSeed(5)
          .forAll(integers().allPositive(), integers().allPositive(),
              integers().allPositive())
          .as((i, j, k) -> new Integer[] { i, j, k })
          .withStringFormat(a -> Arrays.deepToString(a))
          .check(i -> false);
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new Integer[] { 1, 1, 1 }));
    }
  }

  @Test
  public void shouldPrintArrayFromAsWithPrecursorOfThreeElementsToDeepString() {
    try {
      qt().withFixedSeed(5)
          .forAll(integers().allPositive(), integers().allPositive(),
              integers().allPositive())
          .asWithPrecursor((i, j, k) -> new Integer[] { i, j, k })
          .withStringFormat(i -> i.toString(), j -> j.toString(),
              k -> k.toString(), a -> Arrays.deepToString(a))
          .check((i, j, k, a) -> false);
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(
              "1, 1, 1, " + Arrays.deepToString(new Integer[] { 1, 1, 1 }));
    }
  }

  @Test
  public void shouldPrintArrayFromAsOfFourElementsToDeepString() {
    try {
      qt().withFixedSeed(5)
          .forAll(integers().allPositive(), integers().allPositive(),
              integers().allPositive(), integers().allPositive())
          .as((i, j, k, l) -> new Integer[] { i, j, k, l })
          .withStringFormat(a -> Arrays.deepToString(a)).check(i -> false);
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(
              Arrays.deepToString(new Integer[] { 1, 1, 1, 1 }));
    }
  }

  @Test
  public void shouldPrintArrayFromAsWithPrecursorOfFourElementsToDeepString() {
    try {
      qt().withFixedSeed(5)
          .forAll(integers().allPositive(), integers().allPositive(),
              integers().allPositive(), integers().allPositive())
          .asWithPrecursor((i, j, k, l) -> new Integer[] { i, j, k, l })
          .withStringFormat(i -> i.toString(), j -> j.toString(),
              k -> k.toString(), l -> l.toString(), a -> Arrays.deepToString(a))
          .check((i, j, k, l, a) -> false);
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("1, 1, 1, 1, "
              + Arrays.deepToString(new Integer[] { 1, 1, 1, 1 }));
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
