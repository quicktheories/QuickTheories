package org.quicktheories.impl;

import org.quicktheories.core.PseudoRandom;

public class SimpleRandomnessSource extends ShapedDataSource {

  public SimpleRandomnessSource(PseudoRandom r, int maxTries) {
    super(r, new long[0], maxTries);
  }

}
