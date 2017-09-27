package org.quicktheories.coverage;

public class FileUtil {
  public static String randomFilename() {
    return System.currentTimeMillis()
        + ("" + Math.random()).replaceAll("\\.", "");
  }

}
