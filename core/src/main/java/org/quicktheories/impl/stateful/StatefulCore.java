package org.quicktheories.impl.stateful;

import org.quicktheories.core.Gen;
import org.quicktheories.core.RandomnessSource;
import org.quicktheories.core.Strategy;
import org.quicktheories.generators.Generate;
import org.quicktheories.impl.Constraint;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Drives a {@link StatefulTheory}
 */
public class StatefulCore<T> {

    private final RandomnessSource prng;
    private final StatefulTheory<T> theory;

    public static Gen<StatefulCore> generator(Supplier<StatefulTheory<?>> theory) {
        Gen<StatefulCore> gen = prng -> {
            // swallow a random value so that hashes of each StatefulCore differ
            // (hashes are based on the prng not the value)
            prng.next(Constraint.none());
            return new StatefulCore<>(prng, theory.get());
        };
        return gen.describedAs(c -> c.theory.formattedHistory());
    }


    private StatefulCore(RandomnessSource prng, StatefulTheory<T> theory) {
        this.prng = prng;
        this.theory = theory;
    }

    public boolean run(Supplier<Strategy> state) {
        theory.init();
        Iterator<Gen<T>> setupSteps = theory.setupSteps();
        while (setupSteps.hasNext()) {
            theory.executeStep(setupSteps.next().generate(prng));
        }

        Strategy strategy = state.get();
        int numSteps = Generate.longRange(strategy.minStatefulSteps(), strategy.maxStatefulSteps(), strategy.minStatefulSteps())
                               .map(Long::intValue)
                               .generate(prng);
        try {
            for (int i = 0; i < numSteps; i++) {
                if (!theory.executeStep(theory.steps().generate(prng))) {
                    return false;
                }
            }
        } finally {
            theory.teardown();
        }

        return true;
    }
}
