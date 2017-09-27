package com.example;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.generators.Generate;

public class MatrixExample implements WithQuickTheories {

  @Test
  public void foo() {

    
    Gen<Integer> dimensions = integers().between(1, 100);
    Gen<Integer> contents = integers().between(1, 100);    
    
    Gen<Matrix> matrices = Generate.intArrays(dimensions, dimensions, contents)
        .map(arr -> new Matrix(arr));
         
    
    
  }
  
  
}
