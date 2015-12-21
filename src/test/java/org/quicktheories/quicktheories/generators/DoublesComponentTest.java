package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class DoublesComponentTest extends ComponentTest<Double> {

  Reporter reporter = mock(Reporter.class);
  PseudoRandom prng = mock(PseudoRandom.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(2), 1000, 1000,
      this.reporter);

  @Test
  public void shouldShrinkUsingCorrectPositiveGeneratorAcrossRange() {
    assertThatFor(Doubles.fromNegativeInfinityToPositiveInfinity())
        .check(i -> i < 0d);
    listElementsAreAllPositiveInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldShrinkUsingCorrectNegativeGeneratorAcrossRange() {
    assertThatFor(Doubles.fromNegativeInfinityToPositiveInfinity())
        .check(i -> i >= 0d);
    listElementsAreAllNegativeInDecreasingAbsValueOrder();
  }

  private TheoryBuilder<Double> assertThatFor(
      Source<Double> generator) {
    return theoryBuilder(generator, this.strategy, this.reporter);
  }

  private void listElementsAreAllPositiveInDecreasingAbsValueOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue(
          "Expected " + (listOfShrunkenItems().get(i - 1))
              + " to be bigger than " + (listOfShrunkenItems().get(i)),
          Math.abs(listOfShrunkenItems().get(i - 1)) >= Math
              .abs(listOfShrunkenItems().get(i)));
      assertTrue(
          "Expected " + listOfShrunkenItems().get(i - 1) + " to be positive",
          listOfShrunkenItems().get(i - 1) >= 0d);
    }
  }

  private void listElementsAreAllNegativeInDecreasingAbsValueOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue(
          "Expected " + (listOfShrunkenItems().get(i - 1))
              + " to be bigger than " + (listOfShrunkenItems().get(i)),
          Math.abs(listOfShrunkenItems().get(i - 1)) >= Math
              .abs(listOfShrunkenItems().get(i)));
      assertTrue(
          "Expected " + listOfShrunkenItems().get(i - 1) + " to be positive",
          listOfShrunkenItems().get(i - 1) <= -0d);
    }
  }

}
