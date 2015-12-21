package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringsComponentTest extends ComponentTest<String> {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;

  @Test
  public void shouldFindStringEqualToZeroWhereZeroFalsifies() {
    assertThatFor(
        Strings.boundedNumericStrings(Integer.MIN_VALUE, Integer.MAX_VALUE))
            .check(i -> i.length() >= 10);
    smallestValueIsEqualTo("0");
  }

  @Test
  public void shouldFindTargetStringInBoundedNumericStrings() {
    assertThatFor(Strings.boundedNumericStrings(1, 40000))
        .check(i -> i.length() >= 4);
    smallestValueIsEqualTo("1");
  }

  @Test
  public void shouldFindStringEqualToExclamationStringWhereExclamationStringFalsifies() {
    assertThatFor(
        Strings.ofFixedNumberOfCodePointsStrings(BASIC_LATIN_FIRST_CODEPOINT,
            BASIC_LATIN_LAST_CODEPOINT, 5)).check(i -> false);
    smallestValueIsEqualTo("!!!!!");
  }

  @Test
  public void shouldNotShrinkToAnUndefinedCharacterInStringWhereRemainingCyclesInitiallyLessThanDomainSize() {
    assertThatFor(Strings.ofBoundedLengthStrings(0, 196607, 5, 5),
        withShrinkCycles(100))
            .check(i -> false);
    listContainsOnlyDefinedCharacters();
  }

  @Test
  public void shouldNotShrinkToUndefinedCharacterInStringWhereRemainingCyclesGreaterThanDomainSize() {
    assertThatFor(Strings.ofBoundedLengthStrings(0, 196607, 5, 5),
        withShrinkCycles(196608))
            .check(i -> false);
    listContainsOnlyDefinedCharacters();
  }

  @Test
  public void shouldNotShrinkToUndefinedCharacterInStringWhereRemainingCyclesEqualsTheDomainSize() {
    assertThatFor(
        Strings.ofBoundedLengthStrings(Character.MIN_CODE_POINT,
            Character.MAX_CODE_POINT, 6, 6),
        withShrinkCycles(Character.MAX_CODE_POINT)).check(i -> false);
    listContainsOnlyDefinedCharacters();
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForAllPossibleStringsWithFixedNumberOfCodePoints() {
    assertThatFor(
        Strings.ofFixedNumberOfCodePointsStrings(Character.MIN_CODE_POINT,
            Character.MAX_CODE_POINT, 6),
        withShrinkCycles(1000000))
            .check(i -> i.length() == 6);
    atLeastNDistinctFalsifyingValuesAreFound(1);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForAllPossibleStringsWithFixedLength() {
    assertThatFor(Strings.ofBoundedLengthStrings(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 8, 8)).check(i -> i.indexOf('\u0020') >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForAllPossibleStringsWithFixedNumberOfCodePointsinBL() {
    assertThatFor(
        Strings.ofFixedNumberOfCodePointsStrings(BASIC_LATIN_FIRST_CODEPOINT,
            BASIC_LATIN_LAST_CODEPOINT, 8))
                .check(i -> i.indexOf('\u0020') >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void shouldOnlyGenerateCharactersWhenFixedLengthIsSetToOne() {
    assertThatFor(
        Strings.ofBoundedLengthStrings(Character.MIN_CODE_POINT,
            Character.MAX_CODE_POINT, 1, 1))
                .check(i -> false);
    listContainsOnlyBMPCharacters();
  }

  @Test
  public void shouldShrinkToEmptyStringWhenMinimumLengthIsSetToZero() {
    assertThatFor(Strings.ofBoundedLengthStrings(Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 0, 12)).check(i -> false);
    listContainsOnlyDefinedCharacters();
    smallestValueIsEqualTo("");
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForAllPossibleStringsWithMinimumLength1() {
    String target = "\u0021";
    assertThatFor(Strings.ofBoundedLengthStrings(Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 1, 18)).check(i -> i.indexOf('\u0000') >= 0);
    listContainsOnlyDefinedCharacters();
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void willRepeatedlyReturnOriginalShrunkenStringIfDomainIsSupplementaryCharacters() {
    assertThatFor(Strings.ofBoundedLengthStrings(
        Character.MIN_SUPPLEMENTARY_CODE_POINT, Character.MAX_CODE_POINT, 3, 9))
            .check(i -> i.indexOf('\u0000') >= 0);
    assertTrue("Expected all items in the list to be the same",
        listOfShrunkenItems().get(0).equals(
            listOfShrunkenItems().get(listOfShrunkenItems().size() - 1)));
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWhereDomainIncludesLargestBMPCharacter() {
    String target = "\ufffd\ufffd\ufffd";
    assertThatFor(
        Strings.ofBoundedLengthStrings(65533, Character.MAX_CODE_POINT, 3, 9))
            .check(i -> i.indexOf('\u0000') >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldFindAtLeastFiveStringsThatContainAnAmpersand() {
    assertThatFor(Strings.ofBoundedLengthStrings(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 6, 6))
            .check(i -> i.indexOf('\u0026') < 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void shouldOnlyFindOneStringThatFalsifiesConcerningLengthWhenShrinkingLength() {
    assertThatFor(Strings.ofBoundedLengthStrings(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 2, 14), withShrinkCycles(12))
            .check(i -> i.length() != 14);
    assertTrue(
        "Expected list of shrunken values to contain no Strings, but it contains "
            + listOfShrunkenItems(),
        listOfShrunkenItems().isEmpty());
  }

  @Test
  public void shouldShrinkBMPHighSurrogatesLikeOtherBMPCharacters() {
    assertThatFor(Strings.ofBoundedLengthStrings(55296, 56319, 1, 2))
        .check(i -> false);
    smallestValueIsEqualTo("\uD800");
  }

  @Test
  public void shouldShrinkBMPLowSurrogatesLikeOtherBMPCharacters() {
    assertThatFor(Strings.ofBoundedLengthStrings(56320, 57343, 1, 2))
        .check(i -> false);
    smallestValueIsEqualTo("\uDC00");
  }

  @Test
  public void shouldShrinkBMPHighSurrogatesLikeOtherBMPCharactersOverBigRange() {
    assertThatFor(Strings.ofBoundedLengthStrings(55296, 56319, 1, 40))
        .check(i -> false);
    smallestValueIsEqualTo("\uD800");
  }

  @Test
  public void shouldShrinkBMPLowSurrogatesLikeOtherBMPCharactersOverBigRange() {
    assertThatFor(Strings.ofBoundedLengthStrings(56320, 57343, 1, 50))
        .check(i -> false);
    smallestValueIsEqualTo("\uDC00");
  }

  @Test
  public void shouldReturnAndShrinkSupplementaryCharacters() {
    assertThatFor(Strings.ofFixedNumberOfCodePointsStrings(0x0000,
        Character.MAX_CODE_POINT, 1)).check(s -> s.length() != 2);
    atLeastNDistinctFalsifyingValuesAreFound(2);
  }


  private void smallestValueIsEqualTo(String target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        smallestValueFound().equals(target));
  }

  private void listContainsOnlyDefinedCharacters() {
    for (String i : listOfShrunkenItems()) {
      int charactersTraversed = 0;
      while (charactersTraversed < i.length()) {
        int codePoint = i.codePointAt(charactersTraversed);
        assertTrue(
            "Expected " + i + "to only contain valid characters, but was wrong",
            Character.isDefined(codePoint));
        charactersTraversed += Character.charCount(codePoint);
      }
    }
  }

  private void listContainsOnlyBMPCharacters() {
    for (String i : listOfShrunkenItems()) {
      assertTrue("Expected to only generate characters in  BMP, but " + i
          + " consits of " + Character.charCount(i.codePointAt(0))
          + " codepoints", Character.charCount(i.codePointAt(0)) == 1);
    }
  }

}
