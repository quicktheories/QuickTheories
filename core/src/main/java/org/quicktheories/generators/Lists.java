package org.quicktheories.generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.quicktheories.api.AsString;
import org.quicktheories.core.Gen;

public final class Lists {

  
  static <T> Gen<List<T>> listsOf(
      Gen<T> generator, Gen<Integer> sizes) {
    return
        arrayListOf(generator, sizes).mix(
        listsOf(generator, linkedList(), sizes));
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

  /**
   * Create a {@link Gen} using {@link ArrayList}.
   *
   * Why use this function rather than {@link #arrayList()} or {@link #listsOf(Gen, Gen)}? Mostly because of array
   * pre-allocation.  Since the size is known before the values are generated, can pre-allocate the array so array
   * copying does not become a bottleneck; useful for theories which depend on large lists.
   */
  public static <T> Gen<List<T>> arrayListOf(Gen<T> values, Gen<Integer> sizes) {
    Gen<List<T>> gen = prng -> {
      int size = sizes.generate(prng);
      ArrayList<T> list = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        list.add(values.generate(prng));
      }
      return list;

    };
    return gen.describedAs(listDescriber(values::asString));
  }

  static <T> Gen<List<T>> listsOf(
      Gen<T> values, Collector<T, List<T>, List<T>> collector, Gen<Integer> sizes) {
   
    Gen<List<T>> gen = prng -> {
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
