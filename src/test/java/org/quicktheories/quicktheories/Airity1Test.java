package org.quicktheories.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.QTTester;

public class Airity1Test {

  QTTester verifier = new QTTester();

  Source<Integer> g = integers().all();

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
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .as(i -> i.toString())
        .check(a -> a.equals("1"));

    String actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo("2");
  }

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenConvertingTypes() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .assuming(i -> i != 2)
        .asWithPrecursor(i -> i.toString())
        .check((precursor, a) -> a.equals("1"));

    Pair<Integer, String> actual = verifier.smallestFalsifiedValue();
    assertThat(actual).isEqualTo(Pair.of(3, "3"));
  }

  @Test
  public void shouldFalisyWhenAssertionsThrown() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .checkAssert(i -> assertThat(i).isEqualTo(1));

    verifier.isFalsifiedByException();
  }

  @Test
  public void shouldAllowRetentionOfPrecursorValuesWhenAsserting() {
    qt()
        .forAll(arbitrary().sequence(1, 2, 3, 4, 5, 6, 7))
        .assuming(i -> i != 2)
        .asWithPrecursor(i -> i.toString())
        .checkAssert((precursor, i) -> assertThat(i).isEqualTo("1"));

    verifier.isFalsifiedByException();
  }

  private QuickTheory qt() {
    return this.verifier.qt();
  }

}
