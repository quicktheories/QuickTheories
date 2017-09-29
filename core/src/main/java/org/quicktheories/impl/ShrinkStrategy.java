package org.quicktheories.impl;

import org.quicktheories.core.PseudoRandom;

public interface ShrinkStrategy {

  long[] shrink(PseudoRandom r, Precursor in);

}
