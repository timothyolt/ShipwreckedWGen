package me.darkeh.plugins.shipwreckedwgen.populators;

import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

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
            if (y <= 128 && y > 64) dep = Material.COAL_ORE;
            else if (y <= 64 && y > 48) dep = Material.CLAY;
            else if (y <= 48 && y > 32) dep = Material.DIRT;
            else if (y <= 32 && y > 8) dep = Material.GRAVEL;
            else if (y <= 8) dep = Material.LAVA;
            else dep = Material.DIRT;

            if (dCluster.noise(realX, y, realZ, 0.5, 0.5) > 0.6 && deposit.noise(realX, y, realZ, 0.5, 0.5) > 0.8){
                Block target = plugin.getChunkHandler().getBlockSafely(world, realX, y, realZ);
                if (target.getType() == Material.STONE) target.setType(dep);
            }
        }
    }
}
