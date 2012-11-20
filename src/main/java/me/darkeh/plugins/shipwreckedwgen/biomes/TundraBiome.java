package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class TundraBiome implements BiomeGen{

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 4;
    int extraDetail = 6;
    Material[] topsoil = {Material.SNOW, Material.SNOW_BLOCK, Material.SNOW_BLOCK, Material.DIRT, Material.DIRT};

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
        return Biome.ICE_PLAINS;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }

    public void biomePopulate(World world, Random random, Chunk source) {
    }
}
