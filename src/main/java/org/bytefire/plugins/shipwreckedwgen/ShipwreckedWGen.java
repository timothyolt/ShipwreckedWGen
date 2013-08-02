package org.bytefire.plugins.shipwreckedwgen;

import org.bytefire.plugins.shipwreckedwgen.biomes.BiomeHandler;
import org.bytefire.plugins.shipwreckedwgen.biomes.trees.BiomeTreeGen;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureCommands;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureHandler;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureListener;

public class ShipwreckedWGen extends JavaPlugin{
    private ChunkGenerator chunkGenerator;
    private BiomeHandler biomeHandler;
    private ChunkHandler chunkHandler;
    private StructureHandler structHandler;
    private BiomeTreeGen treeGenerator;

    @Override
    public void onEnable(){
        chunkGenerator = new OverworldChunkGenerator(this);
        biomeHandler = new BiomeHandler(this);
        treeGenerator = new BiomeTreeGen();
        chunkHandler = new ChunkHandler(this);
        structHandler = new StructureHandler(this);
        getCommand("structure").setExecutor(new StructureCommands(this));
        getServer().getPluginManager().registerEvents(new TestListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new StructureListener(this), this);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return chunkGenerator;
    }

    public BiomeHandler getBiomeHandler(){
        return biomeHandler;
    }

    public ChunkHandler getChunkHandler(){
        return chunkHandler;
    }

    public StructureHandler getStructureHandler(){
        return structHandler;
    }

    public BiomeTreeGen getTreeGenerator(){
        return treeGenerator;
    }
}

//TODO: Shaddows are under trees
//TODO: Snow layers not in ice biome minority chunks
//TODO: Floating obsidian
