package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

public class StringsDSLTest implements WithQuickTheories {

  @Test
  public void boundedLengthStringsRespectsLengthBounds() {
    Gen<String> testee = strings().allPossible().ofLengthBetween(3, 200);
    qt()
    .withExamples(100000)
    .forAll(testee)
    .check( s -> s.length() <= 200 && s.length() >= 3);
  }
  
  @Test
  public void boundedLengthStringsProducesDistinctValues() {
    Gen<String> testee = strings().allPossible().ofLengthBetween(0, 100);
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }
  
  @Test
  public void fixedLengthStringsAreFixedLength() {
    qt()
    .forAll(strings().allPossible().ofLength(100))
    .check(s -> s.length() == 100);
  }

  @Test
  public void boundedLengthBase32() {
    Gen<String> testee = strings().base32().ofLengthBetween(3, 200);
    qt()
    .withExamples(100000)
    .forAll(testee)
    .check( s -> s.length() <= 200 && s.length() >= 3 && isValidBase32(s));
  }

  @Test
  public void boundedLengthGeohash() {
    Gen<String> testee = strings().geohash().ofLengthBetween(3, 200);
    qt()
    .withExamples(100000)
    .forAll(testee)
    .check( s -> s.length() <= 200 && s.length() >= 3 && isValidGeohash(s));
  }

  private static boolean isValidBase32(final String str) {
    for (int i = 0, len = str.length(); i < len; i++) {
      char c = str.charAt(i);
      if (!(('A' <= c && c <= 'Z') || ('2' <= c & c <= '7'))) {
        return false;
      }
    }
    return true;
  }

  private static boolean isValidGeohash(final String str) {
    for (int i = 0, len = str.length(); i < len; i++) {
      char c = str.charAt(i);
      // geohash has gaps in the alphabet, see https://en.wikipedia.org/wiki/Geohash for range
      if (!(('0' <= c & c <= '9') || ('b' <= c && c <= 'h') || ('j' <= c && c <= 'k') || ('m' <= c && c <= 'n') || ('p' <= c && c <= 'z'))) {
        return false;
      }
    }
    return true;
  }

}
