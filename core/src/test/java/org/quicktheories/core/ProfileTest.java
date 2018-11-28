package org.quicktheories.core;

import org.junit.Before;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileTest implements WithQuickTheories {

    static class Scope1 {
    }

    static class Scope2 {
    }

    @Before
    public void before() {
        Profile.clearProfiles();
    }

    @Test
    public void testIsScopedToClass() {
        int v1 = 1, v2 = 2;
        Profile.registerDefaultProfile(Scope1.class, s -> s.withExamples(v1));
        Profile.registerDefaultProfile(Scope2.class, s -> s.withExamples(v2));

        Optional<Function<Strategy, Strategy>> p1 = Profile.getDefaultProfile(Scope1.class);
        Optional<Function<Strategy, Strategy>> p2 = Profile.getDefaultProfile(Scope2.class);

        assertThat(p1).isNotEmpty();
        assertThat(p2).isNotEmpty();
        assertThat(p1.get().apply(Configuration.systemStrategy()).examples()).isEqualTo(v1);
        assertThat(p2.get().apply(Configuration.systemStrategy()).examples()).isEqualTo(v2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReRegistrationThrowsException() {
        Profile.registerDefaultProfile(Scope1.class, s -> s.withExamples(1));
        Profile.registerDefaultProfile(Scope1.class, s -> s.withExamples(1));
    }

    @Test
    public void propertyCanLoadRegisteredProperties() {
        int numExamples = 4;
        AtomicInteger examplesRun = new AtomicInteger(0); // don't really need concurrency safe object but its a simple mutable container
        Profile.registerDefaultProfile(Scope1.class, s -> s.withExamples(numExamples));
        qt().withRegisteredProfiles(Scope1.class).forAll(integers().all()).check(i -> {
            examplesRun.incrementAndGet();
            return true;
        });
        assertThat(examplesRun.get()).isEqualTo(numExamples);
    }

    @Test
    public void propertyCanLoadExplicitProfile() {
        int numExamples = 5;
        AtomicInteger examplesRun = new AtomicInteger(0); // don't really need concurrency safe object but its a simple mutable container
        Profile.registerProfile(Scope1.class, "ci", s -> s.withExamples(numExamples));
        Profile.registerDefaultProfile(Scope1.class, s -> s.withExamples(1));
        qt().withProfile(Scope1.class, "ci").forAll(integers().all()).check(i -> {
            examplesRun.incrementAndGet();
            return true;
        });
        assertThat(examplesRun.get()).isEqualTo(numExamples);
    }

}
