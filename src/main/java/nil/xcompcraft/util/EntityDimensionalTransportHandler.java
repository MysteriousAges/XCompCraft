package nil.xcompcraft.util;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
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

	public static void teleportPlayerToCoordsSafe(EntityPlayerMP player, WorldServer targetWorld, ChunkCoordinates position) {
		position = getFinalSpawnCoords(targetWorld, position);
		teleportPlayerToCoords(player, targetWorld, position.posX, position.posY, position.posZ);
	}

    // Gets the final spawn location, adjusting for terrain.
    public static ChunkCoordinates getFinalSpawnCoords(WorldServer worldServer, ChunkCoordinates position) {
    	if (isBlocksSuitableForSpawn(worldServer, position.posX, position.posY, position.posZ)) {
    		return position;
    	}
    	else if (worldServer.getBlock(position.posX, position.posY, position.posZ) instanceof BlockBed) {
    		ChunkCoordinates bedExitPos = BlockBed.func_149977_a(worldServer, position.posX, position.posY, position.posZ, 0);//.getSafeExitLocation(worldServer, position.posX, position.posY, position.posZ, 0);
    		bedExitPos.posY += 1;
    		return bedExitPos;
    	}
    	
    	int downwardOffset = Integer.MAX_VALUE;
    	for (int i = position.posY - 1; i >= 0; --i) {
    		if (isBlocksSuitableForSpawn(worldServer, position.posX, i, position.posZ)) {
    			downwardOffset = position.posY - i;
    			break;
    		}
    	}
    	
    	int upwardOffset = Integer.MAX_VALUE;
    	for (int i = position.posY + 1; i < 255; ++i) {
    		if (isBlocksSuitableForSpawn(worldServer, position.posX, i, position.posZ)) {
    			upwardOffset = i - position.posY;
    			break;
    		}
    	}
    	
    	if (downwardOffset < upwardOffset) {
    		position.posY -= downwardOffset;
    	}
    	else if (upwardOffset < downwardOffset) {
    		position.posY += upwardOffset;
    	}
    	else if (upwardOffset == Integer.MAX_VALUE && downwardOffset == Integer.MAX_VALUE)  {
    		// Fuuuuuuuuuuuuuuuck it.
    		worldServer.setBlock(position.posX, position.posY - 1, position.posZ, Blocks.stone);
    		worldServer.setBlock(position.posX, position.posY, position.posZ, Blocks.air);
    		worldServer.setBlock(position.posX, position.posY + 1, position.posZ, Blocks.air);
    	}
    	
		return position;
	}
    
    public static boolean isBlocksSuitableForSpawn(WorldServer worldServer, int x, int y, int z) {
    	Block[] blockCache = new Block[3];
    	blockCache[0] = worldServer.getBlock(x, y - 1, z);
    	blockCache[1] = worldServer.getBlock(x, y, z);
    	blockCache[2] = worldServer.getBlock(x, y + 1, z);
    	
    	return blockCache[0].isSideSolid(worldServer, x, y, z, ForgeDirection.UP) && !blockCache[1].getMaterial().isOpaque() && !blockCache[2].getMaterial().isOpaque();
    }
}
