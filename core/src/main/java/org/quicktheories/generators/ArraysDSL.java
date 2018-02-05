package org.quicktheories.generators;

import org.quicktheories.core.Gen;

/**
 * A Class for creating Sources of type T[], that will generate and shrink array
 * objects of either fixed or bounded size. If fixed size, an array will be
 * shrunk by reducing the individual items in tandem. If the array is of bounded
 * size, shrinking will begin by removing random items until the array is of
 * minimum size, at which point it will continue to shrink as if an array of
 * fixed length.
 * 
 * There is a possibility that the shrinker will get stuck at a local minima
 * when one value in the array shrinks to it's "smallest" value without
 * falsifying
 *
 */
public class ArraysDSL {

  /**
   * Creates an ArrayGeneratorBuilder of Integers that can be used to create an
   * array Source
   * 
   * @param source
   *          a Source of type Integer
   * @return an ArrayGeneratorBuilder of type Integer
   */
  public ArrayGeneratorBuilder<Integer> ofIntegers(
      Gen<Integer> source) {
    return new ArrayGeneratorBuilder<>(source, Integer.class);
  }

  /**
   * Creates an ArrayGeneratorBuilder of Characters that can be used to create
   * an array Source
   * 
   * @param source
   *          a Source of type Character
   * @return an ArrayGeneratorBuilder of type Character
   */
  public ArrayGeneratorBuilder<Character> ofCharacters(
      Gen<Character> source) {
    return new ArrayGeneratorBuilder<>(source, Character.class);
  }

  /**
   * Creates an ArrayGeneratorBuilder of Strings that can be used to create an
   * array Source
   * 
   * @param source
   *          a Source of type String
   * @return an ArrayGeneratorBuilder of type String
   */
  public ArrayGeneratorBuilder<String> ofStrings(
      Gen<String> source) {
    return new ArrayGeneratorBuilder<>(source, String.class);
  }

  /**
   * Creates an ArrayGeneratorBuilder of the given class that
   * can be used to create an array Source
   *
   * @param <T>
   *          type of value to generate
   * @param source
   *          a Source of type T
   * @param c
   *          a Class of type T
   * @return an ArrayGeneratorBuilder of type T
   */
  public <T> ArrayGeneratorBuilder<T> ofClass(Gen<T> source,
      Class<T> c) {
    return new ArrayGeneratorBuilder<>(source, c);
  }

  public static class ArrayGeneratorBuilder<T> {

    private final Gen<T> source;
    private final Class<T> c;

    ArrayGeneratorBuilder(Gen<T> source, Class<T> c) {
      this.source = source;
      this.c = c;
    }

    /**
     * Generates arrays of specified type T of fixed length
     * 
     * @param length
     *          - fixed length
     * @return a Source of type T[]
     */
    public Gen<T[]> withLength(int length) {
      return withLengthBetween(length, length);
    }
    
    public Gen<T[]> withLengths(Gen<Integer> lengths) {
      return Generate.arraysOf(source, c, lengths);
    }


    /**
     * Generates arrays of specified type T of length bounded inclusively
     * between minimumSize and maximumSize
     * 
     * @param minLength
     *          - the inclusive minimum size of the array
     * @param maxLength
     *          - the inclusive maximum size of the array
     * @return a Source of type T[]
     */
    public Gen<T[]> withLengthBetween(int minLength, int maxLength) {
      ArgumentAssertions.checkArguments(minLength <= maxLength,
          "The minLength (%s) is longer than the maxLength(%s)",
          minLength, maxLength);
      ArgumentAssertions.checkArguments(minLength >= 0,
          "The length of an array cannot be negative; %s is not an accepted argument",
          minLength);
      return withLengths(Generate.range(minLength, maxLength));
    }
  }

}
