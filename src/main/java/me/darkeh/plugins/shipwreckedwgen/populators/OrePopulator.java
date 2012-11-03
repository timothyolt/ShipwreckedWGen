package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class OrePopulator extends BlockPopulator{
//Seeded ores idea from DarthAndroid@esper.net <--- delayed for the meanwhile
    private ShipwreckedWGen plugin;
    public OrePopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }
    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 0; y < 8; y++){
            if (random.nextInt(256)==1){
                int section = random.nextInt(12);
                boolean choice = random.nextBoolean();
                int xx = x + (chunk.getX() * 16);
                int yy;
                if(section==11) yy = random.nextInt(125) + 89;
                else yy = random.nextInt(8) + (section * 8) + 1;
                int zz = z + (chunk.getZ() * 16);
                if (section==11) oreBlob(xx, yy, zz, world, 3, random, Material.COAL_ORE);
                if (section <= 10 && section >= 8) oreBlob(xx, yy, zz, world, 2, random, Material.COAL_ORE);
                else if (section == 7||section == 6) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, Material.IRON_ORE);
                else if (section == 5 && choice==true) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, Material.GOLD_ORE);
                else if (section == 4 && choice==true) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, Material.REDSTONE_ORE);
                else if (section == 3){
                    if (choice == true) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, Material.REDSTONE_ORE);
                    else if (random.nextInt(3)==1) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 4, random, Material.LAPIS_ORE);
                }
                else if (section == 2 && random.nextInt(8)==1){
                    if (choice == true) orePowder(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, Material.LAPIS_ORE);
                    else oreShard(xx, yy, zz, 5, random, world, Material.DIAMOND_ORE);
                }
                else if (section == 1 && random.nextInt(5)==1){
                    if (choice == true) oreShard(xx, yy, zz, 5, random, world, Material.DIAMOND_ORE);
                    else oreShard(xx, yy, zz, 5, random, world, Material.EMERALD_ORE);
                }
                else if (section == 0 && random.nextInt(5)==1){
                    if (choice == true) oreVein(new Vector(xx, yy, zz), new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4), world, 6, random, Material.OBSIDIAN);
                    else oreShard(xx, yy, zz, 5, random, world, Material.EMERALD_ORE);
                }
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
                Block block = plugin.chunkManager.getBlockSafely(xx + x, yy + y, zz + z, world);
                if (block != null){
                    double distance = center.distance(block.getLocation());
                    if (distance < radius && random.nextInt(50) == 1 && block.getType() == Material.STONE) block.setType(ore);
                    else if (distance < radius + 1 && block.getType() == Material.STONE) block.setType(ore);
                }
            }
        }
    }
    
    //NOTE: specs of metallic ores are found in the cavern gen code too
    void oreVein(Vector start, Vector dir, World world, int radius, Random random, Material ore){
        int centerY = start.getBlockY();
        if (plugin.chunkManager.getBlockSafely(start.getBlockX(), centerY, start.getBlockZ(), world).getType()==Material.AIR){
            int shiftLife = 8;
            centerY += 4;
            while (plugin.chunkManager.getBlockSafely(start.getBlockX(), centerY, start.getBlockZ(), world).getType()==Material.AIR&&shiftLife>0){
                centerY -= 1;
            }
        }
        start.setY(centerY);
        BlockIterator blit;
        try{ blit = new BlockIterator(world, start, dir, 0, radius);}
        catch (Exception e) {return;}
        if (blit==null) return;
        {int CAP = 10;
        while(blit.hasNext()){
            Block target = blit.next();
            if(target.getType()==Material.STONE) target.setType(ore);
            CAP--;
        }}
        Vector end = start.add(dir);
        Vector mid = new Vector(start.getX()+(dir.getX()/2), start.getY()+(dir.getY()/2.0), start.getZ()+(dir.getZ()/2.0));
        for(int subVeins = random.nextInt(radius/2) + (radius/2); subVeins > 0; subVeins--){
            Vector newdir = new Vector(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4);
            int newstart = random.nextInt(3);
            BlockIterator newblit;
            switch(newstart){
                case 0:
                    try{ newblit = new BlockIterator(world, start, newdir, 0, radius/2);}
                    catch (Exception e) {return;}
                    break;
                case 1:
                    try{ newblit = new BlockIterator(world, mid, newdir, 0, radius/2);}
                    catch (Exception e) {return;}
                    break;
                case 2:
                    try{ newblit = new BlockIterator(world, end, newdir, 0, radius/2);}
                    catch (Exception e) {return;}
                    break;
                default:
                    try{ newblit = new BlockIterator(world, end, newdir, 0, radius/2);}
                    catch (Exception e) {return;}
                    break;
            }
            if (newblit==null) return;
            {int CAP = 10;
            while(newblit.hasNext()){
                Block target = newblit.next();
                if(target.getType()==Material.STONE) target.setType(ore);
                CAP--;
            }}
        }
    }
    
    void orePowder(Vector start, Vector dir, World world, int radius, Random random, Material ore){
        int centerY = start.getBlockY();
        if (plugin.chunkManager.getBlockSafely(start.getBlockX(), centerY, start.getBlockZ(), world).getType()==Material.AIR){
            int shiftLife = 8;
            centerY += 4;
            while (plugin.chunkManager.getBlockSafely(start.getBlockX(), centerY, start.getBlockZ(), world).getType()==Material.AIR&&shiftLife>0){
                centerY -= 1;
            }
        }
        start.setY(centerY);
        BlockIterator blit;
        try{ blit = new BlockIterator(world, start, dir, 0, radius);}
        catch (Exception e) {return;}
        if (blit==null) return;
        {int CAP = 10;
        while(blit.hasNext()&&CAP>0){
            Block target = blit.next();
            if(target.getType()==Material.STONE) target.setType(ore);
            CAP--;
        }}
        Vector end = start.add(dir);
        Vector newdir = dir.multiply(2);
        for(int subVeins = random.nextInt(radius/2) + (radius/2); subVeins > 0; subVeins--){
            BlockIterator newblit;
            try{ newblit = new BlockIterator(world, end, newdir, 0, radius/2);}
            catch (Exception e) {return;}
            if (newblit==null) return;
            dir = dir.multiply(dir);
            //Vector newend = null;
            {int CAP = 10;
            while(newblit.hasNext()&&CAP>0){
                Block target = newblit.next();
                end = new Vector(target.getX(), target.getY(), target.getZ());
                if(target.getType()==Material.STONE) target.setType(ore);
                CAP--;
            }}
            //end = newend;
        }
    }
    
    void oreShard(int xx, int yy, int zz, int field, Random random, World world, Material ore){
        oreBlob(xx, yy, zz, world, 1, random, ore);
        Location center = new Location(world, xx, yy, zz);
        for (int x = (field * -1); x < field; x++) for (int z = (field * -1); z < field; z++) for (int y = (field * -1); y < field; y++){
            if(random.nextInt(128)==1){
                Block block = plugin.chunkManager.getBlockSafely(xx + x, yy + y, zz + z, world);
                if(block != null){
                    double distance = center.distance(block.getLocation());
                    if (distance < field && block.getType() == Material.STONE) block.setType(ore);
                }
            }
        }
    }
}
