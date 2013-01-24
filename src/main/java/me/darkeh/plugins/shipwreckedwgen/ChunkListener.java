package me.darkeh.plugins.shipwreckedwgen;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener{
    private ShipwreckedWGen plugin;
    ChunkListener(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    private long intToLong(int x, int z){
        //return (((long)x) << 32) | ((long)z);
        return (((long)x) << 32) + (long)z;
    }

    @EventHandler
    void load(ChunkLoadEvent event){
        Chunk c = event.getChunk();
        System.out.println("Load   " + Long.toString(intToLong(c.getX(), c.getZ())));
        if (plugin.getServer().getOnlinePlayers().length > 0){
            plugin.getChunkHandler().add(c, false);
        }
        else plugin.getChunkHandler().add(c, true);
    }

    @EventHandler
    void unload(ChunkUnloadEvent event){
        Chunk c = event.getChunk();
        System.out.println("Unload " + Long.toString(intToLong(c.getX(), c.getZ())));
        plugin.getChunkHandler().remove(c);
    }
}
