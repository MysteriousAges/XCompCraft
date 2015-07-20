package nil.simpledim.config;

import net.minecraft.world.WorldType;

public enum DimensionType {
	UNKNOWN("", null),
	OVERWORLD("overworld", WorldType.DEFAULT),
	SUPERFLAT("superflat", WorldType.FLAT),
	LARGE_BIOME("largebiome", WorldType.LARGE_BIOMES),
	AMPLIFIED("amplified", WorldType.AMPLIFIED),
	VOID("void", Config.VOID),
	SINGLE_BIOME("singlebiome", Config.SINGLE_BIOME),
	;
	
	private String name;
	private WorldType type;
	
	private DimensionType(String configTag, WorldType terrainType) {
		name = configTag;
		type = terrainType;
	}
	
	public boolean matches(String configTag) {
		configTag.replaceAll("[\\s]", "");
		return name.equals(configTag.toLowerCase());
	}
	
	public WorldType getWorldType() {
		return type;
	}
	
	public static DimensionType fromString(String typeStr) {
		for (DimensionType type : DimensionType.values()) {
			if (type.matches(typeStr)) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
