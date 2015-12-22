package org.quicktheories.quicktheories.generators;

import java.util.Arrays;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating Boolean Sources that will produce either true or false
 * randomly and shrink to false
 */
public class BooleansDSL {

  /**
   * Generates either true or false randomly. Shrinks to false.
   * 
   * @return a Source of booleans
   */
  public Source<Boolean> all() {
    return Booleans.generate();
  }

  final static class Booleans {
    static Source<Boolean> generate() {
      return Arbitrary.pick(Arrays.asList(false, true));
    }
  }

}
