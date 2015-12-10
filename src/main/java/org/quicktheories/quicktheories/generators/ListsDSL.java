package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.Lists.listsOf;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating List Sources that will produce List objects of either
 * fixed or bounded size. If fixed size, a List will be shrunk by reducing the
 * individual items in tandem. If the List is of bounded size, it will begin
 * shrinking by removing random items until the List is of minimum size, at
 * which point it will continue to shrink as if a List of fixed size.
 * 
 * There is a possibility that the shrinker will get stuck at local minima when
 * one value in the List shrinks to it's "smallest" value without falsifying
 *
 */
public class ListsDSL {

  /**
   * Creates an appropriate Collector for a type of List by specifying the
   * Supplier used as a parameter
   * 
   * @param <T> type to generate
   * @param <A> list type
   * 
   * @param collectionFactory a supplier of A
   * @return a Collector
   */
  public <T, A extends List<T>> Collector<T, A, A> createListCollector(
      Supplier<A> collectionFactory) {
    return Lists.toList(collectionFactory);
  }

  /**
   * Creates a ListGeneratorBuilder.
   * 
   * @param <T> type to generate
   * 
   * @param source
   *          a Source of type T for the items in the list
   * @return a ListGeneratorBuilder of type T
   */
  public <T> ListGeneratorBuilder<T> allListsOf(
      Source<T> source) {
    return new ListGeneratorBuilder<T>(source);
  }

  /**
   * Creates a TypedListGeneratorBuilder. The Collector cannot be changed and is
   * set to collect ArrayLists.
   * 
   * @param <T> type to generate
   * 
   * @param source
   *          a Source of type T for the items in the List
   * @return a TypedListGeneratorBuilder of type T
   */
  public <T> TypedListGeneratorBuilder<T> arrayListsOf(
      Source<T> source) {
    return new TypedListGeneratorBuilder<T>(source,
        Lists.arrayListCollector());
  }

  /**
   * Creates a TypedListGeneratorBuilder. The Collector cannot be changed and is
   * set to collect LinkedLists.
   * 
   * @param <T> type to generate
   * 
   * @param source
   *          a Source of type T for the items in the List
   * @return a TypedListGeneratorBuilder of type T
   */
  public <T> TypedListGeneratorBuilder<T> linkedListsOf(
      Source<T> source) {
    return new TypedListGeneratorBuilder<T>(source,
        Lists.linkedListCollector());
  }

  /**
   * ListGeneratorBuilder enables the creation of Sources for Lists of fixed and
   * bounded size, where no Collector is specified. A ListGeneratorBuilder can
   * be used to create a TypedListGeneratorBuilder, where the Collector is
   * specified.
   * 
   * @param <T> type to generate
   */
  public static class ListGeneratorBuilder<T> {

    protected final Source<T> source;

    ListGeneratorBuilder(Source<T> source) {
      this.source = source;
    }

    /**
     * Generates a List of objects, where the size of the List is fixed
     * 
     * @param size size of lists to generate
     * @return a Source of Lists of type T
     */
    public Source<List<T>> ofSize(int size) {
      checkSizeNotNegative(size);
      return Lists.alternatingFixedListsOf(source, size);
    }

    /**
     * Generates a List of objects, where the size of the List is bounded by
     * minimumSize and maximumSize
     * 
     * @param minimumSize
     *          - inclusive minimum size of List
     * @param maximumSize
     *          - inclusive maximum size of List
     * @return a Source of Lists of type T
     */
    public Source<List<T>> ofSizeBetween(int minimumSize, int maximumSize) {
      checkBoundedListArguments(minimumSize, maximumSize);
      return Lists.alternatingBoundedListsOf(source, minimumSize,
          maximumSize);
    }

    /**
     * Determines how the Lists will be collected and returns an
     * TypedListGeneratorBuilder with the Collector specified
     * 
     * @param collector collector to use to contruct list
     * @return a TypedListGeneratorBuilder
     */
    public TypedListGeneratorBuilder<T> ofType(
        Collector<T, List<T>, List<T>> collector) {
      return new TypedListGeneratorBuilder<T>(source, collector);
    };
  }

  /**
   * TypedListGeneratorBuilder enables the creation of Sources for Lists of
   * fixed and bounded size, where the Collector is fixed.
   */
  public static class TypedListGeneratorBuilder<T> {

    private final Source<T> source;
    private final Collector<T, List<T>, List<T>> collector;

    TypedListGeneratorBuilder(Source<T> source,
        Collector<T, List<T>, List<T>> collector) {
      this.source = source;
      this.collector = collector;
    }

    /**
     * Generates a List of objects, where the size of the List is fixed
     * 
     * @param size size of lists to generate
     * @return a Source of Lists of type T
     */
    public Source<List<T>> ofSize(int size) {
      checkSizeNotNegative(size);
      return listsOf(source, collector, size);
    };

    /**
     * Generates a List of objects, where the size of the List is bounded by
     * minimumSize and maximumSize
     * 
     * @param minimumSize
     *          - inclusive minimum size of List
     * @param maximumSize
     *          - inclusive maximum size of List
     * @return a Source of Lists of type T
     */
    public Source<List<T>> ofSizeBetween(int minimumSize, int maximumSize) {
      checkBoundedListArguments(minimumSize, maximumSize);
      return listsOf(source, collector, minimumSize, maximumSize);
    };
  }

  private static void checkBoundedListArguments(int minimumSize,
      int maximumSize) {
    ArgumentAssertions.checkArguments(minimumSize <= maximumSize,
        "The minimumSize (%s) is longer than the maximumSize(%s)",
        minimumSize, maximumSize);
    ArgumentAssertions.checkArguments(minimumSize >= 0,
        "The size of a List cannot be negative; %s is not an accepted argument",
        minimumSize);
  }

  private static void checkSizeNotNegative(int size) {
    ArgumentAssertions.checkArguments(size >= 0,
        "The size of a List cannot be negative; %s is not an accepted argument",
        size);
  }

}
