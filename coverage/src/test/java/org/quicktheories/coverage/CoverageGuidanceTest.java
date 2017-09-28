package org.quicktheories.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Guidance;
import org.quicktheories.core.NoGuidance;
import org.quicktheories.core.PseudoRandom;

import com.example.HayStack;

public class CoverageGuidanceTest implements WithQuickTheories {

  @Test
  public void exploresBranchesMoreEfficientlyThanWithoutCoverage() {

    final int run = 300;
    
    Function<PseudoRandom, Guidance> noGuidance = prng -> new NoGuidance();
    long falsifiedWithoutGuidance = IntStream.range(0, 300).filter(i -> randomlySearchUsingGuidance(noGuidance)).count();
    
    Function<PseudoRandom, Guidance> coverageGuidance = prng -> new CoverageGuidance(prng);
    long falsifiedWithGuidance = IntStream.range(0, 300).filter(i -> randomlySearchUsingGuidance(coverageGuidance)).count();

    // Four fold improvement is an arbitrary limit chosen as current implementation
    // reliably exceeds it. Increase this if algorithms improves
    assertThat(falsifiedWithGuidance).isGreaterThan(falsifiedWithoutGuidance * 4);

    System.out.println("No guidance falisified " + falsifiedWithoutGuidance + " out of " + run);
    System.out.println("Guidance falisified  " + falsifiedWithGuidance + " out of " + run);    
  }
  
  private boolean randomlySearchUsingGuidance(Function<PseudoRandom, Guidance> g) {
    try {
      qt()
      .withGuidance(g)
      .withExamples(1000)
      .forAll(integers().all(), integers().all(), integers().all())
      .check((i, j, k) -> HayStack.findMe(i, j, k));
    } catch (AssertionError ex) {
      return true;
    }
    return false;
  }

}
