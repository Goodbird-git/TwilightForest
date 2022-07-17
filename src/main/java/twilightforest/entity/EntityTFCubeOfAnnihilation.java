package twilightforest.entity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.item.ItemTFCubeOfAnnihilation;

import java.util.List;

public class EntityTFCubeOfAnnihilation extends EntityThrowable
{
	boolean hasHitObstacle = false;

	// TODO gamerforEA code start
	public final FakePlayerContainer fake = ModUtils.NEXUS_FACTORY.wrapFake(this);

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		this.fake.writeToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.fake.readFromNBT(nbt);
	}
	// TODO gamerforEA code end

	public EntityTFCubeOfAnnihilation(World par1World)
	{
		super(par1World);
		this.setSize(1.1F, 1.0F);
		this.isImmuneToFire = true;
	}

	public EntityTFCubeOfAnnihilation(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
		this.setSize(1.0F, 1.0F);
		this.isImmuneToFire = true;
	}

	public EntityTFCubeOfAnnihilation(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
		this.setSize(1.0F, 1.0F);
		this.isImmuneToFire = true;

		// TODO gamerforEA code start
		if (par2EntityLiving instanceof EntityPlayer)
			this.fake.setRealPlayer((EntityPlayer) par2EntityLiving);
		// TODO gamerforEA code end
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.0F;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		// TODO gamerforEA add condition [2]
		if (mop.entityHit instanceof EntityLivingBase && !this.fake.cantDamage(mop.entityHit) && mop.entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), 10.0F))
			this.ticksExisted += 60;

		if (!this.worldObj.isAirBlock(mop.blockX, mop.blockY, mop.blockZ) && !this.worldObj.isRemote)
			this.affectBlocksInAABB(this.boundingBox.expand(0.20000000298023224D, 0.20000000298023224D, 0.20000000298023224D), this.getThrower());
	}

	private boolean affectBlocksInAABB(AxisAlignedBB par1AxisAlignedBB, EntityLivingBase entity)
	{
		int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
		int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
		int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
		int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
		int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
		int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
		boolean hitBlock = false;

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dy = minY; dy <= maxY; ++dy)
			{
				for (int dz = minZ; dz <= maxZ; ++dz)
				{
					Block block = this.worldObj.getBlock(dx, dy, dz);
					int currentMeta = this.worldObj.getBlockMetadata(dx, dy, dz);
					if (block != Blocks.air)
					{
						// TODO gamerforEA add condition [2]
						if (this.canAnnihilate(dx, dy, dz, block, currentMeta) && !this.fake.cantBreak(dx, dy, dz))
						{
							this.worldObj.setBlockToAir(dx, dy, dz);
							this.worldObj.playSoundAtEntity(this, "random.fizz", 0.125F, this.rand.nextFloat() * 0.25F + 0.75F);
							this.sendAnnihilateBlockPacket(this.worldObj, dx, dy, dz);
						}
						else
							this.hasHitObstacle = true;

						hitBlock = true;
					}
				}
			}
		}

		return hitBlock;
	}

	private boolean canAnnihilate(int dx, int dy, int dz, Block block, int meta)
	{
		return block == TFBlocks.deadrock || block == TFBlocks.castleBlock || block == TFBlocks.castleMagic && meta != 3 || block == TFBlocks.forceField || block == TFBlocks.thorns || block.getExplosionResistance(this) < 8.0F && block.getBlockHardness(this.worldObj, dx, dy, dz) >= 0.0F;
	}

	private void sendAnnihilateBlockPacket(World world, int x, int y, int z)
	{
		FMLProxyPacket message = TFGenericPacketHandler.makeAnnihilateBlockPacket(x, y, z);
		TargetPoint targetPoint = new TargetPoint(world.provider.dimensionId, x, y, z, 64.0D);
		TwilightForestMod.genericChannel.sendToAllAround(message, targetPoint);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (!this.worldObj.isRemote)
		{
			if (this.getThrower() == null)
			{
				this.setDead();
				return;
			}

			if (this.isReturning())
			{
				List destPoint = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
				if (destPoint.contains(this.getThrower()) && !this.worldObj.isRemote)
				{
					if (this.getThrower() instanceof EntityPlayer)
						ItemTFCubeOfAnnihilation.setCubeAsReturned((EntityPlayer) this.getThrower());

					this.setDead();
				}
			}

			Vec3 destPoint1 = Vec3.createVectorHelper(this.getThrower().posX, this.getThrower().posY + this.getThrower().getEyeHeight(), this.getThrower().posZ);
			Vec3 velocity;
			float currentSpeed;
			if (!this.isReturning())
			{
				velocity = this.getThrower().getLookVec();
				currentSpeed = 16.0F;
				velocity.xCoord *= currentSpeed;
				velocity.yCoord *= currentSpeed;
				velocity.zCoord *= currentSpeed;
				destPoint1.xCoord += velocity.xCoord;
				destPoint1.yCoord += velocity.yCoord;
				destPoint1.zCoord += velocity.zCoord;
			}

			velocity = Vec3.createVectorHelper(this.posX - destPoint1.xCoord, this.posY + this.height / 2.0F - destPoint1.yCoord, this.posZ - destPoint1.zCoord);
			this.motionX -= velocity.xCoord;
			this.motionY -= velocity.yCoord;
			this.motionZ -= velocity.zCoord;
			currentSpeed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			float maxSpeed = 0.5F;
			if (currentSpeed > maxSpeed)
			{
				this.motionX /= currentSpeed / maxSpeed;
				this.motionY /= currentSpeed / maxSpeed;
				this.motionZ /= currentSpeed / maxSpeed;
			}
			else
			{
				float slow = 0.5F;
				this.motionX *= slow;
				this.motionY *= slow;
				this.motionZ *= slow;
			}

			this.affectBlocksInAABB(this.boundingBox.expand(0.20000000298023224D, 0.20000000298023224D, 0.20000000298023224D), this.getThrower());
		}

	}

	public boolean isReturning()
	{
		if (!this.hasHitObstacle && this.getThrower() != null && this.getThrower() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) this.getThrower();
			return !player.isUsingItem();
		}
		else
			return true;
	}

	@Override
	protected float func_70182_d()
	{
		return 1.5F;
	}
}
