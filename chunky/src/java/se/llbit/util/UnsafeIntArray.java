package se.llbit.util;

import static sun.misc.Unsafe.getUnsafe;

public class UnsafeIntArray {
  private long address;

  private final static int INT_BYTE_SIZE = 4;

  public UnsafeIntArray(long size) {
    address = getUnsafe().allocateMemory(size * INT_BYTE_SIZE);
  }

  public void set(long idx, int val) {
    getUnsafe().putInt(address + idx * INT_BYTE_SIZE, val);
  }

  public int get(long idx) {
    return getUnsafe().getInt(address + idx * INT_BYTE_SIZE);
  }

  public void free() {
    getUnsafe().freeMemory(address);
  }
}
