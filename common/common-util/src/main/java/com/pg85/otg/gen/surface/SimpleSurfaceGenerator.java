package com.pg85.otg.gen.surface;

import com.pg85.otg.config.settings.biome.SurfaceSettings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.interfaces.ISurfaceGeneratorNoiseProvider;
import com.pg85.otg.interfaces.ISurfaceGenerator;
import com.pg85.otg.util.gen.ChunkBuffer;
import com.pg85.otg.util.gen.GeneratingChunk;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

public class SimpleSurfaceGenerator implements ISurfaceGenerator
{
	@Override
	public LocalMaterialData getSurfaceBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, SurfaceSettings surfaceSettings, int xInWorld, int yInWorld, int zInWorld)
	{	
		return surfaceSettings.getSurfaceBlockReplaced(yInWorld);
	}
	
	@Override
	public LocalMaterialData getGroundBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, SurfaceSettings surfaceSettings, int xInWorld, int yInWorld, int zInWorld)
	{
		return surfaceSettings.getGroundBlockReplaced(yInWorld);
	}
	
	@Override
	public void spawn(long worldSeed, GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, IBiome biome, int xInWorld, int zInWorld)
	{
		spawnColumn(worldSeed, null, generatingChunk, chunkBuffer, biome, xInWorld, zInWorld);
	}

	// net.minecraft.world.biome.Biome.generateBiomeTerrain
	protected void spawnColumn(long worldSeed, MultipleLayersSurfaceGeneratorLayer layer, GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, IBiome biome, int xInWorld, int zInWorld)
	{
		int internalX = xInWorld & 0xf;
		int internalZ = zInWorld & 0xf;
		SurfaceSettings surfaceSettings = biome.getBiomeSettings().getSurfaceSettings();
		// Used to create a variable depth ground layer per column
		int biomeBlocksNoise = (int) (generatingChunk.getNoise(internalX, internalZ) / 3.0D + 3.0D + generatingChunk.random.nextDouble() * 0.25D);

		// Bedrock on the ceiling
		if (surfaceSettings.getBlockSettings().isCeilingBedrock())
		{
			// Moved one block lower to fix lighting issues
			chunkBuffer.setBlock(internalX, generatingChunk.getWorldHeight().getXBelowMax(2), internalZ, surfaceSettings.getBedrockBlockReplaced(generatingChunk.getWorldHeight().getXBelowMax(2)));
		}

		// Traverse down the block column to place bedrock, ground and surface blocks
		
		int groundLayerDepth = -1;
		LocalMaterialData currentSurfaceBlock = null;
		boolean useWaterForSurface = false;
		boolean useIceForSurface = false;
		boolean useAirForSurface = false;
		boolean useLayerSurfaceBlockForSurface = true;
		boolean useBiomeStoneBlockForGround = false;
		boolean useLayerGroundBlockForGround = true;
		boolean useSandStoneForGround = false;
		boolean biomeGroundBlockIsSand = surfaceSettings.getDefaultGroundBlock().isMaterial(LocalMaterials.SAND);
		boolean layerGroundBlockIsSand = layer != null && layer.groundBlock.isMaterial(LocalMaterials.SAND);
		final int currentWaterLevel = generatingChunk.getWaterLevel(internalX, internalZ);
		// Below this Y, air gaps are caves: no surface/ground re-application, cave
		// floor/ceiling hooks fire instead. The first solid run from the column top is
		// always surface, however deep (CHC canyons).
		final int surfaceGateY = generatingChunk.getSurfaceGateY(internalX, internalZ);
		boolean passedFirstAirGap = false;
		LocalMaterialData blockOnCurrentPos;
		LocalMaterialData blockOnPreviousPos = null;

		int highestBlockInColumn = chunkBuffer.getHighestBlockForColumn(internalX, internalZ);
		for (int y = highestBlockInColumn; y >= generatingChunk.getWorldHeight().minY(); y--)
		{
			if (generatingChunk.mustCreateBedrockAt(surfaceSettings.getBlockSettings().isFlatBedrock(), surfaceSettings.getBlockSettings().isDisableBedrock(), surfaceSettings.getBlockSettings().isCeilingBedrock(), y))
			{
				// Place bedrock
				chunkBuffer.setBlock(internalX, y, internalZ, surfaceSettings.getBedrockBlockReplaced(y));
			} else {

				// Surface blocks logic (grass, dirt, sand, sandstone)
				blockOnCurrentPos = chunkBuffer.getBlock(internalX, y, internalZ);
				if (blockOnCurrentPos.isEmptyOrAir())
				{
					// Reset when air is found
					groundLayerDepth = -1;
					if (blockOnPreviousPos != null && !blockOnPreviousPos.isEmptyOrAir())
					{
						passedFirstAirGap = true;
						// Solid block above this air is a cave ceiling when below the surface band.
						if (y + 1 < surfaceGateY && !blockOnPreviousPos.isLiquid())
						{
							onCaveCeiling(generatingChunk, chunkBuffer, biome, xInWorld, y + 1, zInWorld);
						}
					}
				}
				// Never touch liquids: with noise caves and aquifers the chunk can contain
				// water and lava mid-column; overwriting them with surface/ground blocks
				// creates dirt sheets on aquifer lava. The waterblockreplaced check stays
				// for presets that replace water with a non-liquid block.
				else if(!blockOnCurrentPos.isLiquid() && !blockOnCurrentPos.equals(surfaceSettings.getWaterBlockReplaced(y)))
				{
					// Below the surface band, an air-to-solid transition is a cave floor,
					// not a new surface: leave the stone alone and fire the cave hook.
					// groundLayerDepth != -1 means a surface run that started above the
					// band is still finishing; let it complete.
					if (passedFirstAirGap && y < surfaceGateY && groundLayerDepth == -1)
					{
						if (blockOnPreviousPos != null && blockOnPreviousPos.isEmptyOrAir())
						{
							onCaveFloor(generatingChunk, chunkBuffer, biome, xInWorld, y, zInWorld);
						}
						blockOnPreviousPos = blockOnCurrentPos;
						continue;
					}
					// Place surface/ground down to a certain depth per column,
					// determined via noise. groundLayerDepth == 0 means we're
					// done until we hit an air block, in which case reset.
					if (groundLayerDepth == -1)
					{
						// Place surface block
						// Reset the ground layer depth
						groundLayerDepth = biomeBlocksNoise;
						
						// Set when variable was reset
						if (biomeBlocksNoise <= 0 && !surfaceSettings.getBlockSettings().isRemoveSurfaceStone())
						{
							useAirForSurface = true;
							useIceForSurface = false;
							useWaterForSurface = false;
							useLayerSurfaceBlockForSurface = false;
							useSandStoneForGround = false;
							useBiomeStoneBlockForGround = true;
							useLayerGroundBlockForGround = false;
						}
						else if ((y >= currentWaterLevel - 4) && (y <= currentWaterLevel + 1))
						{
							useAirForSurface = false;
							useIceForSurface = false;
							useWaterForSurface = false;
							useLayerSurfaceBlockForSurface = true;
							useSandStoneForGround = false;
							useBiomeStoneBlockForGround = false;
							useLayerGroundBlockForGround = true;
						}
						
						// Use blocks for the top of the water instead
						// when on water
						if (y < currentWaterLevel && y > surfaceSettings.getWaterLevelMin())
						{
							boolean bIsAir = useAirForSurface;
							if(!bIsAir && useLayerSurfaceBlockForSurface)
							{
								bIsAir = (layer != null ? layer.getSurfaceBlockReplaced(y, surfaceSettings) : surfaceSettings.getSurfaceBlockReplaced(y)).isAir();
							}
							if(bIsAir)
							{
								if (biome.getTemperatureAt(xInWorld, y, zInWorld) < Constants.SNOW_AND_ICE_TEMP)
								{
									useAirForSurface = false;
									useIceForSurface = true;
									useWaterForSurface = false;
									useLayerSurfaceBlockForSurface = false;
								} else {
									useAirForSurface = false;
									useIceForSurface = false;
									useWaterForSurface = true;
									useLayerSurfaceBlockForSurface = false;
								}
							}
						}

						if (y >= currentWaterLevel - 1)
						{
							if(useAirForSurface)
							{
								currentSurfaceBlock = LocalMaterials.AIR;
							}
							else if(useIceForSurface)
							{
								currentSurfaceBlock = surfaceSettings.getIceBlockReplaced(y);
							}
							else if(useWaterForSurface)
							{
								currentSurfaceBlock = surfaceSettings.getWaterBlockReplaced(y);
							}
							else if(useLayerSurfaceBlockForSurface)
							{
								currentSurfaceBlock = layer != null ? layer.getSurfaceBlockReplaced(y, surfaceSettings) : surfaceSettings.getSurfaceBlockReplaced(y);
							}
							
							chunkBuffer.setBlock(internalX, y, internalZ, currentSurfaceBlock);
						} else {
							if(useBiomeStoneBlockForGround)
							{
								// block should already be the replaced stoneblock
								blockOnPreviousPos = blockOnCurrentPos;								
								continue;
							}
							else if(useLayerGroundBlockForGround)
							{
								if(blockOnPreviousPos != null && blockOnPreviousPos.isLiquid())
								{
									chunkBuffer.setBlock(internalX, y, internalZ, layer != null ? layer.getUnderWaterSurfaceBlockReplaced(y, surfaceSettings) : surfaceSettings.getUnderWaterSurfaceBlockReplaced(y));
								} else {
									chunkBuffer.setBlock(internalX, y, internalZ, layer != null ? layer.getGroundBlockReplaced(y, surfaceSettings) : surfaceSettings.getGroundBlockReplaced(y));
								}
							}							
						}
					}
					// groundLayerDepth > 0 means we have ground layer left to spawn, 0 is done.
					else if (groundLayerDepth > 0)
					{
						groundLayerDepth--;
						// Place ground/stone block 
						if(useBiomeStoneBlockForGround)
						{
							// block should already be the replaced stoneblock
							blockOnPreviousPos = blockOnCurrentPos;
							continue;
						}
						else if(useLayerGroundBlockForGround)
						{
							if(useSandStoneForGround)
							{
								// TODO: Reimplement this when block data works
								//chunkBuffer.setBlock(x, y, z,
									//(layerGroundBlockIsSand ? layer.groundBlock : biomeConfig.getDefaultGroundBlock())
									//.getBlockData() == 1 ? 
										//biomeConfig.getRedSandStoneBlockReplaced(world, y) : 
										//biomeConfig.getSandStoneBlockReplaced(world, y)
								//);
								chunkBuffer.setBlock(internalX, y, internalZ, surfaceSettings.getSandStoneBlockReplaced(y));
							} else {
								if(blockOnPreviousPos != null && blockOnPreviousPos.isLiquid())
								{
									chunkBuffer.setBlock(internalX, y, internalZ, layer != null ? layer.getUnderWaterSurfaceBlockReplaced(y, surfaceSettings) : surfaceSettings.getUnderWaterSurfaceBlockReplaced(y));
								} else {
									chunkBuffer.setBlock(internalX, y, internalZ, layer != null ? layer.getGroundBlockReplaced(y, surfaceSettings) : surfaceSettings.getGroundBlockReplaced(y));
								}
							}

							// When a ground layer of sand is done spawning, if the BiomeBlocksNoise is above 1
							// spawn layers of sandstone underneath.
							// If we end up at (y >= currentWaterLevel - 4) && (y <= currentWaterLevel + 1)
							// after doing this, the groundblock is set back to sand and we repeat the process,
							// otherwise we stop and leave blocks as stone, until we're done or hit air.

							// BiomeBlocksNoise is used to create a pattern of sandstone vs stone columns.
							// For waterlevel, the higher above it we are, the taller the sandstone sections become.
							// For vanilla deserts, this makes the sand layer deeper around waterlevel, affecting
							// mostly flat terrain, while hills have only a 1 block layer of sand and more sandstone.

							if (
								groundLayerDepth == 0 &&
								biomeBlocksNoise > 1 &&
								(layerGroundBlockIsSand || (layer == null && biomeGroundBlockIsSand))
							)
							{
								groundLayerDepth = generatingChunk.random.nextInt(4) + Math.max(0, y - currentWaterLevel);
								useSandStoneForGround = true;
							}
						} 
					}
				}
				blockOnPreviousPos = blockOnCurrentPos;
			}
		}
	}

	/**
	 * Called for the top solid block of a cave floor (solid block with air directly above)
	 * found below the surface band during the column scan. Applies the
	 * CaveSurfaceAndGroundControl floor rule of the cave biome at the position, or of the
	 * surface biome where no cave biome applies.
	 */
	protected void onCaveFloor(GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, IBiome biome, int xInWorld, int y, int zInWorld)
	{
		SurfaceSettings settings = caveRuleSettings(generatingChunk, biome, xInWorld, y, zInWorld);
		CaveSurfaceRules rules = settings.getCaveSurfaceRules();
		if (!rules.hasFloor())
		{
			return;
		}
		int internalX = xInWorld & 0xf;
		int internalZ = zInWorld & 0xf;
		int minY = generatingChunk.getWorldHeight().minY();
		for (int depth = 0; depth < rules.floorDepth(); depth++)
		{
			int targetY = y - depth;
			if (targetY < minY || notValidReplaceTarget(chunkBuffer.getBlock(internalX, targetY, internalZ)))
			{
				break;
			}
			chunkBuffer.setBlock(internalX, targetY, internalZ, settings.getCaveFloorBlockReplaced(targetY));
		}
	}

	/**
	 * Called for the bottom solid block of a cave ceiling (solid block with air directly
	 * below) found below the surface band during the column scan. Applies the
	 * CaveSurfaceAndGroundControl ceiling rule of the cave biome at the position, or of the
	 * surface biome where no cave biome applies.
	 */
	protected void onCaveCeiling(GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, IBiome biome, int xInWorld, int y, int zInWorld)
	{
		SurfaceSettings settings = caveRuleSettings(generatingChunk, biome, xInWorld, y, zInWorld);
		CaveSurfaceRules rules = settings.getCaveSurfaceRules();
		if (!rules.hasCeiling())
		{
			return;
		}
		int internalX = xInWorld & 0xf;
		int internalZ = zInWorld & 0xf;
		int maxY = generatingChunk.getWorldHeight().maxY();
		for (int depth = 0; depth < rules.ceilingDepth(); depth++)
		{
			int targetY = y + depth;
			if (targetY > maxY || notValidReplaceTarget(chunkBuffer.getBlock(internalX, targetY, internalZ)))
			{
				break;
			}
			chunkBuffer.setBlock(internalX, targetY, internalZ, settings.getCaveCeilingBlockReplaced(targetY));
		}
	}

	// The settings owning the cave rules at a position: the cave biome's when one applies
	// (its rules AND its ReplacedBlocks, even when empty, so cave biome regions never
	// inherit the surface biome's cave styling), otherwise the surface biome's own.
	private static SurfaceSettings caveRuleSettings(GeneratingChunk generatingChunk, IBiome biome, int xInWorld, int y, int zInWorld)
	{
		SurfaceSettings caveBiomeSettings = generatingChunk.getCaveBiomeSurfaceSettings(xInWorld, y, zInWorld);
		return caveBiomeSettings != null ? caveBiomeSettings : biome.getBiomeSettings().getSurfaceSettings();
	}

	// Cave rules replace solid ground only; stop at the next opening (air/liquid) and
	// never eat through bedrock.
	private static boolean notValidReplaceTarget(LocalMaterialData material)
	{
		return material.isEmptyOrAir()
				|| material.isLiquid()
				|| material.isMaterial(LocalMaterials.BEDROCK);
	}

	@Override
	public String toString()
	{
		// Make sure that empty name is written to the config files
		return "";
	}
}
