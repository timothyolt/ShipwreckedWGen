package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;

public class BiomeTreeGen {
    public BiomeTree getTree(Random rand, Location center){
        return getTree(rand, center, TreeType.TREE);
    }
    public BiomeTree getTree(Random rand, Location center, TreeType type){
        Biome biome = center.getBlock().getBiome();
        BiomeTree tree;
        switch (type){
            case REDWOOD: switch (biome){
                case FOREST:
                    tree = new ForestSpruceTree(rand, center);
                    break;
                case FOREST_HILLS:
                    tree = new ForestSpruceTree(rand, center);
                    break;
                case TAIGA:
                    tree = new TaigaPineTree(rand, center);
                    break;
                case TAIGA_HILLS:
                    tree = new TaigaPineTree(rand, center);
                    break;
                default:
                    tree = new ForestSpruceTree(rand, center);
            } break;
            case BIRCH: switch (biome){
                case FOREST:
                    tree = new ForestBirchTree(rand, center);
                    break;
                case FOREST_HILLS:
                    tree = new ForestBirchTree(rand, center);
                    break;
                case JUNGLE:
                    tree = new JungleBush(rand, center);
                    break;
                case JUNGLE_HILLS:
                    tree = new JungleBush(rand, center);
                    break;
                default:
                    tree = new ForestBirchTree(rand, center);
            } break;
            case TREE:
            default: switch (biome){
                case DESERT:
                    tree = new DesertOakTree(rand, center);
                    break;
                case DESERT_HILLS:
                    tree = new DesertOakTree(rand, center);
                    break;
                case PLAINS:
                    tree = new PlainsOakTree(rand, center);
                    break;
                case FOREST:
                    tree = new ForestOakTree(rand, center);
                    break;
                case FOREST_HILLS:
                    tree = new ForestOakTree(rand, center);
                    break;
                case JUNGLE:
                    tree = new JungleTree(rand, center);
                    break;
                case JUNGLE_HILLS:
                    tree = new JungleTree(rand, center);
                    break;
                default:
                    tree = new ForestOakTree(rand, center);
            } break;
        }
        return tree;
    }

    public boolean gen(Random rand, Location center){
        return gen(rand, center, TreeType.TREE);
    }
    public boolean gen(Random rand, Location center, TreeType type){
        boolean interrupted;
        BiomeTree tree = getTree(rand, center, type);
        if (tree != null) interrupted = tree.generate();
        else interrupted = true;
        return interrupted;
    }
}
