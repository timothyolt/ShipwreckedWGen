package org.bytefire.plugins.shipwreckedwgen.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bytefire.libnbt.Tag;
import org.bytefire.libnbt.TagCompound;
import org.bytefire.libnbt.TagString;

public class StructureBlockDataPopulator extends BlockPopulator{

    Structure struct;

    public StructureBlockDataPopulator(Structure struct){
        this.struct = struct;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        StructureChunk chunk = struct.getChunk(source.getX(), source.getZ(), false);
        if (chunk == null) return;
        HashMap<Integer, StructureSection> sections = chunk.getAllSections();
        for (StructureSection sect : sections.values()){
            byte[] data = sect.getData();
            int yOffset = sect.getYIndex() << 4;
            for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++){
                int index = (y << 8) | (z << 4) | x;
                byte localData = data[index];
                if (localData != 0) source.getBlock(x, y + yOffset, z).setData(localData);
            }
        }
        ArrayList<Tag> tileEntities = new ArrayList<Tag>();
        for (Tag tile : tileEntities){
            TagCompound tag = (TagCompound) tile;
            String type = ((TagString)tag.getPayload().get("type")).getPayload();
            if  (type.equals("chest")) StructureUtil.getChestFromTag(tag, source);
        }
    }

}
