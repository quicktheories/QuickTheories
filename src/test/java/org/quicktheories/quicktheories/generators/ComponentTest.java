package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.stream.Collectors;

import org.mockito.ArgumentCaptor;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

abstract class ComponentTest<T> {
  public Reporter reporter;
  public Source<T> source;
  public Strategy strategy;

  public TheoryBuilder<T> theoryBuilder(Source<T> source,
      Strategy strategy, Reporter reporter) {
    this.source = source;
    this.strategy = strategy;
    this.reporter = reporter;
    return new TheoryBuilder<>(() -> strategy, source, i -> true);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected List<T> listOfShrunkenItems() {
    ArgumentCaptor<List> shrunkList = ArgumentCaptor.forClass(List.class);
    verify(this.reporter, times(1)).falisification(anyLong(), anyInt(),
        any(Object.class), shrunkList.capture(), anyObject());
    return shrunkList.getValue();
  }

  protected T smallestValueFound() {
    return captureSmallestValue().getValue();
  }

  @SuppressWarnings("unchecked")
  private ArgumentCaptor<T> captureSmallestValue() {
    ArgumentCaptor<T> smallestValue = (ArgumentCaptor<T>) ArgumentCaptor
        .forClass(Object.class);
    verify(this.reporter, times(1)).falisification(anyLong(), anyInt(),
        smallestValue.capture(), any(List.class), anyObject());
    return smallestValue;
  }

  protected void atLeastFiveDistinctFalsifyingValuesAreFound() {
    List<T> distinctList = listOfShrunkenItems().stream().distinct()
        .collect(Collectors.toList());
    assertTrue("Expected " + listOfShrunkenItems()
        + " to have contained five distinct values rather than "
        + distinctList.size(), distinctList.size() >= 5);
  }

  protected void atLeastNDistinctFalsifyingValuesAreFound(int n) {
    List<T> distinctList = listOfShrunkenItems().stream().distinct()
        .collect(Collectors.toList());
    assertTrue(
        "Expected " + listOfShrunkenItems() + " to have contained " + n
            + " distinct values rather than " + distinctList.size(),
        distinctList.size() >= n);
  }

}