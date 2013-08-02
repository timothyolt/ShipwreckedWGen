package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.Arrays;

public class StructureSection {

    private StructureChunk chunk;
    private final int yIndex;
    private byte[][][] blocks;
    private byte[][][] add;
    private byte[][][] data;
    private boolean[][][] passive;

    public StructureSection(StructureChunk chunk, int yIndex){
        this.chunk = chunk;
        this.yIndex = yIndex;
        blocks = new byte[16][16][16];
        data = new byte[16][16][16];
        passive = new boolean[16][16][16];
        for (boolean[][] high : passive) for (boolean[] low : high) {
            Arrays.fill(low, true);
        }
    }

    public StructureChunk getChunk(){
        return chunk;
    }

    public int getYIndex(){
        return yIndex;
    }

    public byte[][][] getBlocks(){
        return blocks;
    }

    public int getBlockId(int x, int y, int z){
        if (add == null || add[x][y][z] == 0) return blocks[x][y][z];
        return (add[x][y][z] << 8) + blocks[x][y][z];
    }

    public void setBlockId(int x, int y, int z, int id){
        if (id < 256) blocks[x][y][z] = (byte)id;
        else {
            if (add == null) add = new byte[16][16][16];
            int blockMask = 0xf;
            blocks[x][y][z] = (byte)(id & blockMask);
            add[x][y][z] = (byte)(id >> 4);
        }
    }

    public byte[][][] getData(){
        return data;
    }

    public byte getBlockData(int x, int y, int z){
        return data[x][y][z];
    }

    public void setBlockData(int x, int y, int z, byte data){
        this.data[x][y][z] = data;
    }

    public boolean[][][] getPassive(){
        return passive;
    }

    public boolean getBlockPassive(int x, int y, int z){
        return passive[x][y][z];
    }

    public void setBlockPassive(int x, int y, int z, boolean passive){
        this.passive[x][y][z] = passive;
    }

    protected void addBlockArray(byte[][][] blocks){
        this.blocks = blocks;
    }

    protected void addAddArray(byte[][][] add){
        this.add = add;
    }

    protected void addDataArray(byte[][][] data){
        this.data = data;
    }

    protected void addPassiveArray(boolean[][][] passive){
        this.passive = passive;
    }
}