package nil.simpledim.config;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

public class DimensionInfo {
	
	public static final String NAME_END = "end";
	public static final String NAME_NETHER = "nether";
	public static final String NAME_OVERWORLD = "overworld";

	public String name;
	public int dimensionId;
	public DimensionType type;
	public Long seedOverride;
	public ChunkCoordinates spawnPoint;
	public String superflatGenerator;
	public BiomeGenBase biome;
	public BiomeGenBase[] biomeList;
	public boolean loadSpawn;
	
	public DimensionInfo() {
		dimensionId = 0;
		type = DimensionType.UNKNOWN;
		seedOverride = null;
	}
	
	public boolean validateConfiguration() {
		if (type == DimensionType.UNKNOWN) {
			return false;
		}
		if (!isNameValid(name) || !isValidDimensionId(dimensionId)) {
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
		return dimensionId <= -1 || 1 <= dimensionId;
	}
	
	private boolean isNameValid(String name) {
		if (name == null || name.equalsIgnoreCase(NAME_OVERWORLD) 
				|| name.equalsIgnoreCase(NAME_NETHER) || name.equalsIgnoreCase(NAME_END)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Dimension '%s' dimId: %d is type %s", name, dimensionId, type.name());
	}
	
	public WorldServer getWorldServer() {
		return DimensionManager.getWorld(dimensionId);
	}
}
