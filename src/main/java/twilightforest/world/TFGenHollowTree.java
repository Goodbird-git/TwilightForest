package twilightforest.world;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import twilightforest.TFTreasure;
import twilightforest.block.TFBlocks;
import twilightforest.entity.TFCreatures;

import java.util.Random;

public class TFGenHollowTree extends TFGenerator
{
	private static final int LEAF_DUNGEON_CHANCE = 8;
	protected Block treeBlock;
	protected int treeMeta;
	protected int branchMeta;
	protected Block leafBlock;
	protected int leafMeta;
	protected Block rootBlock;
	protected int rootMeta;

	public TFGenHollowTree()
	{
		this(false);
	}

	public TFGenHollowTree(boolean par1)
	{
		super(par1);
		this.treeBlock = TFBlocks.log;
		this.treeMeta = 0;
		this.branchMeta = 12;
		this.leafBlock = TFBlocks.leaves;
		this.leafMeta = 0;
		this.rootBlock = TFBlocks.root;
		this.rootMeta = 0;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		int height = random.nextInt(64) + 32;
		int diameter = random.nextInt(4) + 1;
		if (y >= 1 && y + height + diameter <= TFWorld.MAXHEIGHT)
		{
			int crownRadius = diameter * 4 + 8;

			int numFireflies;
			int numBranches;
			for (int j1 = -crownRadius; j1 <= crownRadius; ++j1)
			{
				for (numFireflies = -crownRadius; numFireflies <= crownRadius; ++numFireflies)
				{
					for (numBranches = height - crownRadius; numBranches <= height + crownRadius; ++numBranches)
					{
						Block i = world.getBlock(j1 + x, numBranches + y, numFireflies + z);
						if (i != Blocks.air && i != Blocks.leaves)
							return false;
					}
				}
			}

			Block var16 = world.getBlock(x, y - 1, z);
			if (var16 != Blocks.grass && var16 != Blocks.dirt)
				return false;
			else
			{
				this.buildTrunk(world, random, x, y, z, diameter, height);
				numFireflies = random.nextInt(3 * diameter) + 5;

				double branchHeight;
				int var17;
				for (numBranches = 0; numBranches <= numFireflies; ++numBranches)
				{
					var17 = (int) (height * random.nextDouble() * 0.9D) + height / 10;
					branchHeight = random.nextDouble();
					this.addFirefly(world, x, y, z, diameter, var17, branchHeight);
				}

				numFireflies = random.nextInt(3 * diameter) + 5;

				for (numBranches = 0; numBranches <= numFireflies; ++numBranches)
				{
					var17 = (int) (height * random.nextDouble() * 0.9D) + height / 10;
					branchHeight = random.nextDouble();
					this.addCicada(world, x, y, z, diameter, var17, branchHeight);
				}

				this.buildFullCrown(world, random, x, y, z, diameter, height);
				numBranches = random.nextInt(3) + 3;

				for (var17 = 0; var17 <= numBranches; ++var17)
				{
					int var18 = (int) (height * random.nextDouble() * 0.9D) + height / 10;
					double branchRotation = random.nextDouble();
					this.makeSmallBranch(world, random, x, y, z, diameter, var18, 4.0D, branchRotation, 0.35D, true);
				}

				this.buildBranchRing(world, random, x, y, z, diameter, 3, 2, 6, 0, 0.75D, 0.0D, 3, 5, 3, false);
				this.buildBranchRing(world, random, x, y, z, diameter, 1, 2, 8, 0, 0.9D, 0.0D, 3, 5, 3, false);
				return true;
			}
		}
		else
			return false;
	}

	protected void buildFullCrown(World world, Random random, int x, int y, int z, int diameter, int height)
	{
		int crownRadius = diameter * 4 + 4;
		int bvar = diameter + 2;
		this.buildBranchRing(world, random, x, y, z, diameter, height - crownRadius, 0, crownRadius, 0, 0.35D, 0.0D, bvar, bvar + 2, 2, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height - crownRadius / 2, 0, crownRadius, 0, 0.28D, 0.0D, bvar, bvar + 2, 1, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius, 0, 0.15D, 0.0D, 2, 4, 2, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius / 2, 0, 0.05D, 0.0D, bvar, bvar + 2, 1, true);
	}

