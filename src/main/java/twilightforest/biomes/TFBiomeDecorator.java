package twilightforest.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TFBiomeDecorator extends BiomeDecorator
{
	TFGenCanopyTree canopyTreeGen = new TFGenCanopyTree();
	TFTreeGenerator alternateCanopyGen = new TFGenCanopyMushroom();
	TFGenHollowTree hollowTreeGen = new TFGenHollowTree();
	TFGenMyceliumBlob myceliumBlobGen = new TFGenMyceliumBlob(5);
	WorldGenLakes extraLakeGen = new WorldGenLakes(Blocks.water);
	WorldGenLakes extraLavaPoolGen = new WorldGenLakes(Blocks.lava);
	TFGenMangroveTree mangroveTreeGen = new TFGenMangroveTree();
	TFGenPlantRoots plantRootGen = new TFGenPlantRoots();
	TFGenWoodRoots woodRootGen = new TFGenWoodRoots();
	WorldGenLiquids caveWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	TFGenTorchBerries torchBerryGen = new TFGenTorchBerries();
	public float canopyPerChunk = TwilightForestMod.canopyCoverage;
	public float alternateCanopyChance = 0.0F;
	public int myceliumPerChunk = 0;
	public int mangrovesPerChunk = 0;
	public int lakesPerChunk = 0;
	public float lavaPoolChance = 0.0F;
	static final List<TFBiomeDecorator.RuinEntry> ruinList = new ArrayList<>();

	@Override
	public void decorateChunk(World world, Random rand, BiomeGenBase biome, int mapX, int mapZ)
	{
		// TODO gamerforEA code start
		World prevWorld = this.currentWorld;
		Random prevRandom = this.randomGenerator;
		int prevX = this.chunk_X;
		int prevZ = this.chunk_Z;
		try
		{
			// TODO gamerforEA code end

			TFFeature nearFeature = TFFeature.getNearestFeature(mapX >> 4, mapZ >> 4, world);
			if (!nearFeature.areChunkDecorationsEnabled)
			{
				this.decorateUnderground(world, rand, mapX, mapZ);
				this.decorateOnlyOres(world, rand, mapX, mapZ);
			}
			else
			{
				this.currentWorld = null;
				super.decorateChunk(world, rand, biome, mapX, mapZ);
			}

			// TODO gamerforEA code start
		}
		finally
		{
			this.currentWorld = prevWorld;
			this.randomGenerator = prevRandom;
			this.chunk_X = prevX;
			this.chunk_Z = prevZ;
		}
		// TODO gamerforEA code end
	}

	@Override
	protected void genDecorations(BiomeGenBase biome)
	{
		// TODO gamerforEA code start
		if (this.currentWorld == null || this.randomGenerator == null)
			return;
		// TODO gamerforEA code end

		if (this.randomGenerator.nextInt(6) == 0)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			TFGenerator rf = this.randomFeature(this.randomGenerator);
			if (rf.generate(this.currentWorld, this.randomGenerator, rx, ry, rz))
				;
		}

		int nc = (int) this.canopyPerChunk + (this.randomGenerator.nextFloat() < this.canopyPerChunk - (int) this.canopyPerChunk ? 1 : 0);

		for (int i = 0; i < nc; ++i)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			if (this.alternateCanopyChance > 0.0F && this.randomGenerator.nextFloat() <= this.alternateCanopyChance)
				this.alternateCanopyGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
			else
				this.canopyTreeGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
		}

		for (int i = 0; i < this.mangrovesPerChunk; ++i)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			this.mangroveTreeGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
		}

		for (int i = 0; i < this.lakesPerChunk; ++i)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			this.extraLakeGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
		}

		if (this.randomGenerator.nextFloat() <= this.lavaPoolChance)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			this.extraLavaPoolGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
		}

		for (int i = 0; i < this.myceliumPerChunk; ++i)
		{
			int rx = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
			int rz = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
			int ry = this.currentWorld.getHeightValue(rx, rz);
			this.myceliumBlobGen.generate(this.currentWorld, this.randomGenerator, rx, ry, rz);
		}

		super.genDecorations(biome);
		this.decorateUnderground(this.currentWorld, this.randomGenerator, this.chunk_X, this.chunk_Z);
	}

	protected void decorateUnderground(World world, Random rand, int mapX, int mapZ)
	{
		for (int i = 0; i < 12; ++i)
		{
			int rx = mapX + rand.nextInt(16) + 8;
			byte ry = 64;
			int rz = mapZ + rand.nextInt(16) + 8;
			this.plantRootGen.generate(world, rand, rx, ry, rz);
		}

		for (int i = 0; i < 20; ++i)
		{
			int rx = mapX + rand.nextInt(16) + 8;
			int ry = rand.nextInt(64);
			int rz = mapZ + rand.nextInt(16) + 8;
			this.woodRootGen.generate(world, rand, rx, ry, rz);
		}

		if (this.generateLakes)
			for (int i = 0; i < 50; ++i)
			{
				int rx = mapX + rand.nextInt(16) + 8;
				int ry = rand.nextInt(24) + 4;
				int rz = mapZ + rand.nextInt(16) + 8;
				this.caveWaterGen.generate(world, rand, rx, ry, rz);
			}

		for (int i = 0; i < 3; ++i)
		{
			int rx = mapX + rand.nextInt(16) + 8;
			int ry = 64;
			int rz = mapZ + rand.nextInt(16) + 8;
			this.torchBerryGen.generate(world, rand, rx, ry, rz);
		}
	}

	public void decorateOnlyOres(World world, Random rand, int mapX, int mapZ)
	{
		// TODO gamerforEA code start
		World prevWorld = this.currentWorld;
		Random prevRandom = this.randomGenerator;
		int prevX = this.chunk_X;
		int prevZ = this.chunk_Z;
		try
		{
			// TODO gamerforEA code end

			this.currentWorld = world;
			this.randomGenerator = rand;
			this.chunk_X = mapX;
			this.chunk_Z = mapZ;
			this.generateOres();
			this.currentWorld = null;
			this.randomGenerator = null;

			// TODO gamerforEA code start
		}
		finally
		{
			this.currentWorld = prevWorld;
			this.randomGenerator = prevRandom;
			this.chunk_X = prevX;
			this.chunk_Z = prevZ;
		}
		// TODO gamerforEA code end
	}

	public TFGenerator randomFeature(Random rand)
	{
		return ((TFBiomeDecorator.RuinEntry) WeightedRandom.getRandomItem(rand, ruinList)).generator;
	}

	public void setTreesPerChunk(int treesPerChunk)
	{
		super.treesPerChunk = treesPerChunk;
	}

	public void setBigMushroomsPerChunk(int bigMushroomsPerChunk)
	{
		super.bigMushroomsPerChunk = bigMushroomsPerChunk;
	}

	public void setClayPerChunk(int clayPerChunk)
	{
		super.clayPerChunk = clayPerChunk;
	}

	public void setDeadBushPerChunk(int deadBushPerChunk)
	{
		super.deadBushPerChunk = deadBushPerChunk;
	}

	public void setMushroomsPerChunk(int mushroomsPerChunk)
	{
		super.mushroomsPerChunk = mushroomsPerChunk;
	}

	public void setFlowersPerChunk(int flowersPerChunk)
	{
		super.flowersPerChunk = flowersPerChunk;
	}

	public void setReedsPerChunk(int reedsPerChunk)
	{
		super.reedsPerChunk = reedsPerChunk;
	}

	public void setWaterlilyPerChunk(int waterlilyPerChunk)
	{
		super.waterlilyPerChunk = waterlilyPerChunk;
	}

	public void setGrassPerChunk(int grassPerChunk)
	{
		super.grassPerChunk = grassPerChunk;
	}

	static
	{
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenStoneCircle(), 10));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenWell(), 10));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenWitchHut(), 5));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenOutsideStalagmite(), 12));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenFoundation(), 10));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenMonolith(), 10));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenGroveRuins(), 5));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenHollowStump(), 12));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenFallenHollowLog(), 10));
		ruinList.add(new TFBiomeDecorator.RuinEntry(new TFGenFallenSmallLog(), 10));
	}

	static class RuinEntry extends Item
	{
		public final TFGenerator generator;

		public RuinEntry(TFGenerator generator, int weight)
		{
			super(weight);
			this.generator = generator;
		}
	}
}
