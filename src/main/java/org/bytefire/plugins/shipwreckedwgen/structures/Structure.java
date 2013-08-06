package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class Structure {

    private String name;
    private Location origin;
    private StructureType type;
    private HashMap<Long,StructureChunk> chunks;
    private Biome biome;
    private int yMax;
    private int yMin;
    private boolean growFromBounds;

    public enum StructureType {SURFACE, AIR, UNDERGROUND};

    public Structure(String name, Location origin, StructureType type){
        this.name = name;
        this.origin = origin;
        this.type = type;
        chunks = new HashMap<Long,StructureChunk>();
        biome = null;
        yMax = 255;
        yMin = 0;
        growFromBounds = true;
    }

    public String getName(){
        return name;
    }

    public Location getOrigin(){
        return origin;
    }

    public void setOrigin(Location origin){
        this.origin = origin;
    }

    public StructureType getType(){
        return type;
    }

    public void setType(StructureType type){
        this.type = type;
    }
    
    public StructureChunk getChunk(Long hash){
        return getChunk(hash, true);
    }

    public StructureChunk getChunk(Long hash, boolean generate){
        if (chunks.containsKey(hash)) return chunks.get(hash);
        if (generate) {
            StructureChunk chunk = new StructureChunk(this, (int)(hash >> 32), (int)(hash & 0xffffffff));
            chunks.put(hash, chunk);
            return chunk;
        }
        return null;
    }
    
    public StructureChunk getChunk(int x, int z){
        return getChunk(x, z, true);
    }

    public StructureChunk getChunk(int x, int z, boolean generate){
        return getChunk((((long)x) << 32) + (long)z, generate);
    }
    public StructureChunk getChunk(Location loc){
        return getChunk(loc, true);
    }

    public StructureChunk getChunk(Location loc, boolean generate){
        return getChunk((int)loc.getX() >> 4, (int)loc.getZ() >> 4, generate);
    }

    protected HashMap<Long,StructureChunk> getAllChunks(){
        return chunks;
    }

    protected void addChunk(StructureChunk chunk){
        long hash = (((long)chunk.getXPos()) << 32) + (long)chunk.getZPos();
        if (!chunks.containsKey(hash)) chunks.put(hash, chunk);
    }

    public int getBlockId(int x, int y, int z){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return 0;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return 0;
        return sect.getBlockId(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockId(int x, int y, int z, int id){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return;
        sect.setBlockId(x, y - ((y >> 4) * 16), z, id);
    }

    public byte getBlockData(int x, int y, int z){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return 0;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return 0;
        return sect.getBlockData(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockData(int x, int y, int z, byte data){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return;
        sect.setBlockData(x, y - ((y >> 4) * 16), z, data);
    }

    public boolean getBlockPassive(int x, int y, int z){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return true;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return true;
        return sect.getBlockPassive(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockPassive(int x, int y, int z, boolean passive){
        StructureChunk chunk = getChunk(x >> 4, z >> 4, false);
        if (chunk == null) return;
        StructureSection sect = chunk.getSection(y >> 4, false);
        if (sect == null) return;
        sect.setBlockPassive(x, y - ((y >> 4) * 16), z, passive);
    }

    public Biome getRequiredBiome(){
        return biome;
    }

    public void setRequiredBiome(Biome biome){
        this.biome = biome;
    }

    public int getMaxHeight(){
        return yMax;
    }

    public void setMaxHeight(int yMax){
        this.yMax = yMax;
    }

    public int getMinHeight(){
        return yMin;
    }

    public void setMinHeight(int yMin){
        this.yMin = yMin;
    }

    public boolean canGrowFromBounds(){
        return growFromBounds;
    }

    public void setGrowFromBounds(boolean growFromBounds){
        this.growFromBounds = growFromBounds;
    }

    public void update(){
        StructureHandler handle = ((ShipwreckedWGen) Bukkit.getPluginManager().getPlugin("ShipGen")).getStructureHandler();
        ArrayList<Long> rawUpdates = handle.getQueuedUpates(getName());
        if (rawUpdates == null) return;
        ArrayList<Long> queuedUpdates = (ArrayList<Long>) rawUpdates.clone();
        World world = Bukkit.getServer().getWorld(getName());

        for (Long update : queuedUpdates){
            int x = (int)(update & 0xFFFFFFFF);
            int z = (int)(update >> 32);
            ChunkSnapshot chunk = world.getChunkAt(x, z).getChunkSnapshot();
            
            for (int y = 0; y < world.getMaxHeight() / 16; y++){
                if (!chunk.isSectionEmpty(y)){
    System.out.println("Queue: " + Integer.toString(x) + ", " + Integer.toString(z));
                    for (int xx = 0; xx < 16; xx ++) for (int yy = 0; yy < 16; yy ++) for (int zz = 0; zz < 16; zz ++){
                        getChunk(update, true).getSection(y).setBlockId(xx, yy, zz, chunk.getBlockTypeId(xx, yy + (y * 16), zz));
                        getChunk(update, true).getSection(y).setBlockData(xx, yy, zz, (byte) chunk.getBlockData(xx, yy + (y * 16), zz));
                    }
                }
            }
        }

        StructureLoader.saveStructure(this);
        //handle.clearQueuedUpdates(getName());
    }

}