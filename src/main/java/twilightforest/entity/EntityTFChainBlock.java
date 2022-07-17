package twilightforest.entity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import twilightforest.item.ItemTFChainBlock;

import java.util.List;

public class EntityTFChainBlock extends EntityThrowable implements IEntityMultiPart
{
	private static final int MAX_SMASH = 12;
	private static final int MAX_CHAIN = 16;
	private boolean isReturning = false;
	private int blocksSmashed = 0;
	private double velX;
	private double velY;
	private double velZ;
	private boolean isAttached;
	private EntityLivingBase attachedTo;
	public EntityTFGoblinChain chain1;
	public EntityTFGoblinChain chain2;
	public EntityTFGoblinChain chain3;
	public EntityTFGoblinChain chain4;
	public EntityTFGoblinChain chain5;
	public Entity[] partsArray;

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

	public EntityTFChainBlock(World par1World)
	{
		super(par1World);
		this.setSize(0.6F, 0.6F);
		this.partsArray = new Entity[] { this.chain1 = new EntityTFGoblinChain(this), this.chain2 = new EntityTFGoblinChain(this), this.chain3 = new EntityTFGoblinChain(this), this.chain4 = new EntityTFGoblinChain(this), this.chain5 = new EntityTFGoblinChain(this) };
	}

	public EntityTFChainBlock(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFChainBlock(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
		this.setSize(0.6F, 0.6F);
		this.isReturning = false;

		// TODO gamerforEA code start
		if (par2EntityLiving instanceof EntityPlayer)
			this.fake.setRealPlayer((EntityPlayer) par2EntityLiving);
		// TODO gamerforEA code end
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float speed, float accuracy)
	{
		super.setThrowableHeading(x, y, z, speed, accuracy);
		this.velX = this.motionX;
		this.velY = this.motionY;
		this.velZ = this.motionZ;
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.05F;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		// TODO gamerforEA add condition
		if (mop.entityHit instanceof EntityLivingBase && mop.entityHit != this.getThrower() && !this.fake.cantDamage(mop.entityHit) && mop.entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), 10F))
			this.ticksExisted += 60;

		if (!this.worldObj.isAirBlock(mop.blockX, mop.blockY, mop.blockZ))
		{
			if (!this.isReturning)
				this.worldObj.playSoundAtEntity(this, "random.anvil_land", 0.125F, this.rand.nextFloat());

			if (!this.worldObj.isRemote && this.blocksSmashed < 12)
			{
				if (this.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ).getBlockHardness(this.worldObj, mop.blockX, mop.blockY, mop.blockZ) > 0.3F)
				{
					this.velX *= 0.6D;
					this.velY *= 0.6D;
					this.velZ *= 0.6D;
					switch (mop.sideHit)
					{
						case 0:
							if (this.velY > 0D)
								this.velY *= -0.6D;
							break;
						case 1:
							if (this.velY < 0D)
								this.velY *= -0.6D;
							break;
						case 2:
							if (this.velZ > 0D)
								this.velZ *= -0.6D;
							break;
						case 3:
							if (this.velZ < 0D)
								this.velZ *= -0.6D;
							break;
						case 4:
							if (this.velX > 0D)
								this.velX *= -0.6D;
							break;
						case 5:
							if (this.velX < 0D)
								this.velX *= -0.6D;
					}
				}

				this.affectBlocksInAABB(this.boundingBox, this.getThrower());
			}

			if (!this.worldObj.isRemote)
				this.isReturning = true;

