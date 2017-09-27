package org.quicktheories.generators;

import java.util.function.Predicate;

import org.quicktheories.core.DetatchedRandomnessSource;
import org.quicktheories.core.Gen;
import org.quicktheories.core.RandomnessSource;

public final class CodePoints {
 
  private static final int FIRST_NON_WHITESPACE_CHARACTER_IN_BLC = 0x0021;

  public static Gen<Integer> codePoints(int startInclusive,
      int endInclusive) {
    return codePoints(startInclusive, endInclusive,
        FIRST_NON_WHITESPACE_CHARACTER_IN_BLC);

  }

  public static Gen<Integer> codePoints(int startInclusive, int endInclusive,
      int idealTarget) {
    
    ArgumentAssertions.checkArguments(startInclusive >= Character.MIN_CODE_POINT,
        "(%s) is less than the minimum codepoint (%s)",
        startInclusive, Character.MIN_CODE_POINT);
    
    ArgumentAssertions.checkArguments(endInclusive <= Character.MAX_CODE_POINT,
        "%s is greater than the maximum codepoint (%s)",
        endInclusive, Character.MAX_CODE_POINT);
    
      return new Retry<>(Generate.range(startInclusive, endInclusive, idealTarget), Character::isDefined);
    }

}

class Retry<T> implements Gen<T> {
  
  private final Gen<T> child;
  private final Predicate<T> assumption;

  Retry(Gen<T> child, Predicate<T> assumption) {
    this.child = child;
    this.assumption = assumption;
  }

  @Override
  public T generate(RandomnessSource in) {
    // danger of infinite loop here but space is densely populated enough for
    // this to be unlikely
    while(true) {
      DetatchedRandomnessSource detatched = in.detach();
      T t = child.generate(detatched);
      if (assumption.test(t)) {
        detatched.commit();
        return t;
      } 
    }  
  }
  
  public String asString(T t) {
    return child.asString(t);
  }
  
}
