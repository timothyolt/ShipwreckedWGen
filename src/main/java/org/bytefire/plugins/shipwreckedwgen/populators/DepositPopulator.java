package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import static org.bukkit.Material.*;

public class DepositPopulator extends BlockPopulator{
    private ShipwreckedWGen plugin;
    public DepositPopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        SimplexOctaveGenerator deposit = new SimplexOctaveGenerator(world.getSeed(), 2);
        deposit.setScale(1/ 8D);
        SimplexOctaveGenerator dCluster = new SimplexOctaveGenerator(world.getSeed(), 2);
        dCluster.setScale(1/ 32D);
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 0; y < 128; y++){
            int realX = x + (chunk.getX() * 16);
            int realZ = z + (chunk.getZ() * 16);

            Material dep;
            if (y <= 128 && y > 64) dep = COAL_ORE;
            else if (y <= 64 && y > 48) dep = CLAY;
            else if (y <= 48 && y > 32) dep = DIRT;
            else if (y <= 32 && y > 8) dep = GRAVEL;
            else if (y <= 8) dep = LAVA;
            else dep = DIRT;

            if (dCluster.noise(realX, y, realZ, 0.5, 0.5) > 0.6 && deposit.noise(realX, y, realZ, 0.5, 0.5) > 0.8){
                Block target = plugin.getChunkHandler().getBlockSafely(world, realX, y, realZ);
                if (target != null && target.getType() == STONE) target.setType(dep);
            }
        }
    }
}
