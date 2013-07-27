package org.bytefire.plugins.shipwreckedwgen.populators;

//im trying to generate rivers based on two (two dimensional) points. im going to get a line between those points, and then use a noise function to give it curves. how would I go about tracing said line with a sphere?

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import static org.bukkit.Material.*;
import static org.bukkit.block.Biome.*;


public class RiverPopulator extends BlockPopulator{
    private ShipwreckedWGen plugin;
    public RiverPopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        Location cursor = new Location(world, chunk.getX() * 16, 0, chunk.getZ() * 16);
        int cursorHeight = getHighestSolidBlockY(world, cursor.getBlockX(), cursor.getBlockZ());
        Biome cursorBiome = cursor.getBlock().getBiome();
        int chance;
        if (cursorBiome == EXTREME_HILLS || cursorBiome == JUNGLE || cursorBiome == JUNGLE_HILLS) chance = random.nextInt(24);
        else chance = random.nextInt(48);
        if (cursorHeight >= 100 && chance == 1 && cursorBiome != OCEAN && cursorBiome != FROZEN_OCEAN && cursorBiome != RIVER && cursorBiome != FROZEN_RIVER){
            cursor.setY(getHighestSolidBlockY(world, cursor.getBlockX(), cursor.getBlockZ()));
            int size = 16;
            chain: for (int i = 0; i < 16; i++){
                Location newCursor = getNextPoint(cursor, size);
                fillRiverSection(cursor, newCursor, 4, random);
                if (cursor == newCursor) {
                    if (size <= 24) size += 8;
                    else break chain;
                }
                else cursor = newCursor;
                if (cursor.getBlock().getBiome() == OCEAN || cursor.getBlock().getBiome() == FROZEN_OCEAN) break chain;
            }
        }
    }

    Location getNextPoint(Location center, int size){
        World world = center.getWorld();
        Location lowEdge = new Location(world, center.getX(), center.getY() + 1, center.getZ());
        for(int x = (size * -1); x < size; x++){
            int height;
            Biome biome;
            height = getHighestSolidBlockY(world, center.getBlockX() + x, center.getBlockZ() - size);
            biome = world.getBiome(center.getBlockX() + x, center.getBlockZ() - size);
            if (height < lowEdge.getY() || biome == OCEAN || biome == FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() - size);
                lowEdge.setY(height);
            }
            height = getHighestSolidBlockY(world, center.getBlockX() + x, center.getBlockZ() + size);
            biome = world.getBiome(center.getBlockX() + x, center.getBlockZ() + size);
            if (height < lowEdge.getY() || biome == OCEAN || biome == FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + x);
                lowEdge.setZ(center.getBlockZ() + size);
                lowEdge.setY(height);
            }
        }
        for(int z = (size * -1); z < size; z++){
            int height;
            Biome biome;
            height = getHighestSolidBlockY(world, center.getBlockX() - size, center.getBlockZ() + z);
            biome = world.getBiome(center.getBlockX() - size, center.getBlockZ() + z);
            if (height < lowEdge.getY() || biome == OCEAN || biome == FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() - size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
            height = getHighestSolidBlockY(world, center.getBlockX() + size, center.getBlockZ() + z);
            biome = world.getBiome(center.getBlockX() + size, center.getBlockZ() + z);
            if (height < lowEdge.getY() || biome == OCEAN || biome == FROZEN_OCEAN){
                lowEdge.setX(center.getBlockX() + size);
                lowEdge.setZ(center.getBlockZ() + z);
                lowEdge.setY(height);
            }
        }
        return lowEdge;
    }

    void fillRiverSection(Location start, Location end, int size, Random rand){
        fillRiverWaypoint(start, size, rand);
        int xdif = end.getBlockX() - start.getBlockX();
        int zdif = end.getBlockZ() - start.getBlockZ();
        if (Math.abs(xdif) > Math.abs(zdif)){
            double zslope = ((double) zdif) / ((double) xdif);
            if (xdif > 0) for (int x = 0; x <= xdif; x++){
                Location target = new Location(start.getWorld(), start.getBlockX() + x, 0, start.getBlockZ() + (x * zslope));
                int height = getHighestSolidBlockY(target.getWorld(), target.getBlockX(), target.getBlockZ());
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, true, rand);
            }
            else for (int x = 0; x >= xdif; x--){
                Location target = new Location(start.getWorld(), start.getBlockX() + x, 0, start.getBlockZ() + (x * zslope));
                int height = getHighestSolidBlockY(target.getWorld(), target.getBlockX(), target.getBlockZ());
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, true, rand);
            }
        }
        else {
            double xslope = ((double) xdif) / ((double) zdif);
            if (zdif > 0) for (int z = 0; z <= zdif; z++){
                Location target = new Location(start.getWorld(), start.getBlockX() + (z * xslope), 0, start.getBlockZ() + z);
                int height = getHighestSolidBlockY(target.getWorld(), target.getBlockX(), target.getBlockZ());
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, false, rand);
            }
            else for (int z = 0; z >= zdif; z--){
                Location target = new Location(start.getWorld(), start.getBlockX() + (z * xslope), 0, start.getBlockZ() + z);
                int height = getHighestSolidBlockY(target.getWorld(), target.getBlockX(), target.getBlockZ());
                if (height > start.getBlockY()) target.setY(start.getBlockY());
                else target.setY(height);
                fillRiverSegment(target, size, false, rand);
            }
        }
    }

    void fillRiverWaypoint(Location center, int size, Random rand){
        for (int x = (size * -1) - 1; x < size + 1; x++) for (int z = (size * -1) - 1; z < size + 1; z++){
            for (int y = (size * -1) - 1; y < size + 1; y++){
                Location target = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                Block targetBlock = target.getBlock();
                double dist = center.distance(target);
                double radi = center.distance(new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z));
                Biome biome = targetBlock.getBiome();
                if (biome != OCEAN && biome != FROZEN_OCEAN){
                    if (biome == TAIGA || biome == TAIGA_HILLS || biome == ICE_PLAINS || biome == ICE_MOUNTAINS) targetBlock.setBiome(FROZEN_RIVER);
                    else targetBlock.setBiome(RIVER);
                    if (dist < size){
                        if (y < 0) targetBlock.setType(WATER);
                        else targetBlock.setType(AIR);
                    }
                    else if (dist <= size + 2 && !targetBlock.isLiquid()){
                        if (y < -1) targetBlock.setType(DIRT);
                        else if (y < 0) targetBlock.setType(GRASS);
                    }
                    if (y > size - 1 && radi <= size + 1 && rand.nextInt(8) == 1){
                        int height = -1;
                        int id = rand.nextInt();
                        heightCheck: for (int s = 0; s < 8; s++){
                            if (target.getBlock().getRelative(0, -1 * (s), 0).getType() == GRASS){
                                height = s;
                                break heightCheck;
                            }
                        }
                        if (height != -1) for (int s = 0; s < height; s++){
                            target.getBlock().getRelative(0, -1 * (s), 0).setTypeId(SUGAR_CANE_BLOCK.getId(), false);
                        }
                    }
                }
            }
        }
    }

    void fillRiverSegment(Location center, int size, boolean face, Random rand){
        for (int i = (size * -1) - 1; i < size + 2; i++) for (int y = (size * -1) - 5; y < size + 5; y++){
            Location target;
            if (face) target = new Location(center.getWorld(), center.getBlockX(), center.getBlockY() + y, center.getBlockZ() + i);
            else target = new Location(center.getWorld(), center.getBlockX() + i, center.getBlockY() + y, center.getBlockZ());
            Block targetBlock = target.getBlock();
            double dist = center.distance(target);
            Biome biome = targetBlock.getBiome();
            if (biome != OCEAN && biome != FROZEN_OCEAN){
                if (biome == TAIGA || biome == TAIGA_HILLS || biome == ICE_PLAINS || biome == ICE_MOUNTAINS) targetBlock.setBiome(FROZEN_RIVER);
                else targetBlock.setBiome(RIVER);
                if (dist < size){
                    if (y < 0) targetBlock.setType(WATER);
                    else {
                        if (y == size - 1){
                            int vineChance;
                            if (biome == JUNGLE || biome == JUNGLE_HILLS) vineChance = rand.nextInt(4);
                            else vineChance = rand.nextInt(16);
                            Block vineTarget = target.getWorld().getBlockAt(target.getBlockX(), target.getBlockY(), target.getBlockZ());
                            if (!vineTarget.getRelative(BlockFace.UP).isEmpty() && vineChance == 1) vineTarget.setTypeIdAndData(106, (byte)1, false);
                            else clearAndPatch(targetBlock);
                        }
                        else clearAndPatch(targetBlock);
                    }
                }
                else if (!targetBlock.isLiquid()){
                    if (dist <= size + 2){
                        if (y < -1) targetBlock.setType(DIRT);
                        else if (y < 0) targetBlock.setType(GRASS);
                        else if (y >= 0 && (targetBlock.getType() == DIRT || targetBlock.getType() == GRASS) && targetBlock.getRelative(BlockFace.DOWN).getType() == AIR) clearAndPatch(targetBlock);
                    }
                    else if (y < 0 && targetBlock.isEmpty()) targetBlock.setType(STONE);
                }
            }
        }
    }

    private void clearAndPatch(Block targetBlock){
        if (targetBlock.getType() == LOG){
            for (int p = 1; p < 16; p++){
                Block patchBlock = targetBlock.getRelative(0, -1 * p, 0);
                switch (patchBlock.getType()){
                    case AIR: case WATER: case STATIONARY_WATER: case LEAVES: case LOG:
                        patchBlock.setTypeIdAndData(LOG.getId(), targetBlock.getData(), false);
                }
            }
        }
        else targetBlock.setType(AIR);
    }

    private int getHighestSolidBlockY(World world, int x, int z){
        Location highest = new Location(world, x, world.getHighestBlockYAt(x, z), z);
        int highestY = highest.getBlockY();
        Material topMat = highest.getBlock().getRelative(0, -1, 0).getType();
        if (topMat == LEAVES || topMat == LOG || topMat == VINE) {
            for (int y = 0; y > -128; y--){
                Material mat = highest.getBlock().getRelative(0, y - 1, 0).getType();
                if (mat == GRASS) highestY = highest.getBlockY() + y;
            }
        }
        return highestY;
    }
}