package nil.xcompcraft.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import nil.xcompcraft.config.DimensionInfo;
import nil.xcompcraft.world.SimpleDimWorldProvider;

public class VoidWorldType extends WorldType {

	public VoidWorldType() {
		super("Empty World");
	}

	@Override
	public boolean hasVoidParticles(boolean flag) {
		return false;
	}

	@Override
	public int getSpawnFuzz() {
		return 1;
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		String generationString = "2;1x0";
		DimensionInfo dimInfo = ((SimpleDimWorldProvider)(world.provider)).getDimensionInfo();
		if (dimInfo.biome != null) {
			generationString = generationString + ";" + dimInfo.biome.biomeID; 
		}
		return new ChunkProviderFlat(world, world.getSeed(), false, generationString);
	}

	@Override
	public float getCloudHeight() {
		return 0f;
	}
}
