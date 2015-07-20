package nil.simpledim.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import net.minecraft.world.biome.BiomeGenBase;
import nil.simpledim.LogHelper;

public class SimpleDimWorldFileParser {
	
	/* The src Regex for easy copypasta & tweaking purporses.
		(?<name>[A-z]+)\s*\{\s*
		(?=[^{]*?id\s*:\s*(?<dimensionId>-?\d+)\s*)
		(?=[^{]*?type\s*:\s*(?<type>[A-z]+))
		(?:[^{]*?generator\s*:\s*['"](?<generator>.+)['"])?
		(?:[^{]*?spawn\s*:\s*\(?(?<spawnCoords>-?\d+\s*,\s*\d+\s*,\s*-?\d+\s*)\)?)?
		(?:[^{]*?biome\s*:\s*(?<biome>[A-z]+))?
		(?:[^{]*?biomeList\s*:\s*\[(?<biomeList>[A-z, ]+)])?
		(?:[^{]*?seed\s*:\s*(?<seed>(?:0x\d+)|(?:-?\d+)))?
		(?:[^{])*}
	*/
	private static String theRegex = "(?<name>[A-z]+)\\s*\\{\\s*"
				+ "(?=[^{]*?id\\s*:\\s*(?<dimensionId>-?\\d+)\\s*)"
				+ "(?=[^{]*?type\\s*:\\s*(?<type>[A-z]+))"
				+ "(?:[^{]*?generator\\s*:\\s*['\"](?<generator>.+)['\"])?"
				+ "(?:[^{]*?spawn\\s*:\\s*\\(?(?<spawnCoords>-?\\d+\\s*,\\s*\\d+\\s*,\\s*-?\\d+\\s*)\\)?)?"
				+ "(?:[^{]*?biome\\s*:\\s*(?<biome>[A-z]+))?"
				+ "(?:[^{]*?biomeList\\s*:\\s*\\[(?<biomeList>[A-z, ]+)])?"
				+ "(?:[^{]*?seed\\s*:\\s*(?<seed>(?:0x\\d+)|(?:-?\\d+)))?"
				+ "(?:[^{])*?\\}";
	private Pattern pattern;
	
	public SimpleDimWorldFileParser() {
		pattern = Pattern.compile(theRegex);
	}
	
	public List<DimensionInfo> parseDimensionFile(File file) {
		List<DimensionInfo> fileDimensions = new ArrayList<DimensionInfo>();
		try {
			pattern = Pattern.compile(theRegex);
			String contents = FileUtils.readFileToString(file);
			Matcher matcher = pattern.matcher(contents);
			while (matcher.find()) {
				fileDimensions.add(getDimensionInfoFromMatcher(matcher));
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return fileDimensions;
	}
	
	public DimensionInfo getDimensionInfoForEntry(String dimensionDescription) {
		Matcher matcher = pattern.matcher(dimensionDescription);
		return getDimensionInfoFromMatcher(matcher);
	}

	private DimensionInfo getDimensionInfoFromMatcher(Matcher matcher) {
		DimensionInfo info = new DimensionInfo();
		info.name = matcher.group("name");
		info.dimensionId = Integer.parseInt(matcher.group("dimensionId"));
		info.type = DimensionType.fromString(matcher.group("type"));
		
		String tmp = matcher.group("seed");
		if (tmp != null && !tmp.isEmpty()) {
			info.seedOverride = Integer.parseInt(tmp);
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
}
