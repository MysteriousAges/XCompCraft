package nil.xcompcraft.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import nil.xcompcraft.world.SingleBiomeWorldChunkManager;

public class SingleBiomeWorldType extends WorldType {

	public SingleBiomeWorldType() {
		super("Single Biome");
	}

	@Override
	public int getSpawnFuzz() {
		return 1;
	}

	@Override
	public WorldChunkManager getChunkManager(World world) {
		return new SingleBiomeWorldChunkManager(world);
	}
}
