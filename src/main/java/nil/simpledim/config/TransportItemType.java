package nil.simpledim.config;

public enum TransportItemType {
	UNKNOWN("?", new byte[] { 0 }),
	CARD("accessCard", new byte[] {1, 1, 1} ),
	POCKETWATCH("pocketwatch", new byte[] { 1, 2 }),
	;
	
	public final String baseName;
	final byte[] layerVariants;
	
	private TransportItemType(String filenameBase, byte[] variants) {
		baseName = filenameBase;
		layerVariants = variants;
	}
	
	public int getNumberLayers() {
		return layerVariants.length;
	}
	
	public byte getNumberLayerVariants(byte layer) {
		if (layer < 0 || layerVariants.length <= layer) {
			throw new IllegalArgumentException("TransportItemInfo.Type " + baseName + " does not contain a layer " + layer);
		}
		return layerVariants[layer];
	}
	
	public boolean hasVariants() {
		for (byte b : layerVariants) {
			if (b > 1) {
				return true;
			}
		}
		return false;
	}
	
	public boolean matches(String configTag) {
		configTag.replaceAll("[\\s]", "");
		return baseName.equalsIgnoreCase(configTag);
	}

	public static TransportItemType fromString(String group) {
		for (TransportItemType type : TransportItemType.values()) {
			if (type.matches(group)) {
				return type;
			}
		}
		return UNKNOWN;
	}
}