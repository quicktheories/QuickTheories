package org.quicktheories.impl;

import org.quicktheories.core.RandomnessSource;

/**
 * RandomnessSource with additional operations - kept separate to allow
 * classes it depends on to remain package private
 */
public interface ExtendedRandomnessSource extends RandomnessSource {
  
  long tryNext(Constraint constraints);
  void add(Precursor other);
  
}
