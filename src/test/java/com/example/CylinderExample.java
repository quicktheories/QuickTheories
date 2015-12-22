package com.example;

import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Source;

public class CylinderExample {

  static class Cylinder {
    private final int radius;
    private final int height;

    Cylinder(int radius, int height) {
      this.radius = radius;
      this.height = height;
    }

    int radius() {
      return radius;
    }

    int height() {
      return height;
    }

    BigDecimal area() {
      BigDecimal r = new BigDecimal(radius);
      BigDecimal h = new BigDecimal(height);

      return BigDecimal.valueOf(Math.PI)
          .multiply(BigDecimal.valueOf(2))
          .multiply(r)
          .multiply(r.add(h));
    }

    // @Override
    // public String toString() {
    // return "Cylinder [radius=" + radius + ", height=" + height + ", area"
    // + area() + "]";
    // }

  }

  @Test
  public void someTestInvolvingCylinders() {
    qt()
        .forAll(integers().allPositive().describedAs(r -> "Radius = " + r),
            integers().allPositive().describedAs(h -> "Height = " + h))
        .as((r, h) -> new Cylinder(r, h))
        .describedAs(cylinder -> "Cylinder r =" + cylinder.radius() + " h ="
            + cylinder.height())
        .check(l -> false);
  }

  @Test
  public void someTestInvolvingCylinders2() {
    qt()
        .forAll(integers().allPositive().describedAs(r -> "Radius = " + r),
            integers().allPositive().describedAs(h -> "Height = " + h))
        .asWithPrecursor((r, h) -> new Cylinder(r, h),
            cylinder -> "Cylinder r =" + cylinder.radius() + " h ="
                + cylinder.height())
        .check((i, j, l) -> false);
  }

  @Test
  public void areaIsAlwaysPositive() {
    qt()
        .forAll(anyCylinder())
        .assuming(cylinder -> cylinder.height > 0 && cylinder.radius > 0)
        .check(cylinder -> cylinder.area().compareTo(BigDecimal.ZERO) > 1000);

  }

  private Source<Cylinder> anyCylinder() {
    return Source.of(cylinders()).withShrinker(shrinkCylinder());
  }

  private Shrink<Cylinder> shrinkCylinder() {
    return (original, context) -> IntStream.range(1, context.remainingCycles())
        .mapToObj(i -> new Cylinder(original.radius - i, original.height - i));
  }

  private Generator<Cylinder> cylinders() {
    return radii().combine(heights(),
        (radius, height) -> new Cylinder(radius, height));
  }

  private Source<Integer> heights() {
    return integers().from(79).upToAndIncluding(1004856);
  }

  private Source<Integer> radii() {
    return integers().allPositive();
  }

}
