package org.quicktheories.quicktheories.core;

import java.util.List;

/**
 * Interface by which falsification of properties is reported
 */
public interface Reporter {

  /**
   * Report falsification of a theory by a throwable
   * 
   * @param seed
   *          seed value of PseudoRandom used to generate objects for testing
   * @param count
   *          number of examples generated before falsification
   * @param smallest
   *          smallest falsifying object
   * @param cause
   *          throwable that caused the theory to falsify
   * @param examples
   *          other falsifying examples, if they exist
   */
  void falisification(long seed, int count, Object smallest, Throwable cause,
      List<Object> examples);

  /**
   * Report falsification of a theory
   * 
   * @param seed
   *          seed value of PseudoRandom used to generate objects for testing
   * @param count
   *          number of examples generated before falsification
   * @param smallest
   *          smallest falsifying object
   * @param examples
   *          other falsifying examples, if they exist
   */
  void falisification(long seed, int count, Object smallest,
      List<Object> examples);

  /**
   * Reports the number of examples generated, which is less than the expected
   * number specified
   * 
   * @param completedExamples
   *          number of examples generated
   */
  void valuesExhausted(int completedExamples);

}
