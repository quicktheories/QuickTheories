package org.quicktheories.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Profile {

    private static final Map<Class<?>, Map<String, Function<Strategy, Strategy>>> profiles = new ConcurrentHashMap<>();
    private static final String DEFAULT = "default";

    public static void registerDefaultProfile(Class<?> cls, Function<Strategy, Strategy> profile) {
        registerProfile(cls, DEFAULT, profile);
    }

    public static void registerProfile(Class<?> cls, String name, Function<Strategy, Strategy> profile) {
        Map<String, Function<Strategy, Strategy>> perClass = profiles.get(cls);
        if (perClass == null) {
            Map<String, Function<Strategy, Strategy>> newPerClass = new ConcurrentHashMap<>();
            perClass = profiles.putIfAbsent(cls, newPerClass);
            if (perClass == null)
                perClass = newPerClass;
        }

        if (perClass.putIfAbsent(name, profile) != null)
            throw new IllegalArgumentException(String.format("Profile %s already exists for class %s", name, cls.getSimpleName()));
    }

    public static Optional<Function<Strategy, Strategy>> getDefaultProfile(Class<?> cls) {
        return getProfile(cls, DEFAULT);
    }

    public static Optional<Function<Strategy, Strategy>> getProfile(Class<?> cls, String name) {
        return Optional.ofNullable(profiles.get(cls)).flatMap(perClass -> Optional.ofNullable(perClass.get(name)));
    }

    static void clearProfiles() {
        profiles.clear();
    }

}
