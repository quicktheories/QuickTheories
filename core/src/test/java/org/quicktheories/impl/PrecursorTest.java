package org.quicktheories.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.impl.Constraint;
import org.quicktheories.impl.Precursor;

public class PrecursorTest {

  Precursor testee = new Precursor();
  
  @Test
  public void capturesConsumedLongs() {
    testee.store(3l, Constraint.none());
    testee.store(6l, Constraint.none());   
    testee.store(9l, Constraint.none());
    testee.store(12l, Constraint.none());
    
    assertThat(testee.current()).containsExactly(3l, 6l, 9l, 12l); 
  }
  
  @Test
  public void capturesConsumedLongsWhenBufferMustBeResized() {
    for (long l = 0; l != 128; l++) {
      testee.store(l, Constraint.none());
    }

    assertThat(testee.current()[0]).isEqualTo(0);
    assertThat(testee.current()[127]).isEqualTo(127);    
  }  
  
  @Test
  public void producesEmptyByteArrayWhenNoValues() {
    assertThat(testee.bytes()).isEmpty();
  }
  
  @Test
  public void produces8ByteArrayWhenContainsSingleLong() {
    testee.store(42l, Constraint.none());
    assertThat(testee.bytes()).hasSize(8);
  }
  
  @Test
  public void producesCorrectlySizedByteAndLongArraysWhenDirectlyAddedAboveInitialBufferSize() {   
    addLongs(testee, 8);
    
    assertThat(testee.bytes()).hasSize(64);
    assertThat(testee.current()).containsExactly(0l, 1l, 2l, 3l, 4l, 5l, 6l, 7l);
  }

  @Test
  public void producesCorrectlySizedByteAndLongArraysWhenCombining() {
    Precursor other = new Precursor();
    addLongs(other, 8);
    
    testee.store(42l,  Constraint.none());
    
    testee.combine(other);
    
    assertThat(testee.bytes()).hasSize(72);
    assertThat(testee.current()).containsExactly(42l, 0l, 1l, 2l, 3l, 4l, 5l, 6l, 7l);
  }
  
  
  private void addLongs(Precursor p, int n) {
    for (int i = 0; i != n; i++) {
      p.store(i, Constraint.none());
    }
    
  }
  
}
