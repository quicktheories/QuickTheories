package org.quicktheories.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class WithQuickTheoriesTest {
  
  WithQuickTheories testee = new WithQuickTheories(){};

  @Test
  public void shouldProvideAQTInstance() {
    assertThat(testee.qt()).isNotNull();
  }

  @Test
  public void shouldProvideAnArbritraryDSLInstance() {
    assertThat(testee.arbitrary()).isNotNull();
  }
  
  @Test
  public void shouldProvideAnArraysDSLInstance() {
    assertThat(testee.arrays()).isNotNull();
  }  
  
  @Test
  public void shouldProvideABigDecimalsDSLInstance() {
    assertThat(testee.bigDecimals()).isNotNull();
  }  
  
  @Test
  public void shouldProvideABigIntegersDSLInstance() {
    assertThat(testee.bigIntegers()).isNotNull();
  }  
  
  @Test
  public void shouldProvideABooleansDSLInstance() {
    assertThat(testee.booleans()).isNotNull();
  }   
  
  @Test
  public void shouldProvideACharactersDSLInstance() {
    assertThat(testee.characters()).isNotNull();
  }  

  @Test
  public void shouldProvideADatesDSLInstance() {
    assertThat(testee.dates()).isNotNull();
  }  
  
  @Test
  public void shouldProvideADoublesDSLInstance() {
    assertThat(testee.doubles()).isNotNull();
  }   
  
  @Test
  public void shouldProvideAFloatsDSLInstance() {
    assertThat(testee.floats()).isNotNull();
  } 
  
  @Test
  public void shouldProvideAnIntegersDSLInstance() {
    assertThat(testee.integers()).isNotNull();
  }   
  
  @Test
  public void shouldProvideAnListsDSLInstance() {
    assertThat(testee.lists()).isNotNull();
  }  
  
  @Test
  public void shouldProvideAnLocalDatesDSLInstance() {
    assertThat(testee.localDates()).isNotNull();
  }   
  
  @Test
  public void shouldProvideALongsDSLInstance() {
    assertThat(testee.longs()).isNotNull();
  }   
  
  @Test
  public void shouldProvideAStringsDSLInstance() {
    assertThat(testee.strings()).isNotNull();
  } 
  
}
