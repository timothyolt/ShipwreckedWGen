package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import static org.bukkit.Material.*;

public class OrePopulator extends BlockPopulator{
    private ShipwreckedWGen plugin;
    public OrePopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 0; y < 8; y++){
            if (random.nextInt(100)==1){
                int section = random.nextInt(8);
                boolean choice = random.nextBoolean();
                int xx = x + (chunk.getX() * 16);
                int yy;
                if(section==11) yy = random.nextInt(125) + 89;
                else yy = random.nextInt(8) + (section * 8) + 1;
                int zz = z + (chunk.getZ() * 16);
                if (section == 7||section == 6) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, IRON_ORE, chunk);
                else if (section == 5 && choice == true) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, GOLD_ORE, chunk);
                else if (section == 4 && random.nextInt(4) == 1) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, REDSTONE_ORE, chunk);
                else if (section == 3){
                    if (choice == true) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, REDSTONE_ORE, chunk);
                    else if (random.nextInt(4)==1) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, LAPIS_ORE, chunk);
                }
                else if (section == 2 && random.nextInt(6)==1){
                    if (choice == true) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, LAPIS_ORE, chunk);
                    else oreShard(xx, yy, zz, 5, random, world, DIAMOND_ORE, chunk);
                }
                else if (section == 1 && random.nextInt(6)==1){
                    if (choice == true) oreShard(xx, yy, zz, 5, random, world, DIAMOND_ORE, chunk);
                    else oreShard(xx, yy, zz, 5, random, world, EMERALD_ORE, chunk);
                }
                else if (section == 0 && random.nextInt(4)==1){
                    if (choice == true) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, OBSIDIAN, chunk);
                    else oreShard(xx, yy, zz, 5, random, world, EMERALD_ORE, chunk);
                }
            }
        }
    }

    //Depricated
    void oreBlob(int centerX, int centerY, int centerZ, World world, int radius, Random random, Material ore, Chunk chunk){
        for (int deposits = random.nextInt(radius*2); deposits >= 0; deposits--){
            int xx = random.nextInt(radius*2) - radius + centerX;
            int yy = random.nextInt(radius*2) - radius + centerY;
            int zz = random.nextInt(radius*2) - radius + centerZ;
            Location center = new Location(world, xx, yy, zz);
            for (int x = (radius * -1); x < radius; x++) for (int z = (radius * -1); z < radius; z++) for (int y = (radius * -1); y < radius; y++){
                Block block = plugin.getChunkHandler().getBlockSafely(world, xx + x, yy + y, zz + z);
                if (block != null){
                    double distance = center.distance(block.getLocation());
                    if (distance < radius && random.nextInt(50) == 1 && block.getType() == STONE) block.setType(ore);
                    else if (distance < radius + 1 && block.getType() == STONE) block.setType(ore);
                }
            }
        }
    }

    //NOTE: specks of metallic ores are found in the cavern gen code too
    void oreVein(Vector start, Vector dir, World world, int radius, Random random, Material ore, Chunk chunk){
        int centerY = start.getBlockY();
        Block origin = plugin.getChunkHandler().getBlockSafely(world, start.getBlockX(), centerY, start.getBlockZ());
        if (origin == null) return;
        if (origin.getType()==AIR){
            int shiftLife = 8;
            centerY += 4;
            while (plugin.getChunkHandler().getBlockSafely(world, start.getBlockX(), centerY, start.getBlockZ()).getType()==AIR&&shiftLife>0){
                centerY -= 1;
            }
        }
        start.setY(centerY);
        oreRaycast(world, start, dir, radius, ore);
        Vector end = start.add(dir);
        Vector mid = new Vector(start.getX()+(dir.getX()/2), start.getY()+(dir.getY()/2.0), start.getZ()+(dir.getZ()/2.0));
        for(int subVeins = random.nextInt(radius/2) + (radius/2); subVeins > 0; subVeins--){
            Vector newdir = new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4);
            int newstart = random.nextInt(3);
            switch(newstart){
                case 0:
                    oreRaycast(world, start, newdir, radius/2, ore);
                    break;
                case 1:
                    oreRaycast(world, mid, newdir, radius/2, ore);
                    break;
                case 2:
                    oreRaycast(world, end, newdir, radius/2, ore);
                    break;
                default:
                    oreRaycast(world, mid, newdir, radius/2, ore);
                    break;
            }
        }
    }

    void orePowder(Vector start, Vector dir, World world, int radius, Random random, Material ore, Chunk chunk){
        int centerY = start.getBlockY();
        Block origin = plugin.getChunkHandler().getBlockSafely(world, start.getBlockX(), centerY, start.getBlockZ());
        if (origin == null) return;
        if (origin.getType() == AIR){
            int shiftLife = 8;
            centerY += 4;
            while (plugin.getChunkHandler().getBlockSafely(world, start.getBlockX(), centerY, start.getBlockZ()).getType()==AIR&&shiftLife>0){
                centerY -= 1;
            }
        }
        start.setY(centerY);
        oreRaycast(world, start, dir, radius, ore);
        Vector end = start.add(dir);
        Vector newdir = dir.multiply(2);
        for(int subVeins = random.nextInt(radius/2) + (radius/2); subVeins > 0; subVeins--){
            dir = dir.multiply(dir);
            oreRaycast(world, end, newdir, radius/2, ore);
        }
    }

    void oreShard(int xx, int yy, int zz, int field, Random random, World world, Material ore, Chunk chunk){
        oreBlob(xx, yy, zz, world, 1, random, ore, chunk);
        Location center = new Location(world, xx, yy, zz);
        for (int x = (field * -1); x < field; x++) for (int z = (field * -1); z < field; z++) for (int y = (field * -1); y < field; y++){
            if(random.nextInt(128)==1){
                Block block = plugin.getChunkHandler().getBlockSafely(world, xx + x, yy + y, zz + z);
                if(block != null){
                    double distance = center.distance(block.getLocation());
                    if (distance < field && block.getType() == STONE) block.setType(ore);
                }
            }
        }
    }

    void oreRaycast(World w, Vector s, Vector d, int length, Material mat){
        Vector start = s;
        double max = Math.max(Math.max(d.getX(), d.getY()), d.getZ());
        double xInc = d.getX()/max;
        double yInc = d.getY()/max;
        double zInc = d.getZ()/max;
        for(int i = 0; i <= length; i++){
            Block target;
            target = plugin.getChunkHandler().getBlockSafely(w,
                    (int) Math.floor(start.getX() + xInc),
                    (int) Math.floor(start.getY() + yInc),
                    (int) Math.floor(start.getZ() + zInc));
            if (target != null && target.getType() == STONE && start.getY() < 240) target.setType(mat);
            target = plugin.getChunkHandler().getBlockSafely(w,
                    (int) Math.ceil(start.getX() + xInc),
                    (int) Math.ceil(start.getY() + yInc),
                    (int) Math.ceil(start.getZ() + zInc));
            if (target != null && target.getType() == STONE && start.getY() < 240) target.setType(mat);
            target = plugin.getChunkHandler().getBlockSafely(w,
                    (int) Math.ceil(start.getX() + xInc),
                    (int) Math.floor(start.getY() + yInc),
                    (int) Math.ceil(start.getZ() + zInc));
            if (target != null && target.getType() == STONE && start.getY() < 240) target.setType(mat);
            target = plugin.getChunkHandler().getBlockSafely(w,
                    (int) Math.floor(start.getX() + xInc),
                    (int) Math.ceil(start.getY() + yInc),
                    (int) Math.floor(start.getZ() + zInc));
            if (target != null && target.getType() == STONE && start.getY() < 240) target.setType(mat);
            s.add(new Vector(xInc, yInc, zInc));
        }
    }
}
