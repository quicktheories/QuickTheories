package org.quicktheories.core;

import org.quicktheories.impl.Constraint;

/**
 * Source of pseudorandom longs
 */
public interface RandomnessSource {
  
  long next(Constraint constraints);

  DetatchedRandomnessSource detach();
  
  void registerFailedAssumption();
 
}
