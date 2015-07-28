package nil.simpledim.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class SimpleDimTeleporter extends Teleporter {
	
	private static Map<Integer, SimpleDimTeleporter> teleporterCache = new HashMap<Integer, SimpleDimTeleporter>();
	
	public static SimpleDimTeleporter forWorldServer(WorldServer worldServer) {
		if (teleporterCache.containsKey(worldServer.provider.dimensionId)) {
			return teleporterCache.get(worldServer.provider.dimensionId);
		}
		else {
			SimpleDimTeleporter teleporter = new SimpleDimTeleporter(worldServer);
			teleporterCache.put(worldServer.provider.dimensionId, teleporter);
			return teleporter;
		}
	}

    private SimpleDimTeleporter(WorldServer worldServer)
    {
    	super(worldServer);
    }

    public void placeInPortal(Entity entity, double x, double y, double z, float yaw)
    {
    	ChunkCoordinates destination = worldServerInstance.getSpawnPoint();
    	destination.posX += 0.5;
    	destination.posZ += 0.5;
    	
    	destination = getFinalSpawnCoords(destination);
    	entity.setLocationAndAngles(destination.posX, destination.posY, destination.posZ, entity.rotationYaw, 0f);
    	// Clear movement speed
    	entity.motionX = entity.motionY = entity.motionZ = 0;
    }

    // Gets the final spawn location, adjusting for terrain.
    private ChunkCoordinates getFinalSpawnCoords(ChunkCoordinates position) {
    	if (isBlocksSuitableForSpawn(position.posX, position.posY, position.posZ)) {
    		return position;
    	}
    	
    	int downwardOffset = Integer.MAX_VALUE;
    	for (int i = position.posY - 1; i >= 0; --i) {
    		if (isBlocksSuitableForSpawn(position.posX, i, position.posZ)) {
    			downwardOffset = position.posY - (position.posY - i);
    			break;
    		}
    	}
    	
    	int upwardOffset = Integer.MAX_VALUE;
    	for (int i = position.posY + 1; i < 255; ++i) {
    		if (isBlocksSuitableForSpawn(position.posX, i, position.posZ)) {
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
    		worldServerInstance.setBlock(position.posX, position.posY - 1, position.posZ, Blocks.stone);
    		worldServerInstance.setBlock(position.posX, position.posY, position.posZ, Blocks.air);
    		worldServerInstance.setBlock(position.posX, position.posY + 1, position.posZ, Blocks.air);
    	}
    	
		return position;
	}
    
    private boolean isBlocksSuitableForSpawn(int x, int y, int z) {
    	Block[] blockCache = new Block[3];
    	blockCache[0] = worldServerInstance.getBlock(x, y - 1, z);
    	blockCache[1] = worldServerInstance.getBlock(x, y, z);
    	blockCache[2] = worldServerInstance.getBlock(x, y + 1, z);
    	
    	return blockCache[0].isSideSolid(worldServerInstance, x, y - 1, z, ForgeDirection.UP)
    			&& blockCache[1].isAir(worldServerInstance, x, y, z)
    			&& blockCache[2].isAir(worldServerInstance, x, y + 1, z);
    }

	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw)
    {
    	return false;
    }

    public boolean makePortal(Entity entity)
    {
    	// Do nothing. We don't care.
        return true;
    }

    public void removeStalePortalLocations(long worldTime)
    {
    	// Do nothing. We don't care.
    }
}
