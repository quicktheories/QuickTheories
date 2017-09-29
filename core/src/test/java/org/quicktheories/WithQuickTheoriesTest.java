package org.quicktheories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.quicktheories.core.Guidance;

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
  
  @Test
  public void usesSuppliedGuidance() {
    Guidance g = Mockito.mock(Guidance.class);
    testee.qt()
      .withGuidance( prng -> g)
      .withExamples(10)
      .forAll(testee.integers().all())
      .check(i -> true);
    
    Mockito.verify(g, times(10)).exampleExecuted();
  }

  
}
