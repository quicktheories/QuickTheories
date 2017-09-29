package org.quicktheories.coverage.analysis;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class BlockTest {

  @Test
  public void shouldObeyHashcodeEqualsContract() {
    EqualsVerifier.forClass(Block.class).withIgnoredFields("lines") .verify();
  }

}
