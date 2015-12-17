package org.quicktheories.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.core.ExceptionReporter;

public class ExceptionReporterTest {

  ExceptionReporter testee = new ExceptionReporter();

  @Test
  public void shouldIncludeSeedInFalsificationReport() throws Exception {
    long seed = 42;
    try {
      testee.falisification(42, 0, 0, Arrays.asList(0), a -> a.toString());
      throw new Exception("Should not reach here");
    } catch (AssertionError expected) {
      assertThat(expected.getMessage()).contains("" + seed);
    }
  }

  @Test
  public void shouldIncludeNumberOfExaminedExamplesInFalsificationReport()
      throws Exception {
    int examples = 100;
    try {
      testee.falisification(0, examples, 0, Arrays.asList(0),
          a -> a.toString());
      throw new Exception("Should not reach here");
    } catch (AssertionError expected) {
      assertThat(expected.getMessage()).contains("" + examples);
    }
  }

  @Test
  public void shouldIncludeSmallestFoundValueInFalsificationReport()
      throws Exception {
    int smallest = 101;
    try {
      testee.falisification(0, 0, smallest, Arrays.asList(0),
          a -> a.toString());
      throw new Exception("Should not reach here");
    } catch (AssertionError expected) {
      assertThat(expected.getMessage()).contains("" + smallest);
    }
  }

  @Test
  public void shouldIncludeAdditionalExamplesInFalsificationReport()
      throws Exception {
    try {
      testee.falisification(0, 0, 0, Arrays.asList(41, 42), a -> a.toString());
      throw new Exception("Should not reach here");
    } catch (AssertionError expected) {
      assertThat(expected.getMessage()).contains("41", "42");
    }
  }

  @Test
  public void shouldIncludeOnlyFirst10ValuesInReport() throws Exception {
    try {
      List<Object> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
      testee.falisification(0, 0, 0, values, a -> a.toString());
      throw new Exception("Should not reach here");
    } catch (AssertionError expected) {
      assertThat(expected.getMessage())
          .contains("1\n2\n3\n4\n5\n6\n7\n8\n9\n10");
      assertThat(expected.getMessage()).doesNotContain("11");
    }
  }

}
