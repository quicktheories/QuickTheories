package org.quicktheories.impl;

import org.quicktheories.core.DetatchedRandomnessSource;
import org.quicktheories.core.PseudoRandom;

class ShapedDataSource implements ExtendedRandomnessSource {
  
  private final PseudoRandom r;
  private final long[] forced;
  private int forcedIndex = 0;
  private int remainingTries;
  private int failedAssumptions = 0;
   
  private final Precursor precursor = new Precursor();
  
  ShapedDataSource(PseudoRandom r, long[] forced, int maxTries) {
    this.r = r;
    this.forced = forced;
    this.remainingTries = maxTries;
  }

  @Override
  public long next(Constraint constraints) {  
    final long val = tryNext(constraints);
    precursor.store(val, constraints);
    return val;
  }
  
  @Override
  public long tryNext(Constraint constraints) {
    if (forcedIndex < forced.length && constraints.allowed(forced[forcedIndex])) {
      return replay();
    } else {
      return drawFresh(constraints);
    } 
  }
  
  @Override
  public void registerFailedAssumption() {
    remainingTries = remainingTries - 1;
    failedAssumptions = failedAssumptions + 1;
    if (remainingTries == 0) {
      throw new AttemptsExhaustedException("Gave up trying to find values matching assumptions");
    }
  }  
  
  Precursor capturedPrecursor() {
    return precursor;
  }
  
  int failedAssumptions() {
    return failedAssumptions;
  }
  
  private long replay() {
    long l = forced[forcedIndex];
    forcedIndex = forcedIndex + 1;
    return l;
  }
  
  private long drawFresh(Constraint constraints) {
    long l = r.nextLong(constraints.min(), constraints.max());  
    return l;
  }

  @Override
  public DetatchedRandomnessSource detach() {
    return new ConcreteDetachedSource(this);
  }
  
  public void add(Precursor other) {
    this.precursor.combine(other);
  }

}
