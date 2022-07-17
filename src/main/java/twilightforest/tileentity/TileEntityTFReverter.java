package twilightforest.tileentity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import twilightforest.block.TFBlocks;

import java.util.Random;

public class TileEntityTFReverter extends TileEntity
{
	private static final int REVERT_CHANCE = 10;
	public int radius = 4;
	public int diameter;
	public double requiredPlayerRange;
	public Random rand;
	private int tickCount;
	private boolean slowScan;
	private int ticksSinceChange;
	private Block[] blockData;
	private byte[] metaData;

	public TileEntityTFReverter()
	{
		this.diameter = 2 * this.radius + 1;
		this.requiredPlayerRange = 16.0D;
		this.rand = new Random();
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		if (this.anyPlayerInRange())
		{
			++this.tickCount;
			if (this.worldObj.isRemote)
			{
				double var1 = this.xCoord + this.worldObj.rand.nextFloat();
				double var3 = this.yCoord + this.worldObj.rand.nextFloat();
				double var5 = this.zCoord + this.worldObj.rand.nextFloat();
				this.worldObj.spawnParticle("reddust", var1, var3, var5, 0.0D, 0.0D, 0.0D);
				if (this.rand.nextInt(10) == 0)
				{
					this.makeRandomOutline();
					this.makeRandomOutline();
					this.makeRandomOutline();
				}
			}
			else
			{
				if (this.blockData == null || this.metaData == null)
				{
					this.captureBlockData();
					this.slowScan = true;
				}

				if (!this.slowScan || this.tickCount % 20 == 0)
					if (this.scanAndRevertChanges())
					{
						this.slowScan = false;
						this.ticksSinceChange = 0;
					}
					else
					{
						++this.ticksSinceChange;
						if (this.ticksSinceChange > 20)
							this.slowScan = true;
					}
			}
		}
		else
		{
			this.blockData = null;
			this.metaData = null;
			this.tickCount = 0;
		}

	}

	private void makeRandomOutline()
	{
		this.makeOutline(this.rand.nextInt(12));
	}

	private void makeOutline(int outline)
	{
		double sx = this.xCoord;
		double sy = this.yCoord;
		double sz = this.zCoord;
		double dx = this.xCoord;
		double dy = this.yCoord;
		double dz = this.zCoord;
		switch (outline)
		{
			case 0:
			case 8:
				sx -= this.radius;
				dx += this.radius + 1;
				sz -= this.radius;
				dz -= this.radius;
				break;
			case 1:
			case 9:
				sx -= this.radius;
				dx -= this.radius;
				sz -= this.radius;
				dz += this.radius + 1;
				break;
			case 2:
			case 10:
				sx -= this.radius;
				dx += this.radius + 1;
				sz += this.radius + 1;
				dz += this.radius + 1;
				break;
			case 3:
			case 11:
				sx += this.radius + 1;
				dx += this.radius + 1;
				sz -= this.radius;
				dz += this.radius + 1;
				break;
			case 4:
				sx -= this.radius;
				dx -= this.radius;
				sz -= this.radius;
				dz -= this.radius;
				break;
			case 5:
				sx += this.radius + 1;
				dx += this.radius + 1;
				sz -= this.radius;
				dz -= this.radius;
				break;
			case 6:
				sx += this.radius + 1;
				dx += this.radius + 1;
				sz += this.radius + 1;
				dz += this.radius + 1;
				break;
			case 7:
				sx -= this.radius;
				dx -= this.radius;
				sz += this.radius + 1;
				dz += this.radius + 1;
		}

		switch (outline)
		{
			case 0:
			case 1:
			case 2:
			case 3:
				sy += this.radius + 1;
				dy += this.radius + 1;
				break;
			case 4:
			case 5:
			case 6:
			case 7:
				sy -= this.radius;
				dy += this.radius + 1;
				break;
			case 8:
			case 9:
			case 10:
			case 11:
				sy -= this.radius;
				dy -= this.radius;
		}

		if (this.rand.nextBoolean())
			this.drawParticleLine(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, dx, dy, dz);
		else
			this.drawParticleLine(sx, sy, sz, this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D);

		this.drawParticleLine(sx, sy, sz, dx, dy, dz);
	}

