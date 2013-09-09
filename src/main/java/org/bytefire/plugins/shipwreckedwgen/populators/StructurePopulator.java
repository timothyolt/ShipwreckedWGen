package org.bytefire.plugins.shipwreckedwgen.populators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
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

    public StructurePopulator(ShipwreckedWGen plugin, String worldName){
        usedStructs = new ArrayList<Structure>();

        //patch the config file if it is missing configuration for a structure
        FileConfiguration config = plugin.getConfig();
        Map<String, Object> configStructs = config.getConfigurationSection("worlds." + worldName.toLowerCase() + ".structures").getValues(false);
        for (Entry<String, Object> entry : configStructs.entrySet())
            if (((Boolean) entry.getValue()) == true) usedStructs.add(loadStructure(entry.getKey()));
    }

    @Override
    //discover appropriate chunks and place them
    public void populate(World world, Random random, Chunk chunk) {
        Random rand = new Random();

        //for every active structure:
        for (Structure struct : usedStructs){

            //get nearest instance
            long hash = getNearestInstance(struct, chunk.getX(), chunk.getZ());
            System.out.println("Chunk Instance: X: " + chunk.getX() + " Z: " + chunk.getZ());
            System.out.println("Nearest Instance: X: " + splitCoords(hash, true) + " Z: " + splitCoords(hash, false));

            //make the hash a relative chunk according to the structure
            hash = mergeCoords(chunk.getX() - splitCoords(hash, true), chunk.getZ() - splitCoords(hash, false));
            System.out.println("Relative Instance: X: " + splitCoords(hash, true) + " Z: " + splitCoords(hash, false));

            //determine if chance will allow for the structure
            rand.setSeed(chunk.getWorld().getSeed() | struct.getSeed());
            if (rand.nextInt() % struct.getChance() == 1) return;
            System.out.println("Passed chance test");

            //find if the current chunk would belong to the structure
            StructureChunk possibleChunk = struct.getChunk(hash, false);
            if (possibleChunk == null) return;
            System.out.println("Passed in structure test");

            //check if the chunk meets the biome requirements
            Biome biome = struct.getRequiredBiome();
            if (biome != null && chunk.getBlock(0, 0, 0).getBiome() != biome) return;
            System.out.println("Passed biome");

            //determine the y coord
            int yOrigin = calcYOrigin(struct, chunk);
            System.out.println("Y Origin:" + yOrigin);

            //paste the structure into the world
            placeStructureChunk(possibleChunk, chunk, yOrigin);
        }
    }

    //get the nearest instance of a given structure and reference point
    public static long getNearestInstance(Structure struct, int x, int z){
        int dist = struct.getDistance();
        Random rand = new Random();

        //mod of the x and z using distance
        //offset x using struct seed and chunk z coord
        rand.setSeed(struct.getSeed() | z);
        int xx = x - (x % dist) + (rand.nextInt() % dist);
        int zz = z - (z % dist);

        //determine which location is closest with the half of distance
        if (xx > x - (dist / 2)) xx += dist;
        if (zz > z - (dist / 2)) zz += dist;

        //return the hash of the coordinates

        return mergeCoords(xx, zz);
    }

    public static int calcYOrigin(Structure struct, Chunk chunk){
        Random rand = new Random();

        StructureType type = struct.getType();
        rand.setSeed(chunk.getWorld().getSeed() | struct.getSeed());
        int height = chunk.getChunkSnapshot().getHighestBlockYAt(0, 0);
        switch (type){
            case ALL:
                return struct.getMinHeight() + (rand.nextInt() % struct.getMaxHeight());
            case AIR:
                return Math.max(struct.getMinHeight(), height) + (rand.nextInt() % struct.getMaxHeight());
            case SURFACE:
                return height;
            case UNDERGROUND:
                return struct.getMinHeight() + (rand.nextInt() % Math.min(struct.getMaxHeight(), height));
            default: return height;
        }
    }

    //place the appropriate structure chunks into the world
    public static void placeStructureChunk(StructureChunk source, Chunk target, int yOrigin){
        HashMap<Integer, StructureSection> sects = source.getAllSections();
        ArrayList<Tag> tileEntities = source.getTileEntities();

        //apply changes from non-null sections
        for (Entry<Integer, StructureSection> entry : sects.entrySet()){
            int index = entry.getKey();
            StructureSection sect = entry.getValue();

            //place blocks according to their section
            for (int x = 0; x < 15; x++) for (int y = 0; y < 15; y++) for (int z = 0; z < 15; z++){
                //check if the block is not passive or the target is air
                int id = sect.getBlockId(x, y, z);
                if (!sect.getBlockPassive(x, y, z) || id == 0){
                    //set block ID and data
                    int yy = (int) (yOrigin - source.getStructure().getOrigin().getY() + (index << 4) + y);
                    target.getBlock(x, yy, z).setTypeIdAndData(id, sect.getBlockData(x, y, z), false);
                }
            }
        }

        //place tile entity information
        for (Tag tag : tileEntities){
            TagCompound tile = (TagCompound) tag;
            Map<String, Tag> loc = ((TagCompound) tile.getPayload().get("location")).getPayload();
            getTileFromTag(tile, target, new Location(target.getWorld(),
                (target.getX() << 4) + ((TagInt) loc.get("x")).getPayload(),
                yOrigin - source.getStructure().getOrigin().getY() + ((TagInt) loc.get("y")).getPayload(),
                (target.getZ() << 4) + ((TagInt) loc.get("z")).getPayload()));
        }
    }

}
