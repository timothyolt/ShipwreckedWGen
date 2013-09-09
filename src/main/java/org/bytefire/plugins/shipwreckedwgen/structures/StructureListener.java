package org.bytefire.plugins.shipwreckedwgen.structures;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
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

    @EventHandler(priority = EventPriority.LOW)
    public void blockSpread(BlockSpreadEvent event){
        if (event.getBlock().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockForm(BlockFormEvent event){
        if (event.getBlock().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockDecay(LeavesDecayEvent event){
        if (event.getBlock().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockFlow(BlockFromToEvent event){
        if (event.getBlock().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockBurn(BlockBurnEvent event){
        if (event.getBlock().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entitySpawn(CreatureSpawnEvent event){
        SpawnReason reason = event.getSpawnReason();
        if (reason != SpawnReason.CHUNK_GEN &&
            reason != SpawnReason.DEFAULT &&
            reason != SpawnReason.LIGHTNING &&
            reason != SpawnReason.SPAWNER &&
            reason != SpawnReason.VILLAGE_DEFENSE &&
            reason != SpawnReason.VILLAGE_INVASION &&
            reason != SpawnReason.EGG &&
            event.getEntity().getWorld().getName().endsWith(".structure"))

            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityBreakDoor(EntityBreakDoorEvent event){
        if (event.getEntity().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityChangeBlock(EntityChangeBlockEvent event){
        if (event.getEntity().getWorld().getName().endsWith(".structure"))
            event.setCancelled(true);
    }

}
