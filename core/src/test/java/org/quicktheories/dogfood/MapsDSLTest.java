package org.quicktheories.dogfood;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class MapsDSLTest implements WithQuickTheories {
  @Test
  public void fixedSizeMapsHaveFixedSize() {
    qt()
    .forAll(maps().of(integers().all(), integers().all()).ofSize(2))
    .check(m -> m.size() == 2);
  }
  
  @Test
  public void boundedSizeMapsHaveBoundedSize() {
    qt()
    .forAll(maps().of(integers().all(), integers().all()).ofSizeBetween(1, 2))
    .check(m -> m.size() >= 1 && m.size() <= 2);
  }
 
}
