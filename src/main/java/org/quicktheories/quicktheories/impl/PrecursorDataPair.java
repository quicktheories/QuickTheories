package org.quicktheories.quicktheories.impl;

class PrecursorDataPair<T> {
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

  T value() {
    return value;
  }
  
  int failedAssumptions() {
    return failedAssumptions;
  }

}
