package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.generators.Lists.arrayList;
import static org.quicktheories.generators.Lists.listsOf;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.quicktheories.core.Gen;

public class ListsTest {

  @SuppressWarnings("unchecked")
  @Test
  public void shouldGenerateAllPossibleLinkedListsWithinSizeRange() {
    Gen<List<Integer>> testee = listsOf(
        Generate.range(1, 1), Lists.linkedList(), Generate.range(1, 4));
    assertThatGenerator(testee).generatesAllOf(
        new LinkedList<>(Arrays.asList(1)),
        new LinkedList<>(Arrays.asList(1, 1)),
        new LinkedList<>(Arrays.asList(1, 1, 1)),
        new LinkedList<>(Arrays.asList(1, 1, 1, 1)));
    assertThatGenerator(testee).doesNotGenerate(
        new LinkedList<>(Arrays.asList(1, 1, 1, 1, 1)),
        new LinkedList<>(Arrays.asList(1, 1, 1, 1, 1, 1)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldGenerateAllPossibleArrayListsWithinSizeRange() {
    Gen<List<Integer>> testee = listsOf(
        Generate.range(1, 1), arrayList(), Generate.range(1, 4));
    assertThatGenerator(testee).generatesAllOf(Arrays.asList(1),
        Arrays.asList(1, 1), Arrays.asList(1, 1, 1), Arrays.asList(1, 1, 1, 1));
    assertThatGenerator(testee).doesNotGenerate(Arrays.asList(1, 1, 1, 1, 1),
        Arrays.asList(1, 1, 1, 1, 1, 1));
  }

  @Test
  public void shouldGenerateBothTypesOfList() {
    Gen<List<Integer>> testee = listsOf(
        Generate.range(1, 1), Generate.constant(5));
    assertThatGenerator(testee).generatesAllOf(Arrays.asList(1, 1, 1, 1, 1),
        new LinkedList<>(Arrays.asList(1, 1, 1, 1, 1)));
  }

  @Test
  public void shouldDescribeListContentsUsingProvidedSource() {
    Gen<String> sourceWithCustomDescription = Generate.constant("x").describedAs(x -> "custom description for x");
    Gen<List<String>> testee = listsOf(sourceWithCustomDescription, arrayList(), Generate.range(2, 3));
    List<String> aList = new ArrayList<>(Arrays.asList("x","x"));
    assertThat(testee.asString(aList)).isEqualTo("[custom description for x, custom description for x]");
  }

}
