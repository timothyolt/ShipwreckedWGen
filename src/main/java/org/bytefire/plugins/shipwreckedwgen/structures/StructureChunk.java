package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public class StructureChunk {

    private Structure struct;
    private int xPos;
    private int zPos;
    private HashMap<Integer, StructureSection> sections;
    private ArrayList<Entity> entities; //TODO: entity support
    private ArrayList<BlockState> tileEntities; //TODO: tile entity support

    public StructureChunk(Structure struct, int xPos, int zPos){
        this.struct = struct;
        this.xPos = xPos;
        this.zPos = zPos;
        sections = new HashMap<Integer, StructureSection>();
        entities = new ArrayList<Entity>();
        tileEntities = new ArrayList<BlockState>();
    }

    public Structure getStructure(){
        return struct;
    }

    public int getXPos(){
        return xPos;
    }

    public int getZPos(){
        return zPos;
    }

    public HashMap<Integer, StructureSection> getAllSections(){
        return sections;
    }

    public StructureSection getSection(int yIndex){
        if (sections.containsKey(yIndex)) return sections.get(yIndex);
        StructureSection newSection = new StructureSection(this, yIndex);
        sections.put(yIndex, newSection);
        return newSection;
    }

    protected void addSection(StructureSection sect){
        if (!sections.containsKey(sect.getYIndex())) sections.put(sect.getYIndex(), sect);
    }

    public int getBlockId(int x, int y, int z){
        return getSection(y >> 4).getBlockId(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockId(int x, int y, int z, int id){
        getSection(y >> 4).setBlockId(x, y - ((y >> 4) * 16), z, id);
    }

    public byte getBlockData(int x, int y, int z){
        return getSection(y >> 4).getBlockData(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockData(int x, int y, int z, byte data){
        getSection(y >> 4).setBlockData(x, y - ((y >> 4) * 16), z, data);
    }

    public boolean getBlockPassive(int x, int y, int z){
        return getSection(y >> 4).getBlockPassive(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockPassive(int x, int y, int z, boolean passive){
        getSection(y >> 4).setBlockPassive(x, y - ((y >> 4) * 16), z, passive);
    }
}
