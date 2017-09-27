package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

public class StringsDSLTest implements WithQuickTheories {

  @Test
  public void boundedLengthStringsRespectsLengthBounds() {
    Gen<String> testee = strings().allPossible().ofLengthBetween(3, 200);
    qt()
    .withExamples(100000)
    .forAll(testee)
    .check( s -> s.length() <= 200 && s.length() >= 3);
  }
  
  @Test
  public void boundedLengthStringsProducesDistinctValues() {
    Gen<String> testee = strings().allPossible().ofLengthBetween(0, 100);
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }
  
  @Test
  public void fixedLengthStringsAreFixedLength() {
    qt()
    .forAll(strings().allPossible().ofLength(100))
    .check(s -> s.length() == 100);
  }
    
}
