package nil.simpledim.config;

import nil.simpledim.SimpleDim;

public class TransportItemInfo {

	public String name;
	public TransportItemType type;
	public byte[] variantInfo;
	public int[] layerColours;
	public String forDimension;
	public String displayName;
	
	public TransportItemInfo() {
		type = TransportItemType.UNKNOWN;
	}
	
	public boolean validateConfiguration() {
		if (type == TransportItemType.UNKNOWN) {
			return false;
		}
		if (name == null) {
			return false;
		}
		if (variantInfo.length != type.getNumberLayers() || (0 < layerColours.length && layerColours.length < type.getNumberLayers())) {
			return false;
		}
		for (int index = 0; index < variantInfo.length; ++index) {
			if (variantInfo[index] >= type.layerVariants[index]) {
				return false;
			}
		}
		if (SimpleDim.getConfig().getDimensionInfoForWorld(forDimension) == null) {
			return false;
		}		

		return true;
	}
	
	public String[] getLayerIconNames() {
		String[] names = new String[type.getNumberLayers()];
		
		for (byte index = 0; index < names.length; ++index) {
			names[index] = type.baseName + "-" + index + ((type.getNumberLayerVariants(index) > 1) ? ("-" + variantInfo[index]) : "");
		}
		
		return names;
	}
	
	public void setColourForLayer(byte layer, int colour) {
		if (layer < layerColours.length) {
			for (byte index = layer; index < layerColours.length; ++index) {
				layerColours[index] = colour;
			}
		}
		else {
			throw new IllegalArgumentException(type.toString() + " does not contain a layer " + layer);
		}
	}
}
