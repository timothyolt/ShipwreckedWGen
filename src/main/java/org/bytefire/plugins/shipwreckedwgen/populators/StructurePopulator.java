package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bytefire.libnbt.Tag;
import org.bytefire.libnbt.TagCompound;
import org.bytefire.libnbt.TagInt;
import org.bytefire.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure;
import org.bytefire.plugins.shipwreckedwgen.structures.Structure.StructureType;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureChunk;
import org.bytefire.plugins.shipwreckedwgen.structures.StructureSection;

import static org.bytefire.plugins.shipwreckedwgen.structures.StructureUtil.*;

public class StructurePopulator extends BlockPopulator{

    private ArrayList<Structure> usedStructs;
    private HashMap<Long, Integer> yMap;
    private HashMap<Long, Biome> biomeMap;

    public StructurePopulator(ShipwreckedWGen plugin, String worldName){
        usedStructs = new ArrayList<Structure>();
        yMap = new HashMap<Long, Integer>();
        biomeMap = new HashMap<Long, Biome>();

        //patch the config file if it is missing configuration for a structure
        FileConfiguration config = plugin.getConfig();
        Map<String, Object> configStructs = config.getConfigurationSection("worlds." + worldName + ".structures").getValues(false);
        for (Entry<String, Object> entry : configStructs.entrySet())
            if (((Boolean) entry.getValue()) == true) usedStructs.add(loadStructure(entry.getKey() + ".structure"));
    }

    @Override
    //discover appropriate chunks and place them
    public void populate(World world, Random random, Chunk chunk) {
        Random rand = new Random();

        //for every active structure:
        for (Structure struct : usedStructs){

            //get nearest instance
            long origin = getNearestInstance(struct, chunk.getX(), chunk.getZ());

            //make the hash a relative chunk according to the structure
            long hash = mergeCoords(chunk.getX() - splitCoords(origin, true), chunk.getZ() - splitCoords(origin, false));
            //System.out.println("Relative Instance: X: " + splitCoords(hash, true) + " Z: " + splitCoords(hash, false));

            //determine if chance will allow for the structure
            rand.setSeed(chunk.getWorld().getSeed() | struct.getSeed());
            //if ((rand.nextInt() % struct.getChance()) == 0) return; //struct.getChance()

            //find if the current chunk would belong to the structure
            StructureChunk possibleChunk = struct.getChunk(hash, false);
            if (possibleChunk == null) return;
            //System.out.println("Passed in structure test");

            //check if the chunk meets the biome requirements
            Biome biome = struct.getRequiredBiome();
            if (biome != null){
                if (!biomeMap.containsKey(origin)) {
                    Biome target = world.getChunkAt(splitCoords(origin, true), splitCoords(origin, false)).getChunkSnapshot().getBiome(0, 0);
                    biomeMap.put(origin, target);
                }
                if (biomeMap.get(origin) != biome) return;
            }

            //determine the y coord
            int yOrigin = calcYOrigin(struct, chunk, origin);
            //System.out.println("Y Origin:" + yOrigin);

            //paste the structure into the world
            placeStructureChunk(possibleChunk, chunk, origin, yOrigin);
        }
    }

    //get the nearest instance of a given structure and reference point
    public static long getNearestInstance(Structure struct, int x, int z){
        int dist = struct.getDistance();
        Random rand = new Random();

        //mod of the x and z using distance
        //offset x using struct seed and chunk z coord
        int zz = z - (z % dist);
        if (zz > z + (dist / 2)) zz -= dist;
        if (zz < z - (dist / 2)) zz += dist;
        rand.setSeed(struct.getSeed() | zz);

        //determine which location is closest with the half of distance
        int xx = x - (x % dist) + (rand.nextInt() % dist);
        if (xx > x + (dist / 2)) xx -= dist;
        if (xx < x - (dist / 2)) xx += dist;


        //return the hash of the coordinates
        return mergeCoords(xx, zz);
    }

    public int calcYOrigin(Structure struct, Chunk chunk, long origin){
        if (yMap.containsKey(origin)) return yMap.get(origin);
        Random rand = new Random();

        StructureType type = struct.getType();
        rand.setSeed(chunk.getWorld().getSeed() | struct.getSeed());
        int height = chunk.getChunkSnapshot().getHighestBlockYAt(0, 0);
        switch (type){
            case ALL:
                yMap.put(origin, struct.getMinHeight() + (rand.nextInt() % struct.getMaxHeight()));
            case AIR:
                yMap.put(origin, Math.max(struct.getMinHeight(), height) + (rand.nextInt() % struct.getMaxHeight()));
            case SURFACE:
                yMap.put(origin, height);
            case UNDERGROUND:
                yMap.put(origin, struct.getMinHeight() + (rand.nextInt() % Math.min(struct.getMaxHeight(), height)));
            default: yMap.put(origin, height);
        }
        return yMap.get(origin);
    }

    //place the appropriate structure chunks into the world
    public static void placeStructureChunk(StructureChunk source, Chunk target, long origin, int yOrigin){
        HashMap<Integer, StructureSection> sects = source.getAllSections();
        ArrayList<Tag> tileEntities = source.getTileEntities();

        //apply changes from non-null sections
        for (Entry<Integer, StructureSection> entry : sects.entrySet()){
            int index = entry.getKey();
            StructureSection sect = entry.getValue();

            //place blocks according to their section
            for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++){
                //check if the block is not passive or the target is air
                int yy = (int) (yOrigin - source.getStructure().getOrigin().getY() + (index << 4) + y);
                Block block = target.getBlock(x, yy, z);
                //set block ID and data
                int id = block.getType().getId();
                //TODO: fix whatever da heck this out of bounds error is
                try{
                if ((!sect.getBlockPassive(x, y, z)) || id == 0 )
                    block.setTypeIdAndData(sect.getBlockId(x, y, z), sect.getBlockData(x, y, z), false);
                } catch (ArrayIndexOutOfBoundsException e){}
            }
        }

        //place tile entity information
        for (Tag tag : tileEntities){
            TagCompound tile = (TagCompound) tag;
            Map<String, Tag> loc = ((TagCompound) tile.getPayload().get("location")).getPayload();
            //System.out.println(((TagInt) loc.get("x")).getPayload());
            getTileFromTag(tile, target, (int)(yOrigin - source.getStructure().getOrigin().getY() + ((TagInt) loc.get("y")).getPayload()));
        }
    }

}
