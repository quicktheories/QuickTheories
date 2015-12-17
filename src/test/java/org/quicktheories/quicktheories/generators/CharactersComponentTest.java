package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class CharactersComponentTest extends ComponentTest<Character> {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;
  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int FIRST_CODEPOINT = 0x0000;
  private static final int LARGEST_DEFINED_BMP_CODEPOINT = 65533;

  Reporter reporter = mock(Reporter.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(2), 1000, 1000,
      this.reporter);

  @Test
  public void shouldShrinkToTargetByOneCodePointWhenRemainingShrinkCyclesIsGreaterThanBLCDomain() {
    assertThatFor(Characters.ofCharacters(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT)).check(i -> false);
    listIsDecreasingByOne();
    smallestValueIsEqualTo('\u0020');
  }

  @Test
  public void shouldShrinkToTargetWhenRemainingShrinkCyclesIsLessThanBLCDomain() {
    assertThatFor(Characters.ofCharacters(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT),
        new Strategy(Configuration.defaultPRNG(2), 1, 10, this.reporter))
            .check(i -> false);
    listIsInDecreasingOrder();
    smallestValueIsEqualTo('\u0020');
  }

  @Test
  public void shouldShrinkToTargetByOneCodePointWhenRemainingShrinkCyclesIsGreaterThanAsciiDomain() {
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT))
            .check(i -> false);
    listIsDecreasingByOne();
    smallestValueIsEqualTo('\u0000');
  }

  @Test
  public void shouldShrinkToTargetWhenRemainingShrinkCyclesIsLessThanAsciiDomain() {
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
        new Strategy(Configuration.defaultPRNG(2), 1, 8, this.reporter))
            .check(i -> false);
    listIsInDecreasingOrder();
    smallestValueIsEqualTo('\u0000');
  }

  @Test
  public void shouldShrinkWhenRemainingShrinkCyclesIsOriginallyLessThanBMPDomain() {
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT),
        new Strategy(Configuration.defaultPRNG(2), 1, 40, this.reporter))
            .check(i -> false);
    listIsInDecreasingOrder();
  }

  @Test
  public void shouldNotShrinkToANonCharacterInTheBMPDomain() {
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT))
            .check(i -> false);
    listContainsOnlyDefinedCharacters();
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainBLC() {
    char target = '\u0020';
    assertThatFor(Characters.ofCharacters(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT), withShrinkCycles(20))
            .check(i -> false);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainAscii() {
    char target = '\u0000';
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
        withShrinkCycles(20)).check(i -> false);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainBMP() {
    char target = '\u0000';
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT),
        withShrinkCycles(50000))
            .check(i -> false);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldFindAtLeastFiveDifferentValuesThatAreNotInTheBasicLatinCharacters() {
    assertThatFor(
        Characters.ofCharacters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT),
        withShrinkCycles(25)).check(i -> 0x0020 <= i && i <= 0x007E);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo('\u0000');
  }

  private TheoryBuilder<Character> assertThatFor(
      Source<Character> generator) {
    return theoryBuilder(generator, this.strategy, this.reporter);
  }

  private TheoryBuilder<Character> assertThatFor(
      Source<Character> generator, Strategy strategy) {
    return theoryBuilder(generator, strategy, this.reporter);
  }

  private void smallestValueIsEqualTo(char target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        Character.compare(smallestValueFound(), target) == 0);
  }

  private void listIsInDecreasingOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue("Expected " + (listOfShrunkenItems().get(i - 1))
          + " to be bigger than " + (listOfShrunkenItems().get(i)),
          listOfShrunkenItems().get(i - 1) >= listOfShrunkenItems().get(i));
    }
  }

  private void listIsDecreasingByOne() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue("Expected " + (listOfShrunkenItems().get(i - 1))
          + " to be one bigger than " + (listOfShrunkenItems().get(i)),
          listOfShrunkenItems().get(i - 1) - listOfShrunkenItems().get(i) <= 1);
    }
  }

  private void listContainsOnlyDefinedCharacters() {
    for (char i : listOfShrunkenItems()) {
      assertTrue(
          "Expected " + i
              + " to be a defined character, but it was not",
          Character.isDefined(i));
    }
  }

  private Strategy withShrinkCycles(int shrinkCycles) {
    return new Strategy(Configuration.defaultPRNG(2), 1000000, shrinkCycles,
        this.reporter);
  }

}
