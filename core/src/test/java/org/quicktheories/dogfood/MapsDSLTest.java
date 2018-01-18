package org.quicktheories.dogfood;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class MapsDSLTest implements WithQuickTheories {
  @Test
  public void fixedSizeMapsHaveFixedSize() {
    qt()
    .forAll(integers().between(0, 10))
    .withPrecursorGen(n -> maps().of(integers().all(), integers().all()).ofSize(n))
    .check((n, m) -> m.size() == n);
  }
  
  @Test
  public void boundedSizeMapsHaveBoundedSize() {
    qt()
    .forAll(integers().between(0, 10), integers().between(0, 10))
    .withPrecursorGen((min, extra) -> maps().of(integers().all(), integers().all()).ofSizeBetween(min, min + extra))
    .check((min, extra, m) -> m.size() >= min && m.size() <= min + extra);
  }
 
}
