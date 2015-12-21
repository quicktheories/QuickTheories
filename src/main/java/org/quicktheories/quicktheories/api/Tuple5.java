package org.quicktheories.quicktheories.api;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

public final class Tuple5<A, B, C, D, E> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;
  public final E _5;

  private Tuple5(A _1, B _2, C _3, D _4, E _5) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
  }

  /**
   * Creates a tuple 5 from the five supplied values
   * 
   * @param <A> first type
   * @param <B> second type
   * @param <C> third type
   * @param <D> fourth type 
   * @param <E> fifth type
   * 
   * @param a
   *          first supplied value
   * @param b
   *          second supplied value
   * @param c
   *          third supplied value
   * @param d
   *          fourth supplied value
   * @param e
   *          fifth supplied value
   * @return a tuple 5
   */
  public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A a, B b, C c, D d,
      E e) {
    return new Tuple5<>(a, b, c, d, e);
  }

  public <A1, B1, C1, D1, E1> Tuple5<A1, B1, C1, D1, E1> map(Function<A, A1> fa,
      Function<B, B1> fb, Function<C, C1> fc, Function<D, D1> fd, Function<E, E1> fe) {
    return Tuple5.of(fa.apply(_1), fb.apply(_2), fc.apply(_3), fd.apply(_4), fe.apply(_5));
  } 
  
  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(", ", "{", "}");
    return sj
        .add("" + _1)
        .add("" + _2)
        .add("" + _3)
        .add("" + _4)
        .add("" + _5)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4, _5);
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
    Tuple5 other = (Tuple5) obj;

    return Objects.equals(_1, other._1) &&
        Objects.equals(_2, other._2) &&
        Objects.equals(_3, other._3) &&
        Objects.equals(_4, other._4) &&
        Objects.equals(_5, other._5);
  }

}
