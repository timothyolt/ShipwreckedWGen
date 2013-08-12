package org.bytefire.plugins.shipwreckedwgen.structures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bytefire.libnbt.NBTTagException;
import org.bytefire.libnbt.Tag;
import org.bytefire.libnbt.TagByte;
import org.bytefire.libnbt.TagByteArray;
import org.bytefire.libnbt.TagCompound;
import org.bytefire.libnbt.TagInt;
import org.bytefire.libnbt.TagString;
import org.bytefire.libnbt.io.NBTInputStream;
import org.bytefire.libnbt.io.NBTOutputStream;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure.StructureType;

import static org.bytefire.plugins.shipwreckedwgen.structures.StructureUtil.*;

public class StructureLoader {

    public static Structure loadStructure(String fileName) {
        try {
            File dataFolder = new File(Bukkit.getPluginManager().getPlugin("ShipGen").getDataFolder().getAbsolutePath() + File.separator + "structures");
            dataFolder.mkdirs();
            String path = dataFolder.getAbsolutePath();
            NBTInputStream structureInput = new NBTInputStream(new FileInputStream(path + File.separator + fileName), false);

            Map<String, Tag> structTag = ((TagCompound)structureInput.readNextTag()).getPayload();

            TagString   tag_name    = (TagString)   structTag.get("name");
            TagInt      tag_xOrigin = (TagInt)      structTag.get("xOrigin");
            TagInt      tag_yOrigin = (TagInt)      structTag.get("yOrigin");
            TagInt      tag_zOrigin = (TagInt)      structTag.get("zOrigin");
            TagString   tag_type    = (TagString)   structTag.get("type");

            TagString   tag_biome   = (TagString)   structTag.get("biome");
            TagInt      tag_yMax    = (TagInt)      structTag.get("yMax");
            TagInt      tag_yMin    = (TagInt)      structTag.get("yMin");
            TagByte     tag_growFB  = (TagByte)     structTag.get("growFromBounds");

            TagCompound tag_chunks  = (TagCompound) structTag.get("chunks");

            if (tag_name == null || tag_xOrigin == null || tag_yOrigin == null || tag_zOrigin == null || tag_type == null){
                System.err.println("Structure header null");
                return getEmptyStructure(fileName);
            }

            Structure struct = new Structure(
                tag_name.getPayload(),
                new Location(null,
                    tag_xOrigin.getPayload(),
                    tag_yOrigin.getPayload(),
                    tag_zOrigin.getPayload()),
                StructureType.valueOf(tag_type.getPayload().toUpperCase()));

            if (tag_biome != null)
                struct.setRequiredBiome(Biome.valueOf(tag_biome.getPayload().toUpperCase()));
            if (tag_yMax != null)
                struct.setMaxHeight(tag_yMax.getPayload());
            if (tag_yMin != null)
                struct.setMinHeight(tag_yMin.getPayload());
            if (tag_growFB != null)
                struct.setGrowFromBounds(tag_growFB.getPayload() != 0);

            Map<String, Tag> chunks = new HashMap<String, Tag>();
            if (tag_chunks != null)
                chunks = tag_chunks.getPayload();
            for (Tag chunkCompound : chunks.values()) {
                Map<String, Tag> chunkTag = ((TagCompound)chunkCompound).getPayload();

                TagInt tag_xPos = (TagInt) chunkTag.get("xPos");
                TagInt tag_zPos = (TagInt) chunkTag.get("zPos");

                if (tag_xPos == null || tag_zPos == null)
                    System.err.println("Chunk header null for chunk " + chunkCompound.getName());
                else {
                    StructureChunk chunk = new StructureChunk(struct, tag_xPos.getPayload(), tag_zPos.getPayload());

                    TagCompound tag_sections = (TagCompound) chunkTag.get("sections");

                    Map<String, Tag> sections = new HashMap<String, Tag>();
                    if (tag_sections != null)
                        sections = tag_sections.getPayload();
                    for (Tag sectCompound : sections.values()) {
                        Map<String, Tag> sectTag = ((TagCompound)sectCompound).getPayload();

                        TagInt tag_yIndex = (TagInt) sectTag.get("yIndex");

                        if (tag_yIndex == null)
                            System.err.println("Section header null for section " + sectCompound.getName());
                        else {
                            StructureSection sect = new StructureSection(chunk, tag_yIndex.getPayload());

                            TagByteArray tag_blocks  = (TagByteArray) sectTag.get("blocks");
                            TagByteArray tag_add     = (TagByteArray) sectTag.get("add");
                            TagByteArray tag_data    = (TagByteArray) sectTag.get("data");
                            TagByteArray tag_passive = (TagByteArray) sectTag.get("passive");

                            if (tag_blocks != null)
                                sect.addBlockArray(tag_blocks.getPayload());
                            if (tag_add != null)
                                sect.addAddArray(tag_add.getPayload());
                            if (tag_data != null)
                                sect.addDataArray(tag_data.getPayload());
                            if (tag_passive != null)
                                sect.addPassiveArray(tag_passive.getPayload());

                            chunk.addSection(sect);
                        }
                    }
                    struct.addChunk(chunk);
                }
            }
            return struct;
        } catch (IOException ex) {
            ex.printStackTrace();
            return getEmptyStructure(fileName);
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return getEmptyStructure(fileName);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            return getEmptyStructure(fileName);
        } catch (NBTTagException ex) {
            ex.printStackTrace();
            return getEmptyStructure(fileName);
        }
    }

