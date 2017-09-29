package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import java.util.Date;

import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.DatesDSL.Dates;

public class DatesTest {

  @Test
  public void shrinksTowardsEpoch() {
    Gen<Date> testee = Dates.withMilliSeconds(70978);
    assertThatGenerator(testee).shrinksTowards(new Date(0));
  }

}
