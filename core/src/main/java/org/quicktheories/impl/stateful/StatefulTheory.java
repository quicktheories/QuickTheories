package org.quicktheories.impl.stateful;

import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Generate;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Main interface and implementation for Stateful QuickTheories tests.
 *
 * See {@link WithHistory} and {@link StepBased} for more information and examples.
 */
public interface StatefulTheory<T> {

    default Iterator<Gen<T>> setupSteps() {
        return Collections.emptyIterator();
    }

    Gen<T> steps();

    boolean executeStep(T s);

    default void init() {}

    default void teardown() {}

    default List<T> history() {
        return new ArrayList<>();
    }

    default String formattedHistory() {
        int i = 0;
        StringBuilder builder = new StringBuilder();

        for (T step : history()) {
            builder.append(String.format("%sS%d = %s",
                                         i == 0 ? "" : "\n", i + 1, step.toString()));
            i++;
        }

        return builder.toString();
    }

    abstract class WithHistory<T> implements StatefulTheory<T> {
        private final List<T> history = new ArrayList<T>();

        public abstract boolean performStep(T s);

        public boolean executeStep(T s) {
            history.add(s);
            return performStep(s);
        }

        @Override
        public List<T> history() {
            return Collections.unmodifiableList(history);
        }
    }

    /**
     * Step Based module allows to build complex models by sharing state between steps.
     *
     * This state can be used for validation as well as for generating input for future steps.
     * For example, if we were to model a hashmap, step validating removals can information
     * about already inserted keys.
     */
    abstract class StepBased extends WithHistory<Step> {
        private final List<Gen<Step>> setupSteps = new ArrayList<>();
        private final List<Pair<Integer, Gen<Step>>> steps = new ArrayList<>();

        /**
         * Returns setup steps added during {@link StepBased#initSteps()}.
         */
        public Iterator<Gen<Step>> setupSteps() {
            return setupSteps.iterator();
        }

        public Gen<Step> steps() {
            return Generate.frequencyWithNoShrinkPoint(steps).assuming(Objects::nonNull);
        }

        public boolean performStep(Step s) {
            try {
                s.run();
            } catch (Throwable t) {
                return false; // failure handled by StatefulCore
            }

            return s.postConditionValid();
        }

        /**
         * Add setup step for this theory.
         *
         * Setup steps are executed _before_ regular steps are executed, _in the order they were added_.
         * Each setup step is executed _only once_.
         *
         * State can be shared between all steps (setup and regular ones).
         */
        protected void addSetupStep(Gen<Step> step) {
            setupSteps.add(step);
        }

        /**
         * Add step for this theory.
         *
         * Step is an execution and validation unit for theory. State is available in step
         * runnable as well as in its generator suppliers, pre- and post-conditions.
         */
        protected void addStep(Gen<Step> step) {
            addStep(1, step);
        }

        /**
         * Add <i>weighted</i> step for this theory.
         *
         * Same as {@link StepBased#addStep(Gen)}, but allowing to assign weight for each step.
         * Weight is assigned proportionally (i.e. when adding two steps with weights 50 and 100,
         * former one will be executed half as often as latter one, on average).
         *
         * See {@link Generate#frequencyWithNoShrinkPoint(List)}.
         */
        protected void addStep(int weight, Gen<Step> step) {
            steps.add(Pair.of(weight, step));
        }

        public void init() {
            initSteps();
        }

        /**
         * Add steps so this step-based theory by calling {@link StepBased#addStep(Gen)} to add
         * regular steps and {@link StepBased#addSetupStep(Gen)} to add setup steps.
         */
        protected abstract void initSteps();

    }

    class Step {
        protected final String desc;
        protected final String argString;
        protected final Supplier<Boolean> precondition;
        protected final Supplier<Boolean> postcondition;
        protected final Runnable action;

        public Step(String desc,
                    String argString,
                    Runnable action,
                    Supplier<Boolean> precondition,
                    Supplier<Boolean> postcondition) {
            this.desc = desc;
            this.argString = argString;
            this.precondition = precondition;
            this.postcondition = postcondition;
            this.action = action;
        }

        public void run() {
            action.run();
        }

        public boolean postConditionValid() {
            return postcondition == null ? true : postcondition.get();
        }


        @Override
        public String toString() {
            return desc + "(" + argString() + ")";
        }

        public String argString() {
            return argString;
        }
    }

    abstract class StepBuilder {
        protected final String desc;
        protected Supplier<Boolean> precondition;
        protected Supplier<Boolean> postcondition;

        public StepBuilder(String desc) {
            this.desc = desc;
        }

        protected boolean allowed() {
            return precondition == null || precondition.get();
        }

        /**
         * Precondition that has to be fulfilled in order for step to run.
         *
         * If supplier returns {@code false}, step will be skipped and its
         * postcondition will not be run even if it is supplied.
         */
        public StepBuilder precondition(Supplier<Boolean> precondition) {
            this.precondition = precondition;
            return this;
        }

        public StepBuilder postcondition(Supplier<Boolean> postcondition) {
            this.postcondition = postcondition;
            return this;
        }

        public abstract Gen<Step> build();
    }

