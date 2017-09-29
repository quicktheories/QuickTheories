package org.quicktheories.core;

import org.quicktheories.impl.Constraint;

/**
 * Source of pseudorandom longs
 */
public interface RandomnessSource {
  
  public long next(Constraint constraints);

  DetatchedRandomnessSource detach();
  
  public void registerFailedAssumption();
 
}
