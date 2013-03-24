package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class JungleBiome implements BiomeGen{
    private ShipwreckedWGen plugin;
    public JungleBiome(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    int smallBlobCount = 32;
    int largeBlobCount = 28;
    int landHeight = 20;
    int extraDetail = 8;
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
        return Biome.JUNGLE;
    }

    public int addBiomeLand(int x, int z, int height, SimplexOctaveGenerator gen) {
        //TODO: Add custom biome additives
        return height;
    }

    public void biomePopulate(World w, Random r, Chunk c) {
        SimplexOctaveGenerator sDensity = new SimplexOctaveGenerator(w.getSeed(), 2);
        sDensity.setScale(1 / 8D);
        double sCount = ((sDensity.noise(c.getX(), c.getZ(), 0.5, 0.5, true) + 1) / 2.0) * 100;
        //Jungle Trees
        int xx = r.nextInt(16) + (c.getX() << 4);
        int zz = r.nextInt(16) + (c.getZ() << 4);
        int yy = w.getHighestBlockYAt(xx, zz);
        Location root = new Location(w, xx, yy, zz);
        if (root.getBlock().getRelative(0, -1, 0).getType() == Material.LEAVES) for (int y = 0; y > -25; y--){
            if (root.getBlock().getRelative(0, y - 1, 0).getType() == Material.GRASS) root = new Location(w, xx, yy + y, zz);
        }
        plugin.getTreeGenerator().gen(r, root);

        //Jungle Bushes
        xx = r.nextInt(16) + (c.getX() << 4);
        zz = r.nextInt(16) + (c.getZ() << 4);
        yy = w.getHighestBlockYAt(xx, zz);
        root = new Location(w, xx, yy, zz);
        for (int y = 0; y > -128; y--){
            if (root.getBlock().getRelative(0, y - 1, 0).getType() == Material.GRASS) root = new Location(w, xx, yy + y, zz);
        }
        plugin.getTreeGenerator().gen(r, root, TreeType.BIRCH);
        xx = r.nextInt(16) + (c.getX() << 4);
        zz = r.nextInt(16) + (c.getZ() << 4);
        yy = w.getHighestBlockYAt(xx, zz);
        root = new Location(w, xx, yy, zz);
        for (int y = 0; y > -128; y--){
            if (root.getBlock().getRelative(0, y - 1, 0).getType() == Material.GRASS) root = new Location(w, xx, yy + y, zz);
        }
        plugin.getTreeGenerator().gen(r, root, TreeType.BIRCH);


        //Vines
        int grassDensity = (int)Math.floor(64 / ((sCount * (12/25D)) + 16)) - 8;
        if (grassDensity == 0) grassDensity = 1;
        for (int x = 0; x < 16; x++) for (int z = 0; z <  16; z++){
            xx = x + (c.getX() << 4);
            zz = z + (c.getZ() << 4);
            yy = w.getHighestBlockYAt(xx, zz) - 1;
            Block target = w.getBlockAt(xx, yy , zz);
            for (int i = 0; target.getRelative(1, i, 0).isEmpty(); i--){
                target.getRelative(1, i, 0).setTypeIdAndData(Material.VINE.getId(), (byte)8, false);
            }
            for (int i = 0; target.getRelative(0, i, 1).isEmpty(); i--){
                target.getRelative(1, i, 0).setTypeIdAndData(Material.VINE.getId(), (byte)1, false);
            }
            for (int i = 0; target.getRelative(-1, i, 0).isEmpty(); i--){
                target.getRelative(1, i, 0).setTypeIdAndData(Material.VINE.getId(), (byte)2, false);
            }
            for (int i = 0; target.getRelative(0, i, -1).isEmpty(); i--){
                target.getRelative(1, i, 0).setTypeIdAndData(Material.VINE.getId(), (byte)4, false);
            }
        }
    }
}
