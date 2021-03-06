package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Entity;
import org.bytefire.libnbt.Tag;

public class StructureChunk {

    private Structure struct;
    private int xPos;
    private int zPos;
    private HashMap<Integer, StructureSection> sections;
    private ArrayList<Entity> entities; //TODO: entity support
    private ArrayList<Tag> tileEntities; //TODO: tile entity support

    public StructureChunk(Structure struct, int xPos, int zPos){
        this.struct = struct;
        this.xPos = xPos;
        this.zPos = zPos;
        sections = new HashMap<Integer, StructureSection>();
        entities = new ArrayList<Entity>();
        tileEntities = new ArrayList<Tag>();
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

    protected void setXPos(int xPos){
        this.xPos = xPos;
    }

    protected void setZPos(int zPos){
        this.zPos = zPos;
    }

    public HashMap<Integer, StructureSection> getAllSections(){
        return sections;
    }

    public StructureSection getSection(int yIndex){
        return getSection(yIndex, true);
    }

    public StructureSection getSection(int yIndex, boolean generate){
        if (sections.containsKey(yIndex)) return sections.get(yIndex);
        if (generate) {
            StructureSection newSection = new StructureSection(this, yIndex);
            sections.put(yIndex, newSection);
            return newSection;
        }
        return null;
    }

    protected void addSection(StructureSection sect){
        if (!sections.containsKey(sect.getYIndex())) sections.put(sect.getYIndex(), sect);
    }

    public int getBlockId(int x, int y, int z){
        StructureSection sect = getSection(y >> 4, false);
        if (sect == null) return 0;
        return sect.getBlockId(x, y & 0xF, z);
    }

    public void setBlockId(int x, int y, int z, int id){
        StructureSection sect = getSection(y >> 4, false);
        if (sect == null) return;
        sect.setBlockId(x, y & 0xF, z, id);
    }

    public byte getBlockData(int x, int y, int z){
        StructureSection sect = getSection(y >> 4, false);
        if (sect == null) return 0;
        return sect.getBlockData(x, y & 0xF, z);
    }

    public void setBlockData(int x, int y, int z, byte data){
        StructureSection sect = getSection(y >> 4, false);
        if (sect == null) return;
        sect.setBlockData(x, y & 0xF, z, data);
    }

    public boolean getBlockPassive(int x, int y, int z){
        StructureSection sect = getSection(y >> 4, false);
        if (sect == null) return true;
        return sect.getBlockPassive(x, y & 0xF, z);
    }

    public void setBlockPassive(int x, int y, int z, boolean passive){
        StructureSection sect = getSection(y >> 4);
        if (sect == null) return;
        sect.setBlockPassive(x, y & 0xF, z, passive);
    }
    
    public ArrayList<Tag> getTileEntities(){
        return tileEntities;
    }
    
    public void addTileEntity(Tag tile){
        tileEntities.add(tile);
    }
    
    public void setTileEntities(ArrayList<Tag> tileEntities){
        this.tileEntities = tileEntities;
    }
}
