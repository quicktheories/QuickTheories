package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import java.time.LocalDate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.generators.LocalDatesDSL.LocalDates;

public class LocalDatesTest {

  @Test
  public void shouldShrinkTowardsEpoch() {
    Gen<LocalDate> testee = LocalDates.withDays(70978);
    assertThatGenerator(testee).shrinksTowards(LocalDate.ofEpochDay(0));
  }

  @Test
  public void shouldShrinkBoundedLocalDateTowardsBound() {
    Gen<LocalDate> testee = LocalDates.withDaysBetween(-30, -2);
    assertThatGenerator(testee).shrinksTowards(LocalDate.ofEpochDay(-2));
  }

}
