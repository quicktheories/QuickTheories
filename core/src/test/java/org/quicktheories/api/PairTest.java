package org.quicktheories.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.api.Pair;
import org.quicktheories.api.Tuple3;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PairTest {

  @Test
  public void shouldDisplayAllDataInToString() {
    Pair<Integer, String> testee = Pair.of(42, "foo");
    assertThat(testee.toString()).isEqualTo("{42, foo}");
  }

  @Test
  public void shouldPrependValueToCreateTuples() {
    Pair<Integer, String> testee = Pair.of(42, "foo");
    Tuple3<String, Integer, String> actual = testee.prepend("boo");
    assertThat(actual).isEqualTo(Tuple3.of("boo", 42, "foo"));
  }

  @Test
  public void shouldExtendToRightBasedOnOwnContents() {
    Pair<Integer, String> testee = Pair.of(42, "foo");
    Tuple3<Integer, String, String> actual = testee
        .extend((a, b) -> "" + a + b);
    assertThat(actual).isEqualTo(Tuple3.of(42, "foo", "42foo"));
  }

  @Test
  public void shouldMapValues() {
    assertThat(Pair.of(1, 2).map(i -> "A" + i, i -> "B" + i)).isEqualTo(Pair.of("A1", "B2"));
  }
  
  @Test
  public void shouldObeyHashcodeEqualsContract() {
    EqualsVerifier.forClass(Pair.class).verify();
  }

  @Test
  public void shouldNotBeEqualToUnrelatedClass() {
    assertThat(Pair.of(1, 2)).isNotEqualTo("");
  }
  
}
