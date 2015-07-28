package nil.simpledim.util;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class EntityDimensionalTransportHandler {

	// Large laundry list of items used to move a player from one dimension to the other.
	@SuppressWarnings("unchecked")
	public static void teleportPlayerToCoords(EntityPlayerMP player, WorldServer targetWorld, double x, double y, double z) {
		WorldServer originWorld = (WorldServer)player.worldObj;
		ServerConfigurationManager serverConfigManager = player.mcServer.getConfigurationManager();
		
		player.dimension = targetWorld.provider.dimensionId;
		player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, targetWorld.difficultySetting, targetWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
		originWorld.removeEntity(player);
		player.isDead = false;
		player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
		targetWorld.spawnEntityInWorld(player);
		player.setWorld(targetWorld);
		serverConfigManager.func_72375_a(player, originWorld);
		player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		player.theItemInWorldManager.setWorld(targetWorld);
		serverConfigManager.updateTimeAndWeatherForPlayer(player, targetWorld);
		serverConfigManager.syncPlayerInventory(player);
		
		// Curse you, type erasure. Clunky casting, ahoy.
		for (PotionEffect potion : (Collection<PotionEffect>)player.getActivePotionEffects()) {
			player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potion));
		}
		
		player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, originWorld.provider.dimensionId, targetWorld.provider.dimensionId);
	}
	
}
