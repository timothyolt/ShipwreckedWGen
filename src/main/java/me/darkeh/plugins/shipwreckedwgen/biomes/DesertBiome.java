package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class DesertBiome implements BiomeGen{

    private ShipwreckedWGen plugin;
    public DesertBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

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
        sDensity.setScale(1 / 16D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Oasis
        if (sCount < 15 && sCount > 14){ //(sCount < 18 && sCount > 17)
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
        else if (sCount > 60){
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

    void lakeBlob(int xx, int yy, int zz, int size, World w, Random r){
        Location center = new Location(w, xx, yy, zz);
        for (int x = (size * -1) - 4; x < size + 4; x++) for (int z = (size * -1) - 4; z < size + 4; z++){
            int height = w.getHighestBlockYAt(xx + x, zz + z);
            for (int y = (size * -1) - 4; y < size; y++){
                Block block = w.getBlockAt(xx + x, yy + y, zz + z);
                double distance = center.distance(block.getLocation());
                double hDistance = center.distance(new Location(w, xx + x, yy, zz + z));
                Material replace = null;
                if (distance < size){
                    if (y < 0) replace = Material.WATER;
                    else replace = Material.AIR;
                    if (!block.isLiquid()) block.setType(replace);
                }
                else if (hDistance < size + 2){
                    if (y == -1) replace = Material.GRASS;
                    else if (y < 0) replace = Material.DIRT;
                    else if (y > -1 && !block.isEmpty() && !block.isLiquid() && block.getType() != Material.SUGAR_CANE_BLOCK){
                        if (block.getRelative(BlockFace.UP).isEmpty()) replace = Material.GRASS;
                        else replace = Material.DIRT;
                    }
                    if (replace != null && !block.isLiquid() && !block.getRelative(BlockFace.DOWN).isEmpty()) {
                        block.setType(replace);
                        if (replace == Material.GRASS){
                            int life = r.nextInt(64);
                            if (life <= 8) block.getRelative(BlockFace.UP).setType(Material.LONG_GRASS);
                            else if (life == 10 || life == 11) block.getRelative(BlockFace.UP).setType(Material.RED_ROSE);
                            else if (life == 20 || life == 21) block.getRelative(BlockFace.UP).setType(Material.YELLOW_FLOWER);
                            else if (life == 30) plugin.getTreeGenerator().gen(r, block.getRelative(BlockFace.UP).getLocation());
                            else if (life == 31 && distance < size + 1 && y == 0){
                                int caneHeight = r.nextInt(2) + 2;
                                for (int cy = 0; cy <= caneHeight; cy++) block.getRelative(0, cy, 0).setType(Material.SUGAR_CANE_BLOCK);
                            }
                        }
                    }
                }
                else if (hDistance < size + 3){
                    if (y == -1) replace = Material.GRASS;
                    else if (y < 0) replace = Material.DIRT;
                    Block nBlock = w.getBlockAt(xx + x, (int)((yy + ((yy + height) / 2.0)) / 2.0) + y, zz + z);
                    if (replace != null && !block.isLiquid()) nBlock.setType(replace);
                }
                else if (hDistance < size + 4){
                    if (y == -1) replace = Material.GRASS;
                    else if (y < 0) replace = Material.DIRT;
                    Block nBlock = w.getBlockAt(xx + x, (int)((height + ((height + yy) / 2.0)) / 2.0) + y, zz + z);
                    if (replace != null && !block.isLiquid()) nBlock.setType(replace);
                }
            }
        }
    }
}