	protected void buildWeakCrown(World world, Random random, int x, int y, int z, int diameter, int height)
	{
		byte crownRadius = 8;
		byte bvar = 2;
		this.buildBranchRing(world, random, x, y, z, diameter, height - crownRadius, 0, crownRadius, 0, 0.35D, 0.0D, bvar, bvar + 2, 1, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height - crownRadius / 2, 0, crownRadius, 0, 0.28D, 0.0D, bvar, bvar + 2, 1, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius, 0, 0.15D, 0.0D, 2, 4, 1, true);
		this.buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius / 2, 0, 0.05D, 0.0D, bvar, bvar + 2, 1, true);
	}

	protected void buildBranchRing(World world, Random random, int x, int y, int z, int diameter, int branchHeight, int heightVar, int length, int lengthVar, double tilt, double tiltVar, int minBranches, int maxBranches, int size, boolean leafy)
	{
		int numBranches = random.nextInt(maxBranches - minBranches) + minBranches;
		double branchRotation = 1.0D / (numBranches + 1);
		double branchOffset = random.nextDouble();

		for (int i = 0; i <= numBranches; ++i)
		{
			int dHeight;
			if (heightVar > 0)
				dHeight = branchHeight - heightVar + random.nextInt(2 * heightVar);
			else
				dHeight = branchHeight;

			if (size == 2)
				this.makeLargeBranch(world, random, x, y, z, diameter, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
			else if (size == 1)
				this.makeMedBranch(world, random, x, y, z, diameter, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
			else if (size == 3)
				this.makeRoot(world, random, x, y, z, diameter, dHeight, length, i * branchRotation + branchOffset, tilt);
			else
				this.makeSmallBranch(world, random, x, y, z, diameter, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
		}

	}

	protected void buildTrunk(World world, Random random, int x, int y, int z, int diameter, int height)
	{
		int hollow = diameter / 2;

		int dx;
		int dz;
		int dy;
		int ax;
		int az;
		int dist;
		for (dx = -diameter; dx <= diameter; ++dx)
		{
			for (dz = -diameter; dz <= diameter; ++dz)
			{
				for (dy = -4; dy < 0; ++dy)
				{
					ax = Math.abs(dx);
					az = Math.abs(dz);
					dist = (int) (Math.max(ax, az) + Math.min(ax, az) * 0.5D);
					if (dist <= diameter)
						if (hasAirAround(world, dx + x, dy + y, dz + z))
							this.setBlockAndMetadata(world, dx + x, dy + y, dz + z, this.treeBlock, dist > hollow ? this.treeMeta : this.branchMeta);
						else
							this.setBlockAndMetadata(world, dx + x, dy + y, dz + z, this.rootBlock, this.rootMeta);
				}
			}
		}

		for (dx = -diameter; dx <= diameter; ++dx)
		{
			for (dz = -diameter; dz <= diameter; ++dz)
			{
				for (dy = 0; dy <= height; ++dy)
				{
					ax = Math.abs(dx);
					az = Math.abs(dz);
					dist = (int) (Math.max(ax, az) + Math.min(ax, az) * 0.5D);
					if (dist <= diameter && dist > hollow)
						this.setBlockAndMetadata(world, dx + x, dy + y, dz + z, this.treeBlock, this.treeMeta);

					if (dist <= hollow)
						;

					if (dist == hollow && dx == hollow)
						this.setBlockAndMetadata(world, dx + x, dy + y, dz + z, Blocks.vine, 8);
				}
			}
		}

	}

	protected void makeMedBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy)
	{
		int sy = y + branchHeight;
		int[] src = translate(x, sy, z, diameter, angle, 0.5D);
		this.makeMedBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);
	}

	protected void makeMedBranch(World world, Random random, int sx, int sy, int sz, double length, double angle, double tilt, boolean leafy)
	{
		int[] src = { sx, sy, sz };
		int[] dest = translate(src[0], src[1], src[2], length, angle, tilt);
		this.drawBresehnam(world, src[0], src[1], src[2], dest[0], dest[1], dest[2], this.treeBlock, this.branchMeta);
		if (leafy)
			this.drawLeafBlob(world, dest[0], dest[1], dest[2], 2, this.leafBlock, this.leafMeta);

		int numShoots = random.nextInt(2) + 1;
		double angleInc = 0.8D / numShoots;

		for (int i = 0; i <= numShoots; ++i)
		{
			double angleVar = angleInc * i - 0.4D;
			double outVar = random.nextDouble() * 0.8D + 0.2D;
			double tiltVar = random.nextDouble() * 0.75D + 0.15D;
			int[] bsrc = translate(src[0], src[1], src[2], length * outVar, angle, tilt);
			double slength = length * 0.4D;
			this.makeSmallBranch(world, random, bsrc[0], bsrc[1], bsrc[2], slength, angle + angleVar, tilt * tiltVar, leafy);
		}

	}

	protected void makeSmallBranch(World world, Random random, int sx, int sy, int sz, double length, double angle, double tilt, boolean leafy)
	{
		int[] src = { sx, sy, sz };
		int[] dest = translate(src[0], src[1], src[2], length, angle, tilt);
		this.drawBresehnam(world, src[0], src[1], src[2], dest[0], dest[1], dest[2], this.treeBlock, this.branchMeta);
		if (leafy)
		{
			byte leafRad = (byte) (random.nextInt(2) + 1);
			this.drawLeafBlob(world, dest[0], dest[1], dest[2], leafRad, this.leafBlock, this.leafMeta);
		}

	}

	protected void makeSmallBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy)
	{
		int sy = y + branchHeight;
		int[] src = translate(x, sy, z, diameter, angle, 0.5D);
		this.makeSmallBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);
	}

	protected void makeRoot(World world, Random random, int x, int y, int z, int diameter, int branchHeight, double length, double angle, double tilt)
	{
		ChunkCoordinates src = translateCoords(x, y + branchHeight, z, diameter, angle, 0.5D);
		ChunkCoordinates dest = translateCoords(src.posX, src.posY, src.posZ, length, angle, tilt);
		ChunkCoordinates[] lineArray = getBresehnamArrayCoords(src, dest);
		boolean stillAboveGround = true;
		int var19 = lineArray.length;

		for (int var20 = 0; var20 < var19; ++var20)
		{
			ChunkCoordinates coord = lineArray[var20];
			if (stillAboveGround && hasAirAround(world, coord.posX, coord.posY, coord.posZ))
			{
				this.setBlockAndMetadata(world, coord.posX, coord.posY, coord.posZ, this.treeBlock, this.branchMeta);
				this.setBlockAndMetadata(world, coord.posX, coord.posY - 1, coord.posZ, this.treeBlock, this.branchMeta);
			}
			else
			{
				this.setBlockAndMetadata(world, coord.posX, coord.posY, coord.posZ, this.rootBlock, this.rootMeta);
				this.setBlockAndMetadata(world, coord.posX, coord.posY - 1, coord.posZ, this.rootBlock, this.rootMeta);
				stillAboveGround = false;
			}
		}

	}

	protected void makeLargeBranch(World world, Random random, int sx, int sy, int sz, double length, double angle, double tilt, boolean leafy)
	{
		int[] src = { sx, sy, sz };
		int[] dest = translate(src[0], src[1], src[2], length, angle, tilt);
		this.drawBresehnam(world, src[0], src[1], src[2], dest[0], dest[1], dest[2], this.treeBlock, this.branchMeta);
		int reinforcements = random.nextInt(3);

		int numMedBranches;
		int numSmallBranches;
		int i;
		for (numMedBranches = 0; numMedBranches <= reinforcements; ++numMedBranches)
		{
			numSmallBranches = (numMedBranches & 2) == 0 ? 1 : 0;
			i = (numMedBranches & 1) == 0 ? 1 : -1;
			int outVar = (numMedBranches & 2) == 0 ? 0 : 1;
			this.drawBresehnam(world, src[0] + numSmallBranches, src[1] + i, src[2] + outVar, dest[0], dest[1], dest[2], this.treeBlock, this.branchMeta);
		}

		if (leafy)
			this.drawLeafBlob(world, dest[0], dest[1] + 1, dest[2], 3, this.leafBlock, this.leafMeta);

		numMedBranches = random.nextInt((int) (length / 6.0D)) + random.nextInt(2) + 1;

		for (numSmallBranches = 0; numSmallBranches <= numMedBranches; ++numSmallBranches)
		{
			double var24 = random.nextDouble() * 0.3D + 0.3D;
			double angleVar = random.nextDouble() * 0.225D * ((numSmallBranches & 1) == 0 ? 1.0D : -1.0D);
			int[] bsrc = translate(src[0], src[1], src[2], length * var24, angle, tilt);
			this.makeMedBranch(world, random, bsrc[0], bsrc[1], bsrc[2], length * 0.6D, angle + angleVar, tilt, leafy);
		}

		numSmallBranches = random.nextInt(2) + 1;

		for (i = 0; i <= numSmallBranches; ++i)
		{
			double var25 = random.nextDouble() * 0.25D + 0.25D;
			double angleVar1 = random.nextDouble() * 0.25D * ((i & 1) == 0 ? 1.0D : -1.0D);
			int[] bsrc1 = translate(src[0], src[1], src[2], length * var25, angle, tilt);
			this.makeSmallBranch(world, random, bsrc1[0], bsrc1[1], bsrc1[2], Math.max(length * 0.3D, 2.0D), angle + angleVar1, tilt, leafy);
		}

		if (random.nextInt(8) == 0)
			this.makeLeafDungeon(world, random, dest[0], dest[1] + 1, dest[2]);

	}

	private void makeLeafDungeon(World world, Random random, int x, int y, int z)
	{
		this.drawLeafBlob(world, x, y, z, 4, this.leafBlock, this.leafMeta);
		this.drawBlob(world, x, y, z, 3, this.treeBlock, this.branchMeta);
		this.drawBlob(world, x, y, z, 2, Blocks.air, 0);
		world.setBlock(x, y + 1, z, Blocks.mob_spawner, 0, 2);
		TileEntityMobSpawner ms = (TileEntityMobSpawner) world.getTileEntity(x, y + 1, z);
		if (ms != null)
			ms.func_145881_a().setEntityName(TFCreatures.getSpawnerNameFor("Swarm Spider"));

		this.makeLeafDungeonChest(world, random, x, y, z);
	}

	private void makeLeafDungeonChest(World world, Random random, int x, int y, int z)
	{
		int dir = random.nextInt(4);
		x += Direction.offsetX[dir];
		x += Direction.offsetX[dir];
		z += Direction.offsetZ[dir];
		z += Direction.offsetZ[dir];
		TFTreasure.tree_cache.generate(world, random, x, y - 1, z);
	}

	protected void makeLargeBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight, double length, double angle, double tilt, boolean leafy)
	{
		int sy = y + branchHeight;
		int[] src = translate(x, sy, z, diameter, angle, 0.5D);
		this.makeLargeBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);
	}

	protected void addFirefly(World world, int x, int y, int z, int diameter, int fHeight, double fAngle)
	{
		int[] src = translate(x, y + fHeight, z, diameter + 1, fAngle, 0.5D);
		fAngle %= 1.0D;
		byte tmeta = 0;
		if (fAngle <= 0.875D && fAngle > 0.125D)
		{
			if (fAngle > 0.125D && fAngle <= 0.375D)
				tmeta = 1;
			else if (fAngle > 0.375D && fAngle <= 0.625D)
				tmeta = 4;
			else if (fAngle > 0.625D && fAngle <= 0.875D)
				tmeta = 2;
		}
		else
			tmeta = 3;

		if (TFBlocks.firefly.canPlaceBlockAt(world, src[0], src[1], src[2]))
			this.setBlockAndMetadata(world, src[0], src[1], src[2], TFBlocks.firefly, tmeta);

	}

	protected void addCicada(World world, int x, int y, int z, int diameter, int fHeight, double fAngle)
	{
		int[] src = translate(x, y + fHeight, z, diameter + 1, fAngle, 0.5D);
		fAngle %= 1.0D;
		byte tmeta = 1;
		if (fAngle <= 0.875D && fAngle > 0.125D)
		{
			if (fAngle > 0.125D && fAngle <= 0.375D)
				tmeta = 1;
			else if (fAngle > 0.375D && fAngle <= 0.625D)
				tmeta = 4;
			else if (fAngle > 0.625D && fAngle <= 0.875D)
				tmeta = 2;
		}
		else
			tmeta = 3;

		if (TFBlocks.cicada.canPlaceBlockAt(world, src[0], src[1], src[2]))
			this.setBlockAndMetadata(world, src[0], src[1], src[2], TFBlocks.cicada, tmeta);
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
