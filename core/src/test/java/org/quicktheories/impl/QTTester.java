package org.quicktheories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiPredicate;

import org.mockito.ArgumentCaptor;
import org.quicktheories.QuickTheory;
import org.quicktheories.api.AsString;
import org.quicktheories.api.Pair;
import org.quicktheories.api.Predicate3;
import org.quicktheories.api.Predicate4;
import org.quicktheories.api.Tuple3;
import org.quicktheories.api.Tuple4;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.NoGuidance;
import org.quicktheories.core.Reporter;
import org.quicktheories.core.Strategy;

public class QTTester {

  private Reporter r = mock(Reporter.class);

  public QuickTheory qt(long seed) {
    Strategy s = new Strategy(Configuration.defaultPRNG(seed), 100, 0, 10000, 10, r, prng -> new NoGuidance());
    return org.quicktheories.QuickTheory.qt(() -> s);
  }

  public QuickTheory qt() {
    return qt(0);
  }

  public void notFalsified() {
    verify(r, never()).falsification(anyLong(), anyInt(), any(Object.class),
        any(List.class), any());
  }

  public void isFalsified() {
    verify(r, times(1)).falsification(anyLong(), anyInt(), any(Object.class),
        any(List.class), any());
  }

  public void isFalsifiedByException() {
    verify(r, times(1)).falsification(anyLong(), anyInt(), any(Object.class),
        any(Throwable.class), any(List.class), any());
  }

  public void reportedSeedIs(long seed) {
    verify(r, times(1)).falsification(eq(seed), anyInt(), any(Object.class),
        any(List.class), any());
  }

  @SuppressWarnings("unchecked")
  public <T> T smallestFalsifiedValue() {
    ArgumentCaptor<T> value = (ArgumentCaptor<T>) ArgumentCaptor
        .forClass(Object.class);
    verify(r, times(1)).falsification(anyLong(), anyInt(), value.capture(),
        any(List.class), any());
    return value.getValue();
  }

  public void isExahusted() {
    verify(r, times(1)).valuesExhausted(anyInt());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B> void smallestValueMatches(BiPredicate<A, B> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falsification(anyLong(), anyInt(), value.capture(),
        any(List.class), any());
    Pair<A, B> v = (Pair<A, B>) value.getValue();
    if (!p.test(v._1, v._2)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B, C> void smallestValueMatches(Predicate3<A, B, C> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falsification(anyLong(), anyInt(), value.capture(),
        any(List.class), any());
    Tuple3<A, B, C> v = (Tuple3<A, B, C>) value.getValue();
    if (!p.test(v._1, v._2, v._3)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <A, B, C, D> void smallestValueMatches(Predicate4<A, B, C, D> p) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    verify(r, times(1)).falsification(anyLong(), anyInt(), value.capture(),
        any(List.class), any());
    Tuple4<A, B, C, D> v = (Tuple4<A, B, C, D>) value.getValue();
    if (!p.test(v._1, v._2, v._3, v._4)) {
      throw new AssertionError(v.toString() + " does not satisfy expectations");
    }

  }

  @SuppressWarnings({ "rawtypes"})
  public void falsificationContainsText(String string) {
    ArgumentCaptor value = ArgumentCaptor.forClass(Object.class);
    ArgumentCaptor<AsString> asString = ArgumentCaptor.forClass(AsString.class);
    verify(r, times(1)).falsification(anyLong(), anyInt(), value.capture(),
        any(List.class), asString.capture());

    assertThat(asString.getValue().asString(value.getValue())).contains(string);

  }

}
