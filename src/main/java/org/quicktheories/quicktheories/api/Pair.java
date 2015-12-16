package org.quicktheories.quicktheories.api;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Pair<A, B> {
  public final A _1;
  public final B _2;

  private Pair(A _1, B _2) {
    super();
    this._1 = _1;
    this._2 = _2;
  }

  /**
   * Creates a pair from the two supplied values
   * 
   * @param <A> first type
   * @param <B> second type
   * @param a
   *          first supplied value
   * @param b
   *          second supplied value
   * @return a pair
   */
  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair<>(a, b);
  }

  /**
   * Creates a tuple 3 by prepending the supplied value
   *
   * @param <T> type to prepend 
   * @param t
   *          value to prepend
   * @return a tuple 3
   */
  public <T> Tuple3<T, A, B> prepend(T t) {
    return Tuple3.of(t, _1, _2);
  }

  /**
   * Creates a tuple 3 by applying the supplied function to this tuple's
   * contents
   * 
   * @param <T> type to extend with 
   * @param mapping
   *          function to apply
   * @return a tuple 3
   */
  public <T> Tuple3<A, B, T> extend(BiFunction<A, B, T> mapping) {
    return Tuple3.of(_1, _2, mapping.apply(_1, _2));
  }
  
  public <A1,B1> Pair<A1, B1> map(Function<A,A1> fa, Function<B,B1> fb) {
    return Pair.of(fa.apply(_1), fb.apply(_2));
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(", ", "{", "}");
    return sj
        .add("" + _1)
        .add("" + _2)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    Pair other = (Pair) obj;
    return Objects.equals(_1, other._1) &&
        Objects.equals(_2, other._2);
  }

}
