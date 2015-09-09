package nil.xcompcraft.config;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import nil.xcompcraft.SimpleDim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransportItemInfo {

	private static final String TAG_DIM = "dim";
	private static final String TAG_NAME = "name";
	private static final String TAG_DISPLAY_NAME = "displayName";
	private static final String TAG_ICON_TYPE = "type";
	private static final String TAG_VARIANT_INFO = "variant";
	private static final String TAG_LAYER_COLOURS = "layerColours";
	private static final String TAG_ITEM_IDENTIFIER = "tii_name";
	
	public String name;
	public String forDimension;
	public String displayName;
	public TransportItemType type;
	public byte[] variantInfo;
	public int[] layerColours;
	public int useTime;
	
	public TransportItemInfo() {
		type = TransportItemType.UNKNOWN;
		useTime = SimpleDim.getConfig().defaultItemUseLength;
	}
	
	public boolean validateConfiguration() {
		if (type == TransportItemType.UNKNOWN) {
			return false;
		}
		if (name == null) {
			return false;
		}
		if (variantInfo.length != type.getNumberLayers() || !(0 < layerColours.length && layerColours.length <= type.getNumberLayers())) {
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
	
	/**
	 * Used for syncronizing TransportItemInfo across the network.
	 * @return
	 */
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
	
	/**
	 * Used for synchronizing TransportItemInfo across the network.
	 * @param tag
	 * @return
	 */
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
	
	public static TransportItemInfo getFromItemStack(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		return getForItemname(tag.getString(TAG_ITEM_IDENTIFIER));
	}
	
	public static TransportItemInfo getForItemname(String name) {
		return SimpleDim.getConfig().getTransportItemInfoForName(name);
	}
	
	public NBTTagCompound getItemNBTIdentifier() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(TAG_ITEM_IDENTIFIER, name);
		return tag;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconForPass(int renderPass) {
		return type.getLayerIcon(renderPass, (renderPass < variantInfo.length) ? variantInfo[renderPass] : 0);
	}

	public int getColourForLayer(int renderPass) {
		if (layerColours != null) {
			if (renderPass < layerColours.length) {
				return layerColours[renderPass];
			}
			else {
				return layerColours[layerColours.length - 1];
			}
		}
		return 0xFFFFFF;
	}
}
