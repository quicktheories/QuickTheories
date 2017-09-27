package org.quicktheories.quicktheories;

/**
 * I'm an integer but my too string method
 * appends "Foo".
 * 
 * Use me in your enterprise apps
 * 
 *
 */
public class FooInteger {
  private final Integer i;

  public FooInteger(Integer i) {
    super();
    this.i = i;
  }

  @Override
  public String toString() {
    return "Foo " + i;
  }
  
  
}
