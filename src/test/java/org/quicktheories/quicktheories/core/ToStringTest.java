package org.quicktheories.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.quicktheories.generators.SourceDSL.characters;
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
  public void shouldPrintSingleLongAsLongAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(longs().all()).as(l -> l)
          .check(i -> i < i + 1);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("9223372036854775807");
    }
  }

  @Test
  public void shouldPrintSingleLongAsWithPrecursorLongAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(longs().all()).asWithPrecursor(l -> l)
          .check((h, i) -> i < i + 1);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("{9223372036854775807, 9223372036854775807}");
    }
  }

  @Test
  public void shouldPrintTwoIntegersAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(integers().all(), integers().allPositive())
          .check((i, j) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("{0, 1}");
    }
  }

  @Test
  public void shouldPrintTwoIntegersAsWithPrecursorIntegerAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(integers().all(), integers().allPositive())
          .asWithPrecursor((i, j) -> i + j)
          .check((i, j, k) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("{0, 1, 1}");
    }
  }

  @Test
  public void shouldPrintFailingThreeCharactersAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(characters().ascii(), characters().ascii(),
          characters().ascii())
          .check((i, j, k) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error).hasMessageContaining("{\u0000, \u0000, \u0000}");
    }
  }

  @Test
  public void shouldPrintThreeCharactersAsStringAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(characters().ascii(), characters().ascii(),
          characters().ascii())
          .as((i, j, k) -> Character.toString(i) + Character.toString(j)
              + Character.toString(k))
          .check(s -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error).hasMessageContaining("\u0000\u0000\u0000");
    }
  }

  @Test
  public void shouldPrintThreeCharactersAsWithPrecursorStringAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(characters().ascii(), characters().ascii(),
          characters().ascii())
          .asWithPrecursor(
              (i, j, k) -> Character.toString(i) + Character.toString(j)
                  + Character.toString(k))
          .check((i, j, k, s) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("{\u0000, \u0000, \u0000, \u0000\u0000\u0000}");
    }
  }

  @Test
  public void shouldPrintFourFailingIntegersAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(integers().all(), integers().all(),
          integers().all(), integers().all())
          .check((i, j, k, l) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error).hasMessageContaining("{0, 0, 0, 0}");
    }
  }

  @Test
  public void shouldPrintFourIntegersAsWithPrecursorLongAsExpected() {
    try {
      qt().withFixedSeed(5).forAll(integers().all(), integers().all(),
          integers().all(), integers().all())
          .asWithPrecursor((i, j, k, l) -> (long) i + j + k + l)
          .check((i, j, k, l, s) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error).hasMessageContaining("{0, 0, 0, 0, 0}");
    }
  }

  @Test
  public void shouldPrintReadableRepresentationOfArrays() {
    try {
      qt().withFixedSeed(5).forAll(
          arrays().ofStrings(strings().ascii().ofLength(1)).withLength(1))
          .check(i -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new String[] { "!" }));
    }
  }

  @Test
  public void shouldPrintArrayAsToDeepString() {
    try {
      qt().withFixedSeed(5).forAll(bigIntegers().ofBytes(5))
          .as(b -> b.toByteArray()).describedAs(a -> Arrays.toString(a))
          .check(i -> false);
      failIfReached();
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
          .describedAs(a -> Arrays.deepToString(a), l -> l.toString())
          .check((a, l) -> integerListIsReducedByRemovingAnItem().test(l));
      failIfReached();
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
          .describedAs(p -> p.toUseToString())
          .check(i -> false);
      failIfReached();
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
          .describedAs(s -> s.toString(), i -> i.toString(),
              p -> p.toUseToString())
          .check((i, s, p) -> false);
      failIfReached();
    } catch (AssertionError error) {
      assertThat(error)
          .hasMessageContaining("Name:");
    }
  }

  @Test
  public void shouldUseCustomToStringOnPersonSubject() {
    try {
      forAllPeople().describedAs(p -> p.toUseToString()).check(i -> false);
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
          .describedAs(a -> Arrays.deepToString(a))
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
          .describedAs(i -> i.toString(), j -> j.toString(),
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
          .describedAs(a -> Arrays.deepToString(a)).check(i -> false);
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
          .describedAs(i -> i.toString(), j -> j.toString(),
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
  

  private void failIfReached() throws AssertionError {
    throw new Error("Expected an AssertionError but didn't get one");
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
