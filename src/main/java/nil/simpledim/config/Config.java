package nil.simpledim.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nil.simpledim.LogHelper;
import nil.simpledim.SimpleDim;

import org.apache.commons.io.FileUtils;

public class Config {
	
	public int dimensionId;
	public boolean dimensionSpawnIsLoaded; 
	
	public static String GENERAL = "General";
	
	private Configuration configuration;
	private Map<Integer, DimensionInfo> dimensionProperties;
	
	private Config() {
		dimensionProperties = new HashMap<Integer, DimensionInfo>();
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

	private void parseFile() {
		Property p = configuration.get(GENERAL, "dimensionId", 2);
		dimensionId = p.getInt();
		if (dimensionId <= -1 && 1 <= dimensionId) {
			throw new RuntimeException("SimpleDim cannot be set to dimension IDs -1, 0, or 1!");
		}
		
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
			if (!dimensionProperties.containsKey(info.dimensionId)) {
				dimensionProperties.put(info.dimensionId, info);
			}
			else {
				LogHelper.error("Dimension " + info.name + " is configured to use a dimension ID already taken by SimpleDim. Fix your configs!");
				String message = String.format("Dimension %s failed to register dimension %d - already occupied by %d!",
						info.name, info.dimensionId, dimensionProperties.get(info.dimensionId).name);
				throw new DuplicateDimensionIdException(message);
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

}
