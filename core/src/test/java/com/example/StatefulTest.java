package com.example;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;
import org.quicktheories.core.stateful.Command;
import org.quicktheories.core.stateful.Parallel;
import org.quicktheories.core.stateful.Sequential;
import org.quicktheories.generators.Generate;
import org.quicktheories.impl.stateful.StatefulTheory;

import static org.quicktheories.impl.stateful.StatefulTheory.*;


public class StatefulTest implements WithQuickTheories {
 
  @Test
  public void sequential() {
    
    Gen<List<Commands>> commandSequences = lists().of(arbitrary()
        .enumValuesWithNoOrder(Commands.class))
        .ofSizeBetween(1, 100);
    
    qt()
    .forAll(longs().between(0, 10), commandSequences)
    .checkAssert((initialState, commands) -> Sequential.modelCheck(initialState,commands, l -> new BuggyCounter(l), sut -> sut.get()));
  }

  @Test
  public void parallel() {
    
    int threads = 4;
    Parallel parallel = new Parallel(100, TimeUnit.MILLISECONDS);
    
    // use short command sequences only
    Gen<List<Commands>> commandSequences = lists().of(arbitrary()
        .enumValuesWithNoOrder(Commands.class))
        .ofSizeBetween(3, 8);
    
    qt()
    .withShrinkCycles(100)
    .withExamples(100)
    .forAll(longs().between(0, 10), commandSequences)
    .checkAssert((initialState, commands) -> parallel.parallelCheck(initialState,commands, l -> new NotThreadSafeCounter(l), sut -> sut.get(), threads));
  }

  @Test
  public void statefulModelSimple() {
    qt().withRegisteredProfiles(ExampleProfiles.class).withStatefulModel(CounterModel::new).checkStateful();
  }

  @Test
  public void statefulModelStepBased() {
    qt().withProfile(ExampleProfiles.class, "ci")
            .withMinStatefulSteps(1)
            .withMaxStatefulSteps(10)
            .stateful(StepBasedCounterModel::new);

  }

  public static class CounterModel extends StatefulTheory.WithHistory<Commands> {

    Long state = 0L;
    Counter counter = new BuggyCounter(0);

    @Override
    public Gen<Commands> steps() {
      return Generate.enumValues(Commands.class);
    }

    @Override
    public boolean performStep(Commands s) {
      System.out.println("Executing step: " + s);
      s.run(counter);
      state = s.nextState(state);
      return counter.get() == state;
    }

  }

  public static class StepBasedCounterModel extends StatefulTheory.StepBased {

    private Long state = 0L;
    private Counter counter = null;

    public void setup() {
      counter = new BuggyCounter(state);
    }

    public void inc(Long by) {
      for (int i = 0; i < by; i++) {
        counter.inc();
      }
      state += by;
    }

    public boolean counterIsCorrect() {
      return state == counter.get();
    }

    public Gen<Long> incrementBy()
    {
      return state == 0 ? Generate.longRange(3, 10) : Generate.longRange(0, state);
    }

    protected void initSteps() {
      addSetupStep(builder("setup", this::setup).build());
      addStep(builder("inc", this::inc, this::incrementBy)
              .postcondition(this::counterIsCorrect)
              .build());
    }

  }


enum Commands implements Command<Counter,Long> {
 
  INC() {
   
    @Override
    public void run(Counter sut) {
      sut.inc();
    }
    
    @Override
    public Long nextState(Long state) {
      return state + 1;
    }
  },
  
  DOUBLE() {
    @Override
    public void run(Counter sut) {
      sut.doubleIt();
    }
   
    @Override
    public Long nextState(Long state) {
      return state = state * 2;
    }
  },
  
  DEC() {
    @Override
    public  void run(Counter sut) {
      sut.dec();
    }

    @Override
    public Long nextState(Long state) {
      return state - 1;
    }    
    
  },  
  
  GET() {
    @Override
    public  void run(Counter sut) {
      sut.get();
    }
    
    @Override
    public Long nextState(Long state) {
      return state;
    }
  }, 
  
  
  RESET() {
    @Override
    public  void run(Counter sut) {
      sut.reset();
    }
    
    @Override
    public Long nextState(Long state) {
      return 0L;
    }
  },   

}


interface Counter {
  void dec();
  
  void doubleIt();
  
  void inc();
  
  long get();
  
  void reset();
}


static class NotThreadSafeCounter implements Counter {

  long n = 0;
  
  NotThreadSafeCounter(long start) {
    n = start;
  }
  
  public void dec() {
    n =  n - 1;
  }
  
  public void doubleIt() {
    n = n * 2;
  }
  
  public void inc() {
    n = n + 1;
  }
  
  public long get() {
    return n;
  }
  
  public void reset() {
    n = 0;
  }
}

static class BuggyCounter implements Counter {

  long n = 0;
  int count = 0;
  
  
  BuggyCounter(long start) {
    n = start;
  }
  
  public void dec() {
    n =  n - 1;
    count = count + 1;
  }
  
  public void doubleIt() {
    n = n * 2;
  }
  
  public void inc() {
    if (count != 3) {
     n = n + 1;
    }
    count = count + 1;
  }
  
  public long get() {
    return n;
  }
  
  public void reset() {
    n = 0;
  }
}

}