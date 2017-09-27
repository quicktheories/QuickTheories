package com.example;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import static org.assertj.core.api.Assertions.assertThat;

public class Haystack implements WithQuickTheories {

  @Test
  public void canFindNeedle() {
    
    int falisifed = 0;
    final int run = 300;
    for (int n = 0; n != run; n++) {

      try {
    qt()
    .forAll(integers().all(), integers().all(), integers().all())
    .check((i,j,k) -> findMe(i,j,k));
      } catch (AssertionError ex) {
        falisifed = falisifed+ 1;
      }
   }
    
    assertThat(falisifed).isGreaterThan(160);
    
    System.out.println("Falsified " + falisifed + " out of " + run);
  }
  
  @Test
  public void canFindOtherNeedle() {
    
    int falisifed = 0;
    final int run = 300;
    for (int n = 0; n != run; n++) {
    System.out.println("-------------");
      try {
    qt()
    .forAll(integers().all(), integers().all())
    .check((i,j) -> findMeToo(i,j));
      } catch (AssertionError ex) {
        falisifed = falisifed+ 1;
      }
   }
    
    System.out.println("Falsified " + falisifed + " out of " + run);
  }

  
  private boolean findMe(int i, int j, int k) {
    if ( i > 10000 && i < 6000000) {
      if ( j < 2000 ) {
        if ( k > 100000000 && k < 1000000000 ) {
          return false;
        }
      }
    }
    return true;
  }
  
  private boolean findMeToo(int i, int j) {
    if ( j > 10000 & i > 100 && i < 20000) {
      return false;
    }
    return true;
  }
  
}


