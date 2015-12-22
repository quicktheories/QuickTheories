package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;
import org.quicktheories.quicktheories.generators.LocalDatesDSL.LocalDates;

public class LocalDatesComponentTest extends ComponentTest<LocalDate> {

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

  private void smallestValueIsEqualTo(LocalDate target) {
    assertTrue(
        "Expected " + smallestValueFound() + " to be equal to " + target,
        (target.compareTo(smallestValueFound()) == 0));
  }

}
