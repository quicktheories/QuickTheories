package org.quicktheories.quicktheories.generators;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.generators.DatesDSL.Dates;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class DatesComponentTest extends ComponentTest<Date> {

    Reporter reporter = mock(Reporter.class);
    Strategy strategy = new Strategy(Configuration.defaultPRNG(5), 1000, 1000,
        this.reporter);

    @Test
    public void shouldShrinkToEpoch() {
      assertThatFor(Dates.withMilliSeconds(53252224233l)).check(i -> false);
      listIsInDecreasingValueOrder();
      smallestValueIsEqualTo(new Date(0));
    }
    
    @Test
    public void shouldShrinkToEpochWhenBetweenEpochs() {
      assertThatFor(Dates.withMilliSecondsBetween(0, 0)).check(i -> false);
      smallestValueIsEqualTo(new Date(0));
    }
    
    @Test
    public void shouldShrinkToSmallestDateWhenBetween() {
      assertThatFor(Dates.withMilliSecondsBetween(32523, 789709709780952l)).check(i -> false);
      listIsInDecreasingValueOrder();
      smallestValueIsEqualTo(new Date(32523));
    }

    private TheoryBuilder<Date, Date> assertThatFor(
        Source<Date> generator) {
      return theoryBuilder(generator, this.strategy, this.reporter);
    }

    private void listIsInDecreasingValueOrder() {
      for (int i = 1; i < listOfShrunkenItems().size(); i++) {
        assertTrue(
            "Expected " + (listOfShrunkenItems().get(i - 1))
                + " to be bigger than " + (listOfShrunkenItems().get(i)),
            listOfShrunkenItems().get(i - 1)
                .compareTo(listOfShrunkenItems().get(i)) >= 0);
      }
    }

    private void smallestValueIsEqualTo(Date target) {
      assertTrue(
          "Expected " + smallestValueFound() + " to be equal to " + target,
          (target.compareTo(smallestValueFound()) == 0));
    }

  }