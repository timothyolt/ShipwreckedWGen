package org.bytefire.plugins.shipwreckedwgen.structures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.bytefire.plugins.shipwreckedwgen.ChunkHandler;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure.StructureType;

public class StructureLoader {

    public static Structure loadStructure(String fileName) {
        try {
            File dataFolder = new File(Bukkit.getPluginManager().getPlugin("ShipGen").getDataFolder().getAbsolutePath() + File.separator + "structures");
            dataFolder.mkdirs();
            String path = dataFolder.getAbsolutePath();
            NBTInputStream structureInput = new NBTInputStream(new FileInputStream(path + File.separator + "test.structure"));

            Map<String, Tag> structTag = ((TagCompound)structureInput.readNextTag()).getPayload();

            String name = (String)structTag.get("name").getPayload();
            Location origin = new Location(null,
                (Integer)structTag.get("xOrigin").getPayload(),
                (Integer)structTag.get("yOrigin").getPayload(),
                (Integer)structTag.get("zOrigin").getPayload());
            StructureType type = StructureType.valueOf(((TagString)structTag.get("type")).getPayload().toUpperCase());

            Structure struct = new Structure(name, origin, type);

            Tag biomeTest = structTag.get("biome");
            Biome biome = null;
            if (biomeTest != null) biome = Biome.valueOf(((TagString)biomeTest).getPayload().toUpperCase());
            struct.setRequiredBiome(biome);

            struct.setMaxHeight((Integer)structTag.get("yMax").getPayload());
            struct.setMinHeight((Integer)structTag.get("yMin").getPayload());
            struct.setGrowFromBounds((Byte)structTag.get("growFromBounds").getPayload() != 0);

            Map<String, Tag> chunks = ((TagCompound)structTag.get("chunks")).getPayload();
            for (Tag chunkCompound : chunks.values()) {
                Map<String, Tag> chunkTag = ((TagCompound)chunkCompound).getPayload();

                int xPos = (Integer)chunkTag.get("xPos").getPayload();
                int zPos = (Integer)chunkTag.get("zPos").getPayload();

                StructureChunk chunk = new StructureChunk(struct, xPos, zPos);

                Map<String, Tag> sections = ((TagCompound)chunkTag.get("sections")).getPayload();
                for (Tag sectCompound : sections.values()) {
                    Map<String, Tag> sectTag = ((TagCompound)sectCompound).getPayload();

                    int yIndex = (Integer)chunkTag.get("yIndex").getPayload();

                    StructureSection sect = new StructureSection(chunk, yIndex);

                    sect.addBlockArray(format8BitArray(((TagByteArray)sectTag.get("blocks")).getPayload()));

                    Tag addTest = sectTag.get("add");
                    byte[][][] add = null;
                    if (addTest != null) add = format4BitArray(((TagByteArray)addTest).getPayload());
                    sect.addAddArray(add);

                    sect.addDataArray(format8BitArray(((TagByteArray)sectTag.get("data")).getPayload()));
                    sect.addPassiveArray(format1BitArray(((TagByteArray)sectTag.get("passive")).getPayload()));

                    chunk.addSection(sect);
                }
                struct.addChunk(chunk);
            }
            return struct;
        } catch (IOException ex) {return getEmptyStructure(fileName);
        //} catch (ArrayOutOfBoundsException ex) {return null;
        //} catch (ClassCastException ex) {return null;
        } catch (NBTTagException ex) {return getEmptyStructure(fileName);}
    }

