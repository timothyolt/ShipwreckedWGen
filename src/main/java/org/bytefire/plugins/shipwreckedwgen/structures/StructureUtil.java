package org.bytefire.plugins.shipwreckedwgen.structures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_6_R2.block.CraftChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bytefire.libnbt.NBTNameException;
import org.bytefire.libnbt.NBTTagException;
import org.bytefire.libnbt.Tag;
import org.bytefire.libnbt.TagByte;
import org.bytefire.libnbt.TagByteArray;
import org.bytefire.libnbt.TagCompound;
import org.bytefire.libnbt.TagInt;
import org.bytefire.libnbt.TagList;
import org.bytefire.libnbt.TagLong;
import org.bytefire.libnbt.TagShort;
import org.bytefire.libnbt.TagString;
import org.bytefire.libnbt.io.NBTInputStream;
import org.bytefire.libnbt.io.NBTOutputStream;

public class StructureUtil {

    public static long mergeCoords(int x, int z){
        return ((long) x << 32) | ((long) z & 0xFFFFFFFFL);
    }

    public static int splitCoords(long xz, boolean left){
        if (left) return (int) (xz >> 32);
        else return (int) (xz | 0xFFFFFFFF00000000L);
    }

    public static void clearEditorCache(){
        File worldFolder = Bukkit.getWorldContainer();
        File[] files = worldFolder.listFiles();
        for (File file : files){
            if (file.getName().endsWith(".structure"))
                deleteEditorFolder(file);
        }
    }    @SuppressWarnings("CallToThreadDumpStack")
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

