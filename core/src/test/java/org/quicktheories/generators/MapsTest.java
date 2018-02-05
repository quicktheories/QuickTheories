package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.generators.Generate.constant;
import static org.quicktheories.generators.Generate.pick;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.quicktheories.core.Gen;

public class MapsTest {

  
  @Test
  public void mapsUseSuppliedKeysAndValues() {
    Gen<String> keys = pick(Arrays.asList("1","2","3"));
    Gen<Map<String, String>> testee = Maps.boundedMapsOf(keys, constant("aValue"), constant(1));
    
    assertThatGenerator(testee).generatesAllOf(map("1", "aValue"), map("2", "aValue"), map("3", "aValue"));
  }
  
  
  @Test
  public void mapsHaveReadableDescription() {
    Gen<Map<String, String>> testee = Maps.boundedMapsOf(constant("aKey"), constant("aValue"), constant(1));
    Map<String,String> aMap = new HashMap<>();
    aMap.put("a", "fish");
    aMap.put("b", "dish");
    String actual = testee.asString(aMap);
    
    assertThat(actual).isEqualToIgnoringWhitespace("[(a,fish), (b,dish)]");
  } 
  
  private static Map<String,String> map(String key, String value) {
    return Collections.singletonMap(key, value);
  }
}
