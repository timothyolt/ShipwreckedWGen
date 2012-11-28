package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class DesertTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    public DesertTree(Random rand, Location center, int height, int branches){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
    }
    public DesertTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(3) + 4;
        this.branches = rand.nextInt(2) + 1;
    }

    public void branch(int ySection, int height){
        int face = rand.nextInt(4);
        int width = rand.nextInt(3) + 1;
        int length = rand.nextInt(2) + 1;
        for (int y = 0; y <= height; y++) for (int i = (width * -1); i <= width; i++) for (int ii = (length * -1); ii <= length; ii++){
            Material replace;
            if (y != height && i == 0 && ii > -1 && ii < 1) replace = Material.LOG;
            else replace = Material.LEAVES; //sets a log if in the center
            if (!(Math.abs(i) == width && Math.abs(ii) == length)){ //if the target isnt in a corner
                Block target;
                switch (face){
                    case 1:
                        target = center.getWorld().getBlockAt(center.getBlockX() + i, center.getBlockY() + ySection + y, center.getBlockZ() + ii + (y * 2));
                        break;
                    case 2:
                        target = center.getWorld().getBlockAt(center.getBlockX() + i, center.getBlockY() + ySection + y, center.getBlockZ() - ii - (y * 2));
                        break;
                    case 3:
                        target = center.getWorld().getBlockAt(center.getBlockX() + ii + (y * 2), center.getBlockY() + ySection + y, center.getBlockZ() + i);
                        break;
                    default:
                        target = center.getWorld().getBlockAt(center.getBlockX() - ii - (y * 2), center.getBlockY() + ySection + y, center.getBlockZ() + i);
                }
                if (target.isEmpty() || target.getType() == Material.LEAVES) target.setType(replace);
            }
        }
    }

    public boolean generate(){
        //bound check
        boolean term = false;
        for (int ySection = 0; ySection <= height && !term; ySection++){
            if (!center.getBlock().getRelative(0, ySection, 0).isEmpty()) term = true;
        }
        //tree generation
        if (!term){
            int thisBranch = this.branches;
            for (int ySection = 0; ySection <= height && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                target.setType(Material.LOG);
                if (height - ySection < height / 4 && thisBranch > 0 && rand.nextInt(4) == 1) branch(ySection, this.branches - thisBranch + 1);
                if (ySection == height) branch(ySection, rand.nextInt(2) + 1);
            }
        }
        return term; //If interrupted, it returns true.
    }
}
