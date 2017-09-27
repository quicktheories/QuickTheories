package org.quicktheories.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.api.Tuple4;
import org.quicktheories.api.Tuple5;

import nl.jqno.equalsverifier.EqualsVerifier;

public class Tuple4Test {

  @Test
  public void shouldPrependDataToFromTuple5() {
    Tuple4<Integer, String, Integer, String> testee = Tuple4.of(42, "foo", 13,
        "boo");
    Tuple5<String, Integer, String, Integer, String> actual = testee
        .prepend("coo");
    assertThat(actual).isEqualTo(Tuple5.of("coo", 42, "foo", 13, "boo"));
  }

  @Test
  public void shouldExtendToRightBasedOnOwnContents() {
    Tuple4<Integer, String, Integer, String> testee = Tuple4.of(42, "foo", 13,
        "boo");
    Tuple5<Integer, String, Integer, String, String> actual = testee
        .extend((a, b, c, d) -> "" + a + b + c + d);
    assertThat(actual).isEqualTo(Tuple5.of(42, "foo", 13, "boo", "42foo13boo"));
  }

  @Test
  public void shouldDisplayAllDataInToString() {
    Tuple4<Integer, String, Integer, String> testee = Tuple4.of(42, "foo",
        13, "boo");
    assertThat(testee.toString()).isEqualTo("{42, foo, 13, boo}");
  }

  @Test
  public void shouldMapValues() {
    assertThat(Tuple4.of(1, 2, 3, 4).map(i -> "A" + i, i -> "B" + i, i -> "C" + i, i -> "D" + i))
        .isEqualTo(Tuple4.of("A1", "B2", "C3", "D4"));
  }
  
  @Test
  public void shouldObeyHashcodeEqualsContract() {
    EqualsVerifier.forClass(Tuple4.class).verify();
  }
  
  @Test
  public void shouldNotBeEqualToUnrelatedClass() {
    assertThat(Tuple4.of(1, 2, 3, 4)).isNotEqualTo("");
  }
}
