package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.time.LocalDate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.generators.LocalDatesDSL.LocalDates;

public class LocalDatesTest {

  @Test
  public void shouldNotShrinkEpoch() {
    Source<LocalDate> testee = LocalDates.withDays(70978);
    assertThatSource(testee).cannotShrink(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldNotShrinkEpochWhenDaysAreBetweenTwoValues() {
    Source<LocalDate> testee = LocalDates.withDaysBetween(-324235, 70978);
    assertThatSource(testee).cannotShrink(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkLocalDateByOneWhenWithinRemainingCycles() {
    Source<LocalDate> testee = LocalDates.withDays(30);
    assertThatSource(testee).shrinksValueTo(LocalDate.ofEpochDay(6),
        LocalDate.ofEpochDay(5));
  }

  @Test
  public void shouldShrinkBoundedLocalDateByOneWhenWithinRemainingCycles() {
    Source<LocalDate> testee = LocalDates.withDaysBetween(-30, -2);
    assertThatSource(testee).shrinksValueTo(LocalDate.ofEpochDay(-6),
        LocalDate.ofEpochDay(-5));
  }

  @Test
  public void shouldShrinkBoundedLocalDateTowardsSmallerLocalDate() {
    Source<LocalDate> testee = LocalDates.withDaysBetween(97987, 999999999);
    assertThatSource(testee).shrinksConformTo(LocalDate.ofEpochDay(7899078),
        d -> d.isBefore(LocalDate.ofEpochDay(7899078))
            && d.compareTo(LocalDate.ofEpochDay(97987)) >= 0,
        withCycles(100));
  }

  @Test
  public void shouldShrinkDateTowardsEpochWhenOutsideRemainingCycles() {
    Source<LocalDate> testee = LocalDates.withDays(352580352);
    assertThatSource(testee).shrinksConformTo(LocalDate.ofEpochDay(32452352),
        d -> d.isBefore(LocalDate.ofEpochDay(32452352))
            && d.compareTo(LocalDate.ofEpochDay(0)) >= 0,
        withCycles(100));
  }

  private ShrinkContext withCycles(int cycles) {
    return new ShrinkContext(0, cycles, Configuration.defaultPRNG(2));
  }

}
