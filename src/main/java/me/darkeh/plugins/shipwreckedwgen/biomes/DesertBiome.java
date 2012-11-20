package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class DesertBiome implements BiomeGen{

    int smallBlobCount = 32;
    int largeBlobCount = 24;
    int landHeight = 4;
    int extraDetail = 6;
    Material[] topsoil = {
        Material.SAND,
        Material.SAND,
        Material.SAND,
        Material.SAND,
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
        return Biome.DESERT;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 64D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Oasis
        if (sCount < 40){
            //Lake
            System.out.println("Oasis at: " + Integer.toString(c.getX() << 4) + ", "+ Integer.toString(c.getZ() << 4));
            int blobX = (c.getX() << 4) + r.nextInt(6) - 3;
            int blobZ = (c.getZ() << 4) + r.nextInt(6) - 3;
            lakeBlob(blobX, w.getHighestBlockYAt(blobX, blobZ), blobZ, 5, w, r);
            blobX = (c.getX() << 4) + r.nextInt(6) - 3;
            blobZ = (c.getZ() << 4) + r.nextInt(6) - 3;
            lakeBlob(blobX, w.getHighestBlockYAt(blobX, blobZ), blobZ, 5, w, r);
            blobX = (c.getX() << 4) + r.nextInt(6) - 3;
            blobZ = (c.getZ() << 4) + r.nextInt(6) - 3;
            lakeBlob(blobX, w.getHighestBlockYAt(blobX, blobZ), blobZ, 5, w, r);
        }
        //Cacti
        else if (sCount < 50){

        }
        //Dead Shrubs
    }

    void lakeBlob(int xx, int yy, int zz, int size, World w, Random r){
        Location center = new Location(w, xx, yy, zz);
        for (int x = (size * -1) - 4; x < size + 4; x++) for (int z = (size * -1) - 4; z < size + 4; z++) for (int y = (size * -1); y < size; y++){
            Block block = w.getBlockAt(xx + x, yy + y, zz + z);
            double distance = center.distance(block.getLocation());
            Material replace = null;
            if (distance < size){
                if (y < 0) replace = Material.WATER;
                else replace = Material.AIR;
                if (!block.isLiquid()) block.setType(replace);
            }
            else if (distance < size + 2){
                if (y == -1) replace = Material.GRASS;
                else if (y < 0) replace = Material.DIRT;
                if (replace != null && !block.isLiquid() && !block.getRelative(BlockFace.DOWN).isEmpty()) block.setType(replace);
                if (distance < size + 1 && y == 0 && r.nextInt(48) == 1 && !block.getRelative(BlockFace.DOWN).isEmpty()){
                    int caneHeight = r.nextInt(2) + 2;
                    for (int cy = 0; cy <= caneHeight; cy++) block.getRelative(0, cy, 0).setType(Material.SUGAR_CANE_BLOCK);
                }
            }
            else if (distance < size + 3){
                if (y == -1) replace = Material.GRASS;
                else if (y < 0) replace = Material.DIRT;
                int height = w.getHighestBlockYAt(xx + x, zz + z);
                Block nBlock = w.getBlockAt(xx + x, (int)((yy + ((yy + height) / 2.0)) / 2.0) + y, zz + z);
                if (replace != null && !block.isLiquid()) nBlock.setType(replace);
            }
            else if (distance < size + 4){
                if (y == -1) replace = Material.GRASS;
                else if (y < 0) replace = Material.DIRT;
                int height = w.getHighestBlockYAt(xx + x, zz + z);
                Block nBlock = w.getBlockAt(xx + x, (int)((height + ((height + yy) / 2.0)) / 2.0) + y, zz + z);
                if (replace != null && !block.isLiquid()) nBlock.setType(replace);
            }
        }
    }
}
