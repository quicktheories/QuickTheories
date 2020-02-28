package org.quicktheories.impl.stateful;

import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.quicktheories.impl.stateful.StatefulTheory.builder;

public class StatefulTest implements WithQuickTheories {

    @Test
    public void testStatefulInitialization() {
        AtomicInteger counter = new AtomicInteger();

        qt().withMinStatefulSteps(100)
                .withMaxStatefulSteps(100)
                .withExamples(1)
                .stateful(() -> new StatefulTheory.StepBased() {
                    @Override
                    protected void initSteps() {
                        counter.incrementAndGet();
                        addStep(builder("test1", () -> {}).build());
                    }
                });
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testMinMaxSteps() {
        qt().forAll(integers().between(1, 10), integers().between(11, 100))
                .checkAssert( (min, max) -> {
                    List<AtomicInteger> counts = new ArrayList<>(10);
                    qt().withMinStatefulSteps(min)
                            .withMaxStatefulSteps(max)
                            .withExamples(10)
                            .stateful(() -> new StatefulTheory.StepBased() {
                                @Override
                                protected void initSteps() {
                                    AtomicInteger counter = new AtomicInteger();
                                    addSetupStep(builder("addToList", () -> counts.add(counter)).build());
                                    addStep(builder("test1", () -> counter.incrementAndGet()).build());
                                }
                            });

                    for (AtomicInteger counter : counts) {
                        int ranSteps = counter.get();
                        Assert.assertTrue(ranSteps >= min && ranSteps <= max);
                    }
                });
    }

    @Test
    public void testPreConditions() {
        AtomicInteger counter = new AtomicInteger();
        Supplier<Boolean> alwaysFalse = () -> false;

        Supplier<Gen<Runnable>> supplier = () -> {
            counter.incrementAndGet();
            return (in) -> () -> {};
        };

        Supplier<Boolean> postcondition = () -> {
            counter.incrementAndGet();
            return false;
        };

        try {
            // Generator supplier should not be called if preconditions is not met
            qt().withStatefulModel(() -> new StatefulTheory.StepBased() {
                @Override
                protected void initSteps() {
                    addStep(builder("test1", (a) -> counter.incrementAndGet(),
                                    supplier)
                                    .precondition(alwaysFalse)
                                    .postcondition(postcondition)
                                    .build());

                    addStep(builder("test2", (a1, a2) -> counter.incrementAndGet(),
                                    supplier, supplier)
                                    .precondition(alwaysFalse)
                                    .postcondition(postcondition)
                                    .build());

                    addStep(builder("test3", (a1, a2, a3) -> counter.incrementAndGet(),
                                    supplier, supplier, supplier)
                                    .precondition(alwaysFalse)
                                    .postcondition(postcondition)
                                    .build());

                    addStep(builder("test4", (a1, a2, a3, a4) -> counter.incrementAndGet(),
                                    supplier, supplier, supplier, supplier)
                                    .precondition(alwaysFalse)
                                    .postcondition(postcondition)
                                    .build());
                }
            }).checkStateful();
        } catch (AssertionError t) {
            // ignore;
        }

        Assert.assertEquals(0, counter.get());
    }

    @Test(expected = AssertionError.class)
    public void testPostCondition() {
        qt().stateful(() -> new StatefulTheory.StepBased() {
            @Override
            protected void initSteps() {
                addStep(builder("test1", () -> {
                }).postcondition(() -> false).build());
            }
        });
    }

    /*
    @Test
    public void argStringTest() {
        Gen<StatefulTheory.Step> step = builder("test1",
                                                (a1) -> {},
                                                (in) -> 1).build();
        Assert.assertEquals("test1(1)",
                            step.example().toString());
        step = builder("test2",
                       (a1, a2) -> {},
                       (in) -> 1, (in) -> 2).build();
        Assert.assertEquals("test2(1, 2)", step.example().toString());
        step = builder("test3",
                       (a1, a2, a3) -> {},
                       (in) -> 1, (in) -> 2, (in) -> 3).build();
        Assert.assertEquals("test3(1, 2, 3)", step.example().toString());
        step = builder("test4",
                       (a1, a2, a3, a4) -> {},
                       (in) -> 1, (in) -> 2, (in) -> 3, (in) -> 4).build();
        Assert.assertEquals("test4(1, 2, 3, 4)", step.example().toString());
    }*/
}