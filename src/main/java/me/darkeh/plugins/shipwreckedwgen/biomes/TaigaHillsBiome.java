package me.darkeh.plugins.shipwreckedwgen.biomes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class TaigaHillsBiome implements BiomeGen{
    
    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 18;
    int extraDetail = 8;
    Material[] topsoil = {Material.SNOW, Material.SNOW_BLOCK, Material.DIRT, Material.DIRT};

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
        return Biome.TAIGA_HILLS;
    }
    
    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        SimplexOctaveGenerator threedee = gen;
        threedee.setScale(1/48.0);
        int additive = 0;
        for (int y = 0; y < 8; y++){
            if (threedee.noise(x, y, z, 0.5, 0.5, true) > 0.1) additive += 1;
        }
        return height + additive;
    }
}
