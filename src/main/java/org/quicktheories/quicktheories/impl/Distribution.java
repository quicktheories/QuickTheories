package org.quicktheories.quicktheories.impl;

public interface Distribution<T> {
  PrecursorDataPair<T> generate();
}
