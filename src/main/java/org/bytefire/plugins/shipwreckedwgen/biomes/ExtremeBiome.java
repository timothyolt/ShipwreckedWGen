package org.bytefire.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import static org.bukkit.Material.*;

public class ExtremeBiome implements BiomeGen{
    private ShipwreckedWGen plugin;
    public ExtremeBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 4;
    int extraDetail = 8;
    Material[] topsoil = {GRASS, DIRT, DIRT};

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
        return Biome.EXTREME_HILLS;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        SimplexOctaveGenerator threedee = gen;
        threedee.setScale(1/64.0);
        int additive = 0;
        for (int y = 0; y < 32; y++){
            if (threedee.noise(x, y, z, 0.5, 0.5, true) > 0.2) additive += 1;
        }
        return height + additive;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 8D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Trees
        if (sCount < 25 && sCount > 20){
           int xx = r.nextInt(16) + (c.getX() << 4);
           int zz = r.nextInt(16) + (c.getZ() << 4);
           int yy = w.getHighestBlockYAt(xx, zz);
           if (r.nextBoolean()) plugin.getTreeGenerator().gen(r, new Location(w, xx, yy, zz));
        }
        //Yellow Flowers
        if (sCount < 44 && sCount > 40){
            int flowerDensity = (int)Math.floor(sCount - 30);
            for (int cacti = 0; cacti < flowerDensity; cacti++){
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy , zz);
                if (!target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(YELLOW_FLOWER.getId(), (byte) 0, false);
            }
        }
        //Red Roses
        else if (sCount < 74 && sCount > 70){
            int flowerDensity = (int)Math.floor(sCount - 60);
            for (int cacti = 0; cacti < flowerDensity; cacti++){
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy , zz);
                if (!target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(RED_ROSE.getId(), (byte) 0, false);
            }
        }
        //Long Grass
        int grassDensity = (int)Math.floor(64 / ((sCount * (12/25D)) + 16)) + 1; //Number between 5 and 2
        if (grassDensity == 0) grassDensity = 1;
        for (int x = 0; x < 16; x++) for (int z = 0; z <  16; z++){
            int xx = x + (c.getX() << 4);
            int zz = z + (c.getZ() << 4);
            int yy = w.getHighestBlockYAt(xx, zz);
            Block target = w.getBlockAt(xx, yy , zz);
            Material groundType = target.getRelative(0, -1, 0).getType();
            if (r.nextInt(grassDensity) == 0 && groundType != LEAVES && groundType != SAND && groundType != ICE && !target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(LONG_GRASS.getId(), (byte) 1, false);
        }
    }
}
