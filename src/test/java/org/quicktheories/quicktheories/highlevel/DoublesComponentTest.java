package org.quicktheories.quicktheories.highlevel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;

public class DoublesComponentTest extends ComponentTest<Double> implements WithQuickTheories {

  @Test
  public void shouldShrinkUsingCorrectPositiveGeneratorAcrossRange() {
    assertThatFor(doubles().any())
        .check(i -> i < 0d);
    listElementsAreAllPositiveInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldShrinkUsingCorrectNegativeGeneratorAcrossRange() {
    assertThatFor(doubles().any())
        .check(i -> i >= 0d);
    listElementsAreAllNegativeInDecreasingAbsValueOrder();
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
