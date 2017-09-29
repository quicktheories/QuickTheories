package org.quicktheories.core;

import java.util.Collection;
import java.util.Collections;

import org.quicktheories.impl.Precursor;

public class NoGuidance implements Guidance {

  @Override
  public void exampleExecuted() {    
  }

  @Override
  public Collection<long[]> suggestValues(int i, Precursor t) {
    return Collections.emptyList();
  }

  @Override
  public void exampleComplete() {
  }

  @Override
  public void newExample(Precursor precursor) {
 
  }

}
