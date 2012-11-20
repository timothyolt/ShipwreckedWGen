package me.darkeh.plugins.shipwreckedwgen;

import me.darkeh.plugins.shipwreckedwgen.biomes.BiomeHandler;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class ShipwreckedWGen extends JavaPlugin{
    public ChunkGenerator chunkGenerator = new OverworldChunkGenerator(this);
    public BiomeHandler biomeHandler = new BiomeHandler();
    @Override
    public void onEnable(){
        //getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return chunkGenerator;
    }
}

