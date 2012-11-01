package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class CavePopulator extends BlockPopulator{
    //average of occurrance in a chunk = (256 [xmax * zmax] / 100 [chance]) = 2.56 times

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++){
            int chance = random.nextInt(25000);
            if (chance <= 5){
                int xx = x + (chunk.getX()*16);
                int zz = z + (chunk.getZ()*16);
                int yy = random.nextInt(90);
                int life = 50 + random.nextInt(30);
                int normals = 6;
                int normal = 3;
                for (int length = 0; length < life; length++){
                    int size;
                    if (life - length >= 3) size = 3;
                    else if (life - length == 2) size = 1;
                    else if (life - length == 1) size = 1;
                    else size = 1;
                    size -= random.nextInt(3);
                    size += random.nextInt(1);
                    if (size <= 0) size = 1;
                    
                    if (normals <= 0){
                        int oldNormal = normal;
                        normal = random.nextInt(3);
                        if (oldNormal == normal) normal += 1;
                        normals = 3 + random.nextInt(6);
                    }
                    normals--;
                    
                    if (normal == 1){ //Chooses negative x-z normal
                        xx -= (random.nextInt(size)+2);
                        zz -= (random.nextInt(size)+2);
                        yy -= random.nextInt(3);
                    }
                    else if (normal == 2){ //Chooses positive x-z normal
                        xx += (random.nextInt(size)+2);
                        zz += (random.nextInt(size)+2);
                        yy -= random.nextInt(3);
                    }
                    else if (normal == 2){ //Chooses positive x-z normal
                        xx -= (random.nextInt(size)+2);
                        zz += (random.nextInt(size)+2);
                        yy -= random.nextInt(3);
                    }
                    else if (normal == 0){ //Chooses positive x-z normal
                        xx += (random.nextInt(size)+2);
                        zz -= (random.nextInt(size)+2);
                        yy -= random.nextInt(3);
                    }
                    else{ //Chooses positive x-z normal
                        xx += (random.nextInt(size)+2);
                        zz -= (random.nextInt(size)+2);
                        yy += random.nextInt(3);
                    }
                    
                    clearBlob(xx, yy, zz, size, chance, world, random);
                    if (random.nextBoolean()) life++;
                    else life--;
                }
            }
        }
    }
    
    void clearBlob(int xx, int yy, int zz, int size, int type, World world, Random random){
        size += 2;
        Location center = new Location(world, xx, yy, zz);
        for (int x = (size * -1); x < size; x++) for (int z = (size * -1); z < size; z++) for (int y = (size * -1); y < size; y++){
            Block block = world.getBlockAt(xx + x, yy + y, zz + z);
            double distance = center.distance(block.getLocation());
            if (distance < size){
                Material replace;
                if (yy + y < 12) replace = Material.LAVA;
                else if (type == 1 && y < -2 && yy + y < 35 && !block.isEmpty()) replace = Material.LAVA;
                else if (type == 2 && y < -2 && yy + y < 80 && !block.isEmpty()) replace = Material.WATER;
                else replace = Material.AIR;
                if (distance > size - 1) {
                    if (random.nextInt(32) != 1 && !block.isLiquid() && block.getType()!=Material.BEDROCK && block.getType()!=Material.SAND && block.getType()!=Material.SANDSTONE)
                        block.setType(replace);
                }
                else if (!block.isLiquid() && block.getType()!=Material.BEDROCK && block.getType()!=Material.SAND && block.getType()!=Material.SANDSTONE)
                    block.setType(replace);
            }
        }
    }
}