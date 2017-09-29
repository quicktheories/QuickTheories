package org.quicktheories.coverage;

import org.quicktheories.core.Guidance;
import org.quicktheories.core.GuidanceFactory;
import org.quicktheories.core.PseudoRandom;

public class CoverageGuidanceFactory implements GuidanceFactory {

  @Override
  public Guidance apply(PseudoRandom t) {
    return new CoverageGuidance(t);
  }

}
