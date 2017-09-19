package org.quicktheories.quicktheories.core.stateful;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Parallel {
  
  private final TimeUnit unit;
  private final int timeout;
  
  public Parallel(int timeout, TimeUnit unit) {
    this.unit = unit;
    this.timeout = timeout;
  }


  /**
   * Checks a stateful SUT (system under test) against a model in parallel.
   * 
   * Parallelisation is not used to improve performance - multiple threads are 
   * used to flush out concurrency issues.
   * 
   * Supplied commands will first be run in sequence and compared against the model,
   * then run concurrently. All possible valid end states of the system will be
   * calculated, then the actual end state compared to this.
   * 
   * As the number of possible end states increases rapidly with the number of commands,
   * command lists should usually be constrained to 10 or less.
   * 
   * The model class *must* correctly implement both equals and hashcode.
   * 
   * @param initialState Initial state of the system
   * @param commands Commands to be executed
   * @param modelToSut Mapping from model to system in that state.
   * @param sutToModel Mapping from sut to model representation
   * @param threads Number of threads to use
   */
  public <S, M> void parallelCheck(M initialState,
      List<? extends Command<S, M>> commands, Function<M, S> toSut,
      Function<S, M> readState, int threads) {
    Sequential.modelCheck(initialState, commands, toSut, readState);
    S sut = toSut.apply(initialState);

    Set<M> validEndStates = calculatePossibleEndStates(initialState, commands).collect(Collectors.toSet());

    ExecutorService executor = Executors.newFixedThreadPool(threads);
    Stream<Runnable> rs = commands.stream()
        .map(command -> (Runnable) () -> command.run(sut));
    List<Future<?>> futures = rs.map(r -> executor.submit(r))
        .collect(Collectors.toList());

    waitForCompletion(futures);
    executor.shutdown();

    M finalState = readState.apply(sut);
    if (!validEndStates.contains(finalState)) {
      throw new AssertionError("Final state " + finalState + " not valid.\n Allowable states :- " 
    + validEndStates.stream().map(s -> s.toString()).collect(Collectors.joining(", ") ));
    }
  }

  private void waitForCompletion(List<Future<?>> futures) {
    for (Future<?> each : futures) {
      try {
        each.get(timeout, unit);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        throw new RuntimeException("Error executing step", e);
      }
    }
  }

  static <S, M, R> Stream<M> calculatePossibleEndStates(M initial,
      List<? extends Command<S, M>> commands) {
    return permutations(commands).map(s -> endState(initial, s));
  }

  private static <S, M> M endState(M initial,
      Stream<? extends Command<S, M>> commands) {
    M state = initial;
    for (Command<S, M> each : commands.collect(Collectors.toList())) {
      state = each.nextState(state);
    }
    return state;
  }

  private static <T> Stream<Stream<T>> permutations(final List<T> items) {
    return IntStream.range(0, factorial(items.size()))
        .mapToObj(i -> permutation(i, items).stream());
  }

  private static int factorial(final int num) {
    return IntStream.rangeClosed(2, num).reduce(1, (x, y) -> x * y);
  }

  private static <T> List<T> permutation(final int count,
      final LinkedList<T> input, final List<T> output) {
    if (input.isEmpty()) {
      return output;
    }

    final int factorial = factorial(input.size() - 1);
    output.add(input.remove(count / factorial));
    return permutation(count % factorial, input, output);
  }

  private static <T> List<T> permutation(final int count, final List<T> items) {
    return permutation(count, new LinkedList<>(items), new ArrayList<>());
  }

}
