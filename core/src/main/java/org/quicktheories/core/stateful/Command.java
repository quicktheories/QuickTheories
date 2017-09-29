package org.quicktheories.core.stateful;

public interface Command<SUT,MODEL> {
  /**
   * Execute a command
   * @param sut The system under test
   */
  void run(SUT sut);

  /**
   * Calculates the expected state after this command has run
   * @param currentState The current state
   * @return The next state
   */
  MODEL nextState(MODEL currentState);
  
  
}