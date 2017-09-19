package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import java.math.BigInteger;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.generators.BigIntegersDSL.BigIntegers;

public class BigIntegersTest  {

  @Test
  public void shouldShrinkTowardsZero() {
    Gen<BigInteger> testee = BigIntegers.random(5);
    assertThatGenerator(testee).shrinksTowards(new BigInteger("0"));
  }
 
}