package backend;

import backend.compiler.CodeGenerator;
import backend.interpreter.Executor;

public class BackendFactory {
  public static Backend createBackend(String operation) {
    if (operation.equalsIgnoreCase("compile")) {
      return new CodeGenerator();
    } else if (operation.equalsIgnoreCase("execute")) {
      return new Executor();
    } else {
      throw new IllegalArgumentException("invalid backend operation type: " + operation);
    }
  }
}
