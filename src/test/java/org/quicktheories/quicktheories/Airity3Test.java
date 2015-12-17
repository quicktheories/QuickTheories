package org.quicktheories.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Tuple4;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.QTTester;

public class Airity3Test {

  QTTester verifier = new QTTester();

  Source<Integer> g = integers().all();

  @Test
  public void shouldNotFalisifyTheTruth() {
    qt()
        .forAll(this.g, this.g, this.g)
        .check((a, b, c) -> true);

    verifier.notFalsified();
  }

  @Test
  public void shouldFalisifyUniversalFalsehood() {
    qt()
        .forAll(this.g, this.g, this.g)
        .check((a, b, c) -> false);

    verifier.isFalsified();
  }

  @Test
  public void shouldFalsifyPartialTruth() {
    qt()
        .forAll(this.g, this.g, this.g)
        .check((a, b, c) -> a + b + c > 100);

    verifier.isFalsified();
  }

  @Test
  public void shouldConstrainGeneratorsByAssumptions() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b, c) -> a > 2 && b > 2 && c > 2)
        .check((a, b, c) -> a > 2 && b > 2 && c > 2);

    verifier.notFalsified();
  }

  @Test
  public void shouldShrinkWhenAllSidesCanBeShrunk() {
    qt()
        .forAll(arbitrary().reverse(1, 2, 3, 4, 5, 6, 7),
            arbitrary().reverse(1, 2, 3, 4, 5, 6, 7),
            arbitrary().reverse(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c) -> a == 5 && b == 5 && c == 5);

    verifier.<Integer, Integer, Integer> smallestValueMatches(
        (a, b, c) -> a == 1 && b == 1 && c == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenLeftHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().constant(0),
            arbitrary().reverse(1, 2, 3, 4, 5, 6, 7),
            arbitrary().reverse(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c) -> c == 5);

    verifier.<Integer, Integer, Integer> smallestValueMatches(
        (a, b, c) -> a == 0 && b == 1 && c == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenRightHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().reverse(1, 2, 3, 4, 5, 6, 7),
            arbitrary().reverse(1, 2, 3, 4, 5, 6, 7), arbitrary().constant(0))
        .check((a, b, c) -> c == 5);

    verifier.<Integer, Integer, Integer> smallestValueMatches(
        (a, b, c) -> a == 1 && b == 1 && c == 0);
  }

  @Test
  public void shouldContinueToShrinkWhenMiddleAtSmallestValue() {
    qt()
        .forAll(arbitrary().reverse(1, 2, 3, 4, 5, 6, 7),
            arbitrary().constant(0), arbitrary().reverse(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c) -> c == 5);

    verifier.<Integer, Integer, Integer> smallestValueMatches(
        (a, b, c) -> a == 1 && b == 0 && c == 1);
  }

  @Test
  public void shouldConvertAndShrinkValues() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .as((a, b, c) -> a + ":" + b + ":" + c)
        .check(a -> a.equals("1:1:1"));

    String actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo("2:2:2");
  }

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenConvertingTypes() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b, c) -> a != 2 && b != 2 && c != 2)
        .asWithPrecursor((a, b, c) -> a + ":" + b + ":" + c)
        .check((a, b, c, value) -> value.equals("1:1:1"));

    Tuple4<Integer, Integer, Integer, String> actual = verifier
        .smallestFalsifiedValue();
    assertThat(actual).isEqualTo(Tuple4.of(3, 3, 3, "3:3:3"));
  }

  @Test
  public void shouldFalisyWhenAssertionsThrown() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .checkAssert((a, b, c) -> assertThat(a + b + c).isEqualTo(3));

    verifier.isFalsifiedByException();
  }
  
  @Test
  public void shouldFalisyWhenAssertionsThrownAfterTypeConversion() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
                arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
                arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .as( (a,b,c) -> a.toString() + b + c)
        .checkAssert(a -> assertEquals("111",a));

    verifier.isFalsifiedByException();
  }   

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenAsserting() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7),
            arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .asWithPrecursor((a, b, c) -> a.toString())
        .checkAssert((a, b, c, i) -> assertThat(i).isEqualTo("1"));

    verifier.isFalsifiedByException();
  }

  private QuickTheory qt() {
    return this.verifier.qt();
  }

}
