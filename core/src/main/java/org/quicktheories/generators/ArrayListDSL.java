package org.quicktheories.generators;

import org.quicktheories.core.Gen;

import java.util.List;

/**
 * A Class for creating ArrayList Sources that will produce List objects of either
 * fixed or bounded size.
 *
 */
public class ArrayListDSL {
    /**
     * Creates a ListGeneratorBuilder.
     *
     * @param <T>
     *          type to generate
     *
     * @param source
     *          a Source of type T for the items in the list
     * @return a ListGeneratorBuilder of type T
     */
    public <T> ListGeneratorBuilder<T> of(
            Gen<T> source) {
        return new ListGeneratorBuilder<>(source);
    }

    /**
     * ListGeneratorBuilder enables the creation of Sources for Lists of fixed and
     * bounded size.
     *
     * @param <T>
     *          type to generate
     */
    public static class ListGeneratorBuilder<T> {

        protected final Gen<T> source;

        ListGeneratorBuilder(Gen<T> source) {
            this.source = source;
        }

        /**
         * Generates a List of objects, where the size of the List is fixed
         *
         * @param size
         *          size of lists to generate
         * @return a Source of Lists of type T
         */
        public Gen<List<T>> ofSize(int size) {
            return ofSizeBetween(size, size);
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
        public Gen<List<T>> ofSizeBetween(int minimumSize, int maximumSize) {
            checkBoundedListArguments(minimumSize, maximumSize);
            return ofSizes(Generate.range(minimumSize,
                    maximumSize));
        }


        public Gen<List<T>> ofSizes(Gen<Integer> sizes) {
            return Lists.arrayListOf(source, sizes);
        }

    }

    private static void checkBoundedListArguments(int minimumSize,
                                                  int maximumSize) {
        //TODO fork from ListsDSL, should this be shared but packaged private?
        ArgumentAssertions.checkArguments(minimumSize <= maximumSize,
                "The minimumSize (%s) is longer than the maximumSize(%s)",
                minimumSize, maximumSize);
        checkSizeNotNegative(minimumSize);
    }

    private static void checkSizeNotNegative(int size) {
        //TODO fork from ListsDSL, should this be shared but packaged private?
        ArgumentAssertions.checkArguments(size >= 0,
                "The size of a List cannot be negative; %s is not an accepted argument",
                size);
    }
}
