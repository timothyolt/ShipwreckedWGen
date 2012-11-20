package me.darkeh.plugins.shipwreckedwgen;

import java.util.EnumMap;
import java.util.Random;
import me.darkeh.plugins.shipwreckedwgen.biomes.BiomeGen;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class OverworldBiomeGenerator {

    //CONFIG
    public final int seaFloor = 64;
    public final int seaLevel = 80;
    public final int smallBlobScale = 512;
    public       int smallBlobCount;
    public final int smallBlobSpread = 64;
    public final int largeBlobScale = 1024;
    public       int largeBlobCount;
    public final int variationScale = 512;
    public final int detailScale = 128;
    public final int cliffScale = 64;
    public       int landHeight;
    public       int extraDetail; //its about double this value for land
    public final int boostFactor = 8;
    public final int biomeScale = 1024;

    private SimplexOctaveGenerator ocean1;
    private SimplexOctaveGenerator ocean2;
    private SimplexOctaveGenerator humid;
    private SimplexOctaveGenerator temp;
    private SimplexOctaveGenerator variation;
    private SimplexOctaveGenerator finalvar;
    private SimplexOctaveGenerator tempvar;
    private SimplexOctaveGenerator humidvar;
    private SimplexOctaveGenerator cliffs;
    private SimplexOctaveGenerator biometerr;
    private Random rand;

    int chunkX;
    int chunkZ;
    private EnumMap<Biome, BiomeGen> biomes = new EnumMap<Biome, BiomeGen>(Biome.class);
    private int[][] heightMap;
    private Biome[][] biomeMap;

        //0.00      Humidity       1.00         Flat/Hills   : 53/47
        //                                      Rain/Snow/Dry: 54/28/27
        //17 17 17 02 02 01 06 21 22 14  1.00
        //17 17 17 02 02 01 06 21 22 22         01   : Plains           12      12
        //02 02 02 02 02 01 06 21 21 21         02/17: Desert           09/06   17
        //01 01 01 01 01 01 06 06 06 06  T         03: Extreme Hills       08   08
        //01 01 01 04 04 04 04 04 04 04  e      04/18: Forest           13/08   21
        //04 04 04 04 04 04 18 18 18 18  m      05/19: Taiga            11/08   19
        //05 05 05 05 05 19 18 18 18 18  p      06   : Swampland        07      07
        //12 12 12 05 05 19 03 03 03 03         12/13: Ice Plains       07/02   09
        //12 12 12 05 05 19 03 03 03 03            14: Mushroom Island     01   01
        //13 13 12 05 05 19 19 19 19 19  0.00   21/22: Jungle           05/03   08

    public OverworldBiomeGenerator(SimplexOctaveGenerator[] noises, Random rand, int chunkX, int chunkZ, ShipwreckedWGen plugin){
        ocean1 = noises[0];
        ocean2 = noises[1];
        variation = noises[2];
        finalvar = noises[3];
        cliffs = noises[8];
        ocean1.setScale(1 / (double) smallBlobScale);
        ocean2.setScale(1 / (double) largeBlobScale);
        variation.setScale(1 / (double) variationScale);
        finalvar.setScale(1 / (double) detailScale);
        cliffs.setXScale(1 / (double) (cliffScale*2));
        cliffs.setYScale(1 / (double) (cliffScale*2));
        cliffs.setZScale(1 / (double) cliffScale);

        humid = noises[4];
        temp = noises[5];
        tempvar = noises[6];
        humidvar = noises[7];
        temp.setScale(1 / (double)biomeScale);
        humid.setScale(1 / (double)biomeScale);
        tempvar.setScale(1/((double)biomeScale / 16.0));
        humidvar.setScale(1/((double)biomeScale / 16.0));


        biometerr = noises[9];
        this.rand = rand;

        chunkX *= 16;
        chunkZ *= 16;
        this.chunkX = chunkX;//coordinates of the origin of the chunk
        this.chunkZ = chunkZ;   //not the coordinates on the chunk grid

        //<editor-fold defaultstate="collapsed" desc="Pregenerate Terrain">
        heightMap = new int[16][16];
        biomeMap = new Biome[16][16];
        //<editor-fold defaultstate="collapsed" desc="Biome Map">
        for (int x = 0; x < 16; x++){
            int realx = x + chunkX - 1;
            for (int z = 0; z < 16; z++){
                int realz = z + chunkZ - 1;
                double dTemp  = (((temp .noise(realx, realz, 0.5, 0.5, true) + 1) / 2.0) + ((temp .noise(-realx, -realz, 0.5, 0.5, true) + 1) / 2.0)) / 2.0;
                double dHumid = (((humid.noise(realx, realz, 0.5, 0.5, true) + 1) / 2.0) + ((humid.noise(-realx, -realz, 0.5, 0.5, true) + 1) / 2.0)) / 2.0;
                dTemp = dTemp - (tempvar.noise(realx, realz, 0.5, 0.5)) / 32.0;
                dHumid = dHumid - (humidvar.noise(realx, realz, 0.5, 0.5)) / 32.0;
                if (dTemp < 0) dTemp = 0.01;
                if (dTemp > 1) dTemp = 1;
                if (dHumid < 0) dHumid = 0.01;
                if (dHumid > 1) dHumid = 1;
                //BiomeGrid Selection
                //<editor-fold defaultstate="collapsed" desc="Old Biome Selector">
                /*
                     if (((dTemp >  7/10.0 && dTemp <= 10/10.0)&&(dHumid >  5/10.0 && dHumid <=  6/10.0))||
                         ((dTemp >  6/10.0 && dTemp <=  7/10.0)&&(dHumid >  0/10.0 && dHumid <=  5/10.0))||
                         ((dTemp >  5/10.0 && dTemp <=  6/10.0)&&(dHumid >  0/10.0 && dHumid <=  3/10.0))){
                         biomeMap[x][z] = Biome.PLAINS;
                }
                else if (((dTemp >  7/10.0 && dTemp <= 10/10.0)&&(dHumid >  0/10.0 && dHumid <=  5/10.0))){
                     if (((dTemp >  8/10.0 && dTemp <= 10/10.0)&&(dHumid >  0/10.0 && dHumid <=  3/10.0)))
                          biomeMap[x][z] = Biome.DESERT_HILLS;
                     else biomeMap[x][z] = Biome.DESERT;
                }
                else if (((dTemp >  1/10.0 && dTemp <=  3/10.0)&&(dHumid >  6/10.0 && dHumid <= 10/10.0))){
                    biomeMap[x][z] = Biome.EXTREME_HILLS;
                }
                else if (((dTemp >  3/10.0 && dTemp <=  4/10.0)&&(dHumid >  6/10.0 && dHumid <=  10/10.0))||
                         ((dTemp >  4/10.0 && dTemp <=  5/10.0)&&(dHumid >  0/10.0 && dHumid <=  10/10.0))||
                         ((dTemp >  5/10.0 && dTemp <=  6/10.0)&&(dHumid >  3/10.0 && dHumid <=  10/10.0))){
                     if (((dTemp >  3/10.0 && dTemp <=  5/10.0)&&(dHumid >  6/10.0 && dHumid <=  10/10.0)))
                          biomeMap[x][z] = Biome.FOREST_HILLS;
                     else biomeMap[x][z] = Biome.FOREST;
                }
                else if (((dTemp >  3/10.0 && dTemp <=  4/10.0)&&(dHumid >  0/10.0 && dHumid <=   6/10.0))||
                         ((dTemp >  1/10.0 && dTemp <=  3/10.0)&&(dHumid >  3/10.0 && dHumid <=   6/10.0))||
                         ((dTemp >  0/10.0 && dTemp <=  1/10.0)&&(dHumid >  3/10.0 && dHumid <=  10/10.0))){
                     if (((dTemp >  1/10.0 && dTemp <=  4/10.0)&&(dHumid >  6/10.0 && dHumid <=  10/10.0))||
                         ((dTemp >  1/10.0 && dTemp <=  4/10.0)&&(dHumid >  6/10.0 && dHumid <=  10/10.0)))
                          biomeMap[x][z] = Biome.TAIGA_HILLS;
                     else biomeMap[x][z] = Biome.TAIGA;
                }
                else if (((dTemp >  7/10.0 && dTemp <= 10/10.0)&&(dHumid >  6/10.0 && dHumid <=   7/10.0))||
                         ((dTemp >  6/10.0 && dTemp <=  7/10.0)&&(dHumid >  6/10.0 && dHumid <=  10/10.0))){
                          biomeMap[x][z] = Biome.FOREST;
                }
                else if (((dTemp >  0/10.0 && dTemp <=  3/10.0)&&(dHumid >  0/10.0 && dHumid <=   3/10.0))){
                     if (((dTemp >  0/10.0 && dTemp <=  1/10.0)&&(dHumid >  0/10.0 && dHumid <=   2/10.0)))
                          biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                     else biomeMap[x][z] = Biome.ICE_PLAINS;
                }
                else if (((dTemp >  9/10.0 && dTemp <= 10/10.0)&&(dHumid >  9/10.0 && dHumid <=  10/10.0))){
                    biomeMap[x][z] = Biome.MUSHROOM_ISLAND;
                }
                else if (((dTemp >  9/10.0 && dTemp <= 10/10.0)&&(dHumid >  7/10.0 && dHumid <=   9/10.0))||
                         ((dTemp >  7/10.0 && dTemp <=  9/10.0)&&(dHumid >  7/10.0 && dHumid <=  10/10.0))){
                     if (((dTemp >  9/10.0 && dTemp <= 10/10.0)&&(dHumid >  8/10.0 && dHumid <=  9/10.0))||
                         ((dTemp >  8/10.0 && dTemp <=  9/10.0)&&(dHumid >  8/10.0 && dHumid <=  10/10.0)))
                          biomeMap[x][z] = Biome.JUNGLE_HILLS;
                     else biomeMap[x][z] = Biome.JUNGLE;
                }
                else biomeMap[x][z] = Biome.PLAINS;
                */
                //</editor-fold>
                     if (dTemp >  0/10.0 && dTemp <=  1/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  4/10.0) biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                    else if (dHumid >  4/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.TAIGA_HILLS;
                    else if (dHumid >  6/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.TAIGA;
                    else if (dHumid >  7/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.EXTREME_HILLS;
                    else biomeMap[x][z] = Biome.ICE_PLAINS;
                }
                else if (dTemp >  1/10.0 && dTemp <=  2/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  3/10.0) biomeMap[x][z] = Biome.ICE_PLAINS;
                    else if (dHumid >  3/10.0 && dHumid <=  4/10.0) biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                    else if (dHumid >  4/10.0 && dHumid <=  5/10.0) biomeMap[x][z] = Biome.TAIGA_HILLS;
                    else if (dHumid >  5/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.TAIGA;
                    else if (dHumid >  7/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.EXTREME_HILLS;
                    else biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                }
                else if (dTemp >  2/10.0 && dTemp <=  3/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  3/10.0) biomeMap[x][z] = Biome.ICE_PLAINS;
                    else if (dHumid >  3/10.0 && dHumid <=  4/10.0) biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                    else if (dHumid >  4/10.0 && dHumid <=  5/10.0) biomeMap[x][z] = Biome.TAIGA_HILLS;
                    else if (dHumid >  5/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.TAIGA;
                    else if (dHumid >  6/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.FOREST_HILLS;
                    else if (dHumid >  7/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.EXTREME_HILLS;
                    else biomeMap[x][z] = Biome.ICE_MOUNTAINS;
                }
                else if (dTemp >  3/10.0 && dTemp <=  4/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.TAIGA;
                    else if (dHumid >  6/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.FOREST_HILLS;
                    else biomeMap[x][z] = Biome.TAIGA;
                }
                else if (dTemp >  4/10.0 && dTemp <=  5/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.FOREST;
                    else if (dHumid >  7/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.FOREST_HILLS;
                    else biomeMap[x][z] = Biome.FOREST;
                }
                else if (dTemp >  5/10.0 && dTemp <=  6/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.PLAINS;
                    else if (dHumid >  6/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.FOREST;
                    else biomeMap[x][z] = Biome.PLAINS;
                }
                else if (dTemp >  6/10.0 && dTemp <=  7/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  5/10.0) biomeMap[x][z] = Biome.DESERT;
                    else if (dHumid >  5/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.PLAINS;
                    else if (dHumid >  6/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.JUNGLE;
                    else biomeMap[x][z] = Biome.DESERT;
                }
                else if (dTemp >  7/10.0 && dTemp <=  8/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  3/10.0) biomeMap[x][z] = Biome.DESERT_HILLS;
                    else if (dHumid >  3/10.0 && dHumid <=  5/10.0) biomeMap[x][z] = Biome.DESERT;
                    else if (dHumid >  5/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.PLAINS;
                    else if (dHumid >  6/10.0 && dHumid <=  8/10.0) biomeMap[x][z] = Biome.JUNGLE;
                    else if (dHumid >  8/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.JUNGLE_HILLS;
                    else biomeMap[x][z] = Biome.DESERT;
                }
                else if (dTemp >  8/10.0 && dTemp <=  9/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  3/10.0) biomeMap[x][z] = Biome.DESERT_HILLS;
                    else if (dHumid >  3/10.0 && dHumid <=  5/10.0) biomeMap[x][z] = Biome.DESERT;
                    else if (dHumid >  5/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.PLAINS;
                    else if (dHumid >  7/10.0 && dHumid <=  8/10.0) biomeMap[x][z] = Biome.JUNGLE;
                    else if (dHumid >  8/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.JUNGLE_HILLS;
                    else biomeMap[x][z] = Biome.DESERT_HILLS;
                }
                else if (dTemp >  9/10.0 && dTemp <= 10/10.0){
                         if (dHumid >  0/10.0 && dHumid <=  3/10.0) biomeMap[x][z] = Biome.DESERT_HILLS;
                    else if (dHumid >  3/10.0 && dHumid <=  6/10.0) biomeMap[x][z] = Biome.DESERT;
                    else if (dHumid >  6/10.0 && dHumid <=  7/10.0) biomeMap[x][z] = Biome.PLAINS;
                    else if (dHumid >  7/10.0 && dHumid <=  8/10.0) biomeMap[x][z] = Biome.JUNGLE;
                    else if (dHumid >  8/10.0 && dHumid <= 10/10.0) biomeMap[x][z] = Biome.JUNGLE_HILLS;
                    else biomeMap[x][z] = Biome.DESERT_HILLS;
                }
                else biomeMap[x][z] = Biome.BEACH;
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Height Map">
        for (int x = 0; x < 16; x++){
            int realx = x + chunkX;
            for (int z = 0; z < 16; z++){
                int realz = z + chunkZ;
                Biome biome = biomeMap[x][z];
                BiomeGen biomeGen = plugin.biomeHandler.getBiomeGen(biome);
                if (biomeGen == null){
                    smallBlobCount = 32;
                    largeBlobCount = 24;
                    landHeight = 8;
                    extraDetail = 4;
                }
                else{
                    smallBlobCount = biomeGen.getSmallBlobCount();
                    largeBlobCount = biomeGen.getLargeBlobCount();
                    landHeight = biomeGen.getLandHeight();
                    extraDetail = biomeGen.getExtraDetail();
                }

                double oceanNoise1 = Math.max(Math.max(
                        ocean1.noise(realx, realz, 0.5, 0.5) * smallBlobCount,
                        ocean1.noise(realx + (smallBlobSpread), realz, 0.5, 0.5) * smallBlobCount), Math.max(
                        ocean1.noise(realx + (smallBlobSpread/2), realz + (smallBlobSpread/2), 0.5, 0.5) * smallBlobCount,
                        ocean1.noise(realx + (smallBlobSpread/2), realz - (smallBlobSpread/2), 0.5, 0.5) * smallBlobCount));

                double underHeight = Math.max(oceanNoise1, ocean2.noise(realx , realz, 1, 0.5) * largeBlobCount);
                double midHeight = ocean1.noise(realx, realz, 0.5, 0.5) * 4;
                double overHeight = (variation.noise(realx, realz, 0.5, 0.5) * landHeight) + underHeight/2;

                double landMod = (finalvar.noise(realx, realz, 0.2, 0.2) + 1) * extraDetail/2 + (finalvar.noise(realx, realz, 0.1, 0.1)+1) * extraDetail/2;
                double allMod = (finalvar.noise(realx, realz, 0.5, 0.5)-1) * extraDetail;

                for (int y = 0; y < 5; y++){
                    if (cliffs.noise(realz, realx, y, 0.5, 0.5) > 0.3) landMod += 1;
                }

                double height;
                underHeight += boostFactor; //Terrain Boost
                overHeight += boostFactor; //Terrain Boost
                overHeight += landMod;
                if (seaFloor + underHeight < seaLevel - 3) height = underHeight;
                else {
                    if (seaFloor + overHeight < seaLevel - 3) height = seaLevel - 5 - seaFloor + midHeight;
                    else if (seaFloor + overHeight < seaLevel - 5 + midHeight) height = seaLevel - 5 - seaFloor + midHeight;
                    else height = overHeight;
                }
                height += allMod;
                height = biomeGen.addBiomeLand(realx, realz, (int)height, biometerr);
                if (height < 0) height = 0;
                heightMap[x][z] = (int)height;
            }
        }
        //</editor-fold>
        //</editor-fold>
    }

    public Biome getBiome(int x, int z){
        int dx = x-chunkX;
        int dz = z-chunkZ;
        if (dx<0) dx = 0;
        if (dz<0) dz = 0;
        if (dx>15) dx = 15;
        if (dz>15) dz = 15;
        return biomeMap[dx][dz];
    }

    public int getTerrain(int x, int z){
        return heightMap[x-chunkX][z-chunkZ];
    }

    public int getSmoothTerrain(int x, int z){
        Biome thisBlock = getBiome(x, z);
        if (getBiome(x + 1, z) != thisBlock){
            return (int)medianSmooth(getTerrain(x, z), getTerrain(x + 1, z));
        }
        else if (getBiome(x - 1, z) != thisBlock){
            return (int)medianSmooth(getTerrain(x, z), getTerrain(x - 1, z));
        }
        else if (getBiome(x, z + 1) != thisBlock){
            return (int)medianSmooth(getTerrain(x, z), getTerrain(x, z + 1));
        }
        else if (getBiome(x, z - 1) != thisBlock){
            return (int)medianSmooth(getTerrain(x, z), getTerrain(x, z - 1));
        }
        else return getTerrain(x, z);
    }

    private double medianSmooth(double pri, double sec){
        return (pri + ((pri + sec) / 2.0)) / 2.0;
    }
}