			if (this.blocksSmashed > 12 && this.ticksExisted < 60)
				this.ticksExisted += 60;
		}
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

		for (int x = minX; x <= maxX; ++x)
		{
			for (int y = minY; y <= maxY; ++y)
			{
				for (int z = minZ; z <= maxZ; ++z)
				{
					Block block = this.worldObj.getBlock(x, y, z);
					if (block != Blocks.air && block.getExplosionResistance(this) < 7F && block.getBlockHardness(this.worldObj, x, y, z) >= 0F)
					{
						// TODO gamerforEA code start
						if (this.fake.cantBreak(x, y, z))
							continue;
						// TODO gamerforEA code end

						int meta = this.worldObj.getBlockMetadata(x, y, z);

						if (entity instanceof EntityPlayer)
						{
							EntityPlayer player = (EntityPlayer) entity;
							if (block.canHarvestBlock(player, meta))
								block.harvestBlock(this.worldObj, player, x, y, z, meta);
						}

						this.worldObj.setBlockToAir(x, y, z);
						this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
						++this.blocksSmashed;
						hitBlock = true;
					}
				}
			}
		}

		return hitBlock;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.chain1 != null)
		{
			this.chain1.onUpdate();
			this.chain2.onUpdate();
			this.chain3.onUpdate();
			this.chain4.onUpdate();
			this.chain5.onUpdate();
		}

		if (this.getThrower() == null && !this.worldObj.isRemote)
			this.setDead();

		if (this.getThrower() != null)
		{
			float distance = this.getDistanceToEntity(this.getThrower());

			if (!this.isReturning && distance > 16F)
				this.isReturning = true;

			if (this.isReturning && distance < 1F)
			{
				if (this.getThrower() instanceof EntityPlayer)
					ItemTFChainBlock.setChainAsReturned((EntityPlayer) this.getThrower());

				this.setDead();
			}
		}

		if (this.isReturning && !this.worldObj.isRemote && this.getThrower() != null)
		{
			EntityLivingBase thrower = this.getThrower();
			Vec3 vec = Vec3.createVectorHelper(thrower.posX - this.posX, thrower.posY + thrower.getEyeHeight() - 1.200000023841858D - this.posY, thrower.posZ - this.posZ).normalize();
			float nearby = Math.min(this.ticksExisted * 0.03F, 1F);
			this.motionX = this.velX * (1D - nearby) + vec.xCoord * 2D * nearby;
			this.motionY = this.velY * (1D - nearby) + vec.yCoord * 2D * nearby - this.getGravityVelocity();
			this.motionZ = this.velZ * (1D - nearby) + vec.zCoord * 2D * nearby;
		}

		if (this.worldObj.isRemote && !this.isAttached)
		{
			List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(-this.motionX, -this.motionY, -this.motionZ).expand(2D, 2D, 2D));

			for (Entity entity : entities)
			{
				if (entity instanceof EntityPlayer)
					this.attachedTo = (EntityPlayer) entity;
			}

			this.isAttached = true;
		}

		if (this.attachedTo != null)
		{
			Vec3 vec = this.attachedTo.getLookVec();
			vec.rotateAroundY(-0.4F);
			double sx = this.attachedTo.posX + vec.xCoord;
			double sy = this.attachedTo.posY + vec.yCoord - 0.6000000238418579D;
			double sz = this.attachedTo.posZ + vec.zCoord;
			double ox = sx - this.posX;
			double oy = sy - this.posY - 0.25D;
			double oz = sz - this.posZ;
			this.chain1.setPosition(sx - ox * 0.05D, sy - oy * 0.05D, sz - oz * 0.05D);
			this.chain2.setPosition(sx - ox * 0.25D, sy - oy * 0.25D, sz - oz * 0.25D);
			this.chain3.setPosition(sx - ox * 0.45D, sy - oy * 0.45D, sz - oz * 0.45D);
			this.chain4.setPosition(sx - ox * 0.65D, sy - oy * 0.65D, sz - oz * 0.65D);
			this.chain5.setPosition(sx - ox * 0.85D, sy - oy * 0.85D, sz - oz * 0.85D);
		}

	}

	@Override
	protected float func_70182_d()
	{
		return 1.5F;
	}

	@Override
	public World func_82194_d()
	{
		return this.worldObj;
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart part, DamageSource damageSource, float p_70965_3_)
	{
		return false;
	}

	@Override
	public Entity[] getParts()
	{
		return this.partsArray;
	}
}
