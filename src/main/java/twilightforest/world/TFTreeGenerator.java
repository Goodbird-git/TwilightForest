package twilightforest.world;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import twilightforest.block.TFBlocks;

public abstract class TFTreeGenerator extends WorldGenAbstractTree
{
	protected Block treeBlock;
	protected int treeMeta;
	protected int branchMeta;
	protected Block leafBlock;
	protected int leafMeta;
	protected Block rootBlock;
	protected int rootMeta;

	public TFTreeGenerator()
	{
		this(false);
	}

	public TFTreeGenerator(boolean par1)
	{
		super(par1);
		this.treeBlock = TFBlocks.log;
		this.treeMeta = 3;
		this.branchMeta = 15;
		this.leafBlock = TFBlocks.hedge;
		this.leafMeta = 1;
		this.rootBlock = TFBlocks.root;
		this.rootMeta = 0;
	}

	protected void buildRoot(World world, int x, int y, int z, double offset, int b)
	{
		ChunkCoordinates dest = translateCoords(x, y - b - 2, z, 5.0D, 0.3D * b + offset, 0.8D);
		ChunkCoordinates[] lineArray = getBresehnamArrayCoords(x, y - b - 2, z, dest.posX, dest.posY, dest.posZ);
		int var11 = lineArray.length;

		for (int var12 = 0; var12 < var11; ++var12)
		{
			ChunkCoordinates coord = lineArray[var12];
			this.placeRootBlock(world, coord.posX, coord.posY, coord.posZ, this.rootBlock, this.rootMeta);
		}

	}

	protected void placeRootBlock(World world, int x, int y, int z, Block rootBlock2, int meta)
	{
		if (canRootGrowIn(world, x, y, z))
			this.setBlockAndMetadata(world, x, y, z, rootBlock2, meta);
	}

	public static boolean canRootGrowIn(World world, int x, int y, int z)
	{
		Block blockID = world.getBlock(x, y, z);
		return blockID == Blocks.air ? isNearSolid(world, x, y, z) : blockID != Blocks.bedrock && blockID != Blocks.obsidian && blockID != TFBlocks.shield;
	}

	public static int[] translate(int sx, int sy, int sz, double distance, double angle, double tilt)
	{
		return TFGenerator.translate(sx, sy, sz, distance, angle, tilt);
	}

	protected static ChunkCoordinates translateCoords(int sx, int sy, int sz, double length, double angle, double tilt)
	{
		return TFGenerator.translateCoords(sx, sy, sz, length, angle, tilt);
	}

	public static ChunkCoordinates[] getBresehnamArrayCoords(ChunkCoordinates src, ChunkCoordinates dest)
	{
		return TFGenerator.getBresehnamArrayCoords(src.posX, src.posY, src.posZ, dest.posX, dest.posY, dest.posZ);
	}

	public static ChunkCoordinates[] getBresehnamArrayCoords(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return TFGenerator.getBresehnamArrayCoords(x1, y1, z1, x2, y2, z2);
	}

	protected static boolean isNearSolid(IBlockAccess world, int bx, int by, int bz)
	{
		return TFGenerator.isNearSolid(world, bx, by, bz);
	}

	protected static boolean hasAirAround(IBlockAccess world, int bx, int by, int bz)
	{
		return TFGenerator.hasAirAround(world, bx, by, bz);
	}

	protected void setBlock(World world, int x, int y, int z, Block block)
	{
		this.func_150515_a(world, x, y, z, block);
	}

	protected void setBlockAndMetadata(World world, int x, int y, int z, Block block, int meta)
	{
		this.setBlockAndNotifyAdequately(world, x, y, z, block, meta);
	}

	public void makeLeafCircle(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue)
	{
		this.makeLeafCircle(world, sx, sy, sz, rad, blockValue, metaValue, false);
	}

