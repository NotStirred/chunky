package se.llbit.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static sun.misc.Unsafe.getUnsafe;

public class UnsafeIntArray {
  private long address;
  private long size;

  private boolean freed = false;

  private static int allocs = 0;
  private static int frees = 0;
  private static int finalize_frees = 0;

  private static Unsafe unsafe = null;
  static {
    Field f = null;
    try {
      f = Unsafe.class.getDeclaredField("theUnsafe");
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    f.setAccessible(true);
    try {
      unsafe = (Unsafe) f.get(null);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private final static int INT_BYTE_SIZE = 4;

  public UnsafeIntArray(long size) {
    freed = false;
    allocs++;
    this.size = size;
    address = unsafe.allocateMemory(size * INT_BYTE_SIZE);
    unsafe.setMemory(address, size*INT_BYTE_SIZE, (byte) 0);
    System.out.println(address + " Allocs: " + allocs);
  }

  public void set(long idx, int val) {
    unsafe.putInt(address + idx * INT_BYTE_SIZE, val);
  }

  public int get(long idx) {
    return unsafe.getInt(address + idx * INT_BYTE_SIZE);
  }

  public void free() {
    if(!freed) {
      frees++;
      System.out.println(address + " Frees: " + frees);

      freed = true;
      unsafe.freeMemory(address);
    } else {
      System.out.println("Attempted to free already freed UnsafeIntArray " + address);
    }
  }

  public void finalize() {
    finalizeFree();
  }

  public void finalizeFree() {
    if(!freed) {
      frees++;
      System.out.println("Frees: " + frees);
      System.out.println("Did not free UnsafeIntArray");
      unsafe.freeMemory(address);
    }
    finalize_frees++;
    System.out.println("Finalize Frees: " + finalize_frees);
  }

  public long size() {
    return size;
  }

  public long address() {
    return address;
  }

  public void copyTo(UnsafeIntArray other) {
    unsafe.copyMemory(address, other.address, size*INT_BYTE_SIZE);
  }
}
