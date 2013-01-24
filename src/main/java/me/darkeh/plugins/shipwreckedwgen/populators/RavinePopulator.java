package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class RavinePopulator extends BlockPopulator{
    private ShipwreckedWGen plugin;
    public RavinePopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        int chance = random.nextInt(2000);
        if (chance == 1){
            int xx = chunk.getX() << 16;
            int zz = chunk.getZ() << 16;
            int yy = random.nextInt(80) + 20;
            int widthA = 1;
            int widthB = 1;
            int height = 14 + random.nextInt(5);
            int steadyA = random.nextInt(5) + 10;
            int steadyB = random.nextInt(5) + 10;
            int steadyH = random.nextInt(5);
            SimplexOctaveGenerator wave = new SimplexOctaveGenerator(random, 2);
            wave.setScale(1 / 48.0);
            int opA = 2;
            int opB = 2;
            int opH = random.nextInt(3);
            boolean direction = random.nextBoolean();
            int ledgeOffsetA = random.nextInt(10)-5;
            int ledgeChangeA = random.nextInt(16)+5;
            int ledgeOffsetB = random.nextInt(10)-5;
            int ledgeChangeB = random.nextInt(16)+5;
            for (int length = 0; widthA > 0 && widthB > 0; length++){
                //Terminate if the height is too small
                if (height <= 1) {
                    widthA = -1;
                    widthB = -1;
                }

                if (length > 400){
                    widthA = -1;
                    widthB = -1;
                }

                //Control changing of operations
                if (steadyA <= 0 && opA != -1){
                    if (widthA < 15) opA = random.nextInt(3);
                    else opA = random.nextInt(2);
                    if (length < 100 && widthA < 2) opA = 2;
                    if (length < 10) opA = 2;
                    if (length > 400) opA = 1;
                    steadyA = random.nextInt(5);
                }
                else steadyA--;
                if (steadyB <= 0 && opB != -1){
                    if (widthB < 15) opB = random.nextInt(3);
                    else opB = random.nextInt(2);
                    if (length < 100 && widthB < 2) opB = 2;
                    if (length < 10) opB = 2;
                    if (length > 400) opB = 1;
                    steadyB = random.nextInt(5);
                }
                else steadyB--;
                if (steadyH <= 0){
                    if (height < 35) opH = random.nextInt(3);
                    else opH = random.nextInt(1);
                    steadyH = random.nextInt(5);
                }
                else steadyH--;

                //Control ledge height change
                if (ledgeChangeA <= 0){
                    ledgeOffsetA = random.nextInt(5);
                    ledgeChangeA = random.nextInt(16)+5;
                }
                else ledgeChangeA--;
                if (ledgeChangeB <= 0){
                    ledgeOffsetB = random.nextInt(5);
                    ledgeChangeB = random.nextInt(16)+5;
                }
                else ledgeChangeA--;

                //Clears Appropriate land
                clearSegment(direction, xx, yy, zz, (int)(wave.noise(length, 0.5, 0.5, true)*12), widthA + (int)(wave.noise(-length, 0.5, 0.5, true)*6), widthB + 4, ledgeOffsetA, ledgeOffsetB, height, length, world);
                //Applies operations
                switch(opA){
                    case 1: widthA -= 1;
                            opA = 3;
                            break;
                    case 2: widthA += 1;
                            opA = 3;
                            break;
                    default: break;
                }
                switch(opB){
                    case 1: widthB -= 1;
                            opB = 3;
                            break;
                    case 2: widthB += 1;
                            opB = 3;
                            break;
                    default: break;
                }
                switch(opH){
                    case 1: height -= 1;
                            opH = 3;
                            break;
                    case 2: height += 1;
                            opH = 3;
                            break;
                    default: break;
                }

                //Terminates operations on width variables if zeroed
                if (widthA <= 0) opA = -1;
                if (widthB <= 0) opB = -1;
            }
        }
    }

    int getLedge(int y, int offset){
        if (offset == 4){
            if ((y + offset) % 10 == 0) return (((y + offset) / 10) + 1);
            else if ((y + offset + 1) % 10 == 0) return (((y + offset + 1) / 10) + 1);
            else return (int)Math.floor((double)(y + offset) / 10.0);
        }
        if (offset < 2){
            if ((y + offset) % 10 == 0) return (((y + offset) / 10) - 1);
            else if ((y + offset + 1) % 10 == 0) return (((y + offset + 1) / 10) - 1);
            else return (int)Math.floor((double)(y + offset) / 10.0);
        }
        else{
            if ((y + offset) % 10 == 0) return (((y + offset) / 10));
            else if ((y + offset + 1) % 10 == 0) return (((y + offset + 1) / 10));
            else return (int)Math.floor((double)(y + offset) / 10.0);
        }
    }

    void clearSegment(boolean dir, int xx, int yy, int zz, int offset, int widthA, int widthB, int ledgeOffsetA, int ledgeOffsetB, int height, int section, World world){
        if (dir){
            for (int y = -1 * height; y < height; y++) for (int x = (-1 * widthA) - getLedge(y, ledgeOffsetA); x < widthB + getLedge(y, ledgeOffsetB); x++){
                Block block = plugin.getChunkHandler().getBlockSafely(world, xx + x + offset, yy + y, zz + section);
                Material replace;
                if (yy + y < 12) replace = Material.LAVA;
                else replace = Material.AIR;
                if (!block.isLiquid() && block.getType()!=Material.BEDROCK) block.setType(replace);
            }
            //System.out.println("Direction: 1, WidthA:" + Integer.toString(widthA) + ", WidthB:" + Integer.toString(widthB) + ", Height:" + Integer.toString(height));
        }
        else{
            for (int y = -1 * height; y < height; y++) for (int z = (-1 * widthA) - getLedge(y, ledgeOffsetA); z < widthB + getLedge(y, ledgeOffsetB); z++){
                Block block = plugin.getChunkHandler().getBlockSafely(world, xx + section, yy + y, zz + z + offset);
                Material replace;
                if (yy + y < 10) replace = Material.LAVA;
                else replace = Material.AIR;
                if (!block.isLiquid() && block.getType()!=Material.BEDROCK) block.setType(replace);
            }
            //System.out.println("Direction: 2, WidthA:" + Integer.toString(widthA) + ", WidthB:" + Integer.toString(widthB) + ", Height:" + Integer.toString(height));
        }
    }
}
