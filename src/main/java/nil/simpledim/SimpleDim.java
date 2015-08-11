package nil.simpledim;

import nil.simpledim.command.EnhancedTeleportCommand;
import nil.simpledim.config.Config;
import nil.simpledim.item.TeleporterItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = SimpleDim.NAME,
		name = SimpleDim.NAME,
		version = "@VERSION@" )
public class SimpleDim {
	public static final String NAME = "SimpleDim";
	
	@Instance(NAME)
	public static SimpleDim modRef;
	private EventListener eventListener;
	public NetworkHandler networkHandler;
	
	public Config config;
	public TeleporterItem teleporterItem;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = Config.fromFile(event.getSuggestedConfigurationFile());
		config.registerDimenions();
		
		teleporterItem = new TeleporterItem();
		GameRegistry.registerItem(teleporterItem, "teleporterItem");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
	}
	
	@EventHandler
	public void postInit(FMLInitializationEvent event) {
		eventListener = new EventListener();
		FMLCommonHandler.instance().bus().register(eventListener);
		networkHandler = NetworkHandler.createNetworkHandler();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new EnhancedTeleportCommand());
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		
	}
	
	public static Config getConfig() {
		return modRef.config;
	}
}
