package com.example;

public class HayStack {
  public static boolean findMe(int i, int j, int k) {
    if (i > 10000 && i < 6000000) {
      if (j < 2000) {
        if (k > 100000000 && k < 1000000000) {
          return false;
        }
      }
    }
    return true;
  }
}
