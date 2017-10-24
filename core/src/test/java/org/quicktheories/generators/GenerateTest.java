package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.core.Gen;

public class GenerateTest {

  @Test
  public void oneOfGeneratesFromSingleSuppliedGen() { 
    Gen<Integer> testee = Generate.oneOf(Generate.constant(1));
    assertThatGenerator(testee).generatesAllOf(1);
  }
  
  @Test
  public void oneOfGeneratesFromAllSuppliedGens() { 
    int samples = 6;
    Gen<Integer> testee = Generate.oneOf(Generate.constant(1), Generate.constant(2), Generate.constant(3));
    
    assertThatGenerator(testee).generatesAllOfWithNSamples(samples, 1,2,3);
  }

}
