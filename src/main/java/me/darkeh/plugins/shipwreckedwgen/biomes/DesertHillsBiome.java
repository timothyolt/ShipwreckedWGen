package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class DesertHillsBiome implements BiomeGen{
    private ShipwreckedWGen plugin;
    public DesertHillsBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 6;
    int extraDetail = 8;
    Material[] topsoil = {
        Material.SAND,
        Material.SAND,
        Material.SANDSTONE,
        Material.SANDSTONE,
        Material.SANDSTONE
    };

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
        return Biome.DESERT_HILLS;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        SimplexOctaveGenerator threedee = gen;
        threedee.setScale(1/48.0);
        int additive = 0;
        for (int y = 0; y < 20; y++){
            if (threedee.noise(x, y, z, 0.5, 0.5, true) > 0.3) additive += 1;
        }
        return height + additive;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 16D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Cacti
        if (sCount > 60){
            int cactiDensity = (int)Math.floor(sCount - 60) / 5;
            for (int cacti = 0; cacti < cactiDensity; cacti++){
                int height = r.nextInt(3) + 2;
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                for (int y = 0; y < height; y++){
                    Block target = w.getBlockAt(xx, yy + y, zz);
                    if (target.getType() == Material.AIR){
                        target.setTypeId(Material.CACTUS.getId(), false);
                    }
                }
            }
        }
        //Dead Shrubs
        if (sCount > 40 && sCount < 80){
            int shrubDensity = (int)Math.floor(sCount - 40) / 20;
            for (int shrubs = 0; shrubs < shrubDensity; shrubs++){
                int height = r.nextInt(3) + 2;
                int xx = r.nextInt(16) + (c.getX() << 4);
                int zz = r.nextInt(16) + (c.getZ() << 4);
                int yy = w.getHighestBlockYAt(xx, zz);
                Block target = w.getBlockAt(xx, yy, zz);
                if (target.getRelative(BlockFace.DOWN).getType() == Material.SAND) target.setType(Material.DEAD_BUSH);
            }
        }
    }
}
