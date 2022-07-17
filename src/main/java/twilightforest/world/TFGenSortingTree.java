package twilightforest.world;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;

import java.util.Random;

public class TFGenSortingTree extends TFGenerator
{
	protected Block treeBlock;
	protected int treeMeta;
	protected int branchMeta;
	protected Block leafBlock;
	protected int leafMeta;
	protected Block rootBlock;
	protected int rootMeta;

	public TFGenSortingTree()
	{
		this(false);
	}

	public TFGenSortingTree(boolean notify)
	{
		super(notify);
		this.treeBlock = TFBlocks.magicLog;
		this.treeMeta = 3;
		this.branchMeta = this.treeMeta | 12;
		this.leafBlock = TFBlocks.magicLeaves;
		this.leafMeta = 3;
		this.rootBlock = TFBlocks.root;
		this.rootMeta = 0;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z)
	{
		Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
		if ((materialUnder == Material.grass || materialUnder == Material.ground) && y < TFWorld.MAXHEIGHT - 12)
		{
			for (int dy = 0; dy < 4; ++dy)
			{
				this.setBlockAndMetadata(world, x, y + dy, z, this.treeBlock, this.treeMeta);
			}

			this.putLeaves(world, x, y + 2, z, false);
			this.putLeaves(world, x, y + 3, z, false);
			this.setBlockAndMetadata(world, x, y + 1, z, TFBlocks.magicLogSpecial, 3);
			return true;
		}
		else
			return false;
	}

	protected void putLeaves(World world, int bx, int by, int bz, boolean bushy)
	{
		for (int lx = -1; lx <= 1; ++lx)
		{
			for (int ly = -1; ly <= 1; ++ly)
			{
				for (int lz = -1; lz <= 1; ++lz)
				{
					if (bushy || Math.abs(ly) <= 0 || Math.abs(lx) + Math.abs(lz) <= 1)
						this.putLeafBlock(world, bx + lx, by + ly, bz + lz, this.leafBlock, this.leafMeta);
				}
			}
		}
	}

	// TODO gamerforEA code start
	@Override
	protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta)
	{
		if ((world.isAirBlock(x, y, z) || block == TFBlocks.magicLogSpecial) && ModUtils.canTreeGrowAt(world, x, y, z))
			super.setBlockAndNotifyAdequately(world, x, y, z, block, meta);
	}
	// TODO gamerforEA code end
}
