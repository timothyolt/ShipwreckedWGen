package org.bytefire.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class JungleTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    private int cap;
    public JungleTree(Random rand, Location center, int height, int branches, int cap){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
        this.cap = cap;
    }
    public JungleTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(6) * 4 + 16;
        this.branches = rand.nextInt(3);
        this.cap = rand.nextInt(2) + 2;
    }

    public void branch(int value, int ySection) {
        double cone = ((double)(height - Math.floor(value / 2D)) / 2D) + 1;
        Vector start = center.toVector();
        start = start.setY(start.getY() + ySection);
        Vector branch1 = new Vector(-4, 0, rand.nextInt(6) - 3); //West
        Vector branch2 = new Vector(rand.nextInt(6) - 3, 0, -4); //North
        Vector branch3 = new Vector(4, 0, rand.nextInt(6) - 3); //East
        Vector branch4 = new Vector(rand.nextInt(6) - 3, 0, 4); //South
        BlockIterator branch;
        branch = new BlockIterator(center.getWorld(), start, branch1, 0, (int)Math.floor(cone) - 2);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)7, false);
        }
        branch = new BlockIterator(center.getWorld(), start, branch2, 0, (int)Math.floor(cone) - 2);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)11, false);
        }
        branch = new BlockIterator(center.getWorld(), start, branch3, 0, (int)Math.floor(cone) - 2);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)7, false);
        }
        branch = new BlockIterator(center.getWorld(), start, branch4, 0, (int)Math.floor(cone) - 2);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)11, false);
        }
    }

    private void foiliage(int value, int ySection){ //Add 4 to leaves data to set it to no-decay
        if (ySection == height + 1) {
            double cone = ((double)(height + 2 - Math.floor(value / 2D)) / 2D) - 1;
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)7, false);
                }
            }
        }
        else if (ySection == height + 2) {
            double cone = ((double)(height + 2 - Math.floor(value / 2D)) / 3D);
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)7, false);
                }
            }
        }
        else if (ySection == height + 3) {
            double cone = ((double)(height + 2 - Math.floor(value / 2D)) / 4D);
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)7, false);
                }
            }
        }
        else if (ySection <= height) {
            double cone = ((height + 2 - Math.floor(value / 2D)) / 2D) + 1;
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone && dist >= cone - 3){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)7, false);
                }
            }
        }
    }

    public boolean generate() {
        //bound check
        boolean term = false;
        for (int ySection = 1; ySection <= height + 2 && !term; ySection++){
            Block block1 = center.getBlock().getRelative(0, ySection, 0);
            Block block2 = center.getBlock().getRelative(1, ySection, 0);
            Block block3 = center.getBlock().getRelative(0, ySection, 1);
            Block block4 = center.getBlock().getRelative(1, ySection, 1);
            if (!block1.isEmpty() || block1.getType() == Material.LEAVES) term = true;
            if (!block2.isEmpty() || block2.getType() == Material.LEAVES) term = true;
            if (!block3.isEmpty() || block3.getType() == Material.LEAVES) term = true;
            if (!block4.isEmpty() || block4.getType() == Material.LEAVES) term = true;
        }
        Material seedType = center.getBlock().getRelative(0, -1, 0).getType();
        if (seedType != Material.GRASS && seedType != Material.DIRT && seedType != Material.SAND) term = true;
        //tree generation
        if (!term){ //(cap * 2) - 2 //
            for (int ySection = 0; ySection <= height + 3 && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                if (ySection <= height){
                    target.setTypeIdAndData(Material.LOG.getId(), (byte)3, false);
                    target.getRelative(1, 0, 0).setTypeIdAndData(Material.LOG.getId(), (byte)3, false);
                    target.getRelative(0, 0, 1).setTypeIdAndData(Material.LOG.getId(), (byte)3, false);
                    target.getRelative(1, 0, 1).setTypeIdAndData(Material.LOG.getId(), (byte)3, false);
                }
                if (ySection >= (height * 3) / 4){
                    foiliage(ySection - ((height * 3) / 4), ySection);
                    if (ySection <= height && rand.nextBoolean()) branch(ySection - ((height * 3) / 4), ySection);
                }
                else if (ySection >= (height * 3) / 5){
                    foiliage(ySection + (height / 4), ySection);
                    if (rand.nextBoolean()) branch(ySection + (height / 4), ySection);
                }
            }
        }
        return term; //If interrupted, it returns true.
    }

}