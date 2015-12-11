package org.quicktheories.quicktheories.generators;

import java.time.LocalDate;

import org.quicktheories.quicktheories.core.Source;

public class LocalDatesDSL {

  private static final int LOCAL_DATE_MIN_EPOCH_DAY_COUNT = -999999999;
  private static final int LOCAL_DATE_MAX_EPOCH_DAY_COUNT = 999999999;

  public Source<LocalDate> withDays(long daysFromEpoch) {
    lowerBoundGEQLongLocalDateMin(daysFromEpoch);
    return Compositions.weightWithValues(LocalDates.withDays(daysFromEpoch),
        LocalDate.ofEpochDay(daysFromEpoch));

  }

  public Source<LocalDate> withDaysBetween(long daysFromEpochStartInclusive,
      long daysFromEpochEndInclusive) {
    acceptableIntervalForLongLocalDate(daysFromEpochStartInclusive,
        daysFromEpochEndInclusive);
    maxGEQMin(daysFromEpochStartInclusive,
        daysFromEpochEndInclusive);
    return Compositions.weightWithValues(
        LocalDates.withDaysBetween(daysFromEpochStartInclusive,
            daysFromEpochEndInclusive),
        LocalDate.ofEpochDay(daysFromEpochEndInclusive),
        LocalDate.ofEpochDay(daysFromEpochStartInclusive));

  }

  private void acceptableIntervalForLongLocalDate(
      long daysFromEpochStartInclusive, long daysFromEpochEndInclusive) {
    ArgumentAssertions.checkArguments(
        LOCAL_DATE_MIN_EPOCH_DAY_COUNT <= daysFromEpochStartInclusive
            && daysFromEpochEndInclusive <= LOCAL_DATE_MAX_EPOCH_DAY_COUNT,
        "The long values representing the number of days from the epoch must be bounded between ["
            + LOCAL_DATE_MIN_EPOCH_DAY_COUNT + " , "
            + LOCAL_DATE_MAX_EPOCH_DAY_COUNT
            + "] . [%s , %s] is outside of these bounds.",
        daysFromEpochStartInclusive, daysFromEpochEndInclusive);
  }

  private void lowerBoundGEQLongLocalDateMin(long daysFromEpoch) {
    ArgumentAssertions.checkArguments(
        LOCAL_DATE_MIN_EPOCH_DAY_COUNT <= daysFromEpoch
            && daysFromEpoch <= LOCAL_DATE_MAX_EPOCH_DAY_COUNT,
        "The long value representing the number of days from the epoch must be bounded between ["
            + LOCAL_DATE_MIN_EPOCH_DAY_COUNT + " , "
            + LOCAL_DATE_MAX_EPOCH_DAY_COUNT
            + "] . %s is outside of these bounds.",
        daysFromEpoch);
  }

  private void maxGEQMin(long startInclusive, long endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "Cannot have the maximum long (%s) smaller than the minimum long value (%s)",
        endInclusive, startInclusive);
  }

  static class LocalDates {

    static Source<LocalDate> withDays(long daysFromEpoch) {
      if (daysFromEpoch < 0) {
        return Longs.range(daysFromEpoch, 0).as(l -> LocalDate.ofEpochDay(l),
            d -> d.toEpochDay());
      }
      return Longs.range(0, daysFromEpoch).as(l -> LocalDate.ofEpochDay(l),
          d -> d.toEpochDay());
    }

    static Source<LocalDate> withDaysBetween(long daysFromEpochStartInclusive,
        long daysFromEpochEndInclusive) {
      return Longs.range(daysFromEpochStartInclusive, daysFromEpochEndInclusive)
          .as(l -> LocalDate.ofEpochDay(l),
              d -> d.toEpochDay());
    }

  }

}
