package org.quicktheories.dogfood;

import java.util.function.Supplier;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

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
  public void doubleRangesAreWithinRange() {
    qt()
    .forAll(doubles().between(-4d, 66d))
    .check(d -> d <= 66d && d >= -4d);
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
  public void floatsBetweenZeroAndOneAreWithinBounds() {
    qt()
    .forAll(floats().fromZeroToOne())
    .check(f -> f <= 1f && f >= 0f);
  }
  
  @Test
  public void flaotsRangesAreWithinRange() {
    qt()
    .forAll(floats().between(-42f, 66f))
    .check(f -> f <= 66f && f >= -42f);
  }  
  
  @Test
  public void constantSuppliesAConstant() {
    Supplier<Integer> s = () -> 42;
    qt()
    .forAll(arbitrary().constant(s))
    .check(a -> a == 42);
  }
  
}
