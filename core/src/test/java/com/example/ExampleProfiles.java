package com.example;

import org.quicktheories.core.Profile;

import java.util.concurrent.TimeUnit;

public class ExampleProfiles {

    static {
        Profile.registerProfile(ExampleProfiles.class,"ci", s -> s.withUnlimitedExamples().withTestingTime(1, TimeUnit.MINUTES));
        Profile.registerDefaultProfile(ExampleProfiles.class, s -> s.withExamples(1000));
    }

}
