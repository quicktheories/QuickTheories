package org.quicktheories.quicktheories.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Predicate4;

public class Predicate4Test {

  @Test
  public void shouldProduceLogicalAnd() {
    Predicate4<Integer, Integer, Integer, Integer> true2 = (a, b, c, d) -> true;
    Predicate4<Integer, Integer, Integer, Integer> false2 = (a, b, c,
        d) -> false;

    assertThat(true2.and(false2).test(1, 2, 3, 4)).isFalse();
    assertThat(false2.and(true2).test(1, 2, 3, 4)).isFalse();
    assertThat(true2.and(true2).test(1, 2, 3, 4)).isTrue();
    assertThat(false2.and(false2).test(1, 2, 3, 4)).isFalse();
  }

}
