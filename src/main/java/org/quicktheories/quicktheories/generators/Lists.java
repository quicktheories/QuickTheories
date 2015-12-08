package org.quicktheories.quicktheories.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;

final class Lists {

  static <T> Source<List<T>> alternatingBoundedListsOf(
      Source<T> generator, int minimumSize,
      int maximumSize) {
    return Compositions.interleave(
        listsOf(generator, arrayListCollector(), minimumSize, maximumSize),
        listsOf(generator, linkedListCollector(), minimumSize, maximumSize));
  }

  static <T> Source<List<T>> alternatingFixedListsOf(
      Source<T> generator, int fixedSize) {
    return Compositions.interleave(
        listsOf(generator, arrayListCollector(), fixedSize),
        listsOf(generator, linkedListCollector(), fixedSize));
  }

  static <T, A extends List<T>> Collector<T, List<T>, List<T>> arrayListCollector() {
    return toList(ArrayList::new);
  }

  static <T, A extends List<T>> Collector<T, List<T>, List<T>> linkedListCollector() {
    return toList(LinkedList::new);
  }

  static <T, A extends List<T>> Collector<T, A, A> toList(
      Supplier<A> collectionFactory) {
    return Collector.of(collectionFactory, List::add, (left, right) -> {
      left.addAll(right);
      return left;
    });
  }

  static <T> Source<List<T>> listsOf(Source<T> generator,
      Collector<T, List<T>, List<T>> collector, int fixedSize) {
    return Source.of(
        (prng, step) -> Collections.nCopies(fixedSize, prng).stream()
            .map(p -> generator.next(p, step))
            .collect(collector))
        .withShrinker(
            shrinkFixedList(generator, collector));
  }

  static Shrink<List<Integer>> swapBetweenShrinkMethodsForBoundedIntegerLists(
      Source<Integer> generator,
      Collector<Integer, List<Integer>, List<Integer>> collector,
      int minimumSize) {
    return mostlyRandomShrinkWithIntegerLists(
        shrinkBoundedList(generator, collector, minimumSize),
        shrinkFixedList(generator, collector));
  }

  static <T> Source<List<T>> listsOf(Source<T> generator,
      Collector<T, List<T>, List<T>> collector, int minimumSize,
      int maximumSize) {
    return Source.of((prng, step) -> Collections
        .nCopies((int) prng.generateRandomLongWithinInterval(minimumSize,
            maximumSize), prng)
        .stream()
        .map(p -> generator.next(p, step))
        .collect(collector)).withShrinker(
            shrinkBoundedList(generator, collector, minimumSize));
  }

  private static <T> Shrink<List<T>> shrinkFixedList(Source<T> generator,
      Collector<T, List<T>, List<T>> collector) {
    return (original, context) -> shrinkFixedSizedStream(generator, collector,
        original, context);
  }

  private static <T> Shrink<List<T>> shrinkBoundedList(Source<T> generator,
      Collector<T, List<T>, List<T>> collector, int minimumSize) {
    return (original, context) -> {
      if (original.size() > minimumSize) {
        List<T> toShrink = original.stream().collect(collector);
        return shrinkBoundedStream(toShrink, context, minimumSize);
      }
      return shrinkFixedSizedStream(generator, collector, original, context);
    };
  }

  private static <T> Stream<List<T>> shrinkFixedSizedStream(
      Source<T> generator, Collector<T, List<T>, List<T>> collector,
      List<T> original, ShrinkContext context) {
    if (original.isEmpty()) {
      return Stream.empty();
    }
    return Stream.iterate(
        shrunkenFixedList(generator, collector, original, context),
        l -> shrunkenFixedList(generator, collector, l, context));
  }

  private static <T> Stream<List<T>> shrinkBoundedStream(List<T> toShrink,
      ShrinkContext context, int minimumSize) {
    int limit = toShrink.size() - minimumSize;
    removeRandomCell(toShrink, context);
    return Stream.iterate(toShrink, l -> {
      removeRandomCell(l, context);
      return l;
    }).limit(limit);
  }

  private static <T> void removeRandomCell(List<T> list,
      ShrinkContext context) {
    int randomCell = context.prng().nextInt(0, list.size() - 1);
    list.remove(randomCell);
  }

  private static <T> List<T> shrunkenFixedList(Source<T> generator,
      Collector<T, List<T>, List<T>> collector, List<T> original,
      ShrinkContext context) {
    return original.stream().map(t -> shrinkItemInList(generator, t, context))
        .collect(collector);
  }

  private static <T> T shrinkItemInList(Source<T> g, T original,
      ShrinkContext context) {
    return g.shrink(original, context)
        .findFirst()
        .orElseGet(() -> original);
  }

  private static Shrink<List<Integer>> mostlyRandomShrinkWithIntegerLists(
      Shrink<List<Integer>> lhs,
      Shrink<List<Integer>> rhs) {
    return chooseShrinkIntegerList(c -> c % 2 == 0, lhs, rhs);
  }

  private static Shrink<List<Integer>> chooseShrinkIntegerList(
      Predicate<Integer> useLHS,
      Shrink<List<Integer>> lhs, Shrink<List<Integer>> rhs) {
    return ((original, context) -> {
      if (useLHS.test(context.prng().nextInt(0, 1)) || original.get(0) == 0) {
        // 2 for odd and even
        // Accounts for case of [0,negative,...] in BigInteger, as shrinks by
        // shortening the list
        return lhs.shrink(original, context);
      }
      return rhs.shrink(original, context);
    });
  }

}
