package nil.xcompcraft.config;

import net.minecraft.world.WorldType;

public enum DimensionType {
	UNKNOWN("", null),
	OVERWORLD("overworld", WorldType.DEFAULT),
	SUPERFLAT("superflat", Config.SUPERFLAT_GENERATOR),
	LARGE_BIOME("largebiome", WorldType.LARGE_BIOMES),
	AMPLIFIED("amplified", WorldType.AMPLIFIED),
	VOID("void", Config.SUPERFLAT_GENERATOR),
	SINGLE_BIOME("singlebiome", Config.SINGLE_BIOME),
	ATG("atg", WorldType.parseWorldType("ATG"), true),
	FUNWORLD("fwg", WorldType.parseWorldType("FWG")),
	;
	
	private String name;
	private WorldType type;
	private boolean externalWorldType;
	
	private DimensionType(String configTag, WorldType terrainType) {
		this(configTag, terrainType, false);
	}
	
	private DimensionType(String configTag, WorldType terrainType, boolean external) {
		name = configTag;
		type = terrainType;
		externalWorldType = external;
	}
	
	public boolean matches(String configTag) {
		configTag.replaceAll("[\\s]", "");
		return name.equals(configTag.toLowerCase());
	}
	
	public WorldType getWorldType() {
		return type;
	}
	
	public boolean isExternalWorldType() {
		return externalWorldType;
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
