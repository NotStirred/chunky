package se.llbit.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import se.llbit.chunky.world.ChunkTexture;
import se.llbit.math.Octree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestPerf {

  @Test
  public void testHashMap() {
    testOldHashMap(10000);
    testNewHashMap(10000);
    testOldHashMap(10000);
    testNewHashMap(10000);
    testOldHashMap(10000);
    testNewHashMap(10000);

    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);
    testOldHashMap(1000000);
    testNewHashMap(1000000);

  }

  public void testOldHashMap(int n) {
    Map<Long, Float> map = new HashMap<>();
    Random r = new Random(0);

    long startTime = System.nanoTime();

    for (long i = 0; i < n; i++) {
      map.put(i, r.nextFloat());
    }
    long counter = 0;
    for (int i = 0; i < n*10; i++) {
      long l = r.nextLong();
      float f = map.get(Math.abs(l % n));
    }
//    System.out.println("Counter: " + counter);
    long endTime = System.nanoTime();
    System.out.println("old map time: " + (((float)endTime - startTime)/1000000000) + " s");
  }

  public void testNewHashMap(int n) {
    Long2ObjectOpenHashMap<Float> map = new Long2ObjectOpenHashMap<>();
    Random r = new Random(0);

    long startTime = System.nanoTime();

    for (long i = 0; i < n; i++) {
      map.put(i, (Float)r.nextFloat());
    }
    long counter = 0;
    for (int i = 0; i < n*10; i++) {
      long l = r.nextLong();
      float f = map.get(Math.abs(l % n));
    }
//    System.out.println("Counter: " + counter);
    long endTime = System.nanoTime();
    System.out.println("new map time: " + (((double)endTime - startTime)/1000000000) + " s");
  }

  @Test
  public void testPair() {
    testNew(10000);
    testOld(10000);
    testOld(10000);
    testNew(10000);
    testNew(10000);
    testOld(10000);

    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);
    testNew(1000000);
    testOld(1000000);

  }

  public void testNew(int n) {
    ArrayList<IntObjectImmutablePair<Octree.Node>> list = new ArrayList<>();
    Random r = new Random(0);

    long startTime = System.nanoTime();
    for (int i = 0; i < n; i++) {
      list.add(new IntObjectImmutablePair<>(r.nextInt(), new Octree.Node(r.nextInt())));
    }
    long counter = 0;
    for (int i = 0; i < n*10; i++) {
      counter += list.get(r.nextInt(n)).leftInt();
    }
//    System.out.println("Counter: " + counter);
    long endTime = System.nanoTime();
    System.out.println("New pair time: " + (((float)endTime - startTime)/1000000000) + "s");
  }

  public void testOld(int n) {
    ArrayList<Pair<Octree.Node, Integer>> list = new ArrayList<>();
    Random r = new Random(0);

    long startTime = System.nanoTime();
    for (int i = 0; i < n; i++) {
      list.add(new Pair<>(new Octree.Node(r.nextInt()), r.nextInt()));
    }
    long counter = 0;
    for (int i = 0; i < n*10; i++) {
      counter += list.get(r.nextInt(n)).getValue();
    }
//    System.out.println("Counter: " + counter);
    long endTime = System.nanoTime();
    System.out.println("Old pair time: " + (((float)endTime - startTime)/1000000000) + "s");
  }
}
