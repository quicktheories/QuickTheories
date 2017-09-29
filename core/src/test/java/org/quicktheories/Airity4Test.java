package org.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.generators.SourceDSL.integers;

import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.api.Tuple5;
import org.quicktheories.core.Gen;
import org.quicktheories.impl.QTTester;

public class Airity4Test {

  QTTester verifier = new QTTester();

  Gen<Integer> g = integers().all();

  @Test
  public void shouldNotFalisifyTheTruth() {
    qt()
        .forAll(this.g, this.g, this.g, this.g)
        .check((a, b, c, d) -> true);

    verifier.notFalsified();
  }

  @Test
  public void shouldFalisifyUniversalFalsehood() {
    qt()
        .forAll(this.g, this.g, this.g, this.g)
        .check((a, b, c, d) -> false);

    verifier.isFalsified();
  }

  @Test
  public void shouldFalsifyPartialTruth() {
    qt()
        .forAll(this.g, this.g, this.g, this.g)
        .check((a, b, c, d) -> a + b + c + d > 100);

    verifier.isFalsified();
  }

  @Test
  public void shouldConstrainGeneratorsByAssumptions() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b, c, d) -> a > 2 && b > 2 && c > 2 && d > 2)
        .check((a, b, c, d) -> a > 2 && b > 2 && c > 2 && d > 2);

    verifier.notFalsified();
  }

  @Test
  public void shouldShrinkWhenAllSidesCanBeShrunk() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c, d) -> a == 5 && b == 5 && c == 5 && d == 5);

    verifier.<Integer, Integer, Integer, Integer> smallestValueMatches(
        (a, b, c, d) -> a == 1 && b == 1 && c == 1 && d == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenLeftHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().constant(0),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c, d) -> d == 5);

    verifier.<Integer, Integer, Integer, Integer> smallestValueMatches(
        (a, b, c, d) -> a == 0 && b == 1 && c == 1 & d == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenRightHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7), arbitrary().constant(0))
        .check((a, b, c, d) -> d == 5);

    verifier.<Integer, Integer, Integer, Integer> smallestValueMatches(
        (a, b, c, d) -> a == 1 && b == 1 && c == 1 && d == 0);
  }

  @Test
  public void shouldContinueToShrinkWhenSecondAtSmallestValue() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().constant(0), arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .check((a, b, c, d) -> d == 5);

    verifier.<Integer, Integer, Integer, Integer> smallestValueMatches(
        (a, b, c, d) -> a == 1 && b == 0 && c == 1 && d == 1);
  }

  @Test
  public void shouldConvertAndShrinkValues() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d)
        .check(a -> a.equals("1:1:1:1"));

    String actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo("2:1:1:1");
  }

  @Test
  public void shouldFalisyWhenAssertionsThrown() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .checkAssert((a, b, c, d) -> assertThat(a + b + c + d).isEqualTo(4));

    verifier.isFalsifiedByException();
  }
  
  @Test
  public void shouldFalisyWhenAssertionsThrownAfterTypeConversion() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
                arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
                arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
                arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as( (a,b,c,d) -> a.toString() + b + c + d)
        .checkAssert(a -> assertThat("111a").isEqualTo(a));

    verifier.isFalsifiedByException();
  }  

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenConvertingTypes() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b, c, d) -> a != 2 && b != 2 && c != 2 && d != 2)
        .asWithPrecursor((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d)
        .check((a, b, c, d, value) -> value.equals("1:1:1:1"));

    Tuple5<Integer, Integer, Integer, Integer, String> actual = verifier
        .smallestFalsifiedValue();
    assertThat(actual).isEqualTo(Tuple5.of(3, 1, 1, 1, "3:1:1:1"));
  }

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenAsserting() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .asWithPrecursor((a, b, c, d) -> a.toString())
        .checkAssert((a, b, c, d, i) -> assertThat(i).isEqualTo("1"));

    verifier.isFalsifiedByException();
  }
  
  @Test
  public void shouldConstructDescriptionUsingToStringWhenConvertingType() {
    qt()
        .forAll(arbitrary().constant(1),
                arbitrary().constant(1),
                arbitrary().constant(1),                
                arbitrary().constant(1))
        .as((a, b, c, d) -> new FooInteger(a))
        .check(f -> false);

    verifier.falsificationContainsText("Foo 1");
  }
  
  @Test
  public void shouldConstructDescriptionUsingToStringWhenConvertingTypeWithPrecursor() {
    qt()
        .forAll(arbitrary().constant(1),
                arbitrary().constant(1),
                arbitrary().constant(1),                
                arbitrary().constant(1))
        .asWithPrecursor((a, b, c, d) -> new FooInteger(a))
        .check((a,b,c,d, e) -> false);

    verifier.falsificationContainsText("{1, 1, 1, 1, Foo 1}");
  }  

  private QuickTheory qt() {
    return this.verifier.qt();
  }

}
