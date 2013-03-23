package me.darkeh.plugins.shipwreckedwgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.biomes.BiomeGen;
import me.darkeh.plugins.shipwreckedwgen.populators.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class OverworldChunkGenerator extends ChunkGenerator{
    private ShipwreckedWGen plugin;

    public OverworldChunkGenerator(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }
    private ArrayList<BlockPopulator> populators;
    //CONFIG
    final Material base = Material.STONE;
    final Material bottom = Material.BEDROCK;
    final Material oceanTop = Material.SAND;
    final Material oceanMid = Material.SANDSTONE;
    final Material oceanLow = Material.GRAVEL;
    final Material landTop = Material.GRASS;
    final Material landMid = Material.DIRT;

    void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
        if (chunk[y >> 4] == null)
            chunk[y >> 4] = new byte[16 * 16 * 16];
        if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
            return;
        try {
            chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
        } catch (Exception e) {}
    }

    byte getBlock(int x, int y, int z, byte[][] chunk) {
        //if the Block section the block is in hasn't been used yet, allocate it
        if (chunk[y >> 4] == null) return 0; //block is air as it hasnt been allocated
        if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0)) return 0;
        try { return chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x];}
        catch (Exception e) {
            return 0;
        }
    }

    @Override
    //Generates block sections. Each block section is 16*16*16 blocks, stacked above each other. //There are world height / 16 sections. section number is world height / 16 (>>4)
    //returns a byte[world height / 16][], formatted [section id][Blocks]. If there are no blocks in a section, it need not be allocated.
    public byte[][] generateBlockSections(World world, Random rand, int ChunkX, int ChunkZ, BiomeGrid biome) {
        //TODO: generate rock spikes over cliffs
        Random seed = new Random(world.getSeed());
        SimplexOctaveGenerator[] noises = {
            new SimplexOctaveGenerator(seed          , 8), //Ocean 1
            new SimplexOctaveGenerator(seed.nextInt(), 4), //Ocean 2
            new SimplexOctaveGenerator(seed.nextInt(), 4), //Variation
            new SimplexOctaveGenerator(seed.nextInt(), 8), //Final Variation
            new SimplexOctaveGenerator(seed.nextInt(), 2), //Humidity
            new SimplexOctaveGenerator(seed.nextInt(), 2), //Temperature
            new SimplexOctaveGenerator(seed.nextInt(), 2), //Humidity Variation
            new SimplexOctaveGenerator(seed.nextInt(), 2), //Temperature Variation
            new SimplexOctaveGenerator(seed.nextInt(), 8), //Cliff Variation
            new SimplexOctaveGenerator(seed.nextInt(), 4)};//Biome Terrain

        OverworldBiomeGenerator terrain = new OverworldBiomeGenerator(noises, rand, ChunkX, ChunkZ, plugin);
        int seaLevel = terrain.seaLevel;
        int seaFloor = terrain.seaFloor;
        byte[][] chunk = new byte[16][];

        SimplexOctaveGenerator fOver = new SimplexOctaveGenerator(seed.nextInt(), 6);
        fOver.setScale(1 / 64.0);
        SimplexOctaveGenerator fUnder = new SimplexOctaveGenerator(seed.nextInt(), 2);
        fUnder.setScale(1 / 128.0);
        SimplexOctaveGenerator fCluster = new SimplexOctaveGenerator(seed.nextInt(), 2);
        fCluster.setScale(1 / 512.0);
        SimplexOctaveGenerator fHeight = new SimplexOctaveGenerator(seed.nextInt(), 2);
        fHeight.setScale(1 / 256.0);
        SimplexOctaveGenerator spires = new SimplexOctaveGenerator(seed.nextInt(), 2);
        spires.setScale(1 / 2.0);

        for (int x = 0; x < 16; x ++){
            for (int z = 0; z < 16; z ++){
                Biome landBiome = terrain.getBiome        (x + (ChunkX * 16), z + (ChunkZ * 16));
                int height =      terrain.getSmoothTerrain(x + (ChunkX * 16), z + (ChunkZ * 16));

                //Base Stone
                for(int y=1; y<seaFloor; y++){
                    setBlock(x, y, z, chunk, base);
                }

                //Base Terrain
                for(int y = seaFloor - 1; y < seaFloor + height; y ++){
                    setBlock(x, y, z, chunk, base);
                }

                //Ocean Topsoil
                if (height + seaFloor < seaLevel){
                    setBlock(x, height + seaFloor    , z, chunk, oceanTop);
                    setBlock(x, height + seaFloor - 1, z, chunk, oceanTop);
                    setBlock(x, height + seaFloor - 2, z, chunk, oceanMid);
                    setBlock(x, height + seaFloor - 3, z, chunk, oceanLow);
                    if (landBiome.equals(Biome.TAIGA)||landBiome.equals(Biome.TAIGA_HILLS)||landBiome.equals(Biome.ICE_PLAINS)||landBiome.equals(Biome.ICE_MOUNTAINS))
                         biome.setBiome(x, z, Biome.FROZEN_OCEAN);
                    else biome.setBiome(x, z, Biome.OCEAN);
                }

                //Beach Topsoil
                else if ((int)height + seaFloor < seaLevel + 3 && (int)height + seaFloor > seaLevel-1){
                    setBlock(x, height + seaFloor    , z, chunk, oceanTop);
                    setBlock(x, height + seaFloor - 1, z, chunk, oceanTop);
                    setBlock(x, height + seaFloor - 2, z, chunk, oceanTop);
                    setBlock(x, height + seaFloor - 3, z, chunk, oceanMid);
                    setBlock(x, height + seaFloor - 4, z, chunk, oceanLow);
                    if (landBiome.equals(Biome.TAIGA)||landBiome.equals(Biome.TAIGA_HILLS)||landBiome.equals(Biome.ICE_PLAINS)||landBiome.equals(Biome.ICE_MOUNTAINS))
                        setBlock(x, height + seaFloor + 1, z, chunk, Material.SNOW);
                    biome.setBiome(x, z, Biome.BEACH);
                }

                //Land Topsoil
                else{
                    BiomeGen biomeGen = plugin.getBiomeHandler().getBiomeGen(landBiome);
                    if (biomeGen != null){
                    Material[] topsoil = biomeGen.getTopsoil();
                        if (topsoil == null){
                            setBlock(x, height + seaFloor,     z, chunk, landTop);
                            setBlock(x, height + seaFloor - 1, z, chunk, landMid);
                            setBlock(x, height + seaFloor - 2, z, chunk, landMid);
                        }
                        else { //setBlock(x, (int)height + seaFloor, z, chunk, topsoil[0]);
                            for (int layer = topsoil.length - 1; layer >= 0; layer--){
                                setBlock(x, (height + seaFloor) - topsoil.length + layer + 1, z, chunk, topsoil[topsoil.length - layer - 1]);
                            }
                        }
                    }
                    biome.setBiome(x, z, landBiome);
                }

                //Ocean
                for (int y = 80; y > height + seaFloor && y > seaFloor; y--){
                    setBlock(x, y, z, chunk, Material.WATER);
                    if ((biome.getBiome(x, z) == Biome.FROZEN_OCEAN)&&(y==80))
                        setBlock(x, y, z, chunk, Material.ICE);
                }

                //Sky Islands
                if (height < 1){
                    int hCluster = (int)Math.floor(fCluster.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*(16));
                    if (hCluster > 15){
                        int hOver = (int)Math.floor(fOver.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*16);
                        int hUnder = (int)Math.floor(fUnder.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*(16));
                        int hHeight = (int)(fHeight.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*10) + 229;
                        if (hOver >= 16) hOver = 15;
                        if (hUnder <= -16) hUnder = -15;
                        if (hOver > 0 || hUnder < 0) for (int y = -16; y < 16; y++){
                            if (y <= hOver && y >= hUnder) {
                                if (y > hOver - 2 && y != hOver) setBlock(x, y +  hHeight, z, chunk, Material.DIRT);
                                else if (y == hOver) setBlock(x, y + hHeight, z, chunk, Material.GRASS);
                                else setBlock(x, y + hHeight, z, chunk, Material.STONE);
                            }
                        }
                    }
                }

                //Caverns
                int caveLow = -1;
                int caveTop = -1;
                //Open Cavern
                for (int y = 0; y < 24; y++){
                    if (fCluster.noise(x + (ChunkX * 16), y, z + (ChunkZ * 16), 0.5, 0.5)*16 > 8 && getBlock(x, y + 40, z, chunk)==1){
                        if (caveLow == -1) caveLow = y;
                        caveTop = y;
                        setBlock(x, y + 40, z, chunk, Material.AIR);
                    }
                }
                if(caveTop != -1 && caveLow != -1){
                    //Top Noise
                    int top = (int) (caveTop - (fOver.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5) * 6));
                    if (top > caveTop) top = caveTop;
                    for(int y = top; y <= caveTop; y++){
                        setBlock(x, y + 40, z, chunk, Material.STONE);
                    }
                    caveTop = top;
                    //Bottom Noise
                    int low = (int) (caveLow + (fOver.noise(z + (ChunkZ * 16), x + (ChunkX * 16), 0.5, 0.5) * 6));
                    if (low > caveLow) low = caveLow;
                    for(int y = caveLow; y <= low; y++){
                        setBlock(x, y + 40, z, chunk, Material.STONE);
                    }
                    caveLow = low;
                    boolean sTSpike = false;
                    //Stalactites
                    int sTites = 0;
                    for (int y = 0; y < 12; y++){
                        if (fUnder.noise(x + (ChunkX * 16), y + 12, z + (ChunkZ * 16), 0.5, 0.5)*16 > 8) sTites++;
                        if (caveTop < 20 && spires.noise(x + (ChunkX * 16), y + 12, z + (ChunkZ * 16), 0.5, 0.5)*16 > 8){
                            sTites++;
                            sTSpike = true;
                        }
                    }
                    for (int y = caveTop; y > caveTop - sTites; y--){
                        if (sTSpike==true&&seed.nextInt(1024)==1) setBlock(x, y + 40, z, chunk, Material.IRON_ORE);
                        else setBlock(x, y + 40, z, chunk, Material.STONE);
                    }
                    //Stalagmites
                    boolean sMSpike = false;
                    int sMites = 0;
                    for (int y = 0; y < 12; y++){
                        if (fUnder.noise(z + (ChunkZ * 16), y, x + (ChunkX * 16), 0.5, 0.5)*16 > 8) sMites++;
                        if (caveLow > 3 && spires.noise(x + (ChunkX * 16), y, z + (ChunkZ * 16), 0.5, 0.5)*16 > 8){
                            sMites++;
                            sMSpike = true;
                        }
                    }
                    for (int y = caveLow; y < caveLow + sMites; y++){
                        if (sMSpike==true&&seed.nextInt(1024)==1) setBlock(x, y + 40, z, chunk, Material.IRON_ORE);
                        else setBlock(x, y + 40, z, chunk, Material.STONE);
                    }
                }

                //Extreme Overhangs
                if (height > 26 && landBiome==Biome.EXTREME_HILLS){
                    int hOver = (int)Math.floor(fOver.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*16);
                    int hUnder = (int)Math.floor(fUnder.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*(16));
                    int hCluster = (int)Math.floor(fCluster.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*(16));
                    hOver -= Math.abs(hCluster);
                    hUnder += Math.abs(hCluster);
                    int hHeight = (int)(fHeight.noise(x + (ChunkX * 16), z + (ChunkZ * 16), 0.5, 0.5)*5) + seaFloor + 52;
                    if (hOver >= 16) hOver = 15;
                    if (hUnder <= -16) hUnder = -15;
                    if (hOver > 0 || hUnder < 0) for (int y = -16; y < 16; y++){
                        if (y <= hOver && y >= hUnder) {
                            if (y == hOver) height = y + hHeight - seaFloor;
                            if (y > hOver - 2 && y != hOver) setBlock(x, y +  hHeight, z, chunk, Material.DIRT);
                            else if (y == hOver) setBlock(x, y + hHeight, z, chunk, Material.GRASS);
                            else setBlock(x, y + hHeight, z, chunk, Material.STONE);
                        }
                    }
                }

                //Extreme Mountains (level 2)
                //Heck, lets put them in any biome. It has to be high to get to 52 anyways.
                if (height > 52){
                    int oldHeight = height;
                    int newHeight = plugin.getBiomeHandler().getBiomeGen(landBiome).addBiomeLand(z + (ChunkZ * 16), x + (ChunkX * 16), height, fOver);
                    for (int y = oldHeight; y <= newHeight; y++){
                        if (y == newHeight){
                            setBlock(x, y + seaFloor, z, chunk, Material.GRASS);
                            setBlock(x, y - 1 + seaFloor, z, chunk, Material.DIRT);
                            setBlock(x, y - 2 + seaFloor, z, chunk, Material.DIRT);
                            setBlock(x, y - 3 + seaFloor, z, chunk, Material.DIRT);
                        }
                        else setBlock(x, y + seaFloor, z, chunk, Material.STONE);
                        height = newHeight;
                    }
                }

                fOver.setScale(1 / 64.0);
                //Extreme Overhangs (level 2)
                if (height > 40 && landBiome==Biome.EXTREME_HILLS){
                    int hOver = (int)Math.floor(fOver.noise(z + (ChunkZ * 16), x + (ChunkX * 16), 0.5, 0.5)*16);
                    int hUnder = (int)Math.floor(fUnder.noise(z + (ChunkZ * 16), x + (ChunkX * 16), 0.5, 0.5)*(16));
                    int hCluster = (int)Math.floor(fCluster.noise(z + (ChunkZ * 16), x + (ChunkX * 16), 0.5, 0.5)*(16));
                    hOver -= Math.abs(hCluster);
                    hUnder += Math.abs(hCluster);
                    int hHeight = (int)(fHeight.noise(z + (ChunkZ * 16), x + (ChunkX * 16), 0.5, 0.5)*5) + seaFloor + 78;
                    if (hOver >= 16) hOver = 15;
                    if (hUnder <= -16) hUnder = -15;
                    if (hOver > 0 || hUnder < 0) for (int y = -16; y < 16; y++){
                        if (y <= hOver && y >= hUnder) {
                            if (y > hOver - 2 && y != hOver) setBlock(x, y +  hHeight, z, chunk, Material.DIRT);
                            else if (y == hOver) setBlock(x, y + hHeight, z, chunk, Material.GRASS);
                            else setBlock(x, y + hHeight, z, chunk, Material.STONE);
                        }
                    }
                }
                setBlock(x, 0, z, chunk, Material.BEDROCK);
            }
        }
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();

        //Underground
        pops.add(new DepositPopulator(plugin));
        pops.add(new OrePopulator(plugin));
        pops.add(new RavinePopulator(plugin));
        pops.add(new AirPocketPopulator(plugin));
        pops.add(new CavePopulator(plugin));

        //Surface
        pops.add(new RiverPopulator(plugin));
        pops.add(new BiomePopulator(plugin));

        return pops;
    }
}
