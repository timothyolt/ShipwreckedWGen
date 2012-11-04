package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class AirPocketPopulator extends BlockPopulator{
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextInt(64)==1){
            //center point
            int x = random.nextInt(16) + (chunk.getX() * 16);
            int z = random.nextInt(16) + (chunk.getZ() * 16);
            int y = random.nextInt(40) + 1;
            int type = random.nextInt(3);
            int radius;
            int size;
            int maxsize = 0;
            
            //0 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob(x + radius, y, z, size, type, world, random);
            //45 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob((int)((x + radius) * (3/4D)), y, (int)((z + radius) * (3/4D)), size, type, world, random);
            //90 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob(x, y, z + radius, size, type, world, random);
            //135 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob((int)((x + radius) * (3/4D)), y, (int)((z - radius) * (3/4D)), size, type, world, random);
            //180 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob(x - radius, y, z, size, type, world, random);
            //215 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob((int)((x - radius) * (3/4D)), y, (int)((z - radius) * (3/4D)), size, type, world, random);
            //250 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob(x, y, z - radius, size, type, world, random);
            //295 degrees
            radius = random.nextInt(8) + 4;
            if (radius > 6) size = radius;
            else size = 6;
            if (maxsize < size) maxsize = size;
            clearBlob((int)((x - radius) * (3/4D)), y, (int)((z + radius) * (3/4D)), size, type, world, random);
            
            //Center
            clearBlob(x, y, z, maxsize, type, world, random);
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
                else if (type == 2 && y < -2 && yy + y < 80 && yy + y > 16 && !block.isEmpty()) replace = Material.WATER;
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
