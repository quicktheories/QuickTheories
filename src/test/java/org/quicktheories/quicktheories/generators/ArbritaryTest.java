package org.quicktheories.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.mockito.Mockito;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.core.RandomnessSource;

public class ArbritaryTest {

  @Test
  public void shouldReturnValuesFromConstant() {
    Gen<Integer> testee = Generate.constant(42);
    RandomnessSource unused = Mockito.mock(RandomnessSource.class);
    assertThat(testee.generate(unused)).isEqualTo(42);
    assertThat(testee.generate(unused)).isEqualTo(42);
  }
  
  @Test
  public void shouldReturnValuesFromSupplier() {
    Gen<Integer> testee = Generate.constant(() -> 42);
    RandomnessSource unused = Mockito.mock(RandomnessSource.class);
    assertThat(testee.generate(unused)).isEqualTo(42);
    assertThat(testee.generate(unused)).isEqualTo(42);
  }


  @Test
  public void shouldReturnAllItemsInListWhenPickingRandomly() {
    Gen<String> testee = Generate
        .pick(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatGenerator(testee).generatesAllOf("a", "1", "b", "2");
  }

  @Test
  public void shouldShrinkTowardsFirstItemInList() {
    Gen<String> testee = Generate
        .pick(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatGenerator(testee).shrinksTowards("a");
  }

  @Test
  public void shouldNotShrinkInAnyParticularDirectionWhenNoShrinkPointRequested() {
    Gen<String> testee = Generate.pickWithNoShrinkPoint(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatGenerator(testee).hasNoShrinkPoint();
  }
  
  @Test
  public void shouldReturnAllItemsInListWhenPickingRandomlyWithoutShrinkPoint() {
    Gen<String> testee = Generate
        .pickWithNoShrinkPoint(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatGenerator(testee).generatesAllOf("a", "1", "b", "2");
  }

  @Test
  public void shouldRandomlySelectEnumValues() {
    Gen<AnEnum> testee = Generate.enumValues(AnEnum.class);
    assertThatGenerator(testee).generatesAllOf(AnEnum.A, AnEnum.B, AnEnum.C,
        AnEnum.D, AnEnum.E);
  }

  @Test
  public void shouldShrinkEnumsTowardsFirstDefinedConstant() {
    Gen<AnEnum> testee = Generate.enumValues(AnEnum.class);
    assertThatGenerator(testee).shrinksTowards(AnEnum.A);
  }
 
  
  static enum AnEnum {
    A, B, C, D, E;
  }
}