	protected void drawBresehnam(World world, int x1, int y1, int z1, int x2, int y2, int z2, Block blockValue, int metaValue)
	{
		ChunkCoordinates[] lineArray = getBresehnamArrayCoords(x1, y1, z1, x2, y2, z2);
		int var12 = lineArray.length;

		for (int var13 = 0; var13 < var12; ++var13)
		{
			ChunkCoordinates pixel = lineArray[var13];
			this.setBlockAndMetadata(world, pixel.posX, pixel.posY, pixel.posZ, blockValue, metaValue);
		}

	}

	public void makeLeafCircle(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue, boolean useHack)
	{
		for (byte dx = 0; dx <= rad; ++dx)
		{
			for (byte dz = 0; dz <= rad; ++dz)
			{
				int dist = Math.max(dx, dz) + (Math.min(dx, dz) >> 1);
				if (useHack && dx == 3 && dz == 3)
					dist = 6;

				if (dist <= rad)
				{
					this.putLeafBlock(world, sx + dx, sy, sz + dz, blockValue, metaValue);
					this.putLeafBlock(world, sx + dx, sy, sz - dz, blockValue, metaValue);
					this.putLeafBlock(world, sx - dx, sy, sz + dz, blockValue, metaValue);
					this.putLeafBlock(world, sx - dx, sy, sz - dz, blockValue, metaValue);
				}
			}
		}

	}

	public void makeLeafCircle2(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue, boolean useHack)
	{
		for (byte dx = 0; dx <= rad; ++dx)
		{
			for (byte dz = 0; dz <= rad; ++dz)
			{
				if (dx * dx + dz * dz <= rad * rad)
				{
					this.putLeafBlock(world, sx + 1 + dx, sy, sz + 1 + dz, blockValue, metaValue);
					this.putLeafBlock(world, sx + 1 + dx, sy, sz - dz, blockValue, metaValue);
					this.putLeafBlock(world, sx - dx, sy, sz + 1 + dz, blockValue, metaValue);
					this.putLeafBlock(world, sx - dx, sy, sz - dz, blockValue, metaValue);
				}
			}
		}

	}

	public void drawLeafBlob(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue)
	{
		for (byte dx = 0; dx <= rad; ++dx)
		{
			for (byte dy = 0; dy <= rad; ++dy)
			{
				for (byte dz = 0; dz <= rad; ++dz)
				{
					boolean dist = false;
					int var12;
					if (dx >= dy && dx >= dz)
						var12 = dx + (Math.max(dy, dz) >> 1) + (Math.min(dy, dz) >> 2);
					else if (dy >= dx && dy >= dz)
						var12 = dy + (Math.max(dx, dz) >> 1) + (Math.min(dx, dz) >> 2);
					else
						var12 = dz + (Math.max(dx, dy) >> 1) + (Math.min(dx, dy) >> 2);

					if (var12 <= rad)
					{
						this.putLeafBlock(world, sx + dx, sy + dy, sz + dz, blockValue, metaValue);
						this.putLeafBlock(world, sx + dx, sy + dy, sz - dz, blockValue, metaValue);
						this.putLeafBlock(world, sx - dx, sy + dy, sz + dz, blockValue, metaValue);
						this.putLeafBlock(world, sx - dx, sy + dy, sz - dz, blockValue, metaValue);
						this.putLeafBlock(world, sx + dx, sy - dy, sz + dz, blockValue, metaValue);
						this.putLeafBlock(world, sx + dx, sy - dy, sz - dz, blockValue, metaValue);
						this.putLeafBlock(world, sx - dx, sy - dy, sz + dz, blockValue, metaValue);
						this.putLeafBlock(world, sx - dx, sy - dy, sz - dz, blockValue, metaValue);
					}
				}
			}
		}

	}

	public void putLeafBlock(World world, int x, int y, int z, Block blockValue, int metaValue)
	{
		Block whatsThere = world.getBlock(x, y, z);
		if (whatsThere == null || whatsThere.canBeReplacedByLeaves(world, x, y, z))
			this.setBlockAndMetadata(world, x, y, z, blockValue, metaValue);
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