	protected void drawParticleLine(double srcX, double srcY, double srcZ, double destX, double destY, double destZ)
	{
		byte particles = 16;

		for (int i = 0; i < particles; ++i)
		{
			double trailFactor = i / (particles - 1.0D);
			double tx = srcX + (destX - srcX) * trailFactor + this.rand.nextFloat() * 0.005D;
			double ty = srcY + (destY - srcY) * trailFactor + this.rand.nextFloat() * 0.005D;
			double tz = srcZ + (destZ - srcZ) * trailFactor + this.rand.nextFloat() * 0.005D;
			this.worldObj.spawnParticle("reddust", tx, ty, tz, 0.0D, 0.0D, 0.0D);
		}

	}

	private boolean scanAndRevertChanges()
	{
		int index = 0;
		boolean reverted = false;

		for (int x = -this.radius; x <= this.radius; ++x)
		{
			for (int y = -this.radius; y <= this.radius; ++y)
			{
				for (int z = -this.radius; z <= this.radius; ++z)
				{
					Block blockID = this.worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z);
					byte meta = (byte) this.worldObj.getBlockMetadata(this.xCoord + x, this.yCoord + y, this.zCoord + z);
					if (this.blockData[index] != blockID)
						if (this.revertBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z, blockID, meta, this.blockData[index], this.metaData[index]))
							reverted = true;
						else
						{
							this.blockData[index] = blockID;
							this.metaData[index] = meta;
						}

					++index;
				}
			}
		}

		return reverted;
	}

	private boolean revertBlock(int x, int y, int z, Block thereBlockID, byte thereMeta, Block replaceBlockID, byte replaceMeta)
	{
		if (thereBlockID == Blocks.air && !replaceBlockID.getMaterial().blocksMovement())
		{
			System.out.println("Not replacing block " + replaceBlockID + " because it doesn\'t block movement");
			return false;
		}
		else if (this.isUnrevertable(thereBlockID, thereMeta, replaceBlockID, replaceMeta))
			return false;
		else
		{
			if (this.rand.nextInt(10) == 0)
			{
				if (replaceBlockID != Blocks.air)
				{
					replaceBlockID = TFBlocks.towerTranslucent;
					replaceMeta = 4;
				}

				// TODO gamerforEA code clear: super.worldObj.setBlock(x, y, z, replaceBlockID, replaceMeta, 2);

				if (thereBlockID == Blocks.air)
					this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(replaceBlockID) + (replaceMeta << 12));
				else if (replaceBlockID == Blocks.air)
				{
					this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(thereBlockID) + (thereMeta << 12));
					thereBlockID.dropBlockAsItem(this.worldObj, x, y, z, thereMeta, 0);
				}

				// TODO gamerforEA code start
				this.worldObj.setBlock(x, y, z, replaceBlockID, replaceMeta, 2);
				// TODO gamerforEA code end
			}

			return true;
		}
	}

	private boolean isUnrevertable(Block thereBlockID, byte thereMeta, Block replaceBlockID, byte replaceMeta)
	{
		return thereBlockID == TFBlocks.towerDevice || replaceBlockID == TFBlocks.towerDevice || thereBlockID == TFBlocks.towerTranslucent && thereMeta != 4 || replaceBlockID == TFBlocks.towerTranslucent && replaceMeta != 4 || thereBlockID == Blocks.redstone_lamp && replaceBlockID == Blocks.lit_redstone_lamp || thereBlockID == Blocks.lit_redstone_lamp && replaceBlockID == Blocks.redstone_lamp || thereBlockID == Blocks.water || replaceBlockID == Blocks.flowing_water || thereBlockID == Blocks.flowing_water || replaceBlockID == Blocks.water || replaceBlockID == Blocks.tnt;
	}

	private void captureBlockData()
	{
		this.blockData = new Block[this.diameter * this.diameter * this.diameter];
		this.metaData = new byte[this.diameter * this.diameter * this.diameter];
		int index = 0;

		for (int x = -this.radius; x <= this.radius; ++x)
		{
			for (int y = -this.radius; y <= this.radius; ++y)
			{
				for (int z = -this.radius; z <= this.radius; ++z)
				{
					Block blockID = this.worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z);
					int meta = this.worldObj.getBlockMetadata(this.xCoord + x, this.yCoord + y, this.zCoord + z);
					this.blockData[index] = blockID;
					this.metaData[index] = (byte) meta;
					++index;
				}
			}
		}

	}

	public boolean anyPlayerInRange()
	{
		return this.worldObj.getClosestPlayer(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, this.requiredPlayerRange) != null;
	}
}
