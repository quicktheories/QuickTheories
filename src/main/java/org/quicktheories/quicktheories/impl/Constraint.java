package org.quicktheories.quicktheories.impl;

import java.util.OptionalLong;

public final class Constraint {
   
  private final static Constraint NONE = new Constraint(Long.MIN_VALUE, Long.MAX_VALUE, OptionalLong.of(0));
  private final static Constraint ZERO_TO_ONE = new Constraint(0, 1, OptionalLong.of(0));  
  
  private final long min;
  private final OptionalLong shrinkTarget;
  private final long max;
     
  private Constraint(long min, long max, OptionalLong shrinkTarget) {
    this.min = min;
    this.max = max;
    this.shrinkTarget = correct(min, max, shrinkTarget);
  }

  public static Constraint between(long min, long max) {
    if (min == 0 && max == 1) {
      return zeroToOne();
    }
    return new Constraint(min, max, correct(min, max, OptionalLong.of(0)));
  }
  
  public static Constraint none() {
    return NONE;
  }
  
  public static Constraint zeroToOne() {
    return ZERO_TO_ONE;
  }
  
  public Constraint withNoShrinkPoint() {
    return new Constraint(min, max, OptionalLong.empty());
  }
  
  public Constraint withShrinkPoint(long shrinkPoint) {
    OptionalLong newShrinkPoint = OptionalLong.of(shrinkPoint);
    if (!newShrinkPoint.equals(shrinkTarget)) {
      return new Constraint(min, max, newShrinkPoint);
    } else {
      return this;
    }
  }
  
  long min() {
    return min;
  }
  
  long max() {
    return max;
  }
  
  OptionalLong shrinkTarget() {    
    return shrinkTarget;
  }

  static private OptionalLong correct(long min, long max, OptionalLong target) {
    if (target.isPresent()) {
      return OptionalLong.of(correctShrinkTarget(min, max, target.getAsLong()));
    }
    return OptionalLong.empty();
  }
  
  static private long correctShrinkTarget(long min, long max, long target) {
    if (target > max) {
      return max;
    }
    
    if (target < min) {
      return min;
    }
    
    return target;
  }
  
  public boolean allowed(long l) {
    return l <= max && l >= min;
  }
  
}
