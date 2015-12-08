package org.quicktheories.quicktheories.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Predicate5;

public class Predicate5Test {

  @Test
  public void shouldProduceLogicalAnd() {
    Predicate5<Integer, Integer, Integer, Integer, Integer> true2 = (a, b, c, d,
        e) -> true;
    Predicate5<Integer, Integer, Integer, Integer, Integer> false2 = (a, b, c,
        d, e) -> false;

    assertThat(true2.and(false2).test(1, 2, 3, 4, 5)).isFalse();
    assertThat(false2.and(true2).test(1, 2, 3, 4, 5)).isFalse();
    assertThat(true2.and(true2).test(1, 2, 3, 4, 5)).isTrue();
    assertThat(false2.and(false2).test(1, 2, 3, 4, 5)).isFalse();
  }

}
