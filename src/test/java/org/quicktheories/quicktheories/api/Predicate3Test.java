package org.quicktheories.quicktheories.api;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Predicate3;

import static org.assertj.core.api.Assertions.assertThat;

public class Predicate3Test {

  @Test
  public void shouldProduceLogicalAnd() {
    Predicate3<Integer, Integer, Integer> true2 = (a, b, c) -> true;
    Predicate3<Integer, Integer, Integer> false2 = (a, b, c) -> false;

    assertThat(true2.and(false2).test(1, 2, 3)).isFalse();
    assertThat(false2.and(true2).test(1, 2, 3)).isFalse();
    assertThat(true2.and(true2).test(1, 2, 3)).isTrue();
    assertThat(false2.and(false2).test(1, 2, 3)).isFalse();
  }

}
