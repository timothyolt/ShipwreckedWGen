package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.BitSet;

public class StructureSection {

    private StructureChunk chunk;
    private final int yIndex;
    private byte[] blocks;
    private byte[] add;
    private byte[] data;
    //private byte[] passive;
    private BitSet passive;

    public StructureSection(StructureChunk chunk, int yIndex){
        this.chunk = chunk;
        this.yIndex = yIndex;
        blocks = new byte[4096];
        data = new byte[4096];
        //passive = new byte[512];
        //Arrays.fill(passive, (byte) 0xFF);
        passive = new BitSet(4096);
    }

    public StructureChunk getChunk(){
        return chunk;
    }

    public int getYIndex(){
        return yIndex;
    }

    public byte[] getBlocks(){
        return blocks;
    }

    public int getBlockId(int x, int y, int z){
        int index = (y << 8) | (z << 4) | x;
        return blocks[index];
    }

    public void setBlockId(int x, int y, int z, int id){
        int index = (y << 8) | (z << 4) | x;
        blocks[index] = (byte) id;
    }

    public byte[] getData(){
        return data;
    }

    public byte getBlockData(int x, int y, int z){
        int index = (y << 8) | (z << 4) | x;
        return data[index];
    }

    public void setBlockData(int x, int y, int z, byte data){
        int index = (y << 8) | (z << 4) | x;
        this.data[index] = data;
    }

    public byte[] getPassive(){
        byte[] array = new byte[512];
        for (int i = 0; i < 4096; i++)
            if (passive.get(i)) array[i/8] |= 1 << (7 - i % 8);
        return array;
    }

    public boolean getBlockPassive(int x, int y, int z){
        return getBlockPassive((y << 8) | (z << 4) | x);
    }

    public boolean getBlockPassive(int index){
        return passive.get(index);
    }

    public void setBlockPassive(int x, int y, int z, boolean bool){
         setBlockPassive((y << 8) | (z << 4) | x, bool);
    }

    protected void setBlockPassive(int index, boolean bool){
        passive.set(index, bool);
    }

    protected void addBlockArray(byte[] blocks){
        this.blocks = blocks;
    }

    protected void addAddArray(byte[] add){
        this.add = add;
    }

    protected void addDataArray(byte[] data){
        this.data = data;
    }

    protected void addPassiveArray(byte[] array){
        for (int i = 0; i < 4096; i++){
            passive.set(i, (array[i/8] & (1 << (7 - i % 8))) != 0);
        }
    }
}