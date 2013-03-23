package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class ForestHillsBiome implements BiomeGen {
    private ShipwreckedWGen plugin;
    public ForestHillsBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 16;
    int extraDetail = 10;
    Material[] topsoil = {Material.GRASS, Material.DIRT, Material.DIRT, Material.DIRT};

    public int getSmallBlobCount() {
        return smallBlobCount;
    }

    public int getLargeBlobCount() {
        return largeBlobCount;
    }

    public int getLandHeight() {
        return landHeight;
    }

    public int getExtraDetail() {
        return extraDetail;
    }

    public Material[] getTopsoil() {
        return topsoil;
    }

    public Biome getBiome() {
        return Biome.FOREST_HILLS;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        SimplexOctaveGenerator threedee = gen;
        threedee.setScale(1/48.0);
        int additive = 0;
        for (int y = 0; y < 16; y++){
            if (threedee.noise(x, y, z, 0.5, 0.5, true) > 0.2) additive += 1;
        }
        return height + additive;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 8D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //TODO: add lakes
        //Oak Trees
        if (sCount < 80 && sCount > 20){
           int xx = r.nextInt(16) + (c.getX() << 4);
           int zz = r.nextInt(16) + (c.getZ() << 4);
           int yy = w.getHighestBlockYAt(xx, zz);
           Location root = new Location(w, xx, yy, zz);
           if (root.getBlock().getRelative(0, -1, 0).getType() == Material.LEAVES) for (int y = 0; y > -25; y--){
               if (root.getBlock().getRelative(0, y - 1, 0).getType() == Material.GRASS) root = new Location(w, xx, yy + y, zz);
           }
           plugin.getTreeGenerator().gen(r, root);
        }
        //Birch Trees
        if (sCount < 50 && sCount > 30){
           int xx = r.nextInt(16) + (c.getX() << 4);
           int zz = r.nextInt(16) + (c.getZ() << 4);
           int yy = w.getHighestBlockYAt(xx, zz);
           Location root = new Location(w, xx, yy, zz);
           if (root.getBlock().getRelative(0, -1, 0).getType() == Material.LEAVES) for (int y = 0; y > -25; y--){
               if (root.getBlock().getRelative(0, y - 1, 0).getType() == Material.GRASS) root = new Location(w, xx, yy + y, zz);
           }
           plugin.getTreeGenerator().gen(r, root, TreeType.BIRCH);
        }
        //Yellow Flowers
        if (sCount < 48 && sCount > 40){
            int flowerDensity = (int)Math.floor(sCount - 30);
            for (int cacti = 0; cacti < flowerDensity; cacti++){
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy , zz);
                Material baseType = target.getRelative(0, -1, 0).getType();
                if (baseType != Material.WATER && baseType != Material.ICE && baseType != Material. LEAVES && target.isEmpty()) target.setTypeIdAndData(Material.YELLOW_FLOWER.getId(), (byte) 0, false);
            }
        }
        //Red Roses
        else if (sCount < 78 && sCount > 70){
            int flowerDensity = (int)Math.floor(sCount - 60);
            for (int cacti = 0; cacti < flowerDensity; cacti++){
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy , zz);
                Material baseType = target.getRelative(0, -1, 0).getType();
                if (baseType != Material.WATER && baseType != Material.ICE && target.isEmpty()) target.setTypeIdAndData(Material.RED_ROSE.getId(), (byte) 0, false);
            }
        }
        //Long Grass
        int grassDensity = (int)Math.floor(64 / ((sCount * (12/25D)) + 16)) + 4; //Number between 10 and 5
        if (grassDensity == 0) grassDensity = 1;
        for (int x = 0; x < 16; x++) for (int z = 0; z <  16; z++){
            int xx = x + (c.getX() << 4);
            int zz = z + (c.getZ() << 4);
            int yy = w.getHighestBlockYAt(xx, zz);
            Block target = w.getBlockAt(xx, yy , zz);
            Material baseType = target.getRelative(0, -1, 0).getType();
            if (r.nextInt(grassDensity) == 0 && baseType != Material.WATER && baseType != Material.ICE && baseType != Material. LEAVES && baseType != Material.SAND && target.isEmpty()) target.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, false);
        }
    }
}
