package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.Random;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;


public class BiomePopulator extends BlockPopulator{
    private ShipwreckedWGen plugin;
    public BiomePopulator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    @Override
    public void populate(World w, Random r, Chunk c) {
        try{plugin.getBiomeHandler().getBiomeGen(w.getBiome(c.getX() * 16, c.getZ() * 16)).biomePopulate(w, r, c);}
        catch (Exception e) {};
    }
}
