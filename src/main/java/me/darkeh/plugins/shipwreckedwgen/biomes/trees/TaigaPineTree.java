package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class TaigaPineTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    public TaigaPineTree(Random rand, Location center, int height, int branches){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = (int)Math.floor(height / 3D);
    }
    public TaigaPineTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(3) * 3 + 6;
        this.branches = (int)Math.floor(height / 3D);
    }

    public void branch(int ySection, int height) {
        branch(ySection);
    }

    public void branch(int ySection) {
        if (ySection > 2){
            double cone = branches + 4;
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
                if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)5, false);
            }
            branch = new BlockIterator(center.getWorld(), start, branch2, 0, (int)Math.floor(cone) - 2);
            if (branch != null) while (branch.hasNext()){
                Block target = branch.next();
                if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)9, false);
            }
            branch = new BlockIterator(center.getWorld(), start, branch3, 0, (int)Math.floor(cone) - 2);
            if (branch != null) while (branch.hasNext()){
                Block target = branch.next();
                if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)5, false);
            }
            branch = new BlockIterator(center.getWorld(), start, branch4, 0, (int)Math.floor(cone) - 2);
            if (branch != null) while (branch.hasNext()){
                Block target = branch.next();
                if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), (byte)9, false);
            }
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)1, false);
                }
            }
            branches -= 1;
        }
    }

    private void foiliage(int ySection){
        if (ySection > height + 2){
            double cone;
            if (ySection == height + 3) cone = 2;
            else cone = 1;
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone && dist >= cone - 3){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)1, false);
                }
           }
        }
        else if (ySection > 3){
            double cone = branches - (ySection % 3) + 5;
            for (int x = ((int)Math.ceil(cone) * -1); x <= (int)Math.ceil(cone); x++) for (int z = ((int)Math.ceil(cone) * -1); z <= (int)Math.ceil(cone); z++){
                Location target = new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z);
                double dist = center.distance(target);
                if (dist <= cone && dist >= cone - 3){
                    Block block = target.getBlock().getRelative(0, ySection, 0);
                    if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte)1, false);
                }
            }
        }
    }

    public boolean generate() {
        //bound check
        boolean term = false;
        for (int ySection = 1; ySection <= height + 2 && !term; ySection++){
            if (!center.getBlock().getRelative(0, ySection, 0).isEmpty()) term = true;
        }
        //tree generation
        if (!term){
            for (int ySection = 0; ySection <= height + 2 && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                target.setTypeIdAndData(Material.LOG.getId(), (byte)1, false);
                if (ySection % 3 == 0) branch(ySection);
                else foiliage(ySection);
                if (ySection == height + 2){
                    foiliage(ySection + 1);
                    foiliage(ySection + 2);
                }
            }
        }
        return term; //If interrupted, it returns true.
    }
}
