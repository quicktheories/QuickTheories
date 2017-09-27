package org.quicktheories.core.stateful;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.stateful.Command;
import org.quicktheories.core.stateful.Parallel;


public class StatefulTest implements WithQuickTheories {

  @Test
  public void endStateIsInitialStateWhenNoCommands() {
    List<Command<AtomicInteger, Integer>> commands = new ArrayList<>();
    Stream<Integer> actual = Parallel.calculatePossibleEndStates(0, commands);
    assertThat(actual).containsExactly(0);
  }
  
  @Test
  public void endStateIsCommandStateWhenOneCommand() {
    List<Command<AtomicInteger, Integer>> commands  = Arrays.asList(Model.SET_42);
    Stream<Integer> actual = Parallel.calculatePossibleEndStates(0, commands);
    assertThat(actual).containsExactly(42);
  }
  
  @Test
  public void twoEndStatesWhenTwoIndependentActions() {
    List<Command<AtomicInteger, Integer>> commands  = Arrays.asList(Model.SET_42, Model.SET_0);
    Stream<Integer> actual = Parallel.calculatePossibleEndStates(0, commands);
    assertThat(actual).containsExactly(0, 42);
  }
  
  @Test
  public void eightDistinctEndStatesWhenTwoIndependentAndOneOrderDependentAction() {
    List<Command<AtomicInteger, Integer>> commands  = Arrays.asList(Model.SET_42, Model.SET_0, Model.TIMES_2, Model.PLUS_1);
    Stream<Integer> actual = Parallel.calculatePossibleEndStates(0, commands);
    assertThat(actual).containsOnly(0, 42, 84, 85, 86, 43, 1, 2);
  }
  
  enum Model implements Command<AtomicInteger, Integer> {
    SET_42 {
      @Override
      public void run(AtomicInteger sut) {
       sut.set(42);  
      }

      @Override
      public Integer nextState(Integer currentState) {
        return 42;
      }    
    },
    
    SET_0 {
      @Override
      public void run(AtomicInteger sut) {
       sut.set(0);  
      }

      @Override
      public Integer nextState(Integer currentState) {
        return 0;
      }    
    },
    
    TIMES_2 {
      @Override
      public void run(AtomicInteger sut) {
       sut.set(sut.get() * 2);  
      }

      @Override
      public Integer nextState(Integer currentState) {
        return currentState * 2;
      } 
    },
    
    PLUS_1 {
      @Override
      public void run(AtomicInteger sut) {
       sut.set(sut.get() + 1);  
      }

      @Override
      public Integer nextState(Integer currentState) {
        return currentState + 1;
      } 
    }    
  }
  
}
