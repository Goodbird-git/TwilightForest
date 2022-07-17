package twilightforest.entity.boss;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import twilightforest.TwilightForestMod;

import java.util.ArrayList;

public class EntityTFFallingIce extends Entity
{
	private static final int HANG_TIME = 100;
	private int fallTime;
	private float hurtAmount;
	private int hurtMax;

	// TODO gamerforEA code start
	public final FakePlayerContainer fake = ModUtils.NEXUS_FACTORY.wrapFake(this);
	// TODO gamerforEA code end

	public EntityTFFallingIce(World par1World)
	{
		super(par1World);
		this.setSize(2.98F, 2.98F);
		this.hurtAmount = 10.0F;
		this.hurtMax = 30;
	}

	public EntityTFFallingIce(World par1World, int x, int y, int z)
	{
		this(par1World);
		this.preventEntitySpawning = true;
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void entityInit()
	{
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		++this.fallTime;
		if (this.fallTime > 100)
			this.motionY -= 0.03999999910593033D;

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;
		if (!this.worldObj.isRemote && this.onGround)
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
			this.setDead();
		}

		if (!this.worldObj.isRemote)
		{
			for (Entity entity : new ArrayList<Entity>(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox)))
			{
				if (entity instanceof EntityTFFallingIce)
				{
					EntityTFFallingIce otherIce = (EntityTFFallingIce) entity;
					if (otherIce.getFallTime() < this.fallTime)
						otherIce.setDead();
				}
			}

			this.destroyIceInAABB(this.boundingBox.expand(0.5D, 0.0D, 0.5D));
		}

		this.makeTrail();
	}

	public void makeTrail()
	{
		for (int i = 0; i < 2; ++i)
		{
			double dx = this.posX + 2.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			double dy = this.posY - 3.0D + 3.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			double dz = this.posZ + 2.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowwarning", dx, dy, dz, 0.0D, -1.0D, 0.0D);
		}

	}

	@Override
	protected void fall(float par1)
	{
		int distance = MathHelper.ceiling_float_int(par1 - 1.0F);
		if (distance > 0)
		{
			ArrayList<Entity> nearby = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(2.0D, 0.0D, 2.0D)));
			DamageSource damagesource = DamageSource.fallingBlock;

			for (Entity entity : nearby)
			{
				if (!(entity instanceof EntityTFYetiAlpha))
				{
					// TODO gamerforEA code start
					if (this.fake.cantDamage(entity))
						continue;
					// TODO gamerforEA code end

					entity.attackEntityFrom(damagesource, Math.min(MathHelper.floor_float(distance * this.hurtAmount), this.hurtMax));
				}
			}
		}

		for (int i = 0; i < 200; ++i)
		{
			double dx = this.posX + 3.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			double dy = this.posY + 2.0D + 3.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			double dz = this.posZ + 3.0F * (this.rand.nextFloat() - this.rand.nextFloat());
			this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(Blocks.packed_ice) + "_0", dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}

		this.playSound(Blocks.anvil.stepSound.getBreakSound(), 3.0F, 0.5F);
		this.playSound(Blocks.packed_ice.stepSound.getBreakSound(), 3.0F, 0.5F);
	}

	public void destroyIceInAABB(AxisAlignedBB par1AxisAlignedBB)
	{
		int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
		int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
		int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
		int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
		int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
		int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dy = minY; dy <= maxY; ++dy)
			{
				for (int dz = minZ; dz <= maxZ; ++dz)
				{
					Block block = this.worldObj.getBlock(dx, dy, dz);
					if (block == Blocks.ice || block == Blocks.packed_ice || block == Blocks.stone)
					{
						// TODO gamerforEA code start
						if (this.fake.cantBreak(dx, dy, dz))
							continue;
						// TODO gamerforEA code end

						this.worldObj.setBlock(dx, dy, dz, Blocks.air, 0, 3);
					}
				}
			}
		}

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1)
	{
		// TODO gamerforEA code start
		this.fake.readFromNBT(var1);
		// TODO gamerforEA code end
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1)
	{
		// TODO gamerforEA code start
		this.fake.writeToNBT(var1);
		// TODO gamerforEA code end
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire()
	{
		return false;
	}

	public Block getBlock()
	{
		return Blocks.packed_ice;
	}

	public int getFallTime()
	{
		return this.fallTime;
	}
}
