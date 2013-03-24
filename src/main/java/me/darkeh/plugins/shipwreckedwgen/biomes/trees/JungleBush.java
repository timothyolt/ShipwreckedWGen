package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class JungleBush implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    public JungleBush(Random rand, Location center, int height, int branches){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
    }
    public JungleBush(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(3) + 4;
        this.branches = rand.nextInt(2) + 1;
    }

    public void branch(int ySection, int height){}

    private void foiliage(Location origin, int size){
        for (int x = (size * -1); x <= size; x++) for (int z = (size * -1); z <= size; z++){
            Location target = new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY(), origin.getBlockZ() + z);
            double dist = origin.distance(target);
            if (dist <= size){
                Block block = target.getBlock();
                if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
                block = block.getRelative(0, 1, 0);
                if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
                if (dist <= size - 2){
                    block = block.getRelative(0, 1, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
                }
            }
        }
    }

    public boolean generate(){
        //bound check
        boolean term = false;
        if (!center.getBlock().isEmpty() || center.getBlock().getType() == Material.LEAVES) term = true;
        Material seedType = center.getBlock().getRelative(0, -1, 0).getType();
        //tree generation
        if (!term){
            int thisBranch = this.branches;
            center.getBlock().setTypeIdAndData(Material.LOG.getId(), (byte)3, false);
            foiliage(center, rand.nextInt(3) + 3);
        }
        return term; //If interrupted, it returns true.
    }
}

