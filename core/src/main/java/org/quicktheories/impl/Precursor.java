package org.quicktheories.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

class Precursor {
  
  private static final int INITIAL_SIZE = 32;
  
  private ByteBuffer buffer = ByteBuffer.allocate(1);
  private final List<Constraint> constraints;
  
  Precursor() {
    this.buffer = ByteBuffer.allocate(INITIAL_SIZE);
    this.constraints = new ArrayList<>();
  }
  
  boolean isEmpty() {
    return buffer.position() == 0;
  }
  
  byte[] bytes() {
    return Arrays.copyOf(buffer.array(),buffer.position());
  }
    
  void store(long l, Constraint constraint) {
    addToBuffer(l);
    storeConstraints(constraint);
  }
  
  long[] current() {
    long[] out = longArraySizedForBuffer();
    ByteBuffer view = buffer.duplicate();
    view.position(0);
    view.asLongBuffer().get(out);
    return out;
  }

  
  long[] maxLimit() {
    long[] max = longArraySizedForBuffer();
    for (int i = 0; i != constraints.size(); i++) {
      max[i] = constraints.get(i).max();
    }
    return max;
  }

  long[] minLimit() {
    long[] max = longArraySizedForBuffer();
    for (int i = 0; i != constraints.size(); i++) {
      max[i] = constraints.get(i).min();
    }
    return max;
  }

  long[] shrinkTarget() {
    long[] max = longArraySizedForBuffer();
    for (int i = 0; i != constraints.size(); i++) {
      max[i] = constraints.get(i).shrinkTarget().orElse(constraints.get(i).min());
    }
    return max;
  }  
  
  OptionalLong shrinkTarget(int index) {
    return constraints.get(index).shrinkTarget();
  }
  
  long min(int index) {
    return constraints.get(index).min();
  }
  
  long max(int index) {
    return constraints.get(index).max();
  }
  
  
  private long[] longArraySizedForBuffer() {
    return new long[buffer.position() / Long.BYTES];
  }
  
  private void addToBuffer(long l) {
    checkBufferSize(Long.BYTES);
    buffer.putLong(l);
  }


  private void storeConstraints(Constraint constraints) {
    this.constraints.add(constraints);
  }

  public void combine(Precursor other) {
    byte[] bs = other.bytes();
    checkBufferSize(bs.length);
    buffer.put(bs, 0, bs.length);
    constraints.addAll(other.constraints);  
  }

  private void checkBufferSize(int required) {
    if (buffer.remaining() < required) {
      int growStep = buffer.capacity()  * 2;
      ByteBuffer tmp =  ByteBuffer.allocate(buffer.capacity() + Math.max(required,growStep));
      tmp.put(bytes());
      buffer = tmp;
    }
  }
  
}
