package me.darkeh.plugins.shipwreckedwgen.populators;

//im trying to generate rivers based on two (two dimensional) points. im going to get a line between those points, and then use a noise function to give it curves. how would I go about tracing said line with a sphere?

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
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
            int size = 16;
            chain: for (int i = 0; i < 16; i++){
                Location newCursor = getNextPoint(cursor, size);
                fillRiverSection(cursor, newCursor, 4);
                for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
                if (cursor == newCursor) {
                    System.out.println("River waypoint expand");
                    if (size <= 24) size += 8;
                    else{
                        System.out.println("River waypoint not found");
                        break chain;
                    }
                }
                else cursor = newCursor;
                //for (int y = (int)cursor.getY(); y < 256; y++) world.getBlockAt(cursor.getBlockX(), y, cursor.getBlockZ()).setType(Material.DIAMOND_ORE);
                if (cursor.getBlock().getBiome() == Biome.OCEAN || cursor.getBlock().getBiome() == Biome.FROZEN_OCEAN){
                    System.out.println("River waypoint hit ocean");
                    break chain;
                }
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
        Location lowEdge = new Location(world, center.getX(), center.getY() + 1, center.getZ());
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

    void fillRiverSection(Location start, Location end, int size){
        fillRiverWaypoint(start, size);
        System.out.println("River waypoint");
        int xdif = end.getBlockX() - start.getBlockX();
        int zdif = end.getBlockZ() - start.getBlockZ();
        if (Math.abs(xdif) > Math.abs(zdif)){
            double zslope = ((double) zdif) / ((double) xdif);
            if (xdif > 0) for (int x = 0; x <= xdif; x++){
                System.out.println("River segment code 1");
                Location target = new Location(start.getWorld(), start.getBlockX() + x, 0, start.getBlockZ() + (x * zslope));
                int height = target.getWorld().getHighestBlockYAt(target);
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, true);
            }
            else for (int x = 0; x >= xdif; x--){
                System.out.println("River segment code 1");
                Location target = new Location(start.getWorld(), start.getBlockX() + x, 0, start.getBlockZ() + (x * zslope));
                int height = target.getWorld().getHighestBlockYAt(target);
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, true);
            }
        }
        else {
            double xslope = ((double) xdif) / ((double) zdif);
            if (zdif > 0) for (int z = 0; z <= zdif; z++){
                System.out.println("River segment code 2");
                Location target = new Location(start.getWorld(), start.getBlockX() + (z * xslope), 0, start.getBlockZ() + z);
                int height = target.getWorld().getHighestBlockYAt(target);
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, false);
            }
            else for (int z = 0; z >= zdif; z--){
                System.out.println("River segment code 2");
                Location target = new Location(start.getWorld(), start.getBlockX() + (z * xslope), 0, start.getBlockZ() + z);
                int height = target.getWorld().getHighestBlockYAt(target);
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, false);
            }
        }
    }

    void fillRiverWaypoint(Location center, int size){
        for (int x = (size * -1) - 1; x < size + 1; x++) for (int z = (size * -1) - 1; z < size + 1; z++){
            for (int y = (size * -1) - 1; y < size + 1; y++){
                Location target = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                Block targetBlock = target.getBlock();
                double dist = center.distance(target);
                if (dist < size){
                    boolean frozen = false;
                    if (target.getY() == center.getY()){
                        Biome biome = center.getWorld().getBiome(center.getBlockX() + x, center.getBlockZ() + z);
                        if (biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS || biome == Biome.ICE_PLAINS || biome == Biome.ICE_MOUNTAINS){
                            targetBlock.setBiome(Biome.FROZEN_RIVER);
                            frozen = true;
                        }
                        else targetBlock.setBiome(Biome.RIVER);
                    }
                    if (y < 0){
                        if (frozen) targetBlock.setType(Material.ICE);
                        else targetBlock.setType(Material.WATER);
                    }
                    else targetBlock.setType(Material.AIR);
                }
                else if (dist <= size + 2 && !targetBlock.isLiquid()){
                    if (y < -1) targetBlock.setType(Material.DIRT);
                    else if (y < 0) targetBlock.setType(Material.GRASS);
                }
            }
        }
    }

    void fillRiverSegment(Location center, int size, boolean face){
        for (int i = (size * -1) - 1; i < size + 2; i++) for (int y = (size * -1) - 5; y < size + 1; y++){
            Location target;
            if (face) target = new Location(center.getWorld(), center.getBlockX(), center.getBlockY() + y, center.getBlockZ() + i);
            else target = new Location(center.getWorld(), center.getBlockX() + i, center.getBlockY() + y, center.getBlockZ());
            Block targetBlock = target.getBlock();
            double dist = center.distance(target);
            if (dist < size){
                boolean frozen = false;
                if (target.getY() == center.getY()){
                    Biome biome;
                    if (face) biome = center.getWorld().getBiome(center.getBlockX() + i, center.getBlockZ());
                    else biome = center.getWorld().getBiome(center.getBlockX(), center.getBlockZ() + i);
                    if (biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS || biome == Biome.ICE_PLAINS || biome == Biome.ICE_MOUNTAINS){
                        targetBlock.setBiome(Biome.FROZEN_RIVER);
                        frozen = true;
                    }
                    else targetBlock.setBiome(Biome.RIVER);
                }
                if (y < 0){
                    if (frozen) targetBlock.setType(Material.ICE);
                    else targetBlock.setType(Material.WATER);
                }
                else targetBlock.setType(Material.AIR);
            }
            else if (!targetBlock.isLiquid()){
                if (dist <= size + 2){
                    if (y < -1) targetBlock.setType(Material.DIRT);
                    else if (y < 0) targetBlock.setType(Material.GRASS);
                }
                else if (y < 0 && targetBlock.isEmpty()) targetBlock.setType(Material.STONE);
            }
        }
    }
    //TODO biome setting method that doesnt override oceans
}