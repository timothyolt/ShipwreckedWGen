package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class ForestHillsBiome implements BiomeGen{

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

    public void biomePopulate(World world, Random random, Chunk source) {
    }
}
