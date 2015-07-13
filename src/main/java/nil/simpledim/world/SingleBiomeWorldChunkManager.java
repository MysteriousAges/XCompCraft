package nil.simpledim.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class SingleBiomeWorldChunkManager extends WorldChunkManager {
	
    public SingleBiomeWorldChunkManager(long worldSeed, WorldType worldType)
    {
    	super();
    	biomeCache = new BiomeCache(this);
    	genBiomes = biomeIndexLayer = new GenLayerSingleBiome(worldSeed, BiomeGenBase.plains);
    }
    
    public SingleBiomeWorldChunkManager(World world) {
    	this(world.getWorldInfo().getSeed(), world.getWorldInfo().getTerrainType());
    }
}
