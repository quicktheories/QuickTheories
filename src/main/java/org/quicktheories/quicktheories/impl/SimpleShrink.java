package org.quicktheories.quicktheories.impl;

import java.util.Arrays;

import org.quicktheories.quicktheories.core.PseudoRandom;

/**
 * Shrinks by randomly modifying one precursor at a time.
 * 
 * If a single step shrink results in the same input a two change shrink will be
 * performed.
 * 
 */
public class SimpleShrink implements ShrinkStrategy {
  

  @Override
  public long[] shrink(PseudoRandom r, Precursor precursor) {
      long[] toShrink = precursor.current();
      singleStepShrink(toShrink, r, precursor);
      
      if (Arrays.equals(toShrink, precursor.current())) {
        twoStepShrink(toShrink, r, precursor);
      }

      return toShrink;
  }
  

  private void twoStepShrink(long[] toShrink, PseudoRandom r, Precursor precursor) {
    singleStepShrink(toShrink, r, precursor);
    singleStepShrink(toShrink, r, precursor);
  }
  
  void singleStepShrink(long[] toShrink, PseudoRandom r, Precursor precursor) {
    int index = pickIndex(r, toShrink);
    long current = toShrink[index];

    long inclusiveTarget = pickShrinkTarget(r, precursor, index);

    final long upperSearchBound;
    final long lowerSearchBound;
    if (current > inclusiveTarget) {
      upperSearchBound = current;
      lowerSearchBound = inclusiveTarget;
    } else {
      upperSearchBound = inclusiveTarget;
      lowerSearchBound = current;
    }

    if (current != inclusiveTarget) {
      long shrunkValue = r.nextLong(lowerSearchBound, upperSearchBound);
      toShrink[index] = shrunkValue;
    }   
  }

  private long pickShrinkTarget(PseudoRandom r, Precursor precursor, int index) {
    // Usually there will be a "smallest" value
    // but occasionally values will have equal "size". It may still be
    // important to vary them to avoid getting stuck in a local minima.
    // An example would be choosing between commands in a stateful test.
    return precursor.shrinkTarget(index).orElseGet( () -> r.nextLong(precursor.min(index), precursor.max(index)));
  }
  

  private int pickIndex(PseudoRandom r, long[] precursor) {
    return r.nextInt(0,precursor.length -1);
  }

}
