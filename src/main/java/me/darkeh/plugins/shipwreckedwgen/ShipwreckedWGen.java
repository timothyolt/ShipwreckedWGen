package me.darkeh.plugins.shipwreckedwgen;

import me.darkeh.plugins.shipwreckedwgen.biomes.BiomeHandler;
import me.darkeh.plugins.shipwreckedwgen.biomes.trees.BiomeTreeGen;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class ShipwreckedWGen extends JavaPlugin{
    private ChunkGenerator chunkGenerator;
    private BiomeHandler biomeHandler;
    private BiomeTreeGen treeGenerator;
    @Override
    public void onEnable(){
        chunkGenerator = new OverworldChunkGenerator(this);
        biomeHandler = new BiomeHandler(this);
        treeGenerator = new BiomeTreeGen();
        getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return chunkGenerator;
    }

    public BiomeHandler getBiomeHandler(){
        return biomeHandler;
    }

    public BiomeTreeGen getTreeGenerator(){
        return treeGenerator;
    }
}

