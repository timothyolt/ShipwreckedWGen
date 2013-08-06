package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.Arrays;

public class StructureSection {

    private StructureChunk chunk;
    private final int yIndex;
    private byte[] blocks;
    private byte[] add;
    private byte[] data;
    private byte[] passive;

    public StructureSection(StructureChunk chunk, int yIndex){
        this.chunk = chunk;
        this.yIndex = yIndex;
        blocks = new byte[4096];
        data = new byte[4096];
        passive = new byte[512];
        Arrays.fill(passive, (byte) 0xFF);
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
        int index = (256 * x) + (16 * z) + y;
        return blocks[index];
    }

    public void setBlockId(int x, int y, int z, int id){
        int index = (256 * x) + (16 * z) + y;
        blocks[index] = (byte) id;
    }

    public byte[] getData(){
        return data;
    }

    public byte getBlockData(int x, int y, int z){
        int index = (256 * x) + (16 * z) + y;
        return data[index];
    }

    public void setBlockData(int x, int y, int z, byte data){
        int index = (256 * x) + (16 * z) + y;
        this.data[index] = data;
    }

    public byte[] getPassive(){
        return passive;
    }

    public boolean getBlockPassive(int x, int y, int z){
        int index = (32 * x) + (2 * z) + (int)Math.floor(y / 8);
        switch (y){
            case 0: case 8:  return (passive[index] & 0x01) != 0;
            case 1: case 9:  return (passive[index] & 0x02) != 0;
            case 2: case 10: return (passive[index] & 0x04) != 0;
            case 3: case 11: return (passive[index] & 0x08) != 0;
            case 4: case 12: return (passive[index] & 0x10) != 0;
            case 5: case 13: return (passive[index] & 0x20) != 0;
            case 6: case 14: return (passive[index] & 0x40) != 0;
            case 7: case 15: return (passive[index] & 0x80) != 0;
            default: return false;
        }
    }

    public void setBlockPassive(int x, int y, int z, boolean bool){
        int index = (32 * x) + (2 * z) + (int)Math.floor(y / 8);
        switch (y){
            case 0: case 8:  
                if (bool) passive[index] = (byte)(passive[index] | 0x01);
                else passive[index] = (byte)~((~passive[index]) | 0x01);
            case 1: case 9:  
                if (bool) passive[index] = (byte)(passive[index] | 0x02);
                else passive[index] = (byte)~((~passive[index]) | 0x02);
            case 2: case 10:  
                if (bool) passive[index] = (byte)(passive[index] | 0x04);
                else passive[index] = (byte)~((~passive[index]) | 0x04);
            case 3: case 11:  
                if (bool) passive[index] = (byte)(passive[index] | 0x08);
                else passive[index] = (byte)~((~passive[index]) | 0x08);
            case 4: case 12:  
                if (bool) passive[index] = (byte)(passive[index] | 0x10);
                else passive[index] = (byte)~((~passive[index]) | 0x10);
            case 5: case 13:  
                if (bool) passive[index] = (byte)(passive[index] | 0x20);
                else passive[index] = (byte)~((~passive[index]) | 0x20);
            case 6: case 14:  
                if (bool) passive[index] = (byte)(passive[index] | 0x40);
                else passive[index] = (byte)~((~passive[index]) | 0x40);
            case 7: case 15:  
                if (bool) passive[index] = (byte)(passive[index] | 0x80);
                else passive[index] = (byte)~((~passive[index]) | 0x80);
        }
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

    protected void addPassiveArray(byte[] passive){
        this.passive = passive;
    }
}