package nil.simpledim.world;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import nil.simpledim.SimpleDim;

public class SimpleDimWorldProvider extends WorldProvider {

	public static final int WORLD_PROVIDER_ID = 0x946731;
	
	protected WorldType worldType;
	
	public SimpleDimWorldProvider() {
		super();
		worldType = SimpleDim.modRef.worldType;
	}
	
	@Override
	public String getDimensionName() {
		return "SimpleDim";
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return BiomeGenBase.plains;
	}
	
	@Override
	public IChunkProvider createChunkGenerator() {
		return worldType.getChunkGenerator(worldObj, field_82913_c);
	}

	@Override
	protected void registerWorldChunkManager() {
        this.worldChunkMgr = worldType.getChunkManager(worldObj);
	}

	@Override
	public float getCloudHeight() {
		return worldType.getCloudHeight();
	}

	@Override
	public int getAverageGroundLevel() {
		return worldType.getMinimumSpawnHeight(worldObj);
	}

	@Override
	public boolean getWorldHasVoidParticles() {
		return worldType.hasVoidParticles(hasNoSky);
	}

	@Override
	public double getVoidFogYFactor() {
		return worldType.voidFadeMagnitude();
	}
	
	@Override
    public ChunkCoordinates getRandomizedSpawnPoint()
    {
        ChunkCoordinates chunkcoordinates = new ChunkCoordinates(this.worldObj.getSpawnPoint());

        boolean isAdventure = worldObj.getWorldInfo().getGameType() == GameType.ADVENTURE;
        int spawnFuzz = worldType.getSpawnFuzz();
        int spawnFuzzHalf = spawnFuzz / 2;

        if (!hasNoSky && !isAdventure && net.minecraftforge.common.ForgeModContainer.defaultHasSpawnFuzz)
        {
            chunkcoordinates.posX += this.worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
            chunkcoordinates.posZ += this.worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
            chunkcoordinates.posY = this.worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
        }

        return chunkcoordinates;
    }
}
