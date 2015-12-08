package org.quicktheories.quicktheories.generators;

import java.lang.reflect.Array;

import org.quicktheories.quicktheories.core.Source;

final class Arrays {

  @SuppressWarnings("unchecked")
  static <T> Source<T[]> arraysOf(Source<T> values, Class<T> c,
      int length) {
    return Lists.listsOf(values, Lists.arrayListCollector(), length).as(
        l -> l.toArray((T[]) Array.newInstance(c, length)),
        a -> java.util.Arrays.asList(a));
  }

  @SuppressWarnings("unchecked")
  static <T> Source<T[]> arraysOf(Source<T> values, Class<T> c,
      int minLength, int maxLength) {
    return Lists
        .listsOf(values, Lists.arrayListCollector(), minLength, maxLength)
        .as(l -> l.toArray((T[]) Array.newInstance(c, 0)), // will generate
                                                           // correct size if
                                                           // zero is less than
                                                           // the length of the
                                                           // array
            a -> java.util.Arrays.asList(a));
  }

}