package org.quicktheories.generators;

final class ArgumentAssertions {

  static void checkArguments(boolean expression, String errorMessageTemplate,
      Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalArgumentException(
          String.format(errorMessageTemplate, errorMessageArgs));
    }
  }

}
