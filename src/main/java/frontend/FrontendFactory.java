package frontend;

import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalScanner;

public class FrontendFactory {
  public static Parser createParser(Source source, String language, String type) {
    if (language.equalsIgnoreCase("pascal") && type.equalsIgnoreCase("top-down")) {
      var scanner = new PascalScanner(source);
      return new PascalParserTD(scanner);
    } else if (!language.equalsIgnoreCase("pascal")) {
      throw new IllegalArgumentException("invalid language: " + language);
    } else {
      throw new IllegalArgumentException("invalid type: " + type);
    }
  }
}
