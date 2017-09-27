package org.quicktheories.quicktheories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class SearchResult<T> {
 
  private final Optional<Throwable> smallestThrowable;
  private final boolean exhausted;
  private final int executedExamples;
  private final List<T> falisifyingValues;
  
  SearchResult(boolean exhausted, int executedExamples, List<T> falisifyingValues, Optional<Throwable> smallestThrowable) {
    this.exhausted = exhausted;
    this.executedExamples = executedExamples;
    this.falisifyingValues = falisifyingValues;
    this.smallestThrowable = smallestThrowable;
  }
  
  boolean isFalsified() {
    return !falisifyingValues.isEmpty();
  }

  int getExecutedExamples() {
    return executedExamples;
  }

  List<T> getFalsifictions() {
    List <T> others = new ArrayList<T>(falisifyingValues);
    others.remove(0);
    return others;
  }

  T smallest() {
    return falisifyingValues.get(0);
  }

  boolean wasExhausted() {
    return exhausted;
  }

  Optional<Throwable> getSmallestThrowable() {
    return smallestThrowable;
  } 
  
}
