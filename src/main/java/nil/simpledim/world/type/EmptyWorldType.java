package nil.simpledim.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;

public class EmptyWorldType extends WorldType {

	public EmptyWorldType() {
		super("Empty World");
	}

	@Override
	public boolean hasVoidParticles(boolean flag) {
		return false;
	}

	@Override
	public int getSpawnFuzz() {
		return 1;
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderFlat(world, world.getSeed(), false, "2;1x0");
	}

	@Override
	public float getCloudHeight() {
		return 0f;
	}
}
