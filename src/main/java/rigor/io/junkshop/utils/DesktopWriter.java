package rigor.io.junkshop.utils;

import java.io.File;

public class DesktopWriter {
  private static final String SEPARATOR = File.separator;
  private static final String DESKTOP = System.getProperty("user.home") + "/Desktop";

  public static String path() {
    return DESKTOP + SEPARATOR;
  }
}