            TagInt      tag_dist    = (TagInt)      structTag.get("dist");
            TagInt      tag_chance  = (TagInt)      structTag.get("chance");
            TagLong     tag_seed    = (TagLong)     structTag.get("seed");
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
                Structure.StructureType.valueOf(tag_type.getPayload().toUpperCase()));

            if (tag_dist != null)
                struct.setDistance(tag_dist.getPayload());
            if (tag_chance != null)
                struct.setChance(tag_chance.getPayload());
            if (tag_seed != null)
                struct.setSeed(tag_seed.getPayload());
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
                    TagList tag_tileEntities = (TagList) chunkTag.get("tileEntities");

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
                    
                    List<Tag> tileEntities = new ArrayList<Tag>();
                    if (tag_tileEntities != null)
                        tileEntities = tag_tileEntities.getPayload();
                    for (Tag tileCompound : tileEntities){
                        Map<String, Tag> tileTag = ((TagCompound) tileCompound).getPayload();
                        
                        TagString tag_tile = (TagString) tileTag.get("type");
                        
                        if (tag_tile.equals("chest")){
                            
                        }
                        //TODO: add deserializaton of tile entities
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

    @SuppressWarnings("CallToThreadDumpStack")
    public static boolean saveStructure(Structure structure){
        try{
            File dataFolder = new File(Bukkit.getPluginManager().getPlugin("ShipGen").getDataFolder().getAbsolutePath() + File.separator + "structures");
            dataFolder.mkdirs();
            String path = dataFolder.getAbsolutePath();
            File target = new File(path + File.separator + structure.getName());
            target.delete();
            NBTOutputStream structOut = new NBTOutputStream(new FileOutputStream(path + File.separator + structure.getName()), false);

            HashMap<String, Tag> struct = new HashMap<String, Tag>();
            struct.put("name"   , new TagString("name"      , structure.getName()));
            struct.put("xOrigin", new TagInt(   "xOrigin"   , structure.getOrigin().getBlockX()));
            struct.put("yOrigin", new TagInt(   "yOrigin"   , structure.getOrigin().getBlockY()));
            struct.put("zOrigin", new TagInt(   "zOrigin"   , structure.getOrigin().getBlockZ()));
            struct.put("type"   , new TagString("type"      , structure.getType().toString()));
            struct.put("dist"   , new TagInt(   "dist"      , structure.getDistance()));
            struct.put("chance" , new TagInt(   "chance"    , structure.getChance()));
            struct.put("seed"   , new TagLong(  "seed"      , structure.getSeed()));
            Biome biome = structure.getRequiredBiome();
            if (biome != null)
            struct.put("biome"  , new TagString("biome"     , biome.toString()));
            struct.put("yMax"   , new TagInt(   "yMax"      , structure.getMaxHeight()));
            struct.put("yMin"   , new TagInt(   "yMin"      , structure.getMinHeight()));
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
                
                chunk.put("tileEntities", new TagList("tileEntities", structChunk.getTileEntities()));

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
        } catch (NBTTagException ex) {ex.printStackTrace();
        } catch (NBTNameException ex) {ex.printStackTrace();}
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
        return new Structure(name, new Location(null, 0, 128, 0), Structure.StructureType.SURFACE);
    }
    
    public static Tag getChestTag(BlockState tile) throws NBTTagException, NBTNameException{
        Inventory inv = ((Chest) tile).getBlockInventory();
        HashMap<String, Tag> chest = new HashMap<String, Tag>();
        
        chest.put("type", new TagString("type", "chest"));
        HashMap<String, Tag> location = new HashMap<String, Tag>();
            location.put("x", new TagInt("x", tile.getX()));
            location.put("y", new TagInt("y", tile.getY())); //TODO: check and make sure blockstate coords are within chunk
            location.put("z", new TagInt("z", tile.getZ()));
        chest.put("location", new TagCompound("location", location));
        
        ArrayList<Tag> items = new ArrayList<Tag>();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++){
            ItemStack item = contents[i];
            
            HashMap<String, Tag> itemData = new HashMap<String, Tag>();
            itemData.put("slot", new TagInt("slot", i));
            itemData.put("id", new TagInt("id", item.getTypeId()));
            itemData.put("ammount", new TagInt("ammount", item.getAmount()));
            itemData.put("durabilty", new TagShort("durability", item.getDurability()));
            
            items.add(new TagCompound(null, itemData));
        }
        chest.put("inventory", new TagList("inventory", items));
        
        return new TagCompound(null, chest);
    }
    
    public static Chest getChestFromTag(TagCompound comp, Chunk source){
        Map<String, Tag> chest = comp.getPayload();
        
        TagCompound tag_location  = (TagCompound) chest.get("location");
        TagList     tag_inventory = (TagList)     chest.get("inventory");
        
        List<Tag> itemList = new ArrayList<Tag>();
        if (tag_inventory != null)
            itemList = tag_inventory.getPayload();
        Map<String, Tag> loc = new HashMap<String, Tag>();
        if (tag_location != null)
            loc = tag_location.getPayload();
        ItemStack[] items = new ItemStack[itemList.size()];
        for (Tag itemCompound : itemList){
            Map<String, Tag> itemTag = (Map<String, Tag>) itemCompound.getPayload();
            
            TagInt   tag_id         = (TagInt)   itemTag.get("id");
            TagInt   tag_slot       = (TagInt)   itemTag.get("slot");
            TagInt   tag_ammount    = (TagInt)   itemTag.get("ammount");
            TagShort tag_durability = (TagShort) itemTag.get("durability");
            
            if (tag_id == null || tag_slot == null){
                System.err.println("Item header null");
                return null;
            }
            ItemStack item = new ItemStack(tag_id.getPayload());
            
            if (tag_ammount != null)
                item.setAmount(tag_ammount.getPayload()); //TODO: rename all amount variables
            if (tag_durability != null)
                item.setDurability(tag_durability.getPayload());
            
            items[tag_slot.getPayload()] = item;
        }
        
        Chest chestBlock = new CraftChest(source.getBlock(
            ((TagInt)loc.get("x")).getPayload(), ((TagInt)loc.get("y")).getPayload(), ((TagInt)loc.get("z")).getPayload())
        );
        chestBlock.getBlockInventory().setContents(items);
        return null;
    }

    public static void deleteEditorFolder(File folder){
        File[] contents = folder.listFiles();
        for (File file : contents){
            if (file.isDirectory())
                deleteEditorFolder(file);
            else
                file.delete();
        }
        folder.delete();
    }
    
    public static boolean isSupportedTileEntity(int id){
        switch (Material.getMaterial(id)){
            case BEACON:        return false;
            case BREWING_STAND: return false;
            case CHEST:         return true;
            case COMMAND:       return false;
            case MOB_SPAWNER:   return false;
            case DISPENSER:     return false;
            case DROPPER:       return false;
            case FURNACE:       return false;
            case HOPPER:        return false;
            case JUKEBOX:       return false;
            case NOTE_BLOCK:    return false;
            case SIGN:          return false;
            default:            return false;
        }
    }
    
    public static ArrayList<Tag> getTileEntites(ChunkSnapshot chunk){
        ArrayList<Tag> tiles = new ArrayList<Tag>();
        World world = Bukkit.getWorld(chunk.getWorldName());
        if (world == null) return tiles;
        for (int y = 0; y < world.getMaxHeight() >> 4; y++){
            if (!chunk.isSectionEmpty(y)){
                for (int xx = 0; xx < 16; xx ++) for (int yy = 0; yy < 16; yy ++) for (int zz = 0; zz < 16; zz ++){
                    try {
                        int id = chunk.getBlockTypeId(xx, yy + (y << 4), zz);
                        if (isSupportedTileEntity(id))
                            tiles.add(getChestTag(world.getBlockAt(xx, yy + (y << 4), zz).getState()));
                    } catch (NBTTagException ex) {
                        Logger.getLogger(StructureUtil.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NBTNameException ex) {
                        Logger.getLogger(StructureUtil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return tiles;
    }
}
