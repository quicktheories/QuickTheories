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
    .forAll(lists().of(integers().all()).ofSize(2))
    .check( l -> l.size() == 2);
  }
  
  @Test
  public void boundedSizeListsHaveBoundedSize() {
    qt()
    .forAll(lists().of(integers().all()).ofSizeBetween(1, 2))
    .check( l -> l.size() >= 1 && l.size() <= 2);
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
