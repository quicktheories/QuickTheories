package org.quicktheories.impl;

public class PrecursorDataPair<T> {
  private final Precursor precursor;
  private final T value;
  private final int failedAssumptions;
  
  PrecursorDataPair(Precursor precursor, int failedAssumptions, T out) {
    this.precursor = precursor;
    this.value = out;
    this.failedAssumptions = failedAssumptions;
  }

  Precursor precursor() {
    return precursor;
  }

  public T value() {
    return value;
  }
  
  int failedAssumptions() {
    return failedAssumptions;
  }

}
