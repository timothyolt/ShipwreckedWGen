package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class PlainsBiome implements BiomeGen{
    private ShipwreckedWGen plugin;
    public PlainsBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 4;
    int extraDetail = 6;
    Material[] topsoil = {Material.GRASS, Material.DIRT};

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
        return Biome.PLAINS;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 8D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Yellow Flowers
        if (sCount < 44 && sCount > 40){
            int flowerDensity = (int)Math.floor(sCount - 30);
            for (int cacti = 0; cacti < flowerDensity; cacti++){
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy , zz);
                if (!target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(Material.YELLOW_FLOWER.getId(), (byte) 0, false);
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
                if (!target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(Material.RED_ROSE.getId(), (byte) 0, false);
            }
        }
        //Long Grass
        int grassDensity = (int)Math.floor(sCount * (12/25D)) + 16; //12/25 + 16 makes the number range from 16-64
        for (int cacti = 0; cacti < grassDensity; cacti++){
            int xx = r.nextInt(16) + (c.getX() << 4);
            int zz = r.nextInt(16) + (c.getZ() << 4);
            int yy = w.getHighestBlockYAt(xx, zz);
            Block target = w.getBlockAt(xx, yy , zz);
            if (target.getRelative(0, -1, 0).getType() != Material.SAND && !target.getRelative(0, -1, 0).isLiquid() && target.isEmpty()) target.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) 1, false);
        }
        //Trees
        if (sCount < 25 && sCount > 20){
           int xx = r.nextInt(16) + (c.getX() << 4);
           int zz = r.nextInt(16) + (c.getZ() << 4);
           int yy = w.getHighestBlockYAt(xx, zz);
           if (r.nextBoolean()) plugin.getTreeGenerator().gen(r, new Location(w, xx, yy, zz));
        }
    }
}
