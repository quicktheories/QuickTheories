package org.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.quicktheories.core.Configuration;

public class ConfigurationTest {

  @Rule
  public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

  @Test
  public void shouldUseSeedFromSystemProperty() {
    System.setProperty("QT_SEED", "42");
    assertThat(Configuration.systemStrategy().prng().getInitialSeed())
        .isEqualTo(42);
  }

  @Test
  public void shouldUseNumberOfExamplesFromSystemProperty() {
    System.setProperty("QT_EXAMPLES", "13");
    assertThat(Configuration.systemStrategy().examples()).isEqualTo(13);
  }

  @Test
  public void shouldUseNumberOfShrinkCyclesFromSystemProperty() {
    System.setProperty("QT_SHRINKS", "7");
    assertThat(Configuration.systemStrategy().shrinkCycles()).isEqualTo(7);
  }

  @Test
  public void shouldDefaultShrinkCyclesTo100TimesTheNumberOfExamples() {
    System.setProperty("QT_EXAMPLES", "500");
    assertThat(Configuration.systemStrategy().shrinkCycles())
        .isEqualTo(500 * 100);
  }

}
