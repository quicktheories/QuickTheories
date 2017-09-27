package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import java.math.BigDecimal;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.generators.BigDecimalsDSL.BigDecimals;

public class BigDecimalsTest {

  @Test
  public void shouldShrinkTowardsZeroWhenScale5() {
    Gen<BigDecimal> testee = BigDecimals.randomWithScale(14, 5);
    assertThatGenerator(testee).shrinksTowards(new BigDecimal("0.00000"));
  }

  @Test
  public void shouldShrinkTowardsZeroWhenScale3() {
    Gen<BigDecimal> testee = BigDecimals.randomWithScale(14, 3);
    assertThatGenerator(testee).shrinksTowards(new BigDecimal("0.000"));
  }

  @Test
  public void shouldShrinkTowardsZeroWhenScale4() {
    Gen<BigDecimal> testee = BigDecimals.randomWithScale(22, 4);
    assertThatGenerator(testee).shrinksTowards(new BigDecimal("0.0000"));
  }

  @Test
  public void shouldShrinkTowardsZeroWhenScale0() {
    Gen<BigDecimal> testee = BigDecimals.randomWithScale(22, 0);
    assertThatGenerator(testee).shrinksTowards(new BigDecimal("0"));
  }
  
}
