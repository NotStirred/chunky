package se.llbit.util;

import org.junit.Test;

import java.util.Random;

public class TestUnsafe {

  @Test
  public void testUnsafe() {
    UnsafeIntArray arr = new UnsafeIntArray(10);
    Random r = new Random(0);
    for (int i = 0; i < arr.size(); i++) {
      arr.set(i, r.nextInt());
    }

    for (int i = 0; i < 10; i++) {

    }
  }
}
