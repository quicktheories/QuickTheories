package org.quicktheories.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quicktheories.core.Guidance;
import org.quicktheories.core.PseudoRandom;
import org.quicktheories.impl.Precursor;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.ByteBuddyAgent.ProcessProvider;
import sun.quicktheories.coverage.CodeCoverageStore;

public class CoverageGuidance implements Guidance {
  private static final int UNGUIDED_EXECUTIONS = 3;
    
  static {
    Installer in = new Installer(new ClassloaderByteArraySource(Thread.currentThread().getContextClassLoader()));
    ByteBuddyAgent.attach(in.createJar(), ProcessProvider.ForCurrentVm.INSTANCE);
  }

  private final PseudoRandom prng; 
  private final Set<Long> visitedBranches = new HashSet<>();
  
  private Collection<Long> currentHits;
  
  CoverageGuidance(PseudoRandom prng) {
    this.prng = prng;
  }

  @Override
  public void newExample(Precursor newExample) {
    CodeCoverageStore.reset(); 
  }

  @Override
  public void exampleExecuted() {
    currentHits = CodeCoverageStore.getHits();  
  }

  @Override
  public Collection<long[]> suggestValues(int execution, Precursor precursor) {
    if (execution <= UNGUIDED_EXECUTIONS) {
      return Collections.emptyList();
    }

    if (!visitedBranches.containsAll(currentHits)) {
      List<long[]> nearBy = new ArrayList<>();
      for (int i = 0; i != 20; i++) {
        nearBy.add(valueNear(precursor));
      }
      return nearBy;
    }
    return Collections.emptyList();
  }

  @Override
  public void exampleComplete() {
    visitedBranches.addAll(currentHits);
    currentHits = null;
  }

  private <T> long[] valueNear(Precursor t) {   
    long[] ls= t.current();
    int index = prng.nextInt(0, ls.length -1);
    ls[index] = prng.nextLong(t.min(index), t.max(index));
    return ls;
  }

}
