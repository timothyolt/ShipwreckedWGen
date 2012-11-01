package me.darkeh.plugins.shipwreckedwgen;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class ShipwreckedWGen extends JavaPlugin{
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return new OverworldChunkGenerator();
    }
}

