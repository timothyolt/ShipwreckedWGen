package me.darkeh.plugins.shipwreckedwgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.util.LongHash;

public class ChunkManager {
    private ShipwreckedWGen plugin;
    private HashMap<UUID, ArrayList<Long>> knownWorlds;
    public ChunkManager(ShipwreckedWGen plugin){
        this.plugin = plugin;
        knownWorlds = new HashMap<UUID, ArrayList<Long>>();
    }
    
    public ArrayList<Long> getKnownChunks(World world){
        if (knownWorlds.containsKey(world)) return knownWorlds.get(world);
        else{
            ArrayList<Long> knownChunks = new ArrayList<Long>();
            knownWorlds.put(world.getUID(), knownChunks);
            return knownChunks;
        }
    }
    
    public void flushKnownChunks(){
        knownWorlds.clear();
    }
    
    public Chunk getChunkSafely(int chunkX, int chunkZ, World world){
        Chunk target = null;
        if (getKnownChunks(world).contains(LongHash.toLong(chunkX, chunkZ))) target = world.getChunkAt(chunkX, chunkZ);
        else if (world.loadChunk(chunkX, chunkZ, false)){
            target = world.getChunkAt(chunkX, chunkZ);
            ArrayList<Long> knownChunks = getKnownChunks(world);
            knownChunks.add(LongHash.toLong(chunkX, chunkZ));
            knownWorlds.put(world.getUID(), knownChunks);
        }
        return target;
    }
    
    public Block getBlockSafely(int x, int y, int z, World world){
        Block target = null;
        if (getKnownChunks(world).contains(LongHash.toLong(x, z))) target = world.getBlockAt(x, y, z);
        else if (world.loadChunk(x, z, false)){
            target = world.getBlockAt(x, y, z);
            ArrayList<Long> knownChunks = getKnownChunks(world);
            knownChunks.add(LongHash.toLong(x, z));
            knownWorlds.put(world.getUID(), knownChunks);
        }
        return target;
    }
}
