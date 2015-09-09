package nil.xcompcraft.config;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.world.biome.BiomeGenBase;
import nil.xcompcraft.LogHelper;

public class SimpleDimWorldFileParser {

	private static final String dimensionConfigEntryPattern = "(?<name>[A-z]+[0-9A-z]*)\\s*\\{\\s*"
				+ "(?=[^{]*?id\\s*:\\s*(?<dimensionId>-?\\d+)\\s*)"
				+ "(?=[^{]*?type\\s*:\\s*(?<type>[A-z]+))"
				+ "(?:[^{]*?generator\\s*:\\s*['\"](?<generator>.+)['\"])?"
				+ "(?:[^{]*?spawn\\s*:\\s*\\(?(?<spawnCoords>-?\\d+\\s*,\\s*\\d+\\s*,\\s*-?\\d+\\s*)\\)?)?"
				+ "(?:[^{]*?biome\\s*:\\s*(?<biome>[A-z]+))?"
				+ "(?:[^{]*?biomeList\\s*:\\s*\\[(?<biomeList>[A-z, ]+)])?"
				+ "(?:[^{]*?seed\\s*:\\s*(?<seed>(?:0x[0-9a-fA-F]+)|(?:-?\\d+)))?"
				+ "(?:[^{]*?loadSpawn\\s*:\\s*(?<loadSpawn>(?:true|false)))?"
				+ "(?:[^{]*)\\}";
	private Pattern dimensionPattern;
	
	private static final String teleporterConfigEntryPattern = "item (?<name>[A-z]+[0-9A-z_\\- ]*?)\\s*\\{\\s*"
				+ "(?=[^{]*?forDim(?:ension)?\\s*:\\s*(?<forDimension>[A-z]+[A-z0-9]*))"
				+ "(?=[^{]*?type\\s*:\\s*(?<type>[A-z]+))"
				+ "(?:[^{]*?colou?rs?\\s*:\\s*\\[(?<colour>(?:(?:0x[0-9a-f-F]+|-?[0-9]+)(?:, )*)+)])?"
				+ "(?:[^{]*?variants?\\s*:\\s*\\[(?<variant>[0-9, ]+))?"
				+ "(?:[^{]*?displayName\\s*:\\s*\"(?<displayName>[A-z0-9_\\- ]+)\")?"
				//+ "(?:[^{]*?useTime\\s*:\\s*(?<useTime>\\d+))?"
				+ "(?:[^{]*)\\}";
	private Pattern itemPattern;
	
	public SimpleDimWorldFileParser() {
		dimensionPattern = Pattern.compile(dimensionConfigEntryPattern);
		itemPattern = Pattern.compile(teleporterConfigEntryPattern);				
	}
	
	public List<DimensionInfo> parseDimensions(String contents) {
		List<DimensionInfo> foundDimensions = new LinkedList<DimensionInfo>();
		DimensionInfo dimensionInfo;
		Matcher dimensionMatcher = dimensionPattern.matcher(contents);
		while (dimensionMatcher.find()) {
			dimensionInfo = getDimensionInfoFromMatcher(dimensionMatcher);
			if (dimensionInfo.validateConfiguration()) {
				foundDimensions.add(dimensionInfo);
				LogHelper.info("Found configuration for " + dimensionInfo.name + " with ID " + dimensionInfo.dimensionId);
			}
			else {
				LogHelper.error("Configuration for " + dimensionInfo.name + " is invalid, and will be ignored!");
			}
		}
		return foundDimensions;
	}
	
	public DimensionInfo getDimensionInfoForEntry(String dimensionDescription) {
		Matcher matcher = dimensionPattern.matcher(dimensionDescription);
		return getDimensionInfoFromMatcher(matcher);
	}
	
	public List<TransportItemInfo> parseTransportItems(String contents) {
		List<TransportItemInfo> foundItems = new LinkedList<TransportItemInfo>();
		TransportItemInfo itemInfo;
		Matcher itemMatcher = itemPattern.matcher(contents);
		while (itemMatcher.find()) {
			itemInfo = getTransportItemInfoFromMatcher(itemMatcher);
			if (itemInfo.validateConfiguration()) {
				foundItems.add(itemInfo);
				LogHelper.info("Found item configuration for " + itemInfo.name + " for dimension " + itemInfo.forDimension);
			}
			else {
				LogHelper.error("Configuration for " + itemInfo.name + " is invalid, and will be ignored!");
			}
		}
		return foundItems;
	}
	
