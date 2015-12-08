package org.quicktheories.quicktheories.impl;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.generate;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;

class Util {

  static <A, B, C> Stream<C> zip(Stream<A> streamA, Stream<B> streamB,
      BiFunction<A, B, C> zipper) {
    final Iterator<A> iteratorA = streamA.iterator();
    final Iterator<B> iteratorB = streamB.iterator();
    final Iterator<C> iteratorC = new Iterator<C>() {
      @Override
      public boolean hasNext() {
        return iteratorA.hasNext() && iteratorB.hasNext();
      }

      @Override
      public C next() {
        return zipper.apply(iteratorA.next(), iteratorB.next());
      }
    };
    final boolean parallel = streamA.isParallel() || streamB.isParallel();
    return iteratorToFiniteStream(iteratorC, parallel);
  }

  static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator,
      boolean parallel) {
    final Iterable<T> iterable = () -> iterator;
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }

  static <T> Stream<T> equaliseShrinkLength(Shrink<T> shrinker,
      Supplier<T> supplier,
      ShrinkContext context) {
    return concat(
        shrinker.shrink(supplier.get(), context), generate(supplier))
            .limit(context.remainingCycles());
  }

}
