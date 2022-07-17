package twilightforest.tileentity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.EventConfig;
import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;

import java.util.Random;

public class TileEntityTFCReactorActive extends TileEntity // TODO gamerforEA use private setBlock() method
{
	int counter = 0;
	int secX;
	int secY;
	int secZ;
	int terX;
	int terY;
	int terZ;

	// TODO gamerforEA code start
	public final FakePlayerContainer fake = ModUtils.NEXUS_FACTORY.wrapFake(this);

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.fake.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.fake.readFromNBT(nbt);
	}

	private boolean setBlock(int x, int y, int z, Block block, int meta, int flag)
	{
		return !this.fake.cantBreak(x, y, z) && this.worldObj.setBlock(x, y, z, block, meta, flag);
	}
	// TODO gamerforEA code end

	public TileEntityTFCReactorActive()
	{
		Random rand = new Random();
		this.secX = 3 * (rand.nextBoolean() ? 1 : -1);
		this.secY = 3 * (rand.nextBoolean() ? 1 : -1);
		this.secZ = 3 * (rand.nextBoolean() ? 1 : -1);
		this.terX = 3 * (rand.nextBoolean() ? 1 : -1);
		this.terY = 3 * (rand.nextBoolean() ? 1 : -1);
		this.terZ = 3 * (rand.nextBoolean() ? 1 : -1);
		if (this.secX == this.terX && this.secY == this.terY && this.secZ == this.terZ)
		{
			this.terX = -this.terX;
			this.terY = -this.terY;
			this.terZ = -this.terZ;
		}
	}

	@Override
	public void updateEntity()
	{
		++this.counter;
		if (!this.worldObj.isRemote)
		{
			byte offset = 10;
			int i;
			if (this.counter % 5 == 0)
			{
				if (this.counter == 5)
				{
					this.setBlock(this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord + 1, this.yCoord + 1, this.zCoord - 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord - 1, this.yCoord + 1, this.zCoord + 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord - 1, this.yCoord + 1, this.zCoord - 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord, this.yCoord + 1, this.zCoord + 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord, this.yCoord + 1, this.zCoord - 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord + 1, this.yCoord + 1, this.zCoord, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord - 1, this.yCoord + 1, this.zCoord, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord + 1, this.yCoord, this.zCoord + 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord + 1, this.yCoord, this.zCoord - 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord - 1, this.yCoord, this.zCoord + 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord - 1, this.yCoord, this.zCoord - 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord, this.yCoord, this.zCoord + 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord, this.yCoord, this.zCoord - 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord + 1, this.yCoord, this.zCoord, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord - 1, this.yCoord, this.zCoord, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord + 1, this.yCoord - 1, this.zCoord + 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord + 1, this.yCoord - 1, this.zCoord - 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord - 1, this.yCoord - 1, this.zCoord + 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord - 1, this.yCoord - 1, this.zCoord - 1, TFBlocks.towerTranslucent, 7, 2);
					this.setBlock(this.xCoord, this.yCoord - 1, this.zCoord + 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord, this.yCoord - 1, this.zCoord - 1, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord + 1, this.yCoord - 1, this.zCoord, TFBlocks.towerTranslucent, 6, 2);
					this.setBlock(this.xCoord - 1, this.yCoord - 1, this.zCoord, TFBlocks.towerTranslucent, 6, 2);
				}

				i = this.counter - 80;
				if (i >= offset && i <= 249)
					this.drawBlob(this.xCoord, this.yCoord, this.zCoord, (i - offset) / 40, Blocks.air, 0, i - offset, false);

				if (i <= 200)
					this.drawBlob(this.xCoord, this.yCoord, this.zCoord, i / 40, TFBlocks.towerTranslucent, 5, this.counter, false);

				int secondary = this.counter - 120;
				if (secondary >= offset && secondary <= 129)
					this.drawBlob(this.xCoord + this.secX, this.yCoord + this.secY, this.zCoord + this.secZ, (secondary - offset) / 40, Blocks.air, 0, secondary - offset, false);

				if (secondary >= 0 && secondary <= 160)
					this.drawBlob(this.xCoord + this.secX, this.yCoord + this.secY, this.zCoord + this.secZ, secondary / 40, Blocks.air, 0, secondary, true);

				int tertiary = this.counter - 160;
				if (tertiary >= offset && tertiary <= 129)
					this.drawBlob(this.xCoord + this.terX, this.yCoord + this.terY, this.zCoord + this.terZ, (tertiary - offset) / 40, Blocks.air, 0, tertiary - offset, false);

				if (tertiary >= 0 && tertiary <= 160)
					this.drawBlob(this.xCoord + this.terX, this.yCoord + this.terY, this.zCoord + this.terZ, tertiary / 40, Blocks.air, 0, tertiary, true);
			}

			if (this.counter >= 350)
			{
				for (i = 0; i < 3; ++i)
				{
					this.spawnGhastNear(this.xCoord + this.secX, this.yCoord + this.secY, this.zCoord + this.secZ);
					this.spawnGhastNear(this.xCoord + this.terX, this.yCoord + this.terY, this.zCoord + this.terZ);
				}

				// TODO gamerforEA use ExplosionByPlayer
				this.fake.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2.0F, true);

				// TODO gamerforEA undo this.setBlock(int, int, int, Block, int, int)
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, Blocks.air, 0, 3);
			}
		}
		else if (this.counter % 5 == 0 && this.counter <= 250)
			this.worldObj.playSound(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "portal.portal", this.counter / 100.0F, this.counter / 100.0F, false);
	}

	private void spawnGhastNear(int x, int y, int z)
	{
		// TODO gamerforEA code start
		if (!EventConfig.enableReactorSpawnGhast)
			return;
		// TODO gamerforEA code end

		EntityTFMiniGhast ghast = new EntityTFMiniGhast(this.worldObj);
		ghast.setLocationAndAngles(x - 1.5D + this.worldObj.rand.nextFloat() * 3.0D, y - 1.5D + this.worldObj.rand.nextFloat() * 3.0D, z - 1.5D + this.worldObj.rand.nextFloat() * 3.0D, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
		this.worldObj.spawnEntityInWorld(ghast);
	}

	public void drawBlob(int sx, int sy, int sz, int rad, Block block, int meta, int fuzz, boolean netherTransform)
	{
		for (byte dx = 0; dx <= rad; ++dx)
		{
			int fuzzX = (fuzz + dx) % 8;

			for (byte dy = 0; dy <= rad; ++dy)
			{
				int fuzzY = (fuzz + dy) % 8;

				for (byte dz = 0; dz <= rad; ++dz)
				{
					boolean dist = false;
					byte var15;
					if (dx >= dy && dx >= dz)
						var15 = (byte) (dx + (byte) (int) (Math.max(dy, dz) * 0.5D + Math.min(dy, dz) * 0.25D));
					else if (dy >= dx && dy >= dz)
						var15 = (byte) (dy + (byte) (int) (Math.max(dx, dz) * 0.5D + Math.min(dx, dz) * 0.25D));
					else
						var15 = (byte) (dz + (byte) (int) (Math.max(dx, dy) * 0.5D + Math.min(dx, dy) * 0.25D));

					if (var15 == rad && (dx != 0 || dy != 0 || dz != 0))
						switch (fuzzX)
						{
							case 0:
								this.transformBlock(sx + dx, sy + dy, sz + dz, block, meta, fuzzY, netherTransform);
								break;
							case 1:
								this.transformBlock(sx + dx, sy + dy, sz - dz, block, meta, fuzzY, netherTransform);
								break;
							case 2:
								this.transformBlock(sx - dx, sy + dy, sz + dz, block, meta, fuzzY, netherTransform);
								break;
							case 3:
								this.transformBlock(sx - dx, sy + dy, sz - dz, block, meta, fuzzY, netherTransform);
								break;
							case 4:
								this.transformBlock(sx + dx, sy - dy, sz + dz, block, meta, fuzzY, netherTransform);
								break;
							case 5:
								this.transformBlock(sx + dx, sy - dy, sz - dz, block, meta, fuzzY, netherTransform);
								break;
							case 6:
								this.transformBlock(sx - dx, sy - dy, sz + dz, block, meta, fuzzY, netherTransform);
								break;
							case 7:
								this.transformBlock(sx - dx, sy - dy, sz - dz, block, meta, fuzzY, netherTransform);
						}
				}
			}
		}
	}

	protected void transformBlock(int x, int y, int z, Block block, int meta, int fuzz, boolean netherTransform)
	{
		Block whatsThere = this.worldObj.getBlock(x, y, z);
		if (whatsThere == Blocks.air || whatsThere.getBlockHardness(this.worldObj, x, y, z) != -1.0F)
		{
			if (fuzz == 0 && whatsThere != Blocks.air)
				this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(whatsThere) + (this.worldObj.getBlockMetadata(x, y, z) << 12));

			if (netherTransform && whatsThere != Blocks.air)
			{
				this.setBlock(x, y, z, Blocks.netherrack, 0, 3);
				if (this.worldObj.getBlock(x, y + 1, z) == Blocks.air && fuzz % 3 == 0)
					this.setBlock(x, y + 1, z, Blocks.fire, 0, 3);
			}
			else
				this.setBlock(x, y, z, block, meta, 3);
		}
	}
}
