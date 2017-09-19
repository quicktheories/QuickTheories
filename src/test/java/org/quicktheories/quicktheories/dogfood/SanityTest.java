package org.quicktheories.quicktheories.dogfood;

import java.util.function.Supplier;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;

public class SanityTest implements WithQuickTheories {

  @Test
  public void positiveDoublesArePositive() {
    qt()
    .forAll(doubles().positive())
    .check(d -> d >= 0d);
  }
  
  @Test
  public void negativeDoublesAreNegative() {
    qt()
    .forAll(doubles().negative()) 
    .check(d -> d <= 0d);
  }
  
  @Test
  public void positiveFloatsArePositive() {
    qt()
    .forAll(floats().positive())
    .check(d -> d >= 0d);
  }
  
  @Test
  public void negativeFloatsAreNegative() {
    qt()
    .forAll(floats().negative())
    .check(d -> d <= 0d);
  }
    
  @Test
  public void constantSuppliesAConstant() {
    Supplier<Integer> s = () -> 42;
    qt()
    .forAll(arbitrary().constant(s))
    .check(a -> a == 42);
  }
  
}
