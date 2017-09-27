package org.quicktheories.coverage.codeassist.samples;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * (Psuedo)randomly generates instances of T
 *
 * @param <T> Type to generate
 */
public interface LambdasInDefaultMethods<T> {

  /**
   * Generate instance of T. Implementation should take randomness from
   * the in parameter **only**
   * @param in source of randomness
   * @return a T
   */
  T generate(RandomnessSource in);

  /**
   * Maps generated values with supplied function consuming one value
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> LambdasInDefaultMethods<R> map(Function<? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in));
  }
  
  /**
   * Maps generated values with supplied function consuming two values
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> LambdasInDefaultMethods<R> map(BiFunction<? super T, ? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in), generate(in));
  }
  
  /**
   * Maps generated values with supplied function consuming three values
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> LambdasInDefaultMethods<R> map(Function3<? super T, ? super T,  ? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in), generate(in), generate(in));
  }

  
  /**
   * Combines output of this Gen with another using mapping
   * @param b A Gen of B
   * @param mapping function to use to combine values
   * @return A Gen of C
   */
  default <B,C> LambdasInDefaultMethods<C> zip(LambdasInDefaultMethods<B> b, BiFunction<T,B,C> mapping) {
    return in -> mapping.apply(generate(in), b.generate(in));
  }
  
  /**
   * Combines output of this Gen with two others using mapping
   * @param b A Gen of B
   * @param c A Gen of C
   * @param mapping function to use to combine values
   * @return A Gen of D
   */
  default <B,C, D> LambdasInDefaultMethods<D> zip(LambdasInDefaultMethods<B> b, LambdasInDefaultMethods<C> c, Function3<T,B,C, D> mapping) {
    return in -> mapping.apply(generate(in), b.generate(in), c.generate(in));
  }
  
  /**
   * Randomly combines output of this Gen with another with an roughly 50:50 weighting
   * @param rhs Gen to mix with
   * @return A Gen of T
   */
  default LambdasInDefaultMethods<T> mix(LambdasInDefaultMethods<T> rhs) {
    return prng -> {
      long picked = prng.next(0);
      if (picked == 0) {
        return this.generate(prng);
      }
      return rhs.generate(prng);
    };
  }  
    
  /**
   * Produces a string representation of T
   */
  default String asString(T t) {
    if ( t == null) {
      return "null";
    }
    return t.toString();
  }
  
 
}


interface Function3<A, B, C, D> {

  D apply(A generate, B generate2, C generate3);
  
}

interface RandomnessSource {

  long next(int i);
  
}


