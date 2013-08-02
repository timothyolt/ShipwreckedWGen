package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class StructureBlockDataPopulator extends BlockPopulator{

    Structure struct;

    public StructureBlockDataPopulator(Structure struct){
        this.struct = struct;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        StructureChunk chunk = struct.getChunk(source.getX(), source.getZ());
        HashMap<Integer, StructureSection> sections = chunk.getAllSections();
        for (StructureSection sect : sections.values()){
            byte[][][] data = sect.getData();
            int yOffset = sect.getYIndex() * 16;
            for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++){
                byte localData = data[x][y][z];
                if (localData != 0) source.getBlock(x, y + yOffset, z).setData(localData);
            }
        }
    }

}
