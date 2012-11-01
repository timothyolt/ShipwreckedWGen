package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class OrePopulator extends BlockPopulator{

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 0; y < 8; y++){
            if (random.nextInt(2048)==1){
                int section = random.nextInt(12);
                boolean choice = random.nextBoolean();
                int xx = random.nextInt(16) + (chunk.getX() * 16) + 1;
                int yy = random.nextInt(8) + (section * 8) + 1;
                int zz = random.nextInt(16) + (chunk.getZ() * 16) + 1;
                if (section <= 11 && section >= 8 && choice==true) oreBlob(xx, yy, zz, world, 2, random, Material.COAL_ORE);
                else if (section == 7||section == 6) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, Material.IRON_ORE);
                else if (section == 5){
                    if (choice == true) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 2, random, Material.GOLD_ORE);
                    else;
                }
                else if (section == 4);
                else if (section == 3){
                    if (choice == true);
                    else;
                }
                else if (section == 2){
                    if (choice == true);
                    else;
                }
                else if (section == 1){
                    if (choice == true);
                    else;
                }
                else if (section == 0);
            }
        }
    }
    
    void oreBlob(int centerX, int centerY, int centerZ, World world, int radius, Random random, Material ore){
        for (int deposits = random.nextInt(radius*2); deposits >= 0; deposits--){
            int xx = random.nextInt(radius*2) - radius + centerX;
            int yy = random.nextInt(radius*2) - radius + centerY;
            int zz = random.nextInt(radius*2) - radius + centerZ;
            Location center = new Location(world, xx, yy, zz);
            for (int x = (radius * -1); x < radius; x++) for (int z = (radius * -1); z < radius; z++) for (int y = (radius * -1); y < radius; y++){
                Block block = world.getBlockAt(xx + x, yy + y, zz + z);
                double distance = center.distance(block.getLocation());
                if (distance < radius && random.nextInt(50) == 1 && block.getType() == Material.STONE) block.setType(ore);
                else if (distance < radius + 1 && block.getType() == Material.STONE) block.setType(ore);
            }
        }
    }
    
    //NOTE: specs of metallic ores are found in the cavern gen code too
    void oreVein(Vector vec1, Vector vec2, World world, int radius, Random random, Material ore){
        int centerY = vec1.getBlockY();
        if (world.getBlockAt(vec1.getBlockX(), centerY, vec1.getBlockZ()).getType()==Material.AIR){
            int shiftLife = 8;
            centerY += 4;
            while (world.getBlockAt(vec1.getBlockX(), centerY, vec1.getBlockZ()).getType()==Material.AIR&&shiftLife>0){
                centerY -= 1;
            }
        }
        vec1.setY(centerY);
        BlockIterator blit;
        try{ blit = new BlockIterator(world, vec1, vec2, 0, radius);}
        catch (Exception e) {return;}
        if (blit==null) return;
        while(blit.hasNext()){
            Block target = blit.next();
            if(target.getType()==Material.STONE) target.setType(ore);
        }
        
    }
}
