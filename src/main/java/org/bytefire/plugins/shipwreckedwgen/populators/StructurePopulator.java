package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureUtil;

public class StructurePopulator extends BlockPopulator{

    private ShipwreckedWGen plugin;
    private ArrayList<Structure> usedStructs;

    public StructurePopulator(ShipwreckedWGen plugin, String worldName){
        this.plugin = plugin;
        usedStructs = new ArrayList<Structure>();

        FileConfiguration config = plugin.getConfig();
        Map<String, Object> configStructs = config.getConfigurationSection("worlds." + worldName.toLowerCase() + ".structures").getValues(false);
        for (Entry<String, Object> entry : configStructs.entrySet())
            if (((Boolean) entry.getValue()) == true) usedStructs.add(StructureUtil.loadStructure(entry.getKey()));
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {

    }

}
