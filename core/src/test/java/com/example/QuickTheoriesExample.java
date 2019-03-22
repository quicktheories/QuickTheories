package com.example;

import static org.junit.Assert.fail;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.generators.SourceDSL.lists;
import static org.quicktheories.generators.SourceDSL.strings;

import org.junit.Test;

public class QuickTheoriesExample {

  @Test
  public void badUseOfAssumptions() {
    qt()
        .forAll(integers().allPositive())
        .assuming(i -> i < 30000)
        .check(i -> i < 3000);
  }

  @Test
  public void losesNoInformationWhenTransformedToString() {
    qt()
        .withFixedSeed(0)
        .withExamples(1000000)
        .forAll(integers().allPositive(), integers().allPositive(),
            integers().allPositive())
        .assuming((a, b, c) -> a > 10000 && b > 100000 && c > 300000000)
        .check(
            (i, j, k) -> i == Integer.parseInt(i.toString()) && k > 300000000);
  }

  @Test
  public void exampleWithTypeConversion() {
    qt()
        .withFixedSeed(0)
        .withExamples(10000)
        .forAll(integers().allPositive())
        .as(i -> "Foo" + i.toString())
        .checkAssert(i -> fail());
  }

  @Test
  public void exampleWithAirtyTwo() {
    qt()
        .withFixedSeed(40)
        .withExamples(10000)
        .forAll(integers().allPositive(), integers().allPositive(),
            integers().allPositive())
        .check((a, b, c) -> b < 5000);
  }

  @Test
  public void exampleWithTypeConversionAndAirtyTwo() {
    qt()
        .withFixedSeed(40)
        .withExamples(10000)
        .forAll(integers().allPositive(), integers().allPositive(),
            integers().allPositive())
        .as((a, b, c) -> "Foo" + a.toString() + "Bar" + b + "Fish" + c)
        .check(i -> i.length() < 40);
  }

  @Test
  public void exampleWithTypeConversionAndAirtyFour() {
    qt()
        .withFixedSeed(40)
        .withExamples(10000)
        .forAll(integers().allPositive(), integers().allPositive(),
            lists().of(integers().from(-20).upTo(20)).ofSize(3),
            strings().basicLatinAlphabet().ofLengthBetween(0, 100))
        .as((a, b, c, d) -> "Foo" + a.toString() + "Bar" + b + "Fish" + c)
        .check(i -> i.length() < 40);
  }

}
