package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.generators.LocalDatesDSL.LocalDates;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class LocalDatesComponentTest extends ComponentTest<LocalDate> {

  Reporter reporter = mock(Reporter.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(5), 1000, 1000,
      this.reporter);

  @Test
  public void shouldShrinkToEpoch() {
    assertThatFor(LocalDates.withDays(-3253252)).check(i -> false);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkToEpochWhenBetweenEpochs() {
    assertThatFor(LocalDates.withDaysBetween(0, 0)).check(i -> false);
    smallestValueIsEqualTo(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkToSmallestDateWhenBetween() {
    assertThatFor(LocalDates.withDaysBetween(3523, 235235325))
        .check(i -> false);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(LocalDate.ofEpochDay(3523));
  }

  @Test
  public void shouldShowThatTheMonthOfFebruaryDoesExist() {
    assertThatFor(
        LocalDates.withDaysBetween(LocalDate.of(1977, 1, 1).toEpochDay(),
            LocalDate.of(2015, 1, 1).toEpochDay()))
                .check(i -> i.getMonthValue() != 2);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(LocalDate.of(1977, 2, 1));
  }

  private TheoryBuilder<LocalDate, LocalDate> assertThatFor(
      Source<LocalDate> generator) {
    return theoryBuilder(generator, this.strategy, this.reporter);
  }

  private void smallestValueIsEqualTo(LocalDate target) {
    assertTrue(
        "Expected " + smallestValueFound() + " to be equal to " + target,
        (target.compareTo(smallestValueFound()) == 0));
  }

}
