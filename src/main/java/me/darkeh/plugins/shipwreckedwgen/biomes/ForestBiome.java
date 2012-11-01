package me.darkeh.plugins.shipwreckedwgen.biomes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class ForestBiome implements BiomeGen{
    
    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 16;
    int extraDetail = 6;
    Material[] topsoil = {Material.GRASS, Material.DIRT, Material.DIRT};

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
        return Biome.FOREST;
    }
    
    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }
}
