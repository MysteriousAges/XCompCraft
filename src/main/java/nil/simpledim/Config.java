package nil.simpledim;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	
	public int dimensionId;
	public boolean dimensionSpawnIsLoaded; 
	
	public static String GENERAL = "General";
	
	private Configuration configuration;

	public static Config fromFile(File configFile) {
		Config config = new Config();
		
		config.configuration = new Configuration(configFile);
		config.configuration.load();
		config.parseFile();
		
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

}
