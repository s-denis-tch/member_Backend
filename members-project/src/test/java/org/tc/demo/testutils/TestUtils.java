package org.tc.demo.testutils;

import static org.junit.Assert.fail;

public class TestUtils {
  @FunctionalInterface
  public interface RunnableEx {
    public void run() throws Exception;
  }

  public static Throwable expectThrows(RunnableEx ex) {
    try {
      ex.run();
    } catch (Throwable t) {
      return t;
    }
    fail("an exception is expected");
    return null;// never happens, fail must throw an ex
  }
}
