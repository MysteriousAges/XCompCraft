package nil.simpledim.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nil.simpledim.LogHelper;
import nil.simpledim.SimpleDim;
import nil.simpledim.world.SimpleDimWorldProvider;
import nil.simpledim.world.type.SingleBiomeWorldType;
import nil.simpledim.world.type.VoidWorldType;

import org.apache.commons.io.FileUtils;

public class Config {
	
	public boolean dimensionSpawnIsLoaded;
	
	public static WorldType SINGLE_BIOME = new SingleBiomeWorldType();
	public static WorldType VOID = new VoidWorldType();
	
	public static String GENERAL = "General";
	
	private Configuration configuration;
	private Map<Integer, DimensionInfo> dimensionProperties;
	private Map<String, DimensionInfo> dimensionInfoByName;
	
	private Config() {
		dimensionProperties = new HashMap<Integer, DimensionInfo>();
		dimensionInfoByName = new HashMap<String, DimensionInfo>();
	}

	public static Config fromFile(File configFile) {
		Config config = new Config();
		
		config.configuration = new Configuration(configFile);
		config.configuration.load();
		config.parseFile();
		
		config.processDimensionFiles(configFile.getParentFile());
		
		return config;
	}

	public void update() {
		parseFile();
	}
	
	public DimensionInfo getDimensionInfoForWorld(int dimId) {
		return dimensionProperties.get(dimId);
	}
	
	public DimensionInfo getDimensionInfoForWorld(String name) {
		return dimensionInfoByName.get(name);
	}

	private void parseFile() {
		Property p;
		
		p = configuration.get(GENERAL, "keepDimensionSpawnLoaded", true);
		dimensionSpawnIsLoaded = p.getBoolean();
		
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}	
	
	private void processDimensionFiles(File path) {
		String dimDirPath = path.getPath() + File.separatorChar + SimpleDim.NAME;
		File dimDir = createConfigSubdirectoryIfNeeded(dimDirPath);
		List<DimensionInfo> infoList = findAndParseAllConfigsInFolder(dimDir);
		registerDimensions(infoList);
	}

	private void registerDimensions(List<DimensionInfo> infoList) {
		for (DimensionInfo info : infoList) {
			if (dimensionProperties.containsKey(info.dimensionId)) {
				LogHelper.error("Dimension " + info.name + " is configured to use a dimension ID already taken by SimpleDim. Fix your configs!");
				String message = String.format("Dimension %s failed to register dimension %d - already occupied by %d!",
						info.name, info.dimensionId, dimensionProperties.get(info.dimensionId).name);
				throw new DuplicateDimensionIdException(message);
			}
			else if (dimensionInfoByName.containsKey(info.name)) {
				DimensionInfo otherDim = dimensionInfoByName.get(info.name);
				LogHelper.error("Dimension" + info.dimensionId + " is configured to use a dimension name already specified in your configs. Please choose a unique name for either ID " + info.dimensionId + " or " + otherDim.dimensionId);
				String message = String.format("Dimension Id %d failed to register with name %s - already used by dimension %d!",
						info.dimensionId, info.name, otherDim.dimensionId);
				throw new DuplicateDimensionIdException(message);
			}
			else {
				dimensionProperties.put(info.dimensionId, info);
				dimensionInfoByName.put(info.name, info);
			}
		}
	}

	private List<DimensionInfo> findAndParseAllConfigsInFolder(File dimDir) {
		LogHelper.info("Searching for SimpleDim configuration files...");
		List<DimensionInfo> dimInfoList = new ArrayList<DimensionInfo>();
		SimpleDimWorldFileParser parser = new SimpleDimWorldFileParser();
		
		for (File file : dimDir.listFiles()) {
			if (file.getName().endsWith("cfg")) {
				LogHelper.info("Found " + file.getName());
				List<DimensionInfo> newInfo = parser.parseDimensionFile(file);
				dimInfoList.addAll(newInfo);
				LogHelper.info("Finished parsing " + newInfo.size() + " dimensions from " + file.getName());
			}
		}
		
		LogHelper.info("Finished searching for SimpleDim configs. Found a total of " + dimInfoList.size() + " dimension entries.");
		return dimInfoList;
	}

	private File createConfigSubdirectoryIfNeeded(String dimDirPath) {
		File dimDir = new File(dimDirPath);
		if (!dimDir.exists()) {
			dimDir.mkdir();
		}
		
		File dimReadme = new File(dimDirPath + File.separatorChar + "ConfigInfo.txt");
		if (!dimReadme.exists()) {
			copyDimInfoToDisk(dimReadme);
		}
		return dimDir;
	}

	private void copyDimInfoToDisk(File dimReadme) {
		URL source = SimpleDim.class.getResource("/assets/text/ConfigInfo.txt");
		try {
			FileUtils.copyURLToFile(source, dimReadme);
		} catch (IOException e) {
			LogHelper.error("Could not create dimension info file " + dimReadme.getPath());
		}
	}

	public void registerDimenions() {
		DimensionManager.registerProviderType(SimpleDimWorldProvider.WORLD_PROVIDER_ID, SimpleDimWorldProvider.class, dimensionSpawnIsLoaded);
		
		for (DimensionInfo info : dimensionProperties.values()) {
			DimensionManager.registerDimension(info.dimensionId, SimpleDimWorldProvider.WORLD_PROVIDER_ID);
		}
	}

	public List<String> getAllDimensionNames() {
		List<String> dimNames = new ArrayList<String>(dimensionProperties.size() + 3);
		dimNames.add(DimensionInfo.NAME_OVERWORLD);
		dimNames.add(DimensionInfo.NAME_NETHER);
		dimNames.add(DimensionInfo.NAME_END);
		for (DimensionInfo info : dimensionProperties.values()) {
			dimNames.add(info.name);
		}
		return dimNames;
	}
}
