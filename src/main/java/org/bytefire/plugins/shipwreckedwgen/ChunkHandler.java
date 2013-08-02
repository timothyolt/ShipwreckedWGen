package org.bytefire.plugins.shipwreckedwgen;

import java.util.ArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChunkHandler {
    private ShipwreckedWGen plugin;
    public ChunkHandler(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    ArrayList<Long> populateChunk = new ArrayList<Long>();

    public static long intToLong(int x, int z){
        return (((long)x) << 32) + (long)z;
    }

    boolean closePlayer(Chunk c){
        int max = plugin.getServer().getViewDistance() + 2;
        Location chunk = new Location(c.getWorld(), c.getX(), 0, c.getZ());
        Player[] players = plugin.getServer().getOnlinePlayers();
        ArrayList<Player> valid = new ArrayList<Player>();
        for (int i = 0; i < players.length; i++){
            Location player = players[i].getPlayer().getLocation();
            if (player.getWorld() == chunk.getWorld()){
                player.setY(0);
                player.setX(player.getBlockX() >> 4);
                player.setZ(player.getBlockZ() >> 4);
                double dist = chunk.distance(player);
                if (dist <= max) return true;
            }
        }
        return false;
    }

    public void add(Chunk c, boolean bypass){
        long addition = intToLong(c.getX(), c.getZ());
        populateChunk.add(addition);
    }

    public boolean get(Chunk c){
        return populateChunk.contains(intToLong(c.getX(), c.getZ()));
    }

    public boolean get(int x, int z){
        return populateChunk.contains(intToLong(x, z));
    }

    public Block getBlockSafely(Location l){
        return getBlockSafely(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public Block getBlockSafely(World w, int x, int y, int z){
        if (get(x >> 4, z >> 4)) return w.getBlockAt(x, y, z);
        else return null;
    }

    public void setBlockSafely(Location l, Material mat){
        getBlockSafely(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public void setBlockSafely(World w, int x, int y, int z, Material mat){
        Block block = null;
        if (get(x >> 4, z >> 4)) block = w.getBlockAt(x, y, z);
        if (block != null) block.setType(mat);
    }

    public void remove(Chunk c){
        long removal = intToLong(c.getX(), c.getZ());
        populateChunk.remove(removal);
    }
}
