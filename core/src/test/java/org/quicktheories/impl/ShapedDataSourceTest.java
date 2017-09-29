package org.quicktheories.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.Gen;
import org.quicktheories.core.PseudoRandom;
import org.quicktheories.generators.Generate;
import org.quicktheories.impl.ShapedDataSource;

public class ShapedDataSourceTest {

  private PseudoRandom r = Configuration.defaultPRNG(0);
  ShapedDataSource testee = new ShapedDataSource(r, new long[0], 100);
  
  @Test
  public void onlyCapturesPrecursorsThatPassCompoundAssumptions() {
    Gen<Pair<Long,Long>> joined = Generate.longRange(0l, 5l)
           .assuming(l -> l != 3)
        .map((a,b) -> Pair.of(a,b) )
        .assuming( p -> p._1 == 2)
        .assuming( p -> p._2 != 0);
    
    Pair<Long, Long> value = joined.generate(testee);
    
    assertThat(testee.capturedPrecursor().current()).containsExactly(value._1, value._2);
  }

}
