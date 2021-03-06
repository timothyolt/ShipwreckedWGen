package org.bytefire.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import static org.bukkit.Material.*;

public class ForestSpruceTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    private int cap;
    public ForestSpruceTree(Random rand, Location center, int height, int branches, int cap){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
        this.cap = cap;
    }
    public ForestSpruceTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(8) + 8;
        this.branches = rand.nextInt(3);
        this.cap = rand.nextInt(2) + 3;
    }

    public void branch(int ySection, int height) {
        branch(ySection);
    }

    public void branch(int ySection) {
        Block target = center.getBlock().getRelative(rand.nextInt(2) - 1, ySection, rand.nextInt(2) - 1);
        if (target.isEmpty()) target.setTypeIdAndData(LEAVES.getId(), (byte)1, false);
    }

    private void foiliage(int ySection){
        double cone = -9.9;
        if (ySection < height - (cap * 2) - 2) cone = cap - height - (cap * 2) - 2 - ySection;
        else if (ySection == height) cone = 2;
        else if (ySection == height + 1) cone = 2;
        else if (ySection == height + 2) cone = 1;
        else if (ySection <= height) cone = ((height - Math.floor(ySection / 2D)) / 2D);
        if (cone != -9.9) for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
            Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
            double dist = center.distance(target);
            if (dist <= cone && dist >= cone - 3){
                Block block = target.getBlock().getRelative(0, ySection, 0);
                if (block.isEmpty()) block.setTypeIdAndData(LEAVES.getId(), (byte)1, false);
            }
        }
    }

    public boolean generate() {
        //bound check
        boolean term = false;
        for (int ySection = 1; ySection <= height + 2 && !term; ySection++){
            if (!center.getBlock().getRelative(0, ySection, 0).isEmpty()) term = true;
        }
        Material seedType = center.getBlock().getRelative(0, -1, 0).getType();
        if (seedType != GRASS && seedType != DIRT && seedType != SAND) term = true;
        //tree generation
        if (!term){ //(cap * 2) - 2 //
            for (int ySection = 0; ySection <= height + 2 && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                if (ySection <= height) target.setTypeIdAndData(LOG.getId(), (byte)1, false);
                if (ySection >= (cap * 2) - 2 + cap) foiliage(ySection);
                else if (rand.nextInt(4) == 1 && branches > 0){
                    branch(ySection);
                    branches -= 1;
                }
            }
        }
        return term; //If interrupted, it returns true.
    }

}
