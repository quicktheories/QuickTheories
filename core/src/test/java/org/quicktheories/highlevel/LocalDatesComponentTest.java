package org.quicktheories.highlevel;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class LocalDatesComponentTest extends ComponentTest<LocalDate> implements WithQuickTheories {

  @Test
  public void shouldShrinkToEpoch() {
    assertThatFor(localDates().withDays(-3253252)).check(i -> false);
    smallestValueIsEqualTo(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkToEpochWhenBetweenEpochs() {
    assertThatFor(localDates().withDaysBetween(0, 0)).check(i -> false);
    smallestValueIsEqualTo(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkToSmallestDateWhenBetween() {
    assertThatFor(localDates().withDaysBetween(3523, 235235325))
        .check(i -> false);
    smallestValueIsEqualTo(LocalDate.ofEpochDay(3523));
  }

  @Test
  public void shouldShowThatTheMonthOfFebruaryDoesExist() {
    assertThatFor(
        localDates().withDaysBetween(LocalDate.of(1977, 1, 1).toEpochDay(),
            LocalDate.of(2015, 1, 1).toEpochDay()))
                .check(i -> i.getMonthValue() != 2);
    smallestValueIsEqualTo(LocalDate.of(1977, 2, 1));
  }

  private void smallestValueIsEqualTo(LocalDate target) {
    assertTrue(
        "Expected " + smallestValueFound() + " to be equal to " + target,
        (target.compareTo(smallestValueFound()) == 0));
  }

}
