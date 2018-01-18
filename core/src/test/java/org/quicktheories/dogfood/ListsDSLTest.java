package org.quicktheories.dogfood;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class ListsDSLTest implements WithQuickTheories {
  
  @Test
  public void fixedSizeListsHaveFixedSize() {
    qt()
    .forAll(integers().between(0, 10))
    .withPrecursorGen(n -> lists().of(integers().all()).ofSize(n))
    .check((n, l) -> l.size() == n);
  }
  
  @Test
  public void boundedSizeListsHaveBoundedSize() {
    qt()
    .forAll(integers().between(0, 10), integers().between(0, 10))
    .withPrecursorGen((min, extra) -> lists().of(integers().all()).ofSizeBetween(min, min + extra))
    .check((min, extra, l) -> l.size() >= min && l.size() <= min + extra);
  }
  
  @Test
  public void listsAreOfSuppliedType() {
    Supplier<List<Integer>> collectionFactory = () -> new LinkedList<>();
    qt()
    .forAll(lists().of(integers().all()).ofType(lists()
        .createListCollector(collectionFactory)).ofSize(2))
    .check(l -> l instanceof LinkedList);
  }

}