    public static boolean saveStructure(Structure structure){
        try{
            File dataFolder = new File(Bukkit.getPluginManager().getPlugin("ShipGen").getDataFolder().getAbsolutePath() + File.separator + "structures");
            dataFolder.mkdirs();
            String path = dataFolder.getAbsolutePath();
            File target = new File(path + File.separator + structure.getName());
            target.delete();
            NBTOutputStream structOut = new NBTOutputStream(new FileOutputStream(path + File.separator + structure.getName()), false);

            HashMap<String, Tag> struct = new HashMap<String, Tag>();
            struct.put("name", new TagString("name", structure.getName()));
            struct.put("xOrigin", new TagInt("xOrigin", structure.getOrigin().getBlockX()));
            struct.put("yOrigin", new TagInt("yOrigin", structure.getOrigin().getBlockY()));
            struct.put("zOrigin", new TagInt("zOrigin", structure.getOrigin().getBlockZ()));
            struct.put("type", new TagString("type", structure.getType().toString()));
            Biome biome = structure.getRequiredBiome();
            if (biome != null) struct.put("biome", new TagString("biome", biome.toString()));
            struct.put("yMax", new TagInt("yMax", structure.getMaxHeight()));
            struct.put("yMin", new TagInt("yMin", structure.getMinHeight()));
            byte growFromBounds = 1;
            if (structure.canGrowFromBounds() == false) growFromBounds = 0;
            HashMap<String, Tag> chunkTags = new HashMap<String, Tag>();
            struct.put("growFromBounds", new TagByte("growFromBounds", growFromBounds));
            Collection<StructureChunk> chunks = structure.getAllChunks().values();
            for (StructureChunk structChunk : chunks){
                HashMap<String, Tag> chunk = new HashMap<String, Tag>();
                chunk.put("xPos", new TagInt("xPos", structChunk.getXPos()));
                chunk.put("zPos", new TagInt("zPos", structChunk.getZPos()));
                HashMap<String, Tag> sectionTags = new HashMap<String, Tag>();
                Collection<StructureSection> sections = structChunk.getAllSections().values();
                for (StructureSection chunkSect : sections){
                    HashMap<String, Tag> sect = new HashMap<String, Tag>();
                    sect.put("yIndex", new TagInt("yIndex", chunkSect.getYIndex()));
                    sect.put("blocks", new TagByteArray("blocks", chunkSect.getBlocks()));
                    sect.put("data", new TagByteArray("data", chunkSect.getData()));
                    sect.put("passive", new TagByteArray("passive", chunkSect.getPassive()));

                    String sectionName = Integer.toString(chunkSect.getYIndex());
                    sectionTags.put(sectionName, new TagCompound(sectionName, sect));
                }
                chunk.put("sections", new TagCompound("sections", sectionTags));

                String chunkName = Long.toString(mergeCoords(structChunk.getXPos(), structChunk.getZPos()));
                chunkTags.put(chunkName, new TagCompound(chunkName, chunk));
            }
            struct.put("chunks", new TagCompound("chunks", chunkTags));

            TagCompound root = new TagCompound(structure.getName(), struct);
            structOut.writeTag(root);
            structOut.close();
            return true;
        } catch (IOException ex) {ex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ex) {ex.printStackTrace();
        } catch (ClassCastException ex) {ex.printStackTrace();
        } catch (NBTTagException ex) {ex.printStackTrace();}
        return false;
    }

//    public static Structure translateSafe(Structure struct){
//        Map<Long, StructureChunk> chunks = struct.getAllChunks();
//        if (chunks.size() < 1) return struct;
//        Collection<StructureChunk> values = chunks.values();
//        StructureChunk firstChunk = (StructureChunk) (values.toArray())[0];
//        int minX = firstChunk.getXPos();
//        int minZ = firstChunk.getZPos();
//        for (StructureChunk test : values){
//            minX = Math.min(minX, test.getXPos());
//            minZ = Math.min(minZ, test.getZPos());
//        }
//        struct.clearChunks();
//        for (StructureChunk test : values){
//            test.setXPos(test.getXPos() - minX);
//            test.setZPos(test.getZPos() - minZ);
//            struct.addChunk(test);
//        }
//    }

    public static Structure getEmptyStructure(String name){
        return new Structure(name, new Location(null, 0, 128, 0), StructureType.SURFACE);
    }
}
