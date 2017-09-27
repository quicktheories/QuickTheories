package org.quicktheories;

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
import org.quicktheories.generators.SourceDSL;
import org.quicktheories.generators.StringsDSL;

public interface WithQuickTheories {
  
  default QuickTheory qt() {
    return QuickTheory.qt();
  }
  
  public default LongsDSL longs() {
    return SourceDSL.longs();
  }

  public default IntegersDSL integers() {
    return SourceDSL.integers();
  }

  public default DoublesDSL doubles() {
    return SourceDSL.doubles();
  }

  public default FloatsDSL floats() {
    return SourceDSL.floats();
  }

  public default CharactersDSL characters() {
    return SourceDSL.characters();
  }

  public default StringsDSL strings() {
    return SourceDSL.strings();
  }

  public default ListsDSL lists() {
    return SourceDSL.lists();
  }

  public default ArraysDSL arrays() {
    return SourceDSL.arrays();
  }

  public default BigIntegersDSL bigIntegers() {
    return SourceDSL.bigIntegers();
  }

  public default BigDecimalsDSL bigDecimals() {
    return SourceDSL.bigDecimals();
  }

  public default ArbitraryDSL arbitrary() {
    return SourceDSL.arbitrary();
  }

  public default DatesDSL dates() {
    return SourceDSL.dates();
  }

  public default LocalDatesDSL localDates() {
    return SourceDSL.localDates();
  }
  
  public default BooleansDSL booleans() {
    return SourceDSL.booleans();
  }
 
}
