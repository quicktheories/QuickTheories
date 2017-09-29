package org.quicktheories.core;

import java.util.Collection;

import org.quicktheories.impl.Precursor;

/**
 * Provides hints for values to visit during search.
 */
public interface Guidance {

  void newExample(Precursor precursor);

  void exampleExecuted();

  Collection<long[]> suggestValues(int i, Precursor t);

  void exampleComplete();

}
