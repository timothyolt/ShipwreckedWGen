package org.bytefire.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public interface BiomeGen {

    int getSmallBlobCount();
    int getLargeBlobCount();
    int getLandHeight();
    int getExtraDetail();

    Biome getBiome();

    Material[] getTopsoil();
    int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen);

    void biomePopulate(World w, Random r, Chunk c);
}
