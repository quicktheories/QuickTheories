package org.quicktheories.quicktheories.generators;

import java.util.Date;

import org.quicktheories.quicktheories.core.Source;

public class DatesDSL {

  public Source<Date> withMilliSeconds(long milliSecondsFromEpoch) {
    lowerBoundGEQZero(milliSecondsFromEpoch);
    return Compositions.weightWithValues(
        Dates.withMilliSeconds(milliSecondsFromEpoch),
        new Date(milliSecondsFromEpoch));
  }

  public Source<Date> withMilliSecondsBetween(
      long milliSecondsFromEpochStartInclusive,
      long milliSecondsFromEpochEndInclusive) {
    lowerBoundGEQZero(milliSecondsFromEpochStartInclusive);
    maxGEQMin(milliSecondsFromEpochStartInclusive,
        milliSecondsFromEpochEndInclusive);
    return Compositions.weightWithValues(
        Dates.withMilliSecondsBetween(milliSecondsFromEpochStartInclusive,
            milliSecondsFromEpochEndInclusive),
        new Date(milliSecondsFromEpochEndInclusive),
        new Date(milliSecondsFromEpochStartInclusive));
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
