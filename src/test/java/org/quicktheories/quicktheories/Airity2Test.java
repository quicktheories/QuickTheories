package org.quicktheories.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.impl.QTTester;

public class Airity2Test {

  QTTester verifier = new QTTester();

  Gen<Integer> g = integers().all();

  @Test
  public void shouldNotFalisifyTheTruth() {
    qt()
        .forAll(this.g, this.g)
        .check((a, b) -> true);

    verifier.notFalsified();
  }

  @Test
  public void shouldFalisifyUniversalFalsehood() {
    qt()
        .forAll(this.g, this.g)
        .check((a, b) -> false);

    verifier.isFalsified();
  }

  @Test
  public void shouldFalsifyPartialTruth() {
    qt()
        .forAll(this.g, this.g)
        .check((a, b) -> a + b > 100);

    verifier.isFalsified();
  }

  @Test
  public void shouldConstrainGeneratorsByAssumptions() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b) -> a > 2 && b > 2)
        .check((a, b) -> a > 2 && b > 2);

    verifier.notFalsified();
  }

  @Test
  public void shouldShrinkWhenBothSidesCanBeShrunk() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .check((a, b) -> a == 5 && b == 5);

    verifier
        .<Integer, Integer> smallestValueMatches((a, b) -> a == 1 && b == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenLeftHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().constant(0),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .check((a, b) -> b == 5);

    verifier
        .<Integer, Integer> smallestValueMatches((a, b) -> a == 0 && b == 1);
  }

  @Test
  public void shouldContinueToShrinkWhenRightHandSideAtSmallestValue() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().constant(0))
        .check((a, b) -> b == 5);

    verifier
        .<Integer, Integer> smallestValueMatches((a, b) -> a == 1 && b == 0);
  }

  @Test
  public void shouldConvertAndShrinkValues() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as((a, b) -> a.toString() + ":" + b.toString())
        .check(a -> a.equals("1:1"));

    String actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo("2:1");
  }

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenConvertingTypes() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming((a, b) -> a != 2 && b != 2)
        .asWithPrecursor((a, b) -> a.toString() + ":" + b)
        .check((a, b, value) -> value.equals("1:1"));

    Tuple3<Integer, Integer, String> actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo(Tuple3.of(3, 1, "3:1"));
  }

  @Test
  public void shouldFalisyWhenAssertionsThrown() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .checkAssert((a, b) -> assertThat(a + b).isEqualTo(2));

    verifier.isFalsifiedByException();
  }

  @Test
  public void shouldFalisyWhenAssertionsThrownAfterTypeConversion() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
                arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as( (a,b) -> a.toString() + b)
        .checkAssert(a -> assertThat("11").isEqualTo(a));

    verifier.isFalsifiedByException();
  }    
  
  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenAsserting() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7),
            arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .asWithPrecursor((a, b) -> a.toString())
        .checkAssert((a, b, i) -> assertThat(i).isEqualTo("1"));

    verifier.isFalsifiedByException();
  }
  
  
  @Test
  public void shouldConstructDescriptionUsingToStringWhenConvertingType() {
    qt()
        .forAll(arbitrary().constant(1),
            arbitrary().constant(1))
        .as((a, b) -> new FooInteger(a))
        .check(f -> false);

    verifier.falsificationContainsText("Foo 1");
  }
  
  @Test
  public void shouldConstructDescriptionUsingToStringWhenConvertingTypeWithPrecursor() {
    qt()
        .forAll(arbitrary().constant(1),
            arbitrary().constant(1))
        .asWithPrecursor((a, b) -> new FooInteger(a))
        .check((a,b,c) -> false);

    verifier.falsificationContainsText("{1, 1, Foo 1}");
  }
  
  

  private QuickTheory qt() {
    return this.verifier.qt();
  }

}
