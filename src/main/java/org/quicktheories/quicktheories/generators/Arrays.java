package org.quicktheories.quicktheories.generators;

import java.lang.reflect.Array;

import org.quicktheories.quicktheories.api.AsString;
import org.quicktheories.quicktheories.core.Source;

final class Arrays {

  static <T> Source<T[]> arraysOf(Source<T> values, Class<T> c,
      int length) {
    return arraysOf(values, c, length, length);
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
            a -> java.util.Arrays.asList(a)).describedAs(arrayDescriber());
  }
  
  private static <T> AsString<T[]> arrayDescriber() {
    return a -> java.util.Arrays.deepToString(a);
  }

}