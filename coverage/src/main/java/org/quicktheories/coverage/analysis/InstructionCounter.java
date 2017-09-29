package org.quicktheories.coverage.analysis;

public interface InstructionCounter {

  void increment();

  int currentInstructionCount();

}
