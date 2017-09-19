package org.quicktheories.quicktheories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.OptionalLong;

import org.junit.Test;

public class ConstraintTest {

  @Test
  public void usesSingletonForZeroToOne() {
    assertSame(Constraint.between(0, 1), Constraint.zeroToOne());
  }
  
  @Test
  public void setShrinkPointWhenWithinRange() {
    Constraint testee = Constraint.between(1, 10).withShrinkPoint(5);
    assertThat(testee.shrinkTarget()).isEqualTo(OptionalLong.of(5));
  }
  
  @Test
  public void correctsShrinkTargetWhenAboveMax() {
    Constraint testee = Constraint.between(1, 10).withShrinkPoint(11);
    assertThat(testee.shrinkTarget()).isEqualTo(OptionalLong.of(10));
  }
  
  @Test
  public void correctsShrinkTargetWhenBelowMin() {
    Constraint testee = Constraint.between(1, 10).withShrinkPoint(0);
    assertThat(testee.shrinkTarget()).isEqualTo(OptionalLong.of(1));
  }
  
  @Test
  public void doesNotCreateNewObjectWhenShrinkTargetDoesNotChange() {
    Constraint testee = Constraint.between(0, 10).withShrinkPoint(5);
    assertSame(testee,testee.withShrinkPoint(5));
  }
  
  @Test
  public void noConstraintAllowsAnyLong() {
    assertThat(Constraint.none().min()).isEqualTo(Long.MIN_VALUE);
    assertThat(Constraint.none().max()).isEqualTo(Long.MAX_VALUE);    
  }

  @Test
  public void shrinkTargetDefaultsToZero() {
    assertThat(Constraint.between(0, 10).shrinkTarget()).isEqualTo(OptionalLong.of(0));
  }
  
  @Test
  public void shrinkTargetCanBeUnset() {
    assertThat(Constraint.between(0, 10).withNoShrinkPoint().shrinkTarget())
    .isEqualTo(OptionalLong.empty());
  }
    
  
}
