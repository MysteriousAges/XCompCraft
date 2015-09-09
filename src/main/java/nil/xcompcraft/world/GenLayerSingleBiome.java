package nil.xcompcraft.world;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerSingleBiome extends GenLayer {

    private final BiomeGenBase biome;

    public GenLayerSingleBiome(long seed, BiomeGenBase biome) {
        super(seed);
        this.biome = biome;
    }

    public int[] getInts(int x, int z, int sizeX, int sizeZ) {
    	int[] ints = IntCache.getIntCache(sizeX*sizeZ);

        for(int zCoord = 0; zCoord < sizeZ; ++zCoord) {
            for(int xCoord = 0; xCoord < sizeX; ++xCoord) {
                initChunkSeed(xCoord+x, zCoord+z);
                ints[xCoord + zCoord * sizeX] = biome.biomeID;
            }
        }

        return ints;
    }
}