	public TransportItemInfo getTransportItemInfoForEntry(String dimensionDescription) {
		Matcher matcher = itemPattern.matcher(dimensionDescription);
		return getTransportItemInfoFromMatcher(matcher);
	}

	private DimensionInfo getDimensionInfoFromMatcher(Matcher matcher) {
		DimensionInfo info = new DimensionInfo();
		info.name = matcher.group("name");
		info.dimensionId = Integer.parseInt(matcher.group("dimensionId"));
		info.type = DimensionType.fromString(matcher.group("type"));
		
		String tmp = matcher.group("seed");
		if (tmp != null && !tmp.isEmpty()) {
			try {
				info.seedOverride = Long.decode(tmp).longValue();
			}
			catch (NumberFormatException e) {
				info.seedOverride = (long)tmp.hashCode();
			}
		}
		
		tmp = matcher.group("generator");
		if (tmp != null && !tmp.isEmpty()) {
			info.superflatGenerator = tmp;
		}
		
		tmp = matcher.group("biome");
		if (tmp != null && !tmp.isEmpty()) {
			info.biome = parseBiomeFromString(tmp);
		}
		
		tmp = matcher.group("biomeList");
		if (tmp != null && !tmp.isEmpty()) {
			info.biomeList = parseBiomeListFromString(tmp);
		}
		
		tmp = matcher.group("loadSpawn");
		if (tmp != null && !tmp.isEmpty()) {
			info.loadSpawn = Boolean.parseBoolean(tmp);
		}
		
		return info;
	}
	
	private TransportItemInfo getTransportItemInfoFromMatcher(Matcher matcher) {
		TransportItemInfo info = new TransportItemInfo();
		info.type = TransportItemType.fromString(matcher.group("type"));
		info.name = matcher.group("name");
		info.forDimension = matcher.group("forDimension");
		
		String tmp = matcher.group("variant");
		byte[] rawVariantData = null;
		if (tmp != null && !tmp.isEmpty()) {
			rawVariantData = parseVariantInfoFromString(tmp);
		}
		info.variantInfo = new byte[info.type.layerVariants.length];
		info.type.populateDefaultVariants(info.variantInfo, rawVariantData);
		
		tmp = matcher.group("colour");
		if (tmp != null && !tmp.isEmpty()) {
			info.layerColours = parseLayerColoursFromString(tmp);
		}
		
		tmp = matcher.group("displayName");
		if (tmp != null && !tmp.isEmpty()) {
			info.displayName = tmp;
		}
		else {
			info.displayName = info.name;
		}
		
		/*tmp = matcher.group("useTime");
		if (tmp != null && !tmp.isEmpty()) {
			info.useTime = Integer.parseInt(tmp);
		}*/
		
		return info;
	}

	private BiomeGenBase parseBiomeFromString(String tmp) {
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (biome.biomeName.replace(" ", "").equalsIgnoreCase(tmp)) {
				return biome;
			}
		}
		LogHelper.warn("Provided biome name '" + tmp + "' does not match any known biomes.");
		return null;
	}
	
	private BiomeGenBase[] parseBiomeListFromString(String tmp) {
		String[] biomeNameList = tmp.split("\\s*,\\s*");
		List<BiomeGenBase> biomeList = new LinkedList<BiomeGenBase>(); 
		
		for (String biomeName : biomeNameList) {
			BiomeGenBase biome = parseBiomeFromString(biomeName);
			if (biome != null) {
				biomeList.add(biome);
			}
		}
		
		if (biomeList.size() < biomeNameList.length) {
			LogHelper.warn("Could not match all biome names to actual biomes. Requested: " + biomeNameList.length + " Converted: " + biomeList.size());
		}

		return biomeList.toArray(new BiomeGenBase[biomeList.size()]);
	}

	private byte[] parseVariantInfoFromString(String tmp) {
		String[] variantInfo = tmp.split("\\s*,\\s*");
		byte[] variants = new byte[variantInfo.length];
		
		for (int index = 0; index < variants.length; ++index) {
			variants[index] = Byte.parseByte(variantInfo[index]);
		}
		
		return variants;
	}

	private int[] parseLayerColoursFromString(String tmp) {
		String[] colourData = tmp.split("\\s*,\\s*");
		int[] colours = new int[colourData.length];
		
		for (int index = 0; index < colours.length; ++index) {
			colours[index] = Integer.decode(colourData[index]).intValue();
		}
		
		return colours;
	}
}
