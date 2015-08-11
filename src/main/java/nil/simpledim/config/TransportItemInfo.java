package nil.simpledim.config;

import net.minecraft.nbt.NBTTagCompound;
import nil.simpledim.SimpleDim;

public class TransportItemInfo {

	private static final String TAG_DIM = "dim";
	private static final String TAG_NAME = "name";
	private static final String TAG_DISPLAY_NAME = "displayName";
	private static final String TAG_ICON_TYPE = "type";
	private static final String TAG_VARIANT_INFO = "variant";
	private static final String TAG_LAYER_COLOURS = "layerColours";
	
	public String name;
	public String forDimension;
	public String displayName;
	public TransportItemType type;
	public byte[] variantInfo;
	public int[] layerColours;
	
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
	
	public NBTTagCompound createNBTDescription() {
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.setString(TAG_NAME, name);
		tag.setString(TAG_DIM, forDimension);
		tag.setString(TAG_ICON_TYPE, type.toString());
		
		if (displayName != null) {
			tag.setString(TAG_DISPLAY_NAME, displayName);
		}
		if (variantInfo != null) {
			tag.setByteArray(TAG_VARIANT_INFO, variantInfo);
		}
		if (layerColours != null) {
			tag.setIntArray(TAG_LAYER_COLOURS, layerColours);
		}
		
		return tag;
	}
	
	public static TransportItemInfo createFromNBT(NBTTagCompound tag) {
		TransportItemInfo info = new TransportItemInfo();
		
		info.name = tag.getString(TAG_NAME);
		info.forDimension = tag.getString(TAG_DIM);
		info.type = TransportItemType.fromString(tag.getString(TAG_ICON_TYPE));

		if (tag.hasKey(TAG_DISPLAY_NAME)) {
			info.displayName = tag.getString(TAG_DISPLAY_NAME);
		}
		if (tag.hasKey(TAG_VARIANT_INFO)) {
			info.variantInfo = tag.getByteArray(TAG_VARIANT_INFO);
		}
		
		return info;
	}
}
