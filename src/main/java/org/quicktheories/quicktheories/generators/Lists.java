package org.quicktheories.quicktheories.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.AsString;
import org.quicktheories.quicktheories.core.Gen;

public final class Lists {

  static <T> Gen<List<T>> boundedListsOf(
      Gen<T> generator, int minimumSize,
      int maximumSize) {
    return 
        listsOf(generator, arrayList(), minimumSize, maximumSize).mix(
        listsOf(generator, linkedList(), minimumSize, maximumSize));
  }

  public static <T, A extends List<T>> Collector<T, List<T>, List<T>> arrayList() {
    return toList(ArrayList::new);
  }

  public static <T, A extends List<T>> Collector<T, List<T>, List<T>> linkedList() {
    return toList(LinkedList::new);
  }

  public static <T, A extends List<T>> Collector<T, A, A> toList(
      Supplier<A> collectionFactory) {
    return Collector.of(collectionFactory, List::add, (left, right) -> {
      left.addAll(right);
      return left;
    });
  }

  static <T> Gen<List<T>> listsOf(Gen<T> values,
      Collector<T, List<T>, List<T>> collector, int minimumSize,
      int maximumSize) {
    Gen<List<T>> gen = prng -> {
      Gen<Integer> sizes = Generate.range(minimumSize, maximumSize);
      int size = sizes.generate(prng);
      return Stream.generate( () -> values.generate(prng))
          .limit(size)
          .collect(collector);

    };
    return gen.describedAs(listDescriber(values::asString));
    
  }

  private static <T> AsString<List<T>> listDescriber(Function<T, String> valueDescriber) {
    return list -> list.stream().map(valueDescriber).collect(Collectors.joining(", ", "[", "]"));
  }

}
