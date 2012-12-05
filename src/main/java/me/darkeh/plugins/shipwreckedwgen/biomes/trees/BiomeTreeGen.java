package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Biome;

public class BiomeTreeGen {
    public BiomeTree getTree(Random rand, Location center){
        Biome biome = center.getBlock().getBiome();
        BiomeTree tree;
        switch (biome){
            case DESERT:
                tree = new DesertTree(rand, center);
                break;
            case DESERT_HILLS:
                tree = new DesertTree(rand, center);
                break;
            default:
                tree = null;
        }
        return tree;
    }

    public boolean gen(Random rand, Location center){
        boolean interrupted;
        BiomeTree tree = getTree(rand, center);
        if (tree != null) interrupted = tree.generate();
        else interrupted = true;
        return interrupted;
    }
}
