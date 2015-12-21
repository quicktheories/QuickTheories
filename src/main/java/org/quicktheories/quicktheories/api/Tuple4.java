package org.quicktheories.quicktheories.api;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

public final class Tuple4<A, B, C, D> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;

  private Tuple4(A _1, B _2, C _3, D _4) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
  }

  /**
   * Creates a tuple 4 from the four supplied values
   *
   * @param <A> first type
   * @param <B> second type
   * @param <C> third type
   * @param <D> fourth type 
   * 
   * @param a
   *          first supplied value
   * @param b
   *          second supplied value
   * @param c
   *          third supplied value
   * @param d
   *          fourth supplied value
   * @return a tuple 4
   */
  public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
    return new Tuple4<>(a, b, c, d);
  }

  /**
   * Creates a tuple 5 by prepending the supplied value
   * 
   * @param <T> type of value to prepend
   * 
   * @param t
   *          value to prepend
   * @return a tuple 5
   */
  public <T> Tuple5<T, A, B, C, D> prepend(T t) {
    return Tuple5.of(t, _1, _2, _3, _4);
  }

  /**
   * Creates a tuple 5 by applying the supplied function to this tuple's
   * contents
   * 
   * @param <T> type of value to extend with
   * 
   * @param mapping
   *          function to apply
   * @return a tuple 5
   */
  public <T> Tuple5<A, B, C, D, T> extend(Function4<A, B, C, D, T> mapping) {
    return Tuple5.of(_1, _2, _3, _4, mapping.apply(_1, _2, _3, _4));
  }
  
  public <A1, B1, C1, D1> Tuple4<A1, B1, C1, D1> map(Function<A, A1> fa,
      Function<B, B1> fb, Function<C, C1> fc, Function<D, D1> fd) {
    return Tuple4.of(fa.apply(_1), fb.apply(_2), fc.apply(_3), fd.apply(_4));
  }  

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(", ", "{", "}");
    return sj
        .add("" + _1)
        .add("" + _2)
        .add("" + _3)
        .add("" + _4)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4);
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
    Tuple4 other = (Tuple4) obj;

    return Objects.equals(_1, other._1) &&
        Objects.equals(_2, other._2) &&
        Objects.equals(_3, other._3) &&
        Objects.equals(_4, other._4);
  }

}
