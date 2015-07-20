package nil.simpledim;

import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import nil.simpledim.config.Config;
import nil.simpledim.item.TeleporterItem;
import nil.simpledim.world.SimpleDimWorldProvider;
import nil.simpledim.world.type.EmptyWorldType;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = SimpleDim.NAME,
		version = "@VERSION@" )
public class SimpleDim {
	public static final String NAME = "SimpleDim";
	
	@Instance(NAME)
	public static SimpleDim modRef;
	
	public Config config;
	public WorldType worldType;
	public TeleporterItem teleporterItem;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = Config.fromFile(event.getSuggestedConfigurationFile());
		
		teleporterItem = new TeleporterItem();
		GameRegistry.registerItem(teleporterItem, "teleporterItem");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//worldType = new SingleBiomeWorldType();
		worldType = new EmptyWorldType();
	}
	
	@EventHandler
	public void postInit(FMLInitializationEvent event) {
		DimensionManager.registerProviderType(SimpleDimWorldProvider.WORLD_PROVIDER_ID, SimpleDimWorldProvider.class, true);//config.dimensionSpawnIsLoaded);
		DimensionManager.registerDimension(config.dimensionId, SimpleDimWorldProvider.WORLD_PROVIDER_ID);
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {

	}
	
	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		
	}
}
