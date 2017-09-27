package org.quicktheories.core.stateful;

import java.util.List;
import java.util.function.Function;

public class Sequential {
  
  /**
   * Checks a stateful SUT (system under test) against a model.
   * 
   * Supplied commands will be run in sequence and compared against the model.
   * 
   * The model class *must* correctly implement both equals and hashcode.
   * 
   * @param initialState Initial state of the system
   * @param commands Commands to be executed
   * @param modelToSut Mapping from model to system in that state.
   * @param sutToModel Mapping from sut to model representation
   */
  public static <S, M> void modelCheck(M initialState,
      List<? extends Command<S, M>> commands, Function<M, S> modelToSut,
      Function<S, M> sutToModel) {
    M state = initialState;
    S sut = modelToSut.apply(initialState);
    int completed = 0;
    for (Command<S, M> each : commands) {
      each.run(sut);
      state = each.nextState(state);
      M realState = sutToModel.apply(sut);
      if (!realState.equals(state)) {
        throw new AssertionError("Expected " + realState + " to be " + state + " after " + each + "\n Ran " + completed + " steps before before failure.");
      }
      completed = completed + 1;
    }
  }

}