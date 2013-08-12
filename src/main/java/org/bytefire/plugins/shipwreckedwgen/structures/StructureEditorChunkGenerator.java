package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class StructureEditorChunkGenerator extends ChunkGenerator {
    private ShipwreckedWGen plugin;
    private Structure struct;

    public StructureEditorChunkGenerator(ShipwreckedWGen plugin, String name){
        this.plugin = plugin;
        this.struct = StructureLoader.loadStructure(name);
        plugin.getStructureHandler().addEditor(name, struct);
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        byte[][] out = new byte[world.getMaxHeight() >> 4][];
        StructureChunk chunk;
        //if (z < 0) chunk = struct.getChunk(x - 1, z, false);
        //else chunk = struct.getChunk(x, z, false);
        chunk = struct.getChunk(x, z, false);
        
        if (chunk == null) return out;
        Biome biome = struct.getRequiredBiome();
        if (biome == null) biome = Biome.OCEAN;
        for (int xx = 0; xx < 16; xx++) for (int zz = 0; zz < 16; zz++){
            biomes.setBiome(xx, zz, biome);
        }
        for (int i = 0; i < out.length; i++){
            StructureSection sect = chunk.getSection(i, false);
            if (sect != null)
                out[i] = sect.getBlocks();
        }
        return out;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();
        pops.add(new StructureBlockDataPopulator(struct));
        return pops;
    }

}
