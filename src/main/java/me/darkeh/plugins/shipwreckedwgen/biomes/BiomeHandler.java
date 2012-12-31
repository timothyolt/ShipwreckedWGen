package me.darkeh.plugins.shipwreckedwgen.biomes;

import java.util.EnumMap;
import me.darkeh.plugins.shipwreckedwgen.ShipwreckedWGen;
import org.bukkit.block.Biome;

public class BiomeHandler {
    private ShipwreckedWGen plugin;
    public BiomeHandler(ShipwreckedWGen plugin){
        this.plugin = plugin;
    }

    private EnumMap<Biome, BiomeGen> biomes = new EnumMap<Biome, BiomeGen>(Biome.class);
    public final BiomeGen getBiomeGen(Biome biome){
        if (biomes.containsKey(biome)) return biomes.get(biome);
        else {
            if (biome == Biome.FOREST) return new ForestBiome(plugin);
            if (biome == Biome.FOREST_HILLS) return new ForestHillsBiome(plugin);
            if (biome == Biome.PLAINS) return new PlainsBiome(plugin);
            if (biome == Biome.BEACH) return new PlainsBiome(plugin);
            if (biome == Biome.JUNGLE) return new JungleBiome();
            if (biome == Biome.JUNGLE_HILLS) return new JungleHillsBiome();
            if (biome == Biome.DESERT) return new DesertBiome(plugin);
            if (biome == Biome.DESERT_HILLS) return new DesertHillsBiome();
            if (biome == Biome.EXTREME_HILLS) return new ExtremeBiome();
            if (biome == Biome.TAIGA) return new TaigaBiome();
            if (biome == Biome.TAIGA_HILLS) return new TaigaHillsBiome();
            if (biome == Biome.ICE_PLAINS) return new TundraBiome();
            if (biome == Biome.ICE_MOUNTAINS) return new TundraHillsBiome();
            else return null;
        }
    }
}
