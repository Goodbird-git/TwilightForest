package twilightforest.structures.hollowtree;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import twilightforest.structures.StructureTFComponent;
import twilightforest.world.TFWorld;

import java.util.Random;

public class StructureTFHollowTreeStart extends StructureStart
{
	public StructureTFHollowTreeStart()
	{
	}

	public StructureTFHollowTreeStart(World world, Random rand, int chunkX, int chunkZ)
	{
		// TODO gamerforEA code start
		super(chunkX, chunkZ);
		// TODO gamerforEA code end

		int x = (chunkX << 4) + 8;
		int z = (chunkZ << 4) + 8;
		int y = TFWorld.SEALEVEL + 1;
		StructureTFComponent trunk = new ComponentTFHollowTreeTrunk(world, rand, 0, x, y, z);
		this.components.add(trunk);
		trunk.buildComponent(trunk, this.components, rand);
		this.updateBoundingBox();
	}
}
