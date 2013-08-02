package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;

public class StructureEditorChunkGenerator extends ChunkGenerator {
    private ShipwreckedWGen plugin;
    private Structure struct;

    public StructureEditorChunkGenerator(ShipwreckedWGen plugin, String name){
        this.plugin = plugin;
        this.struct = StructureLoader.loadStructure(name);
        if (struct == null) System.out.println("PANIC");
        plugin.getStructureHandler().addEditor(name, struct);
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        byte[][] out = new byte[world.getMaxHeight()/16][];
        for (int i = 0; i < out.length; i++){
            out[i] = StructureLoader.to8BitArray(struct.getChunk(x, z).getSection(i).getBlocks());
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
