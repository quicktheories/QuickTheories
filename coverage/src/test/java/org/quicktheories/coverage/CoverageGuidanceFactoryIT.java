package org.quicktheories.coverage;

import org.junit.Test;
import org.quicktheories.core.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class CoverageGuidanceFactoryIT {

  @Test
  public void makesCoverageGuidanceAvailableAsAService() {
    assertThat(Configuration.systemStrategy().guidance()).isInstanceOf(CoverageGuidance.class);
  }

}
