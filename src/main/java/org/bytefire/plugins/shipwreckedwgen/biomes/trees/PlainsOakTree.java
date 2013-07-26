package org.bytefire.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import static org.bukkit.Material.*;

public class PlainsOakTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    public PlainsOakTree(Random rand, Location center, int height, int branches){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
    }
    public PlainsOakTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(3) + 6;
        this.branches = rand.nextInt(1) + 2;
    }

    public void branch(int ySection, int height) {
        int dir = rand.nextInt(3);
        int off = rand.nextInt(6) - 3;
        Vector start;
        Vector mod;
        Byte type;
        switch (dir){
            case 0: // South
                start = new Vector(center.getBlockX(), center.getBlockY() + ySection, center.getBlockZ() + 1);
                mod = new Vector(off, height, 4);
                type = 8;
                if (start.toLocation(center.getWorld()).getBlock().isEmpty()) break;
            case 1: // West
                start = new Vector(center.getBlockX() - 1, center.getBlockY() + ySection, center.getBlockZ());
                mod = new Vector(-4, height, off);
                type = 4;
                if (start.toLocation(center.getWorld()).getBlock().isEmpty()) break;
            case 2: // North
                start = new Vector(center.getBlockX(), center.getBlockY() + ySection, center.getBlockZ() - 1);
                mod = new Vector(off, height, -4);
                type = 8;
                if (start.toLocation(center.getWorld()).getBlock().isEmpty()) break;
            case 3: // East
                start = new Vector(center.getBlockX() + 1, center.getBlockY() + ySection, center.getBlockZ());
                mod = new Vector(4, height, off);
                type = 4;
                if (start.toLocation(center.getWorld()).getBlock().isEmpty()) break;
            default: // South
                start = new Vector(center.getBlockX(), center.getBlockY() + ySection, center.getBlockZ() + 1);
                mod = new Vector(off, height, 4);
                type = 8;
                if (!start.toLocation(center.getWorld()).getBlock().isEmpty()) return;
                else break;
        }
        BlockIterator branch = new BlockIterator(center.getWorld(), start, mod, 0, 4);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == LEAVES) target.setTypeIdAndData(LOG.getId(), type, false);
            else{
                foiliage(target.getLocation(), 4);
                break;
            }
            if (!branch.hasNext()) foiliage(target.getLocation(), 5);
        }
    }

    private void foiliage(Location origin, int size){
        for (int x = (size * -1); x <= size; x++) for (int z = (size * -1); z <= size; z++){
            Location target = new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY(), origin.getBlockZ() + z);
            double dist = origin.distance(target);
            if (dist <= size){
                Block block = target.getBlock();
                if (block.isEmpty()) block.setTypeIdAndData(LEAVES.getId(), (byte) 0, false);
                block = block.getRelative(0, 1, 0);
                if (block.isEmpty()) block.setTypeIdAndData(LEAVES.getId(), (byte) 0, false);
                if (dist <= size - 2){
                    block = block.getRelative(0, 1, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(LEAVES.getId(), (byte) 0, false);
                }
            }
        }
    }

    public boolean generate() {
        int extra = rand.nextInt(2) + 2;
        //bound check
        boolean term = false;
        for (int ySection = 0; ySection <= height + extra && !term; ySection++){
            if (!center.getBlock().getRelative(0, ySection, 0).isEmpty()) term = true;
        }
        Material seedType = center.getBlock().getRelative(0, -1, 0).getType();
        if (seedType != GRASS && seedType != DIRT && seedType != SAND) term = true;
        //tree generation
        if (!term){
            for (int ySection = 0; ySection <= height + extra && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                target.setType(LOG);
                if (ySection == height) for (int i = 0; i <= branches; i++) branch(ySection, extra);
                if (ySection == height + extra) foiliage(target.getLocation(), 6);
            }
        }
        return term; //If interrupted, it returns true.
    }

}