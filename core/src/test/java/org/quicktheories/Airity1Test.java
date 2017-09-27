package org.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.generators.SourceDSL.integers;

import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;
import org.quicktheories.impl.QTTester;

public class Airity1Test {

  QTTester verifier = new QTTester();

  Gen<Integer> g = integers().all();

  @Test
  public void shouldNotFalisifyTheTruth() {
    qt()
        .forAll(this.g)
        .check(a -> true);

    verifier.notFalsified();
  }

  @Test
  public void shouldFalisifyUniversalFalsehood() {
    qt()
        .forAll(this.g)
        .check(a -> false);

    verifier.isFalsified();
  }

  @Test
  public void shouldFalsifyPartialTruth() {
    qt()
        .forAll(this.g)
        .check(a -> a > 100);

    verifier.isFalsified();
  }

  @Test
  public void shouldFalsifyWhenPredicateThrowsException() {
    qt()
        .forAll(this.g)
        .check(a -> {
          if (a != 2)
            throw new AssertionError();
          return true;
        });

    verifier.isFalsifiedByException();
  }

  @Test
  public void shouldConstrainGeneratorsByAssumptions() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming(a -> a > 2)
        .check(a -> a > 2);

    verifier.notFalsified();
  }

  @Test
  public void shouldConstrainShrunkValuesByAssumptions() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming(a -> a != 1)
        .check(a -> a > 2);

    Integer smallest = verifier.smallestFalsifiedValue();

    assertThat(smallest).isEqualTo(2);
  }

  @Test
  public void shouldReportInitialSeed() {
    verifier.qt(42)
        .forAll(this.g)
        .check(a -> a > 1000);

    verifier.reportedSeedIs(42);
  }

  @Test
  public void shouldReportWhenValuesExhausted() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming(a -> false)
        .check(a -> a > 2);

    verifier.isExahusted();
  }

  @Test
  public void shouldFindSameFalisyingValuesGivenSameSeed() {
    verifier.qt(42)
        .forAll(this.g)
        .check(a -> a > 1000);

    int falsifyingValue = verifier.smallestFalsifiedValue();

    QTTester verifier2 = new QTTester();

    verifier2.qt(42)
        .forAll(this.g)
        .check(a -> a > 1000);

    int repeatedCheck = verifier2.smallestFalsifiedValue();

    assertEquals(falsifyingValue, repeatedCheck);
  }

  @Test
  public void shouldConvertAndShrinkValues() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as(i -> i.toString())
        .check(a -> a.equals("1"));

    String actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo("2");
  }
  
  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenConvertingTypes() {
    qt()
    .withShrinkCycles(10)
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming(i -> i != 2)
        .asWithPrecursor(i -> i.toString())
        .check((precursor, a) -> a.equals("1"));

    Pair<Integer, String> actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo(Pair.of(3, "3"));
  }

  @Test
  public void shouldFalsifyWhenAssertionsThrown() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .checkAssert(i -> assertThat(i).isEqualTo(1));

    verifier.isFalsifiedByException();
  }
  
  @Test
  public void shouldFalsifyWhenAssertionsThrownAfterTypeConversion() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .as(i -> i.toString())
        .checkAssert(a -> assertEquals("1",a));

    verifier.isFalsifiedByException();
  }  
  
  @Test
  public void shouldPrintUsingToStringAfterTypeConversion() {
    qt()
    .forAll(arbitrary().pick(1))
    .as(FooInteger::new)
    .check( l -> false);

    verifier.falsificationContainsText("Foo 1");
  }
  
  @Test
  public void shouldPrintUsingToStringAfterTypeConversionWithPrecursor() {
    qt()
    .forAll(arbitrary().pick(1))
    .asWithPrecursor(FooInteger::new)
    .check( (p,l) -> false);

    verifier.falsificationContainsText("{1, Foo 1}");
  }
  

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenAsserting() {
    qt()
        .forAll(arbitrary().pick(1, 2, 3, 4, 5, 6, 7))
        .assuming(i -> i != 2)
        .asWithPrecursor(i -> i.toString())
        .checkAssert((precursor, i) -> assertThat(i).isEqualTo("1"));

    verifier.isFalsifiedByException();
  }

  @Test
  public void shouldUseSuppliedFunctionToDescribeValues() {
    qt()
    .forAll(arbitrary().pick(1))
    .describedAs( i -> "Bar " + 1)
    .check( l -> false);

    verifier.falsificationContainsText("Bar 1");
  }
  
  private QuickTheory qt() {
    return this.verifier.qt();
  }

}
