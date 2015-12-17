package org.quicktheories.quicktheories.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Tuple5;

import nl.jqno.equalsverifier.EqualsVerifier;

public class Tuple5Test {

  @Test
  public void shouldDisplayAllDataInToString() {
    Tuple5<Integer, String, Integer, String, Integer> testee = Tuple5.of(42,
        "foo",
        13, "boo", 7);
    assertThat(testee.toString()).isEqualTo("{42, foo, 13, boo, 7}");
  }

  @Test
  public void shouldMapValues() {
    assertThat(Tuple5.of(1, 2, 3, 4, 5).map(i -> "A" + i, i -> "B" + i,
        i -> "C" + i, i -> "D" + i, i -> "E" + i))
            .isEqualTo(Tuple5.of("A1", "B2", "C3", "D4", "E5"));
  }

  @Test
  public void shouldObeyHashcodeEqualsContract() {
    EqualsVerifier.forClass(Tuple5.class).allFieldsShouldBeUsed().verify();
  }

  @Test
  public void shouldNotBeEqualToUnrelatedClass() {
    assertThat(Tuple5.of(1, 2, 3, 4, 5)).isNotEqualTo("");
  }
  
  
}
