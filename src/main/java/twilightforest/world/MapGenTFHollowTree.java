package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.structures.hollowtree.StructureTFHollowTreeStart;

import java.util.*;
import java.util.concurrent.Callable;

public class MapGenTFHollowTree extends MapGenBase
{
	protected Map<Long, StructureStart> structureMap = new HashMap();
	public static List<BiomeGenBase> oakSpawnBiomes;

	@Override
	protected void func_151538_a(World world, final int chunkX, final int chunkZ, int centerX, int centerZ, Block[] blockData)
	{
		if (!this.structureMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ)))
		{
			this.rand.nextInt();

			try
			{
				if (this.canSpawnStructureAtCoords(chunkX, chunkZ))
				{
					StructureStart throwable = this.getStructureStart(chunkX, chunkZ);
					this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), throwable);
				}
			}
			catch (Throwable var10)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(var10, "Exception preparing hollow tree");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Feature being prepared");
				crashreportcategory.addCrashSectionCallable("Is feature chunk", new Callable()
				{
					private static final String __OBFID = "CL_00000506";

					@Override
					public String call()
					{
						return MapGenTFHollowTree.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
					}
				});
				crashreportcategory.addCrashSection("Chunk location", String.format("%d,%d", chunkX, chunkZ));
				crashreportcategory.addCrashSectionCallable("Chunk pos hash", new Callable()
				{
					private static final String __OBFID = "CL_00000507";

					@Override
					public String call()
					{
						return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
					}
				});
				crashreportcategory.addCrashSectionCallable("Structure type", new Callable()
				{
					private static final String __OBFID = "CL_00000508";

					@Override
					public String call()
					{
						return MapGenTFHollowTree.this.getClass().getCanonicalName();
					}
				});
				throw new ReportedException(crashreport);
			}
		}

	}

	public boolean generateStructuresInChunk(World world, Random rand, int chunkX, int chunkZ)
	{
		int mapX = (chunkX << 4) + 8;
		int mapZ = (chunkZ << 4) + 8;
		boolean flag = false;

		// TODO gamerforEA code replace, old code:
		// for (StructureStart structurestart : this.structureMap.values())
		for (StructureStart structurestart : new ArrayList<>(this.structureMap.values()))
		// TODO gamerforEA code end
		{
			if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(mapX, mapZ, mapX + 15, mapZ + 15))
			{
				structurestart.generateStructure(world, rand, new StructureBoundingBox(mapX, mapZ, mapX + 15, mapZ + 15));
				flag = true;
			}
		}

		return flag;
	}

	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		return this.rand.nextInt(TwilightForestMod.twilightOakChance) == 0 && TFFeature.getNearestFeature(chunkX, chunkZ, this.worldObj).areChunkDecorationsEnabled && this.worldObj.getWorldChunkManager().areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 0, oakSpawnBiomes);
	}

	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new StructureTFHollowTreeStart(this.worldObj, this.rand, chunkX, chunkZ);
	}

	static
	{
		oakSpawnBiomes = Arrays.asList(TFBiomeBase.twilightForest, TFBiomeBase.twilightForest2, TFBiomeBase.mushrooms, TFBiomeBase.tfSwamp, TFBiomeBase.clearing, TFBiomeBase.oakSavanna, TFBiomeBase.fireflyForest, TFBiomeBase.deepMushrooms, TFBiomeBase.enchantedForest, TFBiomeBase.fireSwamp);
	}
}
