package nil.xcompcraft.world;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import nil.xcompcraft.SimpleDim;
import nil.xcompcraft.config.DimensionInfo;

public class SimpleDimWorldProvider extends WorldProvider {

	public static final int WORLD_PROVIDER_ID = 0x946731;
	
	private DimensionInfo dimInfo;
		
	public SimpleDimWorldProvider() {
		super();
	}

	@Override
	public String getDimensionName() {
		return dimInfo.name;
	}
	
	public DimensionInfo getDimensionInfo() {
		return dimInfo;
	}

	@Override
	public ChunkCoordinates getSpawnPoint() {
		if (dimInfo.spawnPoint != null) {
			return dimInfo.spawnPoint;
		}
		return super.getSpawnPoint();
	}

	@Override
	public void setDimension(int dim) {
		super.setDimension(dim);
		dimInfo = SimpleDim.getConfig().getDimensionInfoForWorld(dim);
		if (dimInfo == null) {
			throw new RuntimeException("SimpleDimWorldProvider is providing a world for an unregistered dimension (" + dim + ") - WTF?");
		}
	}

	@Override
	protected void registerWorldChunkManager() {
		terrainType = dimInfo.type.getWorldType();
		if (dimInfo.seedOverride != null) {
			worldObj.getWorldInfo().randomSeed = dimInfo.seedOverride.intValue();
		}
		
		super.registerWorldChunkManager();
	}

}