    public static StepBuilder builder(String desc, Runnable action) {
        return new StepBuilder(desc) {
            @Override
            public Gen<Step> build() {
                return (in) -> {
                    if (!allowed())
                        return null;

                    return new Step(desc, "", action, precondition, postcondition);
                };
            }
        };
    }

    public static <T1> StepBuilder builder(String desc, Consumer<T1> action, Gen<T1> g1) {
        return builder(desc, action, () -> g1);
    }

    public static <T1> StepBuilder builder(String desc, Consumer<T1> action, Supplier<Gen<T1>> s1) {
        return new StepBuilder(desc) {
            @Override
            public Gen<Step> build() {
                return (in) -> {
                    if (!allowed())
                        return null;

                    Gen<T1> g1 = s1.get();
                    T1 arg1 = g1.generate(in);
                    return new Step(desc, g1.asString(arg1), () -> action.accept(arg1), precondition, postcondition);
                };
            }
        };
    }

    public static <T1, T2> StepBuilder builder(String desc,
                                               BiConsumer<T1, T2> action,
                                               Gen<T1> g1,
                                               Gen<T2> g2) {
        return builder(desc, action, () -> g1, () -> g2);
    }

    public static <T1, T2> StepBuilder builder(String desc,
                                               BiConsumer<T1, T2> action,
                                               Supplier<Gen<T1>> s1,
                                               Supplier<Gen<T2>> s2) {
        return new StepBuilder(desc) {
            @Override
            public Gen<Step> build() {
                return (in) -> {
                    if (!allowed())
                        return null;

                    Gen<T1> g1 = s1.get();
                    Gen<T2> g2 = s2.get();
                    T1 arg1 = g1.generate(in);
                    T2 arg2 = g2.generate(in);
                    return new Step(desc, StatefulTheory.buildArgString(g1.asString(arg1), g2.asString(arg2)),
                                    () -> action.accept(arg1, arg2), precondition, postcondition);
                };
            }
        };
    }

    public interface TriConsumer<T1, T2, T3> {
        public void accept(T1 t1, T2 t2, T3 t3);
    }

    public static <T1, T2, T3> StepBuilder builder(String desc,
                                                   TriConsumer<T1, T2, T3> action,
                                                   Gen<T1> g1,
                                                   Gen<T2> g2,
                                                   Gen<T3> g3) {
        return builder(desc, action, () -> g1, () -> g2, () -> g3);
    }

    public static <T1, T2, T3> StepBuilder builder(String desc,
                                                   TriConsumer<T1, T2, T3> action,
                                                   Supplier<Gen<T1>> s1,
                                                   Supplier<Gen<T2>> s2,
                                                   Supplier<Gen<T3>> s3) {
        return new StepBuilder(desc) {
            @Override
            public Gen<Step> build() {
                return (in) -> {
                    if (!allowed())
                        return null;

                    Gen<T1> g1 = s1.get();
                    Gen<T2> g2 = s2.get();
                    Gen<T3> g3 = s3.get();
                    T1 arg1 = g1.generate(in);
                    T2 arg2 = g2.generate(in);
                    T3 arg3 = g3.generate(in);
                    return new Step(desc,
                                    StatefulTheory.buildArgString(g1.asString(arg1), g2.asString(arg2), g3.asString(arg3)),
                                    () -> action.accept(arg1, arg2, arg3),
                                    precondition, postcondition);
                };
            }
        };
    }

    public interface QuadConsumer<T1, T2, T3, T4> {
        public void accept(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    public static <T1, T2, T3, T4> StepBuilder builder(String desc,
                                                       QuadConsumer<T1, T2, T3, T4> action,
                                                       Gen<T1> g1,
                                                       Gen<T2> g2,
                                                       Gen<T3> g3,
                                                       Gen<T4> g4) {
        return builder(desc, action, () -> g1, () -> g2, () -> g3, () -> g4);
    }

    public static <T1, T2, T3, T4> StepBuilder builder(String desc,
                                                       QuadConsumer<T1, T2, T3, T4> action,
                                                       Supplier<Gen<T1>> s1,
                                                       Supplier<Gen<T2>> s2,
                                                       Supplier<Gen<T3>> s3,
                                                       Supplier<Gen<T4>> s4) {
        return new StepBuilder(desc) {
            @Override
            public Gen<Step> build() {
                return (in) -> {
                    if (!allowed())
                        return null;

                    Gen<T1> g1 = s1.get();
                    Gen<T2> g2 = s2.get();
                    Gen<T3> g3 = s3.get();
                    Gen<T4> g4 = s4.get();
                    T1 arg1 = g1.generate(in);
                    T2 arg2 = g2.generate(in);
                    T3 arg3 = g3.generate(in);
                    T4 arg4 = g4.generate(in);
                    return new Step(desc,
                                    StatefulTheory.buildArgString(g1.asString(arg1), g2.asString(arg2),
                                                                  g3.asString(arg3), g4.asString(arg4)),
                                    () -> action.accept(arg1, arg2, arg3, arg4),
                                    precondition, postcondition);
                };
            }
        };
    }

    static String buildArgString(String... objects) {
        return String.join(", ", objects);
    }
}
