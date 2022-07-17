package twilightforest.structures;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Stairs2;
import twilightforest.TFFeature;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.structures.darktower.ComponentTFDarkTowerMain;
import twilightforest.structures.darktower.TFDarkTowerPieces;
import twilightforest.structures.hollowtree.StructureTFHollowTreeStart;
import twilightforest.structures.hollowtree.TFHollowTreePieces;
import twilightforest.structures.icetower.ComponentTFIceTowerMain;
import twilightforest.structures.icetower.TFIceTowerPieces;
import twilightforest.structures.lichtower.ComponentTFTowerMain;
import twilightforest.structures.lichtower.TFLichTowerPieces;
import twilightforest.structures.minotaurmaze.ComponentTFMazeRuins;
import twilightforest.structures.minotaurmaze.TFMinotaurMazePieces;
import twilightforest.structures.mushroomtower.ComponentTFMushroomTowerMain;
import twilightforest.structures.mushroomtower.TFMushroomTowerPieces;
import twilightforest.structures.stronghold.ComponentTFStrongholdEntrance;
import twilightforest.structures.stronghold.TFStrongholdPieces;
import twilightforest.structures.trollcave.ComponentTFTrollCaveMain;
import twilightforest.structures.trollcave.TFTrollCavePieces;
import twilightforest.world.TFWorld;
import twilightforest.world.TFWorldChunkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureTFMajorFeatureStart extends StructureStart
{
	public TFFeature feature;
	public boolean isConquered;

	public StructureTFMajorFeatureStart()
	{
	}

	public StructureTFMajorFeatureStart(World world, Random rand, int chunkX, int chunkZ)
	{
		// TODO gamerforEA code start
		super(chunkX, chunkZ);
		// TODO gamerforEA code end

		StructureStrongholdPieces.prepareStructurePieces();
		int x = (chunkX << 4) + 8;
		int z = (chunkZ << 4) + 8;
		int y = TFWorld.SEALEVEL + 1;
		this.feature = TFFeature.getFeatureDirectlyAt(chunkX, chunkZ, world);
		this.isConquered = false;
		StructureComponent firstComponent = this.makeFirstComponent(world, rand, this.feature, x, y, z);
		if (firstComponent != null)
		{
			this.components.add(firstComponent);
			firstComponent.buildComponent(firstComponent, this.components, rand);
		}

		this.updateBoundingBox();
		if (firstComponent instanceof Stairs2)
		{
			List var6 = ((Stairs2) firstComponent).field_75026_c;

			while (!var6.isEmpty())
			{
				int var7 = rand.nextInt(var6.size());
				StructureComponent var8 = (StructureComponent) var6.remove(var7);
				var8.buildComponent(firstComponent, this.components, rand);
			}

			this.updateBoundingBox();
			int offY = -33;
			this.boundingBox.offset(0, offY, 0);

			for (StructureComponent com : (Iterable<? extends StructureComponent>) this.getComponents())
			{
				com.getBoundingBox().offset(0, offY, 0);
			}
		}

		if (firstComponent instanceof ComponentTFTowerMain || firstComponent instanceof ComponentTFDarkTowerMain)
			this.moveToAvgGroundLevel(world, x, z);

	}

	public StructureComponent makeFirstComponent(World world, Random rand, TFFeature feature, int x, int y, int z)
	{
		return feature == TFFeature.nagaCourtyard ? new ComponentTFNagaCourtyard(world, rand, 0, x, y, z) : feature == TFFeature.hedgeMaze ? new ComponentTFHedgeMaze(world, rand, 0, x, y, z) : feature == TFFeature.hill1 ? new ComponentTFHollowHill(world, rand, 0, 1, x, y, z) : feature == TFFeature.hill2 ? new ComponentTFHollowHill(world, rand, 0, 2, x, y, z) : feature == TFFeature.hill3 ? new ComponentTFHollowHill(world, rand, 0, 3, x, y, z) : feature == TFFeature.lichTower ? new ComponentTFTowerMain(world, rand, 0, x, y, z) : feature == TFFeature.questGrove ? new ComponentTFQuestGrove(world, rand, 0, x, y, z) : feature == TFFeature.hydraLair ? new ComponentTFHydraLair(world, rand, 0, x, y, z) : feature == TFFeature.labyrinth ? new ComponentTFMazeRuins(world, rand, 0, x, y, z) : feature == TFFeature.darkTower ? new ComponentTFDarkTowerMain(world, rand, 0, x, y - 1, z) : feature == TFFeature.tfStronghold ? new ComponentTFStrongholdEntrance(world, rand, 0, x, y, z) : feature == TFFeature.iceTower ? new ComponentTFIceTowerMain(world, rand, 0, x, y, z) : feature == TFFeature.mushroomTower ? new ComponentTFMushroomTowerMain(world, rand, 0, x, y, z) : feature == TFFeature.yetiCave ? new ComponentTFYetiCave(world, rand, 0, x, y, z) : feature == TFFeature.trollCave ? new ComponentTFTrollCaveMain(world, rand, 0, x, y, z) : feature == TFFeature.finalCastle ? new TFFinalCastlePieces.Main(world, rand, 0, x, y, z) : null;
	}

	@Override
	public boolean isSizeableStructure()
	{
		return this.feature.isStructureEnabled;
	}

	protected void moveToAvgGroundLevel(World world, int x, int z)
	{
		if (world.getWorldChunkManager() instanceof TFWorldChunkManager)
		{
			BiomeGenBase biomeAt = world.getBiomeGenForCoords(x, z);
			int offY = (int) ((biomeAt.rootHeight + biomeAt.heightVariation) * 8.0F);
			if (biomeAt == TFBiomeBase.darkForest)
				offY += 4;

			if (offY > 0)
			{
				this.boundingBox.offset(0, offY, 0);

				for (StructureComponent com : (Iterable<? extends StructureComponent>) this.getComponents())
				{
					com.getBoundingBox().offset(0, offY, 0);
				}
			}
		}

	}

	private boolean isIntersectingLarger(StructureBoundingBox chunkBB, StructureComponent component)
	{
		StructureBoundingBox compBB = component.getBoundingBox();
		return compBB.maxX + 1 >= chunkBB.minX && compBB.minX - 1 <= chunkBB.maxX && compBB.maxZ + 1 >= chunkBB.minZ && compBB.minZ - 1 <= chunkBB.maxZ;
	}

	private boolean isShieldable(StructureComponent component)
	{
		return component.getBoundingBox().maxY <= 32;
	}

	private void addShieldFor(World world, StructureComponent component, List<StructureComponent> otherComponents, StructureBoundingBox chunkBox)
	{
		StructureBoundingBox shieldBox = new StructureBoundingBox(component.getBoundingBox());
		--shieldBox.minX;
		--shieldBox.minY;
		--shieldBox.minZ;
		++shieldBox.maxX;
		++shieldBox.maxY;
		++shieldBox.maxZ;
		ArrayList<StructureComponent> intersecting = new ArrayList();

		for (StructureComponent other : otherComponents)
		{
			if (other != component && shieldBox.intersectsWith(other.getBoundingBox()))
				intersecting.add(other);
		}

		for (int x = shieldBox.minX; x <= shieldBox.maxX; ++x)
		{
			for (int y = shieldBox.minY; y <= shieldBox.maxY; ++y)
			{
				for (int z = shieldBox.minZ; z <= shieldBox.maxZ; ++z)
				{
					if ((x == shieldBox.minX || x == shieldBox.maxX || y == shieldBox.minY || y == shieldBox.maxY || z == shieldBox.minZ || z == shieldBox.maxZ) && chunkBox.isVecInside(x, y, z))
					{
						boolean notIntersecting = true;

						for (StructureComponent other : intersecting)
						{
							if (other.getBoundingBox().isVecInside(x, y, z))
								notIntersecting = false;
						}

						if (notIntersecting)
							world.setBlock(x, y, z, TFBlocks.shield, this.calculateShieldMeta(shieldBox, x, y, z), 2);
					}
				}
			}
		}

	}

	private int calculateShieldMeta(StructureBoundingBox shieldBox, int x, int y, int z)
	{
		int shieldMeta = 0;
		if (x == shieldBox.minX)
			shieldMeta = 5;

		if (x == shieldBox.maxX)
			shieldMeta = 4;

		if (z == shieldBox.minZ)
			shieldMeta = 3;

		if (z == shieldBox.maxZ)
			shieldMeta = 2;

		if (y == shieldBox.minY)
			shieldMeta = 1;

		if (y == shieldBox.maxY)
			shieldMeta = 0;

		return shieldMeta;
	}

	@Override
	public void func_143022_a(NBTTagCompound par1NBTTagCompound)
	{
		super.func_143022_a(par1NBTTagCompound);
		par1NBTTagCompound.setBoolean("Conquered", this.isConquered);
		par1NBTTagCompound.setInteger("FeatureID", this.feature.featureID);
	}

	@Override
	public void func_143017_b(NBTTagCompound nbttagcompound)
	{
		super.func_143017_b(nbttagcompound);
		this.isConquered = nbttagcompound.getBoolean("Conquered");
		this.feature = TFFeature.featureList[nbttagcompound.getInteger("FeatureID")];
	}

	static
	{
		MapGenStructureIO.registerStructure(StructureTFMajorFeatureStart.class, "TFFeature");
		MapGenStructureIO.registerStructure(StructureTFHollowTreeStart.class, "TFHollowTree");
		TFStrongholdPieces.registerPieces();
		TFMinotaurMazePieces.registerPieces();
		TFDarkTowerPieces.registerPieces();
		TFLichTowerPieces.registerPieces();
		TFIceTowerPieces.registerPieces();
		TFMushroomTowerPieces.registerPieces();
		TFHollowTreePieces.registerPieces();
		TFTrollCavePieces.registerPieces();
		TFFinalCastlePieces.registerFinalCastlePieces();
		MapGenStructureIO.func_143031_a(ComponentTFHedgeMaze.class, "TFHedge");
		MapGenStructureIO.func_143031_a(ComponentTFHillMaze.class, "TFHillMaze");
		MapGenStructureIO.func_143031_a(ComponentTFHollowHill.class, "TFHill");
		MapGenStructureIO.func_143031_a(ComponentTFHydraLair.class, "TFHydra");
		MapGenStructureIO.func_143031_a(ComponentTFNagaCourtyard.class, "TFNaga");
		MapGenStructureIO.func_143031_a(ComponentTFQuestGrove.class, "TFQuest1");
		MapGenStructureIO.func_143031_a(ComponentTFYetiCave.class, "TFYeti");
	}
}
