package org.quicktheories.quicktheories.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiPredicate;

import org.mockito.ArgumentCaptor;
import org.quicktheories.quicktheories.QuickTheory;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Predicate3;
import org.quicktheories.quicktheories.api.Predicate4;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.api.Tuple4;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Strategy;

public class QTTester {

  private Reporter r = mock(Reporter.class);

  public QuickTheory qt(long seed) {
    Strategy s = new Strategy(Configuration.defaultPRNG(seed), 100, 10000, r);
    return org.quicktheories.quicktheories.QuickTheory.qt(() -> s);
  }

  public QuickTheory qt() {
    return qt(0);
  }

  @SuppressWarnings("unchecked")
  public void notFalsified() {
    verify(r, never()).falisification(anyLong(), anyInt(), any(Object.class),
        any(List.class), anyObject());
  }

  @SuppressWarnings("unchecked")
  public void isFalsified() {
    verify(r, times(1)).falisification(anyLong(), anyInt(), any(Object.class),
        any(List.class), anyObject());
  }

  @SuppressWarnings("unchecked")
  public void isFalsifiedByException() {
    verify(r, times(1)).falisification(anyLong(), anyInt(), any(Object.class),
        any(Throwable.class), any(List.class), anyObject());
  }

  @SuppressWarnings("unchecked")
  public void reportedSeedIs(long seed) {
    verify(r, times(1)).falisification(eq(seed), anyInt(), any(Object.class),
        any(List.class), anyObject());
  }

  @SuppressWarnings("unchecked")
  public <T> T smallestFalsifiedValue() {
    ArgumentCaptor<T> value = (ArgumentCaptor<T>) ArgumentCaptor
        .forClass(Object.class);
    verify(r, times(1)).falisification(anyLong(), anyInt(), value.capture(),
        any(List.class), anyObject());
    return value.getValue();
  }

  public void isExahusted() {
    verify(r, times(1)).valuesExhausted(anyInt());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B> void smallestValueMatches(BiPredicate<A, B> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falisification(anyLong(), anyInt(), value.capture(),
        any(List.class), anyObject());
    Pair<A, B> v = (Pair<A, B>) value.getValue();
    if (!p.test(v._1, v._2)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B, C> void smallestValueMatches(Predicate3<A, B, C> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falisification(anyLong(), anyInt(), value.capture(),
        any(List.class), anyObject());
    Tuple3<A, B, C> v = (Tuple3<A, B, C>) value.getValue();
    if (!p.test(v._1, v._2, v._3)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B, C, D> void smallestValueMatches(Predicate4<A, B, C, D> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falisification(anyLong(), anyInt(), value.capture(),
        any(List.class), anyObject());
    Tuple4<A, B, C, D> v = (Tuple4<A, B, C, D>) value.getValue();
    if (!p.test(v._1, v._2, v._3, v._4)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }

  }

}
