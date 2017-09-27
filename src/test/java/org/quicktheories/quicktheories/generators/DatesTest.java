package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import java.util.Date;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.generators.DatesDSL.Dates;

public class DatesTest {

  @Test
  public void shrinksTowardsEpoch() {
    Gen<Date> testee = Dates.withMilliSeconds(70978);
    assertThatGenerator(testee).shrinksTowards(new Date(0));
  }

}
