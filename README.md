[![Build Status](https://travis-ci.org/ncredinburgh/QuickTheories.svg)](https://travis-ci.org/ncredinburgh/QuickTheories)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.quicktheories/quicktheories/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.quicktheories/quicktheories)

# QuickTheories

Property-based testing for Java 8.

If you were looking for QuickCheck for Java you just found it.

## What is property based testing 

Traditional unit testing is performed by specifying a series of concrete examples and asserting on the outputs/behaviour of the unit under test.

Property based testing moves away from concrete examples and instead checks that certain properties hold true for all possible inputs. It does this by automatically generating
a random sample of valid inputs from the possible values. 

This can be a good way to uncover bad assumptions made by you and your code.

If the word random is making you feel a little nervous, don't worry QuickTheories provides ways to keep your tests repeatable.

## Quick Start

Add the QuickTheories jar to your build (see the badge at the top of the page for the maven co-ordinates of the latest version).

You can run QuickTheories from JUnit, TestNG or any other test framework.

Here we are using JUnit 

```java
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.*;

public class SomeTests {

  @Test
  public void addingTwoPositiveIntegersAlwaysGivesAPositiveInteger(){
    qt()
    .forAll(integers().allPositive()
          , integers().allPositive())
    .check((i,j) -> i + j > 0); 
  }

}
```

The static import `org.quicktheories.quicktheories.QuickTheory.qt` provides access to the QuickTheories DSL.

The static import `org.quicktheories.quicktheories.generators.SourceDSL.*` provides access to a DSL that allows valid inputs to be defined.

This property looks pretty simple, it just checks that adding two integers always produces a number greater than 0.

This couldn't possibly fail could it? That would mean math was broken.

If we run this test we get something like :-

```
java.lang.AssertionError: Property falsified after 1 example(s) 
Smallest found falsifying value(s) :-
{840226137, 1309274625}
Other found falsifying value(s) :- 
{848253830, 1320535400}
{841714728, 1317667877}
{840894251, 1310141916}
{840226137, 1309274625}
 
Seed was 29678088851250	
```

The falsified theory has highlighted something that we forgot. 

Math works just fine, but in Java integers can overflow.

### Without static imports

If you prefer the QuickTheories entry points can be brought into scope by implementing and interface, removing the need for static imports.

```java
public class SomeTests implements WithQuickTheories {

  @Test
  public void addingTwoPositiveIntegersAlwaysGivesAPositiveInteger(){
    qt()
    .forAll(integers().allPositive()
          , integers().allPositive())
    .check((i,j) -> i + j > 0); 
  }

}
```

### Shrinking

QuickTheories supports shrinking. 

This means that it doesn't just find a falsifying value and stop. Instead it will try to find other smaller (or "simpler") values that also invalidate the theory. 

By default QuickTheories will spend about 100 times more effort looking for smaller values than it did looking for the original falsifying value.

The smallest found value is reported along with a sample of any other falsifying values found along the way. 

There is no guarantee that this is the smallest possible falsifying value or that others don't exist. Generally the shrunk values will be easier to understand and work with than the original un-shrunk ones - patterns might be visible in the reported values.

### Seeds and repeatable tests

At the end of the report the Seed is reported. 

This is the value from which all randomness is derived in QuickTheories. 

By default it is set to the System.nanoTime() so the values will be different each time QuickTheories is run, however the seed can also be set explicitly so runs can be reproduced and deterministic.

Whenever a property is falsified the seed used is reported so you can always reproduce the exact same run.

It is therefore always possible to recreate a run, and you can opt for a fully deterministic behaviour by using a single fixed seed.

Two methods are provided to set the seed.

Directly using the DSL

```java
  qt()
  .withFixedSeed(0)
  .forAll( . . .)
``` 

Or using the QT_SEED system property.

The same tests can therefore be run with a fixed seed for the purpose of catching regression, or with a changing seed so that falsifying values are constantly being searched for.

### Assertions

Our example theory used a simple predicate, but sometimes it would be nice to take advantage of the functionality provided by assertion libraries such as [assertj](http://joel-costigliola.github.io/assertj/) and [hamcrest](https://github.com/hamcrest).

This can be done using the checkAssert method. 

```java
  @Test
  public void someTheory() {
    qt().forAll(longs().all())
        .checkAssert(i -> assertThat(i).isEqualsTo(42));
  }
``` 	
	  
Any block of code that returns void can be passed to checkAssert. Any unchecked exception will be interpreted as falsifying the theory.

### Source and assumptions

As we've seen we can create theories from a pair of Sources - which produce a pair of values. 

In fact we can create theories about any number of values between 1 and 4.

```java
   @Test
  public void someTheoryOrOther(){
    qt()
    .forAll(integers().allPositive()
          , strings().basicLatinAlphabet().ofLengthBetween(0, 10)
          , lists().allListsOf(integers().all()).ofSize(42))
    .check((i,s,l) -> l.contains(i) && s.equals(""));
  }
```

In the example above we use three Sources, as you can see QuickTheories provides ways of generating most common Java types.

Sources are made up of two parts

* A Generator
* A Shrinker

A generator is just a simple function from a random number generator to a value. A shrinker is something that can intelligently produce simpler instances of that type.

As we can see, the Sources DSL provides a way to put constraints on the values we generate (e.g we will only generate positive integers and the lists in this example will only be of size 42).

Whenever possible you should use the Sources DSL to provide constraints, but sometimes you might need to constrain the domain in ways that cannot be expressed with the DSL.

When this happens use assumptions. 

```java
  @Test
  public void someTheoryOrOther(){
    qt()
    .forAll(integers().allPositive()
          , strings().basicLatinAlphabet().ofLengthBetween(0, 10)
          , lists().allListsOf(integers().all()).ofSize(42))
    .assuming((i,s,l) -> s.contains(i.toString())) // <-- an assumption
    .check((i,s,l) -> l.contains(i) && s.contains(i.toString()));
  }
```

Assumptions further constrain the values which form the subject of the theory.

Although we could always replace the constraints we created in the Sources DSL with assumptions, this would be very inefficient. QuickTheories would have to spend a lot of effort just trying to find valid values before it could try to invalidate a theory.

As difficult to find values probably represent a coding error, QuickTheories will throw an error if less than 10% of the generated values pass the assumptions:

```java
  @Test
  public void badUseOfAssumptions() {
    qt()
    .forAll(integers().allPositive())
    .assuming(i -> i < 30000)
    .check( i -> i < 3000);
  }
```

Gives

```
java.lang.IllegalStateException: Gave up after finding only 107 example(s) matching the assumptions
	at org.quicktheories.quicktheories.core.ExceptionReporter.valuesExhausted(ExceptionReporter.java:20)
```  
(Note: this assumption could have been replaced by the following: 

```java
   @Test
  public void goodUseOfSource(){
    qt().forAll(integers().from(1).upTo(30000))
    .check( i -> i < 3000);
  }
```
Which gives the following failure message: )
```
java.lang.AssertionError: Property falsified after 1 example(s) 
Smallest found falsifying value(s) :-
3000
Other found falsifying value(s) :- 
13723
13722
13721
13720
13719
13718
13717
13716
13715
13714
 
Seed was 2563360080237
```  

### Gens 

It is likely that you will want to construct instances of your own types. You could do this within each check, but this would result in a lot of code duplication.

Instead you can define a conversion function. This can be done inline, or placed somewhere convenient for reuse.

```java
  @Test
  public void someTheoryOrOther(){
    qt()
    .forAll(integers().allPositive()
          , integers().allPositive())
    .as( (width,height) -> new Widget(width,height) ) // <-- convert to our own type here
    .check( widget -> widget.isValid());
  }
```

This works well for simple cases, but there are two problems.

1. We cannot refer to the original width and height integers in our theory. So we couldn't (for example) check that the widget had the expected size.
2. If our widget doesn't define a toString method it is hard to know what the falsifying values were

Both of these problems are solved by the asWithPrecursors method

```
  @Test
  public void someTheoryOrOther(){
     qt()
    .forAll(integers().allPositive()
          , integers().allPositive())
    .asWithPrecursor( (width,height) -> new Widget(width,height) )
    .check( (width,height,widget) -> widget.size() > width * height ); 
  }
  ```

When this fails it gives us

```
java.lang.AssertionError: Property falsified after 2 example(s)
Smallest found falsifying value(s) :-
{43, 23259, com.example.QuickTheoriesExample$Widget@9e89d68}
Other found falsifying value(s) :- 
{536238991, 619642140, com.example.QuickTheoriesExample$Widget@59f95c5d}
{2891501, 215920967, com.example.QuickTheoriesExample$Widget@5ccd43c2}
{1479099, 47930205, com.example.QuickTheoriesExample$Widget@4aa8f0b4}
{297099, 11425635, com.example.QuickTheoriesExample$Widget@7960847b}
{288582, 10972429, com.example.QuickTheoriesExample$Widget@6a6824be}
{14457, 5650202, com.example.QuickTheoriesExample$Widget@5c8da962}
{14456, 393098, com.example.QuickTheoriesExample$Widget@512ddf17}
{14454, 38038, com.example.QuickTheoriesExample$Widget@2c13da15}
{14453, 38037, com.example.QuickTheoriesExample$Widget@77556fd}
{14452, 38036, com.example.QuickTheoriesExample$Widget@368239c8}
 
Seed was 4314310398163
```

Notice that shrinking works for our custom type without any effort on our part.

Defining the values that make up the valid domain for your objects might not be straightforward and could result in a lot of repeated code between theories.

Fortunately the Gen<T> objects that produce the random values can be freely reused and combined with other Gens.

e.g

```java
  @Test
  public void cylindersHavePositiveAreas() {
    qt()
    .forAll(cylinders())
    .check( cylinder -> cylinder.area().compareTo(BigDecimal.ZERO) > 0);
  }
  
  private Gen<Cylinder> cylinders() {
    return radii().combine(heights(),
        (radius, height) -> new Cylinder(radius, height))
        .assuming(cylinder -> some sort of validation );
  }


  private Gen<Integer> heights() {
    return integers().from(79).upToAndIncluding(1004856);
  }

  private Gen<Integer> radii() {
    return integers().allPositive();
  }
```

## Modifying the falsification output

Values produces by the sources DSL should provide clear falsification messages.

If you are working with your own Gens, or would like to modify the defaults, you can supply your own function to be used when describing the falsifying values.

For example:

```java
  @Test
  public void someTestInvolvingCylinders() {
      qt()
      .forAll(integers().allPositive().describedAs(r -> "Radius = " + r)
             , integers().allPositive().describedAs(h -> "Height = " + h))
      .check((r,h) -> whatever);
  }
```

Custom description functions will be retained when converting to a type with precursors. A description function for the converted type can be optionally passed to the asWithPrecursor function.

```java
  @Test
  public void someTestInvolvingCylinders() {
      qt()
      .forAll(integers().allPositive().describedAs(r -> "Radius = " + r)
             , integers().allPositive().describedAs(h -> "Height = " + h))
      .asWithPrecursor((r,h) -> new Cylinder(r,h)
                       , cylinder -> "Cylinder r =" + cylinder.radius() + " h =" + cylinder.height())        
      .check((i,j,k) -> whatever);
  }
```

A description function can be provided for a type converted without precursors as follows:

```java
  @Test
  public void someTestInvolvingCylinders() {
      qt()
      .forAll(integers().allPositive().describedAs(r -> "Radius = " + r)
             , integers().allPositive().describesAs(h -> "Height = " + h))
      .as( (r,h) -> new Cylinder(r,h))
      .describedAs(cylinder -> "Cylinder r =" + cylinder.radius() + " h =" + cylinder.height())        
      .check(l -> whatever);
  }
```

## Configuration properties

Three system properties can be set that determine QuickTheories behaviour:

* QT_SEED - the random seed to use
* QT_EXAMPLES - the number of examples to try for each theory
* QT_SHRINKS - the number of shrink attempts to make

## Writing good properties

Properties should not just duplicate the logic of your code under test (this is equally true for the example based testing).

Instead properties should try to specify very simple but general invariants that should hold true. Start with very simple general properties and get more specific as
you go along.

Some common patterns that produce good properties include :-

(note, these patterns are largely a summary of the material at [fsharpforfunandprofit](http://fsharpforfunandprofit.com/posts/property-based-testing-2/))

### The invariant pattern aka "Some things never change"

Some things are expected to remain constant, e.g a map operation should produce the same number of items that it was given, the total balance across two bank accounts should
remain constant after a transfer etc.
	
### The inverse function pattern aka "There and back again"

If we have two functions that are the inverse of each other then applying the input of one to the other should result in no change.

Common examples of inverse function pairs include

* serialisation / deserialisation
* compression / decompression
* encryption / decryption
* create / delete

### Analogous function pattern aka "Different paths same destination"		

If you have two functions that implement the same logic, but differ in some other property (perhaps one is inefficient, insecure or implemented in a third party library) then a property can be defined that checks the outputs of the functions match given the same input. 

### Idempotence aka "The more things change, the more they stay the same"

Sometimes it's important/logical that performing an operation multiple times has no effect. e.g if you trim the whitespace from a string multiple times, only the first call to trim should have any observable effect. 

## Simple examples

An example test that is falsifying, showing that adding two positive integers in Java does not always give a positive integer:

```java
@Test
  public void addingTwoPositiveIntegersAlwaysGivesAPositiveInteger(){
    qt()
    .forAll(integers().allPositive()
          , integers().allPositive())
    .check((i,j) -> i + j > 0);  //fails
  }

```
An example of multiple tests for code that claims to find the greatest common divisor between two integers. The first property test fails due to a java.lang.StackOverflowError error (caused by attempting to take the absolute value of Integer.MIN_VALUE).
```java
  @Test
  public void shouldFindThatAllIntegersHaveGcdOfOneWithOne() {
    qt().forAll(integers().all()).check(n -> gcd(n, 1) == 1); // fails on
                                                              // -2147483648
  }

  @Test
  public void shouldFindThatAllIntegersInRangeHaveGcdOfOneWithOne() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check(n -> gcd(n, 1) == 1);
  }

  @Test
  public void shouldFindThatAllIntegersHaveGcdThemselvesWithThemselves() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check(n -> gcd(n, n) == Math.abs(n));
  }

  @Test
  public void shouldFindThatGcdOfNAndMEqualsGcdMModNAndN() {
    qt().forAll(integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE)
               ,integers().between(-Integer.MAX_VALUE, Integer.MAX_VALUE))
        .check((n, m) -> gcd(n, m) == gcd(m % n, n));
  }

  private int gcd(int n, int m) {
    if (n == 0) {
      return Math.abs(m);
    }
    if (m == 0) {
      return Math.abs(n);
    }
    if (n < 0) {
      return gcd(-n, m);
    }
    if (m < 0) {
      return gcd(n, -m);
    }
    if (n > m) {
      return gcd(m, n);
    }
    return gcd(m % n, n);
  }
```


## Design Goals

QuickTheories was written with the following design goals

1. Random by default, but builds must be repeatable 
2. Support for shrinking
3. Independent of test api (JUnit, TestNG etc)

It turned out that number 2 was the hard bit as it had many implications for the design. The approach was completely changed between releases in the 0.x and 1.x series.

As of 1.x shrinking uses an approach similar to the python library hypothesis, whereby shrinking is unaware of the type it is generating. This approach is less flexible than the original approach but allows Gens to be freely composed together while greatly reducing the size of the codebase.

## Background

QuickTheories was produced at [NCR Edinburgh](http://ncredinburgh.com/) as part of our graduate training program. 

We like to do training a little differently - our [new graduates](http://github.com/katyrae) get to work on an interesting project for a weeks with a more worn in and [weathered member of our staff](http://github.com/hcoles). Our motto for these projects is "software that can fail" - so we get to play with interesting ideas that may come to nothing. 

We're happy to share the results as open source when we think they're successful.

## Other property based testing systems for Java

If you don't like QuickTheories you might want to try one of the other systems below which have different design goals. Besides junit-quickcheck, none of them look at implementing shrinking, but all provide ways of generating random values and should work on earlier versions of Java.

* [JUnit-quickcheck](http://pholser.github.io/junit-quickcheck). Tightly integrated with JUnit, uses annotations to configure generators.
    + As of v0.6 junit-quickcheck also supports shrinking.
* [JCheck](http://www.jcheck.org/). Tightly integrated with JUnit. Does not look to be maintained.
* [QuickCheck](https://bitbucket.org/blob79/quickcheck). Not tied to a test framework - provides generators of random values to be used in tests.
* [FunctionalJava](http://www.functionaljava.org/). Apparently contains a property based testing system, but appears to be completely undocumented.
* [ScalaCheck](http://www.scalacheck.org/). Mature property based testing system with shrinking, but requires Scala rather than Java.
