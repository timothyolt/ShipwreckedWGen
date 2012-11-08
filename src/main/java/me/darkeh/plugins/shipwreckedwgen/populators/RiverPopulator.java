package me.darkeh.plugins.shipwreckedwgen.populators;

//im trying to generate rivers based on two (two dimensional) points. im going to get a line between those points, and then use a noise function to give it curves. how would I go about tracing said line with a sphere?

import java.util.HashMap;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class RiverPopulator extends BlockPopulator{
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int chance = random.nextInt(256);
        if (chance == 1){
            //Height, Internal Chunk X, Internal Chunk Z
            Location cursor = new Location(world, chunk.getX() * 16, 0, chunk.getZ() * 16);
            if (cursor.getBlock().getBiome() != Biome.OCEAN && cursor.getBlock().getBiome() != Biome.FROZEN_OCEAN){
                System.out.println("River test at " + Integer.toString(chunk.getX() * 16) + ", " + Integer.toString(chunk.getZ() * 16));
                cursor.setY(world.getHighestBlockYAt(cursor));
                for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
                for (int i = 0; i < 16; i++){
                    cursor = getNextPoint(cursor, 16);
                    if (cursor.getBlock().getBiome() == Biome.OCEAN || cursor.getBlock().getBiome() == Biome.FROZEN_OCEAN) break;
                    for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
                }
            }
            //for (int y = loEdge[0] - 4; y < 256; y++){
            //    world.getBlockAt(8 + (chunk.getX() * 16), y, 8 + (chunk.getZ() * 16)).setType(Material.DIAMOND_ORE);
            //    world.getBlockAt(loEdge[1] + (chunk.getX() * 16), y, loEdge[2] + (chunk.getZ() * 16)).setType(Material.DIAMOND_ORE);
            //}
        }
    }

    Location getNextPoint(Location center, int size){
        World world = center.getWorld();
        Location lowEdge = new Location(world, center.getX(), 256, center.getZ());
        for(int x = (size * -1); x < size; x++){
            int height;
            height = world.getHighestBlockYAt(center.getBlockX() + x, center.getBlockZ() - size);
            if (height < lowEdge.getY()){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() - size);
                lowEdge.setY(height);
            }
            height = world.getHighestBlockYAt(center.getBlockX() + x, center.getBlockZ() + size);
            if (height < lowEdge.getY()){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() + size);
                lowEdge.setY(height);
            }
        }
        for(int z = (size * -1); z < size; z++){
            int height;
            height = world.getHighestBlockYAt(center.getBlockX() - size, center.getBlockZ() + z);
            if (height < lowEdge.getY()){
                lowEdge.setX(center.getBlockX() - size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
            height = world.getHighestBlockYAt(center.getBlockX() + size, center.getBlockZ() + z);
            if (height < lowEdge.getY()){
                lowEdge.setX(center.getBlockX() + size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
        }
        return lowEdge;
    }
}
