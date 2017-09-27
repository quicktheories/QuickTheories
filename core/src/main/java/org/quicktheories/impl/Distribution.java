package org.quicktheories.impl;

public interface Distribution<T> {
  PrecursorDataPair<T> generate();
}
