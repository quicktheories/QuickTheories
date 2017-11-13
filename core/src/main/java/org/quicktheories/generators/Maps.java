package org.quicktheories.generators;

import org.quicktheories.api.AsString;
import org.quicktheories.core.Gen;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Maps {
  static <K, V> Gen<Map<K, V>> boundedMapsOf(Gen<K> kg, Gen<V> vg,
      Gen<Integer> sizes) {
    return mapsOf(kg, vg, defaultMap(), sizes);
  }

  public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> defaultMap() {
    return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  static <K, V> Gen<Map<K, V>> mapsOf(Gen<K> kg, Gen<V> vg,
      Collector<Map.Entry<K, V>, ?, Map<K, V>> collector, Gen<Integer> sizes) {
    Gen<Map<K, V>> gen = prng -> {
      int size = sizes.generate(prng);
      return Stream.generate(() -> kg.generate(prng))
          .distinct()
          .map(k -> mapEntry(k, vg.generate(prng)))
          .limit(size)
          .collect(collector);

    };
    return gen.describedAs(mapDescriber(kg::asString, vg::asString));
  }

  private static <K, V> AsString<Map<K, V>> mapDescriber(Function<K, String> kd,
      Function<V, String> vd) {
    return list -> list.entrySet().stream().map(
        e -> "(" + kd.apply(e.getKey()) + "," + vd.apply(e.getValue()) + ")")
        .collect(Collectors.joining(", ", "[", "]"));
  }

  static <K, V> Map.Entry<K, V> mapEntry(K k, V v) {
    return Collections.singletonMap(k, v).entrySet().iterator().next();
  }
}
