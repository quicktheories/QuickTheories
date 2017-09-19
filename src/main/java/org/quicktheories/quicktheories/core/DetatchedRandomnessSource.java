package org.quicktheories.quicktheories.core;

public interface DetatchedRandomnessSource extends RandomnessSource {

  void commit();

}
