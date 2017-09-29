package org.quicktheories.core;

public interface DetatchedRandomnessSource extends RandomnessSource {

  void commit();

}
