package org.quicktheories.highlevel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.mockito.ArgumentCaptor;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Reporter;
import org.quicktheories.core.Strategy;
import org.quicktheories.dsl.TheoryBuilder;

abstract class ComponentTest<T> {
  
  protected Reporter reporter = mock(Reporter.class);
  protected Strategy defaultStrategy = new Strategy(Configuration.defaultPRNG(2), 1000, 10000, 10,
      this.reporter);

  public TheoryBuilder<T> assertThatFor(Gen<T> generator) {
    return assertThatFor(generator, defaultStrategy);
  }

  public TheoryBuilder<T> assertThatFor(Gen<T> source, Strategy strategy) {
    return new TheoryBuilder<>(() -> strategy, source);
  }

  public Strategy withShrinkCycles(int shrinkCycles) {
    return defaultStrategy.withShrinkCycles(shrinkCycles);
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

}