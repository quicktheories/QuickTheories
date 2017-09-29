package org.quicktheories.generators;

import java.util.Date;

import org.quicktheories.core.Gen;

/**
 * A Class for creating Date Sources that will produce Dates based on the number
 * of milliseconds since epoch
 */
public class DatesDSL {

  /**
   * Generates Dates inclusively bounded between January 1, 1970, 00:00:00 GMT
   * and new Date(milliSecondsFromEpoch). The Source restricts Date generation,
   * so that no Dates before 1970 can be created. The Source is weighted so it
   * is likely to produce new Date(millisecondsFromEpoch) one or more times.
   * 
   * @param millisecondsFromEpoch
   *          the number of milliseconds from the epoch such that Dates are
   *          generated within this interval.
   * @return a Source of type Date
   */
  public Gen<Date> withMilliseconds(long millisecondsFromEpoch) {
    lowerBoundGEQZero(millisecondsFromEpoch);
    return Dates.withMilliSeconds(millisecondsFromEpoch);
  }

  /**
   * Generates Dates inclusively bounded between new
   * Date(millisecondsFromEpochStartInclusive) and new
   * Date(millisecondsFromEpochEndInclusive).
   * 
   * @param millisecondsFromEpochStartInclusive
   *          the number of milliseconds from epoch for the desired older Date
   * @param millisecondsFromEpochEndInclusive
   *          the number of milliseconds from epoch for the desired more recent
   *          Date
   * @return a source of Dates
   */
  public Gen<Date> withMillisecondsBetween(
      long millisecondsFromEpochStartInclusive,
      long millisecondsFromEpochEndInclusive) {
    lowerBoundGEQZero(millisecondsFromEpochStartInclusive);
    maxGEQMin(millisecondsFromEpochStartInclusive,
        millisecondsFromEpochEndInclusive);
    return 
        Dates.withMilliSecondsBetween(millisecondsFromEpochStartInclusive,
            millisecondsFromEpochEndInclusive);
  }

  private void lowerBoundGEQZero(long milliSecondsFromEpoch) {
    ArgumentAssertions.checkArguments(milliSecondsFromEpoch >= 0,
        "A negative long (%s) is not an accepted number of milliseconds",
        milliSecondsFromEpoch);
  }

  private void maxGEQMin(long startInclusive, long endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "Cannot have the maximum long (%s) smaller than the minimum long value (%s)",
        endInclusive, startInclusive);
  }

  static class Dates {

    static Gen<Date> withMilliSeconds(long milliSecondsFromEpoch) {
      return withMilliSecondsBetween(0, milliSecondsFromEpoch);
    }

    static Gen<Date> withMilliSecondsBetween(
        long milliSecondsFromEpochStartInclusive,
        long milliSecondsFromEpochEndInclusive) {
      return Generate.longRange(milliSecondsFromEpochStartInclusive,
          milliSecondsFromEpochEndInclusive).map(l -> new Date(l));
    }
  }

}
