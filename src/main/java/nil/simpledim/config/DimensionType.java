package nil.simpledim.config;

public enum DimensionType {
	UNKNOWN(""),
	OVERWORLD("overworld"),
	VOID("void"),
	SUPERFLAT("superflat"),
	SINGLE_BIOME("singlebiome"),
	;
	
	private String name;
	private DimensionType(String configTag) {
		name = configTag;
	}
	
	public boolean matches(String configTag) {
		configTag.replaceAll("[\\s]", "");
		return name.equals(configTag.toLowerCase());
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
