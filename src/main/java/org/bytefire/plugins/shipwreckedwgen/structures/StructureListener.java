package org.bytefire.plugins.shipwreckedwgen.structures;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class StructureListener implements Listener{

    private ShipwreckedWGen plugin;

    public StructureListener(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void detectNewChunk(ChunkLoadEvent event){
        Chunk chunk = event.getChunk();
        String name = chunk.getWorld().getName();
        if (name.endsWith(".structure")){
            plugin.getStructureHandler().queueUpdate(chunk);
        }
    }

}
