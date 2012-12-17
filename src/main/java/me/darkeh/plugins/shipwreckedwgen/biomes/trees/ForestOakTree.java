package me.darkeh.plugins.shipwreckedwgen.biomes.trees;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class ForestOakTree implements BiomeTree{
    private Random rand;
    private Location center;
    private int height;
    private int branches;
    private ArrayList<Integer> usedBranches = new ArrayList<Integer>();
    public ForestOakTree(Random rand, Location center, int height, int branches){
        this.rand = rand;
        this.center = center;
        this.height = height;
        this.branches = branches;
    }
    public ForestOakTree(Random rand, Location center){
        this.rand = rand;
        this.center = center;
        this.height = rand.nextInt(2) + 5;
        this.branches = rand.nextInt(1) + 2;
    }

    public void branch(int ySection, int height) {
        int dir = rand.nextInt(3);
        int off = rand.nextInt(6) - 3;
        if (!usedBranches.contains(dir % 4)){
            dir = dir % 4;
            usedBranches.add(dir);
        }
        else if (!usedBranches.contains((dir + 1) % 4)){
            dir = (dir + 1) % 4;
            usedBranches.add(dir);
        }
        else if (!usedBranches.contains((dir + 2) % 4)){
            dir = (dir + 2) % 4;
            usedBranches.add(dir);
        }
        else if (!usedBranches.contains((dir + 3) % 4)){
            dir = (dir + 3) % 4;
            usedBranches.add(dir);
        }
        else return;
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
        BlockIterator branch = new BlockIterator(center.getWorld(), start, mod, 0, rand.nextInt(2) + 3);
        if (branch != null) while (branch.hasNext()){
            Block target = branch.next();
            if (target.isEmpty() || target.getType() == Material.LEAVES) target.setTypeIdAndData(Material.LOG.getId(), type, false);
            else{
                foiliage(target.getLocation(), 2);
                break;
            }
            if (!branch.hasNext()) foiliage(target.getLocation(), 2);
        }
    }

    private void foiliage(Location origin, int size){
        for (int x = (size * -1); x <= size; x++) for (int z = (size * -1); z <= size; z++){
            Location target = new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY(), origin.getBlockZ() + z);
            double dist = origin.distance(target);
            Block block = target.getBlock().getRelative(0, -2, 0);
            if (!(Math.abs(x) == size && Math.abs(z) == size) && block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
            block = block.getRelative(0, 1, 0);
            if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
            block = block.getRelative(0, 1, 0);
            if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
            block = block.getRelative(0, 1, 0);
            if (!(Math.abs(x) == size && Math.abs(z) == size) && block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
            if (dist <= size - 1){
                block = block.getRelative(0, 1, 0);
                if (block.isEmpty()) block.setTypeIdAndData(Material.LEAVES.getId(), (byte) 0, false);
            }
        }
    }

    public boolean generate() {
        //bound check
        boolean term = false;
        for (int ySection = 0; ySection <= height + 4 && !term; ySection++){
            if (!center.getBlock().getRelative(0, ySection, 0).isEmpty()) term = true;
        }
        //tree generation
        if (!term){
            for (int ySection = 0; ySection <= height + 4 && !term; ySection++){
                Block target = center.getBlock().getRelative(0, ySection, 0);
                target.setType(Material.LOG);
                if (ySection > height) branch(ySection, 3);
                if (ySection == height + 4){
                    int crown1 = rand.nextInt(2) + 2;
                    int crown2 = rand.nextInt(2) + 2;
                    int crown3 = rand.nextInt(2) + 2;
                    int crown4 = rand.nextInt(2) + 2;
                    for (int i = 0; i <= 4; i++){
                        if (i <= crown1){
                            Block newTar = target.getRelative(i, i, i);
                            if (newTar.isEmpty() || newTar.getType() == Material.LEAVES) newTar.setType(Material.LOG);
                            if (i + 1 > crown1) foiliage(target.getRelative(i, i, i).getLocation(), 2);
                        }
                        if (i <= crown2){
                            Block newTar = target.getRelative( i, i, -i);
                            if (newTar.isEmpty() || newTar.getType() == Material.LEAVES) newTar.setType(Material.LOG);
                            if (i + 1 > crown2) foiliage(target.getRelative(i, i, -i).getLocation(), 2);
                        }
                        if (i <= crown3){
                            Block newTar = target.getRelative(-i, i,  i);
                            if (newTar.isEmpty() || newTar.getType() == Material.LEAVES) newTar.setType(Material.LOG);
                            if (i + 1 > crown3) foiliage(target.getRelative(-i, i, i).getLocation(), 2);
                        }
                        if (i <= crown4){
                            Block newTar = target.getRelative(-i, i, -i);
                            if (newTar.isEmpty() || newTar.getType() == Material.LEAVES) newTar.setType(Material.LOG);
                            if (i + 1 > crown4) foiliage(target.getRelative(-i, i, -i).getLocation(), 2);
                        }
                    }
                    boolean ext = rand.nextBoolean();
                    int extHeight = rand.nextInt(3) + 3;
                    for (int i = 0; i < extHeight + 2; i++){
                        if (ext){
                            if (i < extHeight) {
                                target.getRelative(1, i, 1).setTypeIdAndData(Material.LOG.getId(), (byte) 0, false);
                                if (i + 1 >= extHeight) foiliage(target.getRelative(1, i, 1).getLocation(), 2);
                            }
                            target.getRelative(-1, i, -1).setTypeIdAndData(Material.LOG.getId(), (byte) 0, false);
                            if (i + 1 >= extHeight + 2) foiliage(target.getRelative(-1, i, -1).getLocation(), 2);
                        }
                        else{
                            if (i < extHeight) {
                                target.getRelative(1, i, -1).setTypeIdAndData(Material.LOG.getId(), (byte) 0, false);
                                if (i + 1 >= extHeight) foiliage(target.getRelative(1, i, -1).getLocation(), 2);
                            }
                            target.getRelative(-1, i, 1).setTypeIdAndData(Material.LOG.getId(), (byte) 0, false);
                            if (i + 1 >= extHeight + 2) foiliage(target.getRelative(-1, i, 1).getLocation(), 2);
                        }
                    }
                }
            }
        }
        return term; //If interrupted, it returns true.
    }
}
