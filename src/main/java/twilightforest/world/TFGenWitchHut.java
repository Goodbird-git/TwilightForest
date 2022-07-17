package twilightforest.world;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import twilightforest.entity.TFCreatures;

import java.util.Random;

public class TFGenWitchHut extends TFGenerator
{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z)
	{
		return this.generateTinyHut(world, rand, x, y, z);
	}

	public boolean generateTinyHut(World world, Random rand, int x, int y, int z)
	{
		if (!this.isAreaClear(world, rand, x, y, z, 5, 7, 6))
			return false;
		else
		{
			this.setBlock(world, x + 1, y, z + 1, this.randStone(rand, 1));
			this.setBlock(world, x + 2, y, z + 1, this.randStone(rand, 1));
			this.setBlock(world, x + 3, y, z + 1, this.randStone(rand, 1));
			this.setBlock(world, x + 5, y, z + 1, this.randStone(rand, 1));
			this.setBlock(world, x, y, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 1, y, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 5, y, z + 2, this.randStone(rand, 1));
			this.setBlock(world, x, y, z + 3, Blocks.brick_block);
			this.setBlock(world, x + 5, y, z + 3, this.randStone(rand, 1));
			this.setBlock(world, x, y, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 1, y, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 5, y, z + 4, this.randStone(rand, 1));
			this.setBlock(world, x + 1, y, z + 5, this.randStone(rand, 1));
			this.setBlock(world, x + 2, y, z + 5, this.randStone(rand, 1));
			this.setBlock(world, x + 3, y, z + 5, this.randStone(rand, 1));
			this.setBlock(world, x + 5, y, z + 5, this.randStone(rand, 1));
			this.setBlock(world, x + 1, y + 1, z + 1, this.randStone(rand, 2));
			this.setBlock(world, x + 3, y + 1, z + 1, this.randStone(rand, 2));
			this.setBlock(world, x + 5, y + 1, z + 1, this.randStone(rand, 2));
			this.setBlock(world, x, y + 1, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 1, y + 1, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 5, y + 1, z + 2, this.randStone(rand, 2));
			this.setBlock(world, x, y + 1, z + 3, Blocks.brick_block);
			this.setBlock(world, x, y + 1, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 1, y + 1, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 5, y + 1, z + 4, this.randStone(rand, 2));
			this.setBlock(world, x + 1, y + 1, z + 5, this.randStone(rand, 2));
			this.setBlock(world, x + 3, y + 1, z + 5, this.randStone(rand, 2));
			this.setBlock(world, x + 5, y + 1, z + 5, this.randStone(rand, 2));
			this.setBlock(world, x + 1, y + 2, z + 1, this.randStone(rand, 3));
			this.setBlock(world, x + 2, y + 2, z + 1, this.randStone(rand, 3));
			this.setBlock(world, x + 3, y + 2, z + 1, this.randStone(rand, 3));
			this.setBlock(world, x + 4, y + 2, z + 1, this.randStone(rand, 3));
			this.setBlock(world, x + 5, y + 2, z + 1, this.randStone(rand, 3));
			this.setBlock(world, x, y + 2, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 1, y + 2, z + 2, Blocks.brick_block);
			this.setBlock(world, x + 5, y + 2, z + 2, this.randStone(rand, 3));
			this.setBlock(world, x, y + 2, z + 3, Blocks.brick_block);
			this.setBlock(world, x + 5, y + 2, z + 3, this.randStone(rand, 3));
			this.setBlock(world, x, y + 2, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 1, y + 2, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 5, y + 2, z + 4, this.randStone(rand, 1));
			this.setBlock(world, x + 1, y + 2, z + 5, this.randStone(rand, 3));
			this.setBlock(world, x + 2, y + 2, z + 5, this.randStone(rand, 3));
			this.setBlock(world, x + 3, y + 2, z + 5, this.randStone(rand, 3));
			this.setBlock(world, x + 4, y + 2, z + 5, this.randStone(rand, 3));
			this.setBlock(world, x + 5, y + 2, z + 5, this.randStone(rand, 3));
			this.setBlock(world, x, y + 3, z + 2, Blocks.brick_block);
			this.setBlock(world, x, y + 3, z + 3, Blocks.brick_block);
			this.setBlock(world, x, y + 3, z + 4, Blocks.brick_block);
			this.setBlock(world, x + 2, y + 3, z + 1, this.randStone(rand, 4));
			this.setBlock(world, x + 3, y + 3, z + 1, this.randStone(rand, 4));
			this.setBlock(world, x + 4, y + 3, z + 1, this.randStone(rand, 4));
			this.setBlock(world, x + 2, y + 3, z + 5, this.randStone(rand, 4));
			this.setBlock(world, x + 3, y + 3, z + 5, this.randStone(rand, 4));
			this.setBlock(world, x + 4, y + 3, z + 5, this.randStone(rand, 4));
			this.setBlock(world, x, y + 4, z + 3, Blocks.brick_block);
			this.setBlock(world, x + 3, y + 4, z + 1, this.randStone(rand, 5));
			this.setBlock(world, x + 3, y + 4, z + 5, this.randStone(rand, 5));
			this.setBlock(world, x, y + 5, z + 3, Blocks.brick_block);
			this.setBlock(world, x, y + 6, z + 3, Blocks.brick_block);
			this.setBlockAndMetadata(world, x, y + 2, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x, y + 2, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x, y + 2, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x, y + 2, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 3, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 6, y + 2, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 3, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 3, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 3, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 4, z, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 3, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 4, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 1, y + 4, z + 6, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 4, z, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 3, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 4, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 5, y + 4, z + 6, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 5, z, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 5, z + 1, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 5, z, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 5, z + 1, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 2, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 3, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 4, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 5, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 5, z + 5, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 2, y + 5, z + 6, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 5, z + 5, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 4, y + 5, z + 6, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z + 1, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z + 2, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z + 4, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z + 5, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 6, z + 6, Blocks.double_stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 7, z, Blocks.stone_slab, 2);
			this.setBlockAndMetadata(world, x + 3, y + 7, z + 6, Blocks.stone_slab, 2);
			this.setBlock(world, x + 1, y - 1, z + 3, Blocks.netherrack);
			this.setBlock(world, x + 1, y, z + 3, Blocks.fire);
			world.setBlock(x + 3, y + 1, z + 3, Blocks.mob_spawner, 0, 2);
			TileEntityMobSpawner ms = (TileEntityMobSpawner) world.getTileEntity(x + 3, y + 1, z + 3);

			// TODO gamerforEA code replace, old code:
			// ms.func_145881_a().setEntityName(TFCreatures.getSpawnerNameFor("Skeleton Druid"));
			if (ms != null)
			{
				MobSpawnerBaseLogic logic = ms.func_145881_a();
				if (logic != null)
					logic.setEntityName(TFCreatures.getSpawnerNameFor("Skeleton Druid"));
			}
			// TODO gamerforEA code end

			return true;
		}
	}
}
