package com.example;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Generate;

public class MatrixExample implements WithQuickTheories {

  @Test
  public void foo() {

    
    Gen<Integer> dimensions = integers().between(1, 100);
    Gen<Integer> contents = integers().between(1, 100);    
    
    Gen<Matrix> matrices = Generate.intArrays(dimensions, dimensions, contents)
        .map(arr -> new Matrix(arr));
         
    
    
  }
  
  
}
