package nil.xcompcraft.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import nil.xcompcraft.config.DimensionInfo;
import nil.xcompcraft.config.DimensionType;
import nil.xcompcraft.world.SimpleDimWorldProvider;

public class SuperflatWorldType extends WorldType {

	public SuperflatWorldType() {
		super("SimpleDimFlat");
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
		DimensionInfo dimInfo = ((SimpleDimWorldProvider)(world.provider)).getDimensionInfo();
		if (dimInfo.type == DimensionType.VOID) {
			dimInfo.superflatGenerator = "2;1x0";
			if (dimInfo.biome != null) {
				dimInfo.superflatGenerator = dimInfo.superflatGenerator + ";" + dimInfo.biome.biomeID; 
			}
		}
		return new ChunkProviderFlat(world, world.getSeed(), false, dimInfo.superflatGenerator);
	}

	@Override
	public float getCloudHeight() {
		return 0f;
	}
}
