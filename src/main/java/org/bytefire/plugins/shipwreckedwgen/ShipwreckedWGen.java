package org.bytefire.plugins.shipwreckedwgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bytefire.plugins.shipwreckedwgen.biomes.BiomeHandler;
import org.bytefire.plugins.shipwreckedwgen.biomes.trees.BiomeTreeGen;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureCommands;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureHandler;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureListener;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureUtil;

public class ShipwreckedWGen extends JavaPlugin{

    private BiomeHandler biomeHandler;
    private ChunkHandler chunkHandler;
    private StructureHandler structHandler;
    private BiomeTreeGen treeGenerator;
    private File configFile;
    private FileConfiguration config;

    @Override
    public void onEnable(){

        biomeHandler = new BiomeHandler(this);
        treeGenerator = new BiomeTreeGen();
        chunkHandler = new ChunkHandler(this);
        structHandler = new StructureHandler(this);

        getCommand("structure").setExecutor(new StructureCommands(this));

        getServer().getPluginManager().registerEvents(new TestListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new StructureListener(this), this);

        try {
            configFile = getDataFolder();
            configFile.mkdirs();
            configFile = new File(getDataFolder() + File.separator + "ShipGen.yml");
            config = YamlConfiguration.loadConfiguration(new FileInputStream(configFile.getAbsolutePath()));
            patchConfig();
        } catch (FileNotFoundException e){
            config = new YamlConfiguration();
            patchConfig();
        } catch (Exception e) {
            System.err.print("[ShipGen] " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void onDisable(){
        StructureUtil.clearEditorCache();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
        return new OverworldChunkGenerator(this, worldName);
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

    @Override
    public FileConfiguration getConfig(){
        return config;
    }

    public File getConfigFile(){
        return configFile;
    }

    private boolean patchConfig(){
        boolean modified = false;
        try {
//            List<World> worlds = getServer().getWorlds();
//            System.out.println(worlds.size());
//
//            for (World world : worlds){
//                System.out.println(world.getName());
//
//                if (world.getGenerator().getClass() == OverworldChunkGenerator.class){
//                    System.out.println(world.getName());
//
//                    String local = "worlds." + world.getName().toLowerCase();
//                    File structures = new File(getDataFolder() + "structures");
//                    structures.mkdirs();
//                    String[] files = structures.list();
//                    for (String file : files){
//                        System.out.println(file);
//
//                        String yamlPath = local + ".structures." + file.split(".")[0];
//                        if (!config.contains(yamlPath)) config.set(yamlPath, true);
//                    }
//                }
//            }
            config.save(configFile);
        } catch (Exception e) {
             System.err.print("[ShipGen] " + e.getMessage());
             e.printStackTrace(System.err);
        }
        return modified;
    }
}

//TODO: Shaddows are under trees
//TODO: Snow layers not in ice biome minority chunks
//TODO: Floating obsidian
