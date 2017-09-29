package org.quicktheories.impl;

import org.quicktheories.core.DetatchedRandomnessSource;

public class ConcreteDetachedSource implements DetatchedRandomnessSource, ExtendedRandomnessSource {
  
  private final ExtendedRandomnessSource parent;
  private final Precursor precursor = new Precursor();
 
  public ConcreteDetachedSource(ExtendedRandomnessSource parent) {
    this.parent = parent;
  }
  
  @Override
  public long next(Constraint constraints) {
    long val = parent.tryNext(constraints);
    precursor.store(val, constraints);
    return val;
  }

  @Override
  public DetatchedRandomnessSource detach() {
    return new ConcreteDetachedSource(this);
  }

  @Override
  public void commit() {
    parent.add(precursor);    
  }


  @Override
  public void registerFailedAssumption() {
    parent.registerFailedAssumption();
  }

  @Override
  public long tryNext(Constraint constraints) {
    return parent.tryNext(constraints);
  }

  @Override
  public void add(Precursor other) {
    precursor.combine(other);
  }
  
}