    public static boolean saveStructure(Structure structure){
        try{
            File dataFolder = new File(Bukkit.getPluginManager().getPlugin("ShipGen").getDataFolder().getAbsolutePath() + File.separator + "structures");
            dataFolder.mkdirs();
            String path = dataFolder.getAbsolutePath();
            NBTOutputStream structOut = new NBTOutputStream(new FileOutputStream(path + File.separator + "test.structure"));

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
                    sect.put("blocks", new TagByteArray("blocks", to8BitArray(chunkSect.getBlocks())));
                    sect.put("data", new TagByteArray("data", to8BitArray(chunkSect.getData())));
                    sect.put("passive", new TagByteArray("passive", to1BitArray(chunkSect.getPassive())));

                    String sectionName = Integer.toString(chunkSect.getYIndex());
                    sectionTags.put(sectionName, new TagCompound(sectionName, sect));
                }
                chunk.put("sections", new TagCompound("sections", sectionTags));

                String chunkName = Long.toString(ChunkHandler.intToLong(structChunk.getXPos(), structChunk.getZPos()));
                chunkTags.put(chunkName, new TagCompound(chunkName, chunk));
            }
            struct.put("chunks", new TagCompound("chunks", chunkTags));

            structOut.writeTag(new TagCompound(structure.getName(), struct));
            return true;
        } catch (IOException ex) {ex.printStackTrace();
        //} catch (ArrayOutOfBoundsException ex) {return null;
        //} catch (ClassCastException ex) {return null;
        } catch (NBTTagException ex) {ex.printStackTrace();}
        return false;
    }

    public static Structure getEmptyStructure(String name){
        return new Structure(name, new Location(null, 0, 128, 0), StructureType.SURFACE);
    }

    public static byte[][][] format8BitArray(byte[] bytes){
        byte[][][] out = new byte[16][16][16];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 16; y++){
                    out[x][y][z] = bytes[(x * 256) + (z * 16) + y];
                }
            }
        }
        return out;
    }

    public static byte[] to8BitArray(byte[][][] bytes){
        byte[] out = new byte[4096];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 16; y++){
                    out[(x * 256) + (z * 16) + y] = bytes[x][y][z];
                }
            }
        }
        return out;
    }

    public static byte[][][] format4BitArray(byte[] bytes){
        byte[][][] out = new byte[16][16][16];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 8; y++){
                    byte temp = bytes[(x * 128) + (z * 8) + y];
                    out[x][y*2][z] = (byte) (temp & 0xf);
                    out[x][(y*2)+1][z] = (byte) (temp >> 4);
                }
            }
        }
        return out;
    }

    public static byte[] to4BitArray(byte[][][] bytes){
        byte[] out = new byte[2048];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 8; y++){
                    out[(x * 128) + (z * 8) + y] = (byte)
                        ((bytes[x][y*2][z] << 4) + bytes[x][(y*2) + 1][z]);
                }
            }
        }
        return out;
    }

    public static boolean[][][] format1BitArray(byte[] bytes){
        boolean[][][] out = new boolean[16][16][16];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 2; y++){
                    byte temp = bytes[(x * 32) + (z * 2) + y];
                    out[x][0 + (y * 8)][z] = (temp & 0x01) != 0;
                    out[x][1 + (y * 8)][z] = (temp & 0x02) != 0;
                    out[x][2 + (y * 8)][z] = (temp & 0x04) != 0;
                    out[x][3 + (y * 8)][z] = (temp & 0x08) != 0;
                    out[x][4 + (y * 8)][z] = (temp & 0x10) != 0;
                    out[x][5 + (y * 8)][z] = (temp & 0x20) != 0;
                    out[x][6 + (y * 8)][z] = (temp & 0x40) != 0;
                    out[x][7 + (y * 8)][z] = (temp & 0x80) != 0;
                }
            }
        }
        return out;
    }

    public static byte[] to1BitArray(boolean[][][] booleans){
        byte[] out = new byte[512];
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 2; y++){
                    byte temp = 0;
                    if (booleans[x][0 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][1 + (y * 8)][z] = true) temp = (byte) (temp | 0x02);
                    if (booleans[x][2 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][3 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][4 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][5 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][6 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    if (booleans[x][7 + (y * 8)][z] = true) temp = (byte) (temp | 0x01);
                    out[(x * 32) + (z * 2) + y] = temp;
                }
            }
        }
        return out;
    }
}
