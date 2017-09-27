package org.quicktheories.impl;

final class AttemptsExhaustedException extends RuntimeException {

  AttemptsExhaustedException(String msg) {
    super(msg);
  }

  private static final long serialVersionUID = 1L;

}
