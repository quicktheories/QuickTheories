package org.quicktheories.quicktheories.generators;

import java.util.Date;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating Date Sources that will produce Dates based on the number
 * of milliseconds since epoch
 */
public class DatesDSL {

  /**
   * Generates Dates inclusively bounded between January 1, 1970, 00:00:00 GMT
   * and new Date(milliSecondsFromEpoch). The Source restricts Date generation,
   * so that no Dates before 1970 can be created. . The Source is weighted so it
   * is likely to produce new Date(millisecondsFromEpoch) one or more times.
   * 
   * @param millisecondsFromEpoch
   *          the number of milliseconds from the epoch such that Dates are
   *          generated within this interval.
   * @return a Source of type Date
   */
  public Source<Date> withMilliseconds(long millisecondsFromEpoch) {
    lowerBoundGEQZero(millisecondsFromEpoch);
    return Compositions.weightWithValues(
        Dates.withMilliSeconds(millisecondsFromEpoch),
        new Date(millisecondsFromEpoch));
  }

  /**
   * Generates Dates inclusively bounded between new
   * Date(millisecondsFromEpochStartInclusive) and new
   * Date(millisecondsFromEpochEndInclusive).
   * 
   * The Source is weighted so it is likely to produce new
   * Date(millisecondsFromEpochStartInclusive) and new
   * Date(millisecondsFromEpochEndInclusive) one or more times.
   * 
   * @param millisecondsFromEpochStartInclusive
   *          the number of milliseconds from epoch for the desired older Date
   * @param millisecondsFromEpochEndInclusive
   *          the number of milliseconds from epoch for the desired more recent
   *          Date
   * @return a source of Dates
   */
  public Source<Date> withMillisecondsBetween(
      long millisecondsFromEpochStartInclusive,
      long millisecondsFromEpochEndInclusive) {
    lowerBoundGEQZero(millisecondsFromEpochStartInclusive);
    maxGEQMin(millisecondsFromEpochStartInclusive,
        millisecondsFromEpochEndInclusive);
    return Compositions.weightWithValues(
        Dates.withMilliSecondsBetween(millisecondsFromEpochStartInclusive,
            millisecondsFromEpochEndInclusive),
        new Date(millisecondsFromEpochEndInclusive),
        new Date(millisecondsFromEpochStartInclusive));
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

    static Source<Date> withMilliSeconds(long milliSecondsFromEpoch) {
      return Longs.range(0, milliSecondsFromEpoch).as(l -> new Date(l),
          d -> d.getTime());
    }

    static Source<Date> withMilliSecondsBetween(
        long milliSecondsFromEpochStartInclusive,
        long milliSecondsFromEpochEndInclusive) {
      return Longs.range(milliSecondsFromEpochStartInclusive,
          milliSecondsFromEpochEndInclusive).as(l -> new Date(l),
              d -> d.getTime());
    }
  }

}
