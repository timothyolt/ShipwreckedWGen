package me.darkeh.plugins.shipwreckedwgen.biomes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class TaigaBiome implements BiomeGen{
    
    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 14;
    int extraDetail = 8;
    Material[] topsoil = {Material.SNOW, Material.GRASS, Material.DIRT, Material.DIRT};

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
        return Biome.TAIGA;
    }
    
    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }
}
