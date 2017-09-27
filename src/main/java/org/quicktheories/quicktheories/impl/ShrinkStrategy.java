package org.quicktheories.quicktheories.impl;

import org.quicktheories.quicktheories.core.PseudoRandom;

public interface ShrinkStrategy {

  long[] shrink(PseudoRandom r, Precursor in);

}
