package nil.simpledim.config;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;

public class DimensionInfo {

	public String name;
	public int dimensionId;
	public DimensionType type;
	public Integer seedOverride;
	public ChunkCoordinates spawnPoint;
	public String superflatGenerator;
	public BiomeGenBase biome;
	public BiomeGenBase[] biomeList;
	
	public DimensionInfo() {
		dimensionId = 0;
		type = DimensionType.UNKNOWN;
		seedOverride = null;
	}
	
	public boolean validateConfiguration() {
		if (type == DimensionType.UNKNOWN) {
			return false;
		}
		if (name == null || !isValidDimensionId(dimensionId)) {
			return false;
		}
		else if (type == DimensionType.SUPERFLAT) {
			return superflatGenerator != null && !superflatGenerator.isEmpty();
		}
		else if (type == DimensionType.VOID) {
			return true;
		}
		else if (type == DimensionType.SINGLE_BIOME) {
			return biome != null;
		}
		else if (type == DimensionType.OVERWORLD) {
			return true;
		}
		return true;
	}
	
	private boolean isValidDimensionId(int dimensionId) {
		return dimensionId < -1 | 1 < dimensionId;
	}
	
	@Override
	public String toString() {
		return String.format("Dimension '%s' dimId: %d is type %s", name, dimensionId, type.name());
	}
}
