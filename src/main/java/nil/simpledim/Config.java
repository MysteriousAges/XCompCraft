package nil.simpledim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nil.simpledim.config.DimensionInfo;
import nil.simpledim.config.SimpleDimWorldFileParser;

import org.apache.commons.io.FileUtils;

public class Config {
	
	public int dimensionId;
	public boolean dimensionSpawnIsLoaded; 
	
	public static String GENERAL = "General";
	
	private Configuration configuration;
	private Map<Integer, DimensionInfo> dimensionProperties;

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
		File dimDir = new File(dimDirPath);
		if (!dimDir.exists()) {
			dimDir.mkdir();
		}
		
		File dimReadme = new File(dimDirPath + File.separatorChar + "ConfigInfo.txt");
		if (!dimReadme.exists()) {
			copyDimInfoToDisk(dimReadme);
		}
		
		List<DimensionInfo> dimfoList = new ArrayList<DimensionInfo>();
		SimpleDimWorldFileParser parser = new SimpleDimWorldFileParser();
		for (File file : dimDir.listFiles()) {
			if (file.getName().endsWith("cfg")) {
				dimfoList.addAll(parser.parseDimensionFile(file));
			}
		}
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
