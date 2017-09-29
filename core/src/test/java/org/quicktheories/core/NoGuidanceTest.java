package org.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.impl.Precursor;

public class NoGuidanceTest {

  @Test
  public void doesNotSuggestValues() {
    NoGuidance testee = new NoGuidance();
    assertThat(testee.suggestValues(0, new Precursor())).isEmpty();
  }

}
