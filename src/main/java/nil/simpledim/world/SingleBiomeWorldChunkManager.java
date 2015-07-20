package nil.simpledim.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import nil.simpledim.SimpleDim;
import nil.simpledim.config.DimensionInfo;

public class SingleBiomeWorldChunkManager extends WorldChunkManager {
	
	private static GenLayer defaultGenLayer = new GenLayerSingleBiome(0, BiomeGenBase.plains);
	
    public SingleBiomeWorldChunkManager(long worldSeed, WorldType worldType)
    {
    	super();
    	biomeCache = new BiomeCache(this);
    	genBiomes = biomeIndexLayer = defaultGenLayer;
    }
    
    public SingleBiomeWorldChunkManager(World world) {
    	this(world.getWorldInfo().getSeed(), world.getWorldInfo().getTerrainType());
    	DimensionInfo info = SimpleDim.getConfig().getDimensionInfoForWorld(world.provider.dimensionId);
    	genBiomes = biomeIndexLayer = new GenLayerSingleBiome(world.getWorldInfo().getSeed(), info.biome);
    }
}
