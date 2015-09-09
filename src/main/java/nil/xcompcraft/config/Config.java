package nil.xcompcraft.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import nil.xcompcraft.LogHelper;
import nil.xcompcraft.SimpleDim;
import nil.xcompcraft.util.DuplicateTransportItemNameException;
import nil.xcompcraft.world.SimpleDimWorldProvider;
import nil.xcompcraft.world.type.SingleBiomeWorldType;
import nil.xcompcraft.world.type.VoidWorldType;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.registry.GameRegistry;

public class Config {
	
	public boolean dimensionSpawnIsLoaded;
	public int defaultItemUseLength;
	
	public static WorldType SINGLE_BIOME = new SingleBiomeWorldType();
	public static WorldType VOID = new VoidWorldType();
	
	public static String GENERAL = "General";
	
	private Configuration configuration;
	private File configFileRoot;
	private Map<Integer, DimensionInfo> dimensionProperties;
	private Map<String, DimensionInfo> dimensionInfoByName;
	private Map<String, TransportItemInfo> transportItemByName;
	private List<ItemStack> transportItemStacks;
	
	private Config() {
		dimensionProperties = new HashMap<Integer, DimensionInfo>();
		dimensionInfoByName = new HashMap<String, DimensionInfo>();
		transportItemByName = new HashMap<String, TransportItemInfo>();
		transportItemStacks = new ArrayList<ItemStack>();
	}

	public static Config fromFile(File configFile) {
		Config config = new Config();
		config.configFileRoot = configFile;
		
		config.configuration = new Configuration(configFile);
		config.configuration.load();
		config.parseFile();
		
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
		
		p = configuration.get(GENERAL, "defaultItemUseLength", 5);
		defaultItemUseLength = p.getInt();
		
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}	
	
	public void processDimensionFiles() {
		String dimDirPath = configFileRoot.getParent() + File.separatorChar + SimpleDim.NAME;
		File dimDir = createConfigSubdirectoryIfNeeded(dimDirPath);
		findAndParseAllConfigsInFolder(dimDir);
	}

	private void registerDimensionInfo(DimensionInfo info) {
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
	
	private void registerItemInfo(TransportItemInfo info) {
		if (transportItemByName.containsKey(info.name)) {
			LogHelper.error("Transport Item " + info.name + " already registered! Please choose a new name for the item for the dimension " + info.forDimension);
			String message = String.format("Transport Item name %s is already used with dimension %s - please choose a new name for the item used with %s",
					info.name, transportItemByName.get(info.name).forDimension, info.forDimension);
			throw new DuplicateTransportItemNameException(message);
		}
		else {
			transportItemByName.put(info.name, info);
			ItemStack stack = new ItemStack(SimpleDim.modRef.teleporterItem);
			stack.setTagCompound(info.getItemNBTIdentifier());
			transportItemStacks.add(stack);
			GameRegistry.registerCustomItemStack(info.name, stack);
		}
	}

	private void findAndParseAllConfigsInFolder(File dimDir) {
		LogHelper.info("Searching for SimpleDim configuration files...");
		int dimsFound = 0;
		int itemsFound = 0;
		SimpleDimWorldFileParser parser = new SimpleDimWorldFileParser();
		
		for (File file : dimDir.listFiles()) {
			if (file.getName().endsWith(".cfg")) {
				LogHelper.info("Found " + file.getName());
				String fileContents;
				try {
					fileContents = FileUtils.readFileToString(file);
					List<DimensionInfo> infoList = parser.parseDimensions(fileContents);
					for (DimensionInfo dimInfo : infoList) {
						++dimsFound;
						registerDimensionInfo(dimInfo);
					}
					
					List<TransportItemInfo> itemList = parser.parseTransportItems(fileContents);
					for (TransportItemInfo itemInfo : itemList) {
						++itemsFound;
						registerItemInfo(itemInfo);
					}
					LogHelper.info("Finished parsing " + infoList.size() + " dimensions and " + itemList.size() + " items from " + file.getName());
				} catch (IOException e) {
					LogHelper.error("Encountered an error parsing " + file.getName());
					e.printStackTrace();
					LogHelper.error("Unable to parse file. Skipping.");
				}
			}
		}
		
		LogHelper.info(
				String.format("Finished searching for SimpleDim configs. Found a total of %d dimension entries and %d transport items.",
				dimsFound, itemsFound));
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
	
	public List<TransportItemInfo> getAllTransportItemInfo() {
		return Collections.unmodifiableList(new ArrayList<TransportItemInfo>(transportItemByName.values()));
	}

	public List<ItemStack> getAllTransportItemStacks() {
		return Collections.unmodifiableList(transportItemStacks);
	}

	public TransportItemInfo getTransportItemInfoForName(String name) {
		return transportItemByName.get(name);
	}
}
