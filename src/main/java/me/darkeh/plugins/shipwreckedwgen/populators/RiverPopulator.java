package me.darkeh.plugins.shipwreckedwgen.populators;

//im trying to generate rivers based on two (two dimensional) points. im going to get a line between those points, and then use a noise function to give it curves. how would I go about tracing said line with a sphere?

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

public class RiverPopulator extends BlockPopulator{
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        Location cursor = new Location(world, chunk.getX() * 16, 0, chunk.getZ() * 16);
        int cursorHeight = world.getHighestBlockYAt(cursor);
        Biome cursorBiome = cursor.getBlock().getBiome();
        int chance;
        if (cursorBiome == Biome.EXTREME_HILLS) chance = random.nextInt(24);
        else chance = random.nextInt(48);
        if (cursorHeight >= 100 && chance == 1 && cursorBiome != Biome.OCEAN && cursorBiome != Biome.FROZEN_OCEAN&& cursorBiome != Biome.RIVER&& cursorBiome != Biome.FROZEN_RIVER){
            System.out.println("River test at " + Integer.toString(chunk.getX() * 16) + ", " + Integer.toString(chunk.getZ() * 16));
            cursor.setY(world.getHighestBlockYAt(cursor));
            for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
            int size = 16;
            chain: for (int i = 0; i < 16; i++){
                Location newCursor = getNextPoint(cursor, size);
                if (cursor == newCursor) {
                    if (size <= 24) size += 8;
                    else break chain;
                }
                else cursor = newCursor;
                for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
                if (cursor.getBlock().getBiome() == Biome.OCEAN || cursor.getBlock().getBiome() == Biome.FROZEN_OCEAN) break chain;
            }
            //for (int y = loEdge[0] - 4; y < 256; y++){
            //    world.getBlockAt(8 + (chunk.getX() * 16), y, 8 + (chunk.getZ() * 16)).setType(Material.DIAMOND_ORE);
            //    world.getBlockAt(loEdge[1] + (chunk.getX() * 16), y, loEdge[2] + (chunk.getZ() * 16)).setType(Material.DIAMOND_ORE);
            //}
        }
    }

    //Chain reactions could either be from a similarity with the modulus
    //division for the random chunk selector and the java random code
    //or it could be ravines extending the generated regions, then rivers
    //generating and extending it again, and so on
    //PS check the epic home area and save a schematic
    Location getNextPoint(Location center, int size){
        World world = center.getWorld();
        Location lowEdge = new Location(world, center.getX(), 256, center.getZ());
        for(int x = (size * -1); x < size; x++){
            int height;
            Biome biome;
            height = world.getHighestBlockYAt(center.getBlockX() + x, center.getBlockZ() - size);
            biome = world.getBiome(center.getBlockX() + x, center.getBlockZ() - size);
            if (height < lowEdge.getY() || biome == Biome.OCEAN || biome == Biome.FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() - size);
                lowEdge.setY(height);
            }
            height = world.getHighestBlockYAt(center.getBlockX() + x, center.getBlockZ() + size);
            biome = world.getBiome(center.getBlockX() + x, center.getBlockZ() + size);
            if (height < lowEdge.getY() || biome == Biome.OCEAN || biome == Biome.FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() + size);
                lowEdge.setY(height);
            }
        }
        for(int z = (size * -1); z < size; z++){
            int height;
            Biome biome;
            height = world.getHighestBlockYAt(center.getBlockX() - size, center.getBlockZ() + z);
            biome = world.getBiome(center.getBlockX() - size, center.getBlockZ() + z);
            if (height < lowEdge.getY() || biome == Biome.OCEAN || biome == Biome.FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() - size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
            height = world.getHighestBlockYAt(center.getBlockX() + size, center.getBlockZ() + z);
            biome = world.getBiome(center.getBlockX() + size, center.getBlockZ() + z);
            if (height < lowEdge.getY() || biome == Biome.OCEAN || biome == Biome.FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
        }
        return lowEdge;
    }
}