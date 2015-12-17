package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.Date;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.generators.DatesDSL.Dates;

public class DatesTest {

  @Test
  public void shouldNotShrinkEpoch() {
    Source<Date> testee = Dates.withMilliSeconds(70978);
    assertThatSource(testee).cannotShrink(new Date(0));
  }

  @Test
  public void shouldNotShrinkEpochWhenMilliSecondsBetweenTwoValues() {
    Source<Date> testee = Dates.withMilliSecondsBetween(0, 8687689756l);
    assertThatSource(testee).cannotShrink(new Date(0));
  }

  @Test
  public void shouldShrinkDateByOneWhenWithinRemainingCycles() {
    Source<Date> testee = Dates.withMilliSeconds(30);
    assertThatSource(testee).shrinksValueTo(new Date(5), new Date(4));
  }

  @Test
  public void shouldShrinkBoundedDateByOneWhenWithinRemainingCycles() {
    Source<Date> testee = Dates.withMilliSecondsBetween(0, 40);
    assertThatSource(testee).shrinksValueTo(new Date(7), new Date(6));
  }

  @Test
  public void shouldShrinkBoundedDateTowardsSmallerDate() {
    Source<Date> testee = Dates.withMilliSecondsBetween(97987, 797098708732l);
    assertThatSource(testee).shrinksConformTo(new Date(7899078),
        d -> d.before(new Date(7899078)) && d.compareTo(new Date(97987)) >= 0,
        withCycles(100));
  }

  @Test
  public void shouldShrinkDateTowardsEpochWhenOutsideRemainingCycles() {
    Source<Date> testee = Dates.withMilliSeconds(352580352);
    assertThatSource(testee).shrinksConformTo(new Date(32452352),
        d -> d.before(new Date(32452352)) && d.compareTo(new Date(0)) >= 0,
        withCycles(100));
  }

  private ShrinkContext withCycles(int cycles) {
    return new ShrinkContext(0, cycles, Configuration.defaultPRNG(2));
  }

}
