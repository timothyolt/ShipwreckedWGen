package org.bytefire.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import static org.bukkit.Material.*;

public class TaigaBiome implements BiomeGen{
    private ShipwreckedWGen plugin;
    public TaigaBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 14;
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
        return Biome.TAIGA;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 8D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //TODO: add lakes
        //Pine Trees
        if (sCount < 80 && sCount > 20){
           int xx = r.nextInt(16) + (c.getX() << 4);
           int zz = r.nextInt(16) + (c.getZ() << 4);
           int yy = w.getHighestBlockYAt(xx, zz);
           Location root = new Location(w, xx, yy, zz);
           if (root.getBlock().getRelative(0, -1, 0).getType() == LEAVES) for (int y = 0; y > -25; y--){
               if (root.getBlock().getRelative(0, y - 1, 0).getType() == GRASS) root = new Location(w, xx, yy + y, zz);
           }
           plugin.getTreeGenerator().gen(r, root, TreeType.REDWOOD);
        }
        //Snow
        for (int x = -8; x < 24; x++) for (int z = -8; z < 24; z++){
            Block target = w.getHighestBlockAt((c.getX() << 4) + x, (c.getZ() << 4) + z);
            if (target.isEmpty() && target.getBiome() == Biome.TAIGA) target.setType(SNOW);
        }
    }
}
