package org.quicktheories.core;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Function3;
import org.quicktheories.impl.Constraint;

/**
 * (Psuedo)randomly generates instances of T
 *
 * @param <T> Type to generate
 */
public interface Gen<T> extends AsString<T>{

  /**
   * Generate instance of T. Implementation should take randomness from
   * the in parameter **only**
   * @param in source of randomness
   * @return a T
   */
  T generate(RandomnessSource in);

  /**
   * Maps generated values with supplied function consuming one value
   * @param <R> Type to map to
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> Gen<R> map(Function<? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in));
  }
  
  /**
   * Maps generated values with supplied Mod consuming one value
   * @param <R> Type to map to
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> Gen<R> mutate(Mod<? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in), in);
  }  
  
  /**
   * Maps generated values with supplied function consuming two values
   * @param <R> Type to map to   * 
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> Gen<R> map(BiFunction<? super T, ? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in), generate(in));
  }
  
  /**
   * Maps generated values with supplied function consuming three values
   * @param <R> Type to map to 
   * @param mapper function to map with 
   * @return A Gen of R
   */
  default <R> Gen<R> map(Function3<? super T, ? super T,  ? super T, ? extends R> mapper) {
    return in -> mapper.apply(generate(in), generate(in), generate(in));
  }

  /**
   * Limits values produced by this Gen to ones meeting supplied predicate
   * @param assumption assumption to meet
   * @return A Gen of T
   */
  default Gen<T> assuming(Predicate<T> assumption) {
    return new FilteredGenerator<>(this, assumption);
  }

  /**
   * Combines output of this Gen with another using mapping
   * @param <B> Type to combine with
   * @param <C> Type to generate
   * @param b A Gen of B
   * @param mapping function to use to combine values
   * @return A Gen of C
   */
  default <B,C> Gen<C> zip(Gen<B> b, BiFunction<T,B,C> mapping) {
    return in -> mapping.apply(generate(in), b.generate(in));
  }
  
  /**
   * Combines output of this Gen with two others using mapping
   * @param <B> Type to combine with
   * @param <C> Type to combine with
   * @param <D> Type to generate
   * @param b A Gen of B
   * @param c A Gen of C
   * @param mapping function to use to combine values
   * @return A Gen of D
   */
  default <B,C, D> Gen<D> zip(Gen<B> b, Gen<C> c, Function3<T,B,C, D> mapping) {
    return in -> mapping.apply(generate(in), b.generate(in), c.generate(in));
  }
  
  /**
   * Randomly combines output of this Gen with another with an roughly 50:50 weighting
   * @param rhs Gen to mix with
   * @return A Gen of T
   */
  default Gen<T> mix(Gen<T> rhs) {
    return prng -> {
      long picked = prng.next(Constraint.zeroToOne());
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
  
  /**
   * Produces a Gen which will be described using the supplied function.
   * @param asString function to use to convert to string
   * @return a Source of T
   */
  default Gen<T> describedAs(AsString<T> asString) {
    return new DescribingGenerator<>(this,asString);
  }
  
}

class FilteredGenerator<T> implements Gen<T> {
  
  private final Gen<T> child;
  private final Predicate<T> assumption;

  public FilteredGenerator(Gen<T> child, Predicate<T> assumption) {
    this.child = child;
    this.assumption = assumption;
  }

  @Override
  public T generate(RandomnessSource in) {
    // responsibility lies with the source to exit 
    // if we have too many attempts
    while(true) {
      DetatchedRandomnessSource detached = in.detach();
      T t = child.generate(detached);
      if (assumption.test(t)) {
        detached.commit();
        return t;
      } else {
        detached.registerFailedAssumption();
      }
    }  
  }
  
  public String asString(T t) {
    return child.asString(t);
  }
  
}



class DescribingGenerator<T> implements Gen<T> {
  
  private final Gen<T> child;
  private final AsString<T> toString;

  public DescribingGenerator(Gen<T> child, AsString<T> toString) {
    this.child = child;
    this.toString = toString;
  }

  @Override
  public T generate(RandomnessSource in) {
    return child.generate(in);
  }
  
  public String asString(T t) {
    return toString.asString(t);
  }
  
}
