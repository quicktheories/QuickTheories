package org.quicktheories.dogfood;

import java.util.Date;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class ArraysDSLTest implements WithQuickTheories {

  @Test
  public void fixedLengthIntegerArraysAreFixedLength() {
    qt()
    .forAll(arrays().ofIntegers(integers().all()).withLength(10))
    .check( is -> is.length == 10);
  }
  
  @Test
  public void boundedLengthIntegerArraysHaveBoundedLengths() {
    qt()
    .forAll(arrays().ofIntegers(integers().all()).withLengthBetween(1, 9))
    .check( is -> is.length >= 1 && is.length <= 9);
  }  
  
  @Test
  public void fixedLengthCharacterArraysAreFixedLength() {
    qt()
    .forAll(arrays().ofCharacters(characters().ascii()).withLength(5))
    .check( is -> is.length == 5);
  }

  @Test
  public void boundedLengthCharacterArraysHaveBoundedLength() {
    qt()
    .forAll(arrays().ofCharacters(characters().ascii()).withLengthBetween(1,5))
    .check( is -> is.length >= 1 && is.length <= 5);
  }  
  
  @Test
  public void fixedLengthClassArraysHaveFixedLength() {
    qt()
    .forAll(arrays().ofClass(dates().withMilliseconds(100), Date.class).withLength(3))
    .check( is -> is.length  == 3);
  }    
  
  @Test
  public void boundedLengthClassArraysHaveBoundedLength() {
    qt()
    .forAll(arrays().ofClass(dates().withMilliseconds(100), Date.class).withLengthBetween(1, 10))
    .check( is -> is.length  >= 1 && is.length <= 10);
  } 
  
  @Test
  public void fixedLengthStringArraysHaveFixedLength() {
    qt()
    .forAll(arrays().ofStrings(strings().allPossible().ofLength(2)).withLength(3))
    .check( is -> is.length  == 3);
  }    
  
  @Test
  public void boundedLengthStringArraysHaveBoundedLength() {
    qt()
    .forAll(arrays().ofStrings(strings().allPossible().ofLength(2)).withLengthBetween(1, 10))
    .check( is -> is.length  >= 1 && is.length <= 10);
  }   
}
