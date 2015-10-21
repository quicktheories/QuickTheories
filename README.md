# QuickTheories

Property based testing for Java 8.

## Goals

Investigate possible api for property based testing that allows

1. Controlled randomness allowing deterministic builds
2. Efficient generation of constained types
3. Support for shrinking
4. Independent of test api (JUnit, TestNG etc)
5. Easier definition of custom generators

## Controlled randomness

Traditional property based testing is purely random. This is great for finding issues you hadn;t thought about, but conflicts with having a deterministic build process.

If a seed can be injected so that testing can be fully random while developing, but the seed fixed on build servers then we can have the best of both worlds. If a property is falisified it should be possible to re-run with the same seed.
