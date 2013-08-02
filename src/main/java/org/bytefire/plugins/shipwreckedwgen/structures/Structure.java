package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
        if (chunks.containsKey(hash)) return chunks.get(hash);
        StructureChunk chunk = new StructureChunk(this, (int)(hash & 0xffffffff), (int)(hash >> 32));
        chunks.put(hash, chunk);
        return chunk;
    }

    public StructureChunk getChunk(int x, int z){
        return getChunk((((long)x) << 32) + (long)z);
    }

    public StructureChunk getChunk(Location loc){
        return getChunk((int)loc.getX() >> 4, (int)loc.getY() >> 4);
    }

    protected HashMap<Long,StructureChunk> getAllChunks(){
        return chunks;
    }

    protected void addChunk(StructureChunk chunk){
        long hash = (((long)chunk.getXPos()) << 32) + (long)chunk.getZPos();
        if (!chunks.containsKey(hash)) chunks.put(hash, chunk);
    }

    public int getBlockId(int x, int y, int z){
        return getChunk(x >> 4, z >> 4).getSection(y >> 4).getBlockId(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockId(int x, int y, int z, int id){
        getChunk(x >> 4, z >> 4).getSection(y >> 4).setBlockId(x, y - ((y >> 4) * 16), z, id);
    }

    public byte getBlockData(int x, int y, int z){
        return getChunk(x >> 4, z >> 4).getSection(y >> 4).getBlockData(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockData(int x, int y, int z, byte data){
        getChunk(x >> 4, z >> 4).getSection(y >> 4).setBlockData(x, y - ((y >> 4) * 16), z, data);
    }

    public boolean getBlockPassive(int x, int y, int z){
        return getChunk(x >> 4, z >> 4).getSection(y >> 4).getBlockPassive(x, y - ((y >> 4) * 16), z);
    }

    public void setBlockPassive(int x, int y, int z, boolean passive){
        getChunk(x >> 4, z >> 4).getSection(y >> 4).setBlockPassive(x, y - ((y >> 4) * 16), z, passive);
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

    public void save(){
        StructureHandler handle = ((ShipwreckedWGen) Bukkit.getPluginManager().getPlugin("ShipGen")).getStructureHandler();
        ArrayList<Long> queuedUpdates = handle.getQueuedUpates(getName());
        if (queuedUpdates == null) return;
        World world = Bukkit.getServer().getWorld(getName());

        for (Long update : queuedUpdates){
            int x = (int)(update >> 32);
            int z = (int)(update & 0xFFFFFFFF);

            ChunkSnapshot chunk = world.getChunkAt(x, z).getChunkSnapshot();

            for (int y = 0; y < world.getMaxHeight() / 3; y++){
                if (!chunk.isSectionEmpty(y)){
                    for (int xx = 0; xx < 16; xx ++) for (int yy = 0; yy < 16; yy ++) for (int zz = 0; zz < 16; zz ++){
                        getChunk(x, z).getSection(y).setBlockId(xx, yy, zz, chunk.getBlockTypeId(xx, yy + (y * 16), zz));
                        getChunk(x, z).getSection(y).setBlockData(xx, yy, zz, (byte) chunk.getBlockData(xx, yy + (y * 16), zz));
                    }
                }
            }
        }

        StructureLoader.saveStructure(this);
        handle.clearQueuedUpdates(getName());
    }

}