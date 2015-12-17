package org.quicktheories.quicktheories.api;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

public final class Tuple3<A, B, C> {
  public final A _1;
  public final B _2;
  public final C _3;

  private Tuple3(A _1, B _2, C _3) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
  }

  /**
   * Creates a tuple 3 from the three supplied values
   *
   * @param <A> first type
   * @param <B> second type
   * @param <C> third type
   * 
   * @param a
   *          first supplied value
   * @param b
   *          second supplied value
   * @param c
   *          third supplied value
   * @return a tuple 3
   */
  public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
    return new Tuple3<>(a, b, c);
  }

  /**
   * Creates a tuple 4 by prepending the supplied value
   * 
   * @param <T> type to prepend with 
   * 
   * @param t
   *          value to prepend
   * @return a tuple 4
   */
  public <T> Tuple4<T, A, B, C> prepend(T t) {
    return Tuple4.of(t, _1, _2, _3);
  }

  /**
   * Creates a tuple 4 by applying the supplied function to this tuple's
   * contents
   * 
   * @param <T> type to extend with 
   * 
   * @param mapping
   *          function to apply
   * @return a tuple 4
   */
  public <T> Tuple4<A, B, C, T> extend(Function3<A, B, C, T> mapping) {
    return Tuple4.of(_1, _2, _3, mapping.apply(_1, _2, _3));
  }
  
  public <A1,B1,C1> Tuple3<A1, B1, C1> map(Function<A,A1> fa, Function<B,B1> fb, Function<C,C1> fc ) {
    return Tuple3.of(fa.apply(_1), fb.apply(_2), fc.apply(_3));
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(", ", "{", "}");
    return sj
        .add("" + _1)
        .add("" + _2)
        .add("" + _3)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3);
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
    Tuple3 other = (Tuple3) obj;

    return Objects.equals(_1, other._1) &&
        Objects.equals(_2, other._2) &&
        Objects.equals(_3, other._3);
  }



}
