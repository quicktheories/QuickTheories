package org.quicktheories;

import org.quicktheories.core.GuidanceFactory;
import org.quicktheories.core.NoGuidance;
import org.quicktheories.generators.ArbitraryDSL;
import org.quicktheories.generators.ArraysDSL;
import org.quicktheories.generators.BigDecimalsDSL;
import org.quicktheories.generators.BigIntegersDSL;
import org.quicktheories.generators.BooleansDSL;
import org.quicktheories.generators.CharactersDSL;
import org.quicktheories.generators.DatesDSL;
import org.quicktheories.generators.DoublesDSL;
import org.quicktheories.generators.FloatsDSL;
import org.quicktheories.generators.IntegersDSL;
import org.quicktheories.generators.ListsDSL;
import org.quicktheories.generators.LocalDatesDSL;
import org.quicktheories.generators.LongsDSL;
import org.quicktheories.generators.MapsDSL;
import org.quicktheories.generators.SourceDSL;
import org.quicktheories.generators.StringsDSL;

public interface WithQuickTheories {
  
  default QuickTheory qt() {
    return QuickTheory.qt();
  }
  
  default LongsDSL longs() {
    return SourceDSL.longs();
  }

  default IntegersDSL integers() {
    return SourceDSL.integers();
  }

  default DoublesDSL doubles() {
    return SourceDSL.doubles();
  }

  default FloatsDSL floats() {
    return SourceDSL.floats();
  }

  default CharactersDSL characters() {
    return SourceDSL.characters();
  }

  default StringsDSL strings() {
    return SourceDSL.strings();
  }

  default ListsDSL lists() {
    return SourceDSL.lists();
  }

  default MapsDSL maps() {
    return SourceDSL.maps();
  }

  default ArraysDSL arrays() {
    return SourceDSL.arrays();
  }

  default BigIntegersDSL bigIntegers() {
    return SourceDSL.bigIntegers();
  }

  default BigDecimalsDSL bigDecimals() {
    return SourceDSL.bigDecimals();
  }

  default ArbitraryDSL arbitrary() {
    return SourceDSL.arbitrary();
  }

  default DatesDSL dates() {
    return SourceDSL.dates();
  }

  default LocalDatesDSL localDates() {
    return SourceDSL.localDates();
  }
  
  default BooleansDSL booleans() {
    return SourceDSL.booleans();
  }
  
  default GuidanceFactory noGuidance() {
    return prng -> new NoGuidance();
  }
 
}
