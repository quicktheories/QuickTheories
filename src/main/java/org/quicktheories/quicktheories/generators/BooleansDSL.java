package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Gen;

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
  public Gen<Boolean> all() {
    return Generate.booleans();
  }
}
