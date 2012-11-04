package me.darkeh.plugins.shipwreckedwgen;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class ShipwreckedWGen extends JavaPlugin{
    //public ChunkManager chunkManager = new ChunkManager(this);
    @Override
    public void onEnable(){
        //getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return new OverworldChunkGenerator(this);
    }
}

