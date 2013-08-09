package org.bytefire.plugins.shipwreckedwgen.structures;

public class StructureUtil {

    public static long mergeCoords(int x, int z){
        return ((long) x << 32) | ((long) z & 0xFFFFFFFFL);
    }

    public static int splitCoords(long xz, boolean left){
        if (left) return (int) (xz >> 32);
        else return (int) (xz | 0xFFFFFFFF00000000L);
    }
}
