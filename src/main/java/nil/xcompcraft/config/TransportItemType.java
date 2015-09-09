package nil.xcompcraft.config;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import nil.xcompcraft.SimpleDim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum TransportItemType {
	UNKNOWN("?", new byte[] { 0 }),
	CARD("accessCard", new byte[] {1, 1, 1} ),
	POCKETWATCH("pocketwatch", new byte[] { 1, 2 }),
	;
	// Update if maximum number of layers changes.
	public static final int maxLayers = 3;
	
	public final String baseName;
	final byte[] layerVariants;
	@SideOnly(Side.CLIENT)
	private IIcon[][] icons;
	@SideOnly(Side.CLIENT)
	private static IIcon emptyLayer;
	
	private TransportItemType(String filenameBase, byte[] variants) {
		baseName = filenameBase;
		layerVariants = variants;
	}
	
	public int getNumberLayers() {
		return layerVariants.length;
	}
	
	public boolean hasVariants() {
		for (byte b : layerVariants) {
			if (b > 1) {
				return true;
			}
		}
		return false;
	}
	
	public byte getNumberLayerVariants(byte layer) {
		if (layer < 0 || layerVariants.length <= layer) {
			throw new IllegalArgumentException("TransportItemInfo.Type " + baseName + " does not contain a layer " + layer);
		}
		return layerVariants[layer];
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
	
	public static int getNumberTypes() {
		return TransportItemType.values().length - 1;
	}

	public void populateDefaultVariants(byte[] variantInfo, byte[] rawVariantData) {
		int rawLength = (rawVariantData != null) ? rawVariantData.length : 0;
		int index = 0;
		while (index < variantInfo.length && index < rawLength) {
			variantInfo[index] = rawVariantData[index];
			++index;
		}
		while (index < variantInfo.length) {
			variantInfo[index] = 0;
			++index;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerIcons(IIconRegister iconRegister) {
		for (TransportItemType type : TransportItemType.values()) {
			type.icons = new IIcon[type.layerVariants.length][];
			for (int layer = 0; layer < type.icons.length; ++layer) {
				type.icons[layer] = new IIcon[type.layerVariants[layer]];
				
				for (int variant = 0; variant < type.layerVariants[layer]; ++variant) {
					type.icons[layer][variant] = iconRegister.registerIcon(SimpleDim.NAME + ":" + type.baseName + "-" + layer + "-" + variant);
				}
			}
		}
		emptyLayer = iconRegister.registerIcon(SimpleDim.NAME + ":" + "empty");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getLayerIcon(int layer, byte variant) {
		if (layer < layerVariants.length && variant < layerVariants[layer]) {
			return icons[layer][variant];
		}
		else {
			return emptyLayer;
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getLayerIcon(int layer) {
		return icons[layer][0];
	}
}