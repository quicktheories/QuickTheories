package com.example;

import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.characters;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.longs;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.junit.Test;

public class PropertyTestExamples {

  // Failing property tests:
  
  @Test
  public void addingTwoPositiveIntegersAlwaysGivesAPositiveInteger() {
    qt()
        .forAll(integers().allPositive(), integers().allPositive())
        .check((i, j) -> i + j > 0);
  }

  @Test
  public void addingOneToALongAlwaysGivesAnIntegerBiggerThanPrevious() {
    qt().forAll(longs().all())
        .check(i -> i < i + 1);
  }

  @Test
  public void dividingTwoIntegersAsFloatsAndMultiplyingByIntegerReturnsInteger() {
    qt().forAll(integers().all(), integers().allPositive())
        .check((i, j) -> ((float) i / (float) j) * j == i);
  }

  @Test
  public void canHaveSingleCharactersAsHighSurrogates() {
    qt().forAll(strings().allPossible().ofLength(1))
        .asWithPrecursor(s -> s.codePointAt(0)).check(
            (s, i) -> Character.isHighSurrogate((char) i.intValue()) == false);
  }

  @Test
  public void allStringsHaveTheSameNumberOfCodePointsAsLength() {
    qt().forAll(strings().allPossible().ofLengthBetween(0, 100))
        .asWithPrecursor(s -> s.length())
        .check((s, l) -> s.codePoints().count() == l);
  }

  @Test
  public void arraysAsListsGetShorterWhenUsingRemove() {
    qt().forAll(arrays().ofIntegers(integers().all()).withLengthBetween(1, 100))
        .asWithPrecursor(a -> Arrays.asList(a))
        .check((a, l) -> integerListIsReducedByRemovingAnItem().test(l));
  }

  private Predicate<List<Integer>> integerListIsReducedByRemovingAnItem() {
    return l -> {
      int length = l.size();
      l.remove(0);
      return l.size() == length - 1;
    };
  }

  @Test
  public void checkingEqualityOfTwoDimensionalArrays() {
    qt().forAll(arrays().ofIntegers(integers().all()).withLength(2)
               ,arrays().ofIntegers(integers().all()).withLength(3))
        .asWithPrecursor((a, b) -> new Integer[][] { a, b })
        .check((a, b, c) -> {
          Integer[][] d = new Integer[][] { Arrays.copyOf(c[0], 2),
              Arrays.copyOf(c[1], 3) };
          return Arrays.equals(c, d);
        }); // have to use deepEquals for this test to pass
  }

  // Palindrome example: How to check that a String is a palindrome - can't
  // compare chars

  @Test
  public void palindromeTester1() {
    qt().withFixedSeed(23432432)
        .forAll(strings().allPossible().ofLengthBetween(1, 12),
            characters().basicMultilingualPlane())
        .asWithPrecursor((s, c) -> new Palindrome(s, c).construct())
        .check((s, c, palindrome) -> isPalindrome().test(palindrome));
  }

  @Test
  public void palindromeTester2() {
    qt().withFixedSeed(23432432)
        .forAll(strings().allPossible().ofLengthBetween(1, 12),
            characters().basicMultilingualPlane())
        .asWithPrecursor((s, c) -> new Palindrome(s, c).construct())
        .check((s, c, palindrome) -> isPalindromeCheckingIndices()
            .test(palindrome));
  }

  private Predicate<String> isPalindrome() {
    StringBuilder sb = new StringBuilder();
    return s -> s.equals(sb.append(s).reverse().toString());
  }

  private Predicate<String> isPalindromeCheckingIndices() {
    return s -> {
      for (int i = 0, j = s.length() - 1;; i++, j--) {
        if (i + 1 == j) {
          return s.charAt(i) == s.charAt(j);
        }
        if (i == j) {
          return true;
        }
        if (s.charAt(i) != s.charAt(j)) {
          return false;
        }
      }
    };
  }

  static class Palindrome {

    private final String repeat;
    private final char center;

    Palindrome(String repeat, char center) {
      this.repeat = repeat;
      this.center = center;
    }

    String construct() {
      StringBuilder sb = new StringBuilder();
      return repeat + center + sb.append(repeat).reverse().toString();
    }
  }

  // Person example where no examples can be found that meet this criteria (as
  // none exist)

  @Test
  public void canMakePeopleWithOddLengthNamesFromSupplementaryCodePoints() {
    qt().withExamples(100000)
        .forAll(strings().allPossible().ofLengthBetween(6, 12),
            integers().between(0, 120))
        .assuming((name, age) -> name.chars()
            .allMatch(c -> Character.isSupplementaryCodePoint(c)))
        .asWithPrecursor((name, age) -> new Person(name, age))
        .check((name, age, person) -> person.getName().length() % 2 == 1);
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
  }

  // Fermat's Little Theorem Example
  @Test
  public void truthOfFermatsLittleTheorem() {
    qt().forAll(longs().between(1, Long.MAX_VALUE), longs().between(2, 1000))
        .assuming((i, j) -> isPrime(j))
        .check((n, p) -> fermatsLittleTheorem().test(n, p));
  }

  @Test
  public void truthOfFermatsLittleTheorem2() {
    qt().forAll(longs().between(1, 700), longs().between(2, 7))
        .assuming((i, j) -> isPrime(j))
        .check((n, p) -> fermatsLittleTheorem().test(n, p));
  }

  private BiPredicate<Long, Long> fermatsLittleTheorem() {
    return (n, prime) -> areCongruentModN((long) Math.pow(n, prime), n, prime);
  }

  private boolean isPrime(long n) {
    if (n == 2)
      return true;
    if (n % 2 == 0)
      return false;
    for (int i = 3; i * i <= n; i += 2) {
      if (n % i == 0)
        return false;
    }
    return true;
  }

  @Test
  public void goodCongruenceDefinition() {
    qt().forAll(longs().between(0, 50), longs().between(0, 50),
        longs().between(1, 50))
        .assuming((a, b, n) -> areCongruentModN(a, b, n))
        .check((a, b, n) -> (a % n + b % n) % n == (a + b) % n);
  }

  private boolean areCongruentModN(long a, long b, long n) {
    return Math.abs(a - b) % n == 0;
  }

  // Greatest Common Divisor example

  @Test
  public void shouldFindThatAllIntegersHaveGcdOfOneWithOne() {
    qt().forAll(integers().all()).check(n -> gcd(n, 1) == 1); // fails on
                                                              // -2147483648
  }

  @Test
  public void shouldFindThatAllIntegersInRangeHaveGcdOfOneWithOne() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check(n -> gcd(n, 1) == 1);
  }

  @Test
  public void shouldFindThatAllIntegersHaveGcdThemselvesWithThemselves() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check(n -> gcd(n, n) == Math.abs(n));
  }

  @Test
  public void shouldFindThatGcdOfNAndMEqualsGcdMModNAndN() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE),
        integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check((n, m) -> gcd(n, m) == gcd(m % n, n));
  }

  private int gcd(int n, int m) {
    if (n == 0) {
      return Math.abs(m);
    }
    if (m == 0) {
      return Math.abs(n);
    }
    if (n < 0) {
      return gcd(-n, m);
    }
    if (m < 0) {
      return gcd(n, -m);
    }
    if (n > m) {
      return gcd(m, n);
    }
    return gcd(m % n, n);
  }

}
