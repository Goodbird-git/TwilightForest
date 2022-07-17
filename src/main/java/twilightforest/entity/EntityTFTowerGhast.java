package twilightforest.entity;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import com.gamerforea.twilightforest.entity.EntityCustomFireball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import twilightforest.TFFeature;
import twilightforest.entity.boss.EntityTFUrGhast;

public class EntityTFTowerGhast extends EntityGhast
{
	private static final int AGGRO_STATUS = 16;
	protected EntityLivingBase field_70792_g;
	protected boolean isAggressive;
	protected int field_70798_h;
	protected int explosionPower;
	protected int aggroCounter;
	protected float aggroRange;
	protected float stareRange;
	protected float wanderFactor;
	protected int inTrapCounter;
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
	private float maximumHomeDistance = -1.0F;

	public EntityTFTowerGhast(World par1World)
	{
		super(par1World);
		this.setSize(4.0F, 6.0F);
		this.aggroRange = 64.0F;
		this.stareRange = 32.0F;
		this.wanderFactor = 16.0F;
		this.inTrapCounter = 0;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
	}

	@Override
	protected float getSoundVolume()
	{
		return 0.5F;
	}

	@Override
	public int getTalkInterval()
	{
		return 160;
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 8;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
	}

	public int getAttackStatus()
	{
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	@Override
	public void onLivingUpdate()
	{
		float var1 = this.getBrightness(1.0F);
		if (var1 > 0.5F)
			this.entityAge += 2;

		if (this.rand.nextBoolean())
			this.worldObj.spawnParticle("reddust", this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);

		super.onLivingUpdate();
	}

	@Override
	protected void updateEntityActionState()
	{
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			this.setDead();

		this.despawnEntity();
		this.checkForTowerHome();
		if (this.inTrapCounter > 0)
		{
			--this.inTrapCounter;
			this.field_70792_g = null;
		}
		else
		{
			this.prevAttackCounter = this.attackCounter;
			if (this.field_70792_g != null && this.field_70792_g.isDead)
				this.field_70792_g = null;

			if (this.field_70792_g == null)
				this.field_70792_g = this.findPlayerInRange();
			else if (!this.isAggressive && this.field_70792_g instanceof EntityPlayer)
				this.checkToIncreaseAggro((EntityPlayer) this.field_70792_g);

			double offsetX = this.waypointX - this.posX;
			double offsetY = this.waypointY - this.posY;
			double offsetZ = this.waypointZ - this.posZ;
			double distanceDesired = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
			if ((distanceDesired < 1.0D || distanceDesired > 3600.0D) && this.wanderFactor > 0.0F)
			{
				this.waypointX = this.posX + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * this.wanderFactor);
				this.waypointY = this.posY + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * this.wanderFactor);
				this.waypointZ = this.posZ + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * this.wanderFactor);
			}

			if (this.field_70792_g == null && this.wanderFactor > 0.0F)
			{
				if (this.courseChangeCooldown-- <= 0)
				{
					this.courseChangeCooldown += this.rand.nextInt(20) + 20;
					distanceDesired = (double) MathHelper.sqrt_double(distanceDesired);
					if (!this.isWithinHomeDistance(MathHelper.floor_double(this.waypointX), MathHelper.floor_double(this.waypointY), MathHelper.floor_double(this.waypointZ)))
					{
						ChunkCoordinates cc = TFFeature.getNearestCenterXYZ(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ), this.worldObj);
						Vec3 homeVector = Vec3.createVectorHelper((double) cc.posX - this.posX, (double) (cc.posY + 128) - this.posY, (double) cc.posZ - this.posZ);
						homeVector = homeVector.normalize();
						this.waypointX = this.posX + homeVector.xCoord * 16.0D + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
						this.waypointY = this.posY + homeVector.yCoord * 16.0D + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
						this.waypointZ = this.posZ + homeVector.zCoord * 16.0D + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
					}

					if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, distanceDesired))
					{
						this.motionX += offsetX / distanceDesired * 0.1D;
						this.motionY += offsetY / distanceDesired * 0.1D;
						this.motionZ += offsetZ / distanceDesired * 0.1D;
					}
					else
					{
						this.waypointX = this.posX;
						this.waypointY = this.posY;
						this.waypointZ = this.posZ;
					}
				}
			}
			else
			{
				this.motionX *= 0.75D;
				this.motionY *= 0.75D;
				this.motionZ *= 0.75D;
			}

			double targetRange = this.aggroCounter <= 0 && !this.isAggressive ? (double) this.stareRange : (double) this.aggroRange;
			if (this.field_70792_g != null && this.field_70792_g.getDistanceSqToEntity(this) < targetRange * targetRange && this.canEntityBeSeen(this.field_70792_g))
			{
				this.faceEntity(this.field_70792_g, 10.0F, (float) this.getVerticalFaceSpeed());
				if (this.isAggressive)
				{
					if (this.attackCounter == 10)
						this.worldObj.playAuxSFXAtEntity(null, 1007, (int) this.posX, (int) this.posY, (int) this.posZ, 0);

					++this.attackCounter;
					if (this.attackCounter == 20)
					{
						this.spitFireball();
						this.attackCounter = -40;
					}
				}
			}
			else
			{
				this.isAggressive = false;
				this.field_70792_g = null;
				this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / 3.1415927F;
				this.rotationPitch = 0.0F;
			}

			if (this.attackCounter > 0 && !this.isAggressive)
				--this.attackCounter;

			byte currentAggroStatus = this.dataWatcher.getWatchableObjectByte(16);
			byte newAggroStatus = (byte) (this.attackCounter > 10 ? 2 : this.aggroCounter <= 0 && !this.isAggressive ? 0 : 1);
			if (currentAggroStatus != newAggroStatus)
				this.dataWatcher.updateObject(16, Byte.valueOf(newAggroStatus));

		}
	}

	@Override
	public int getVerticalFaceSpeed()
	{
		return 500;
	}

	protected void spitFireball()
	{
		double offsetX = this.field_70792_g.posX - this.posX;
		double offsetY = this.field_70792_g.boundingBox.minY + (double) (this.field_70792_g.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
		double offsetZ = this.field_70792_g.posZ - this.posZ;
		this.worldObj.playAuxSFXAtEntity(null, 1008, (int) this.posX, (int) this.posY, (int) this.posZ, 0);

		// TODO gamerforEA code replace, old code:
		// EntityLargeFireball entityFireball = new EntityLargeFireball(this.worldObj, this, offsetX, offsetY, offsetZ);
		EntityCustomFireball entityFireball = new EntityCustomFireball(this.worldObj, this, offsetX, offsetY, offsetZ);

		if (this instanceof EntityTFMiniGhast)
			entityFireball.setDamage(BalanceConfig.miniGhastFireballDamage);
		else if (this instanceof EntityTFUrGhast)
			entityFireball.setDamage(BalanceConfig.urGhastFireballDamage);
		else
			entityFireball.setDamage(BalanceConfig.towerGhastFireballDamage);
		// TODO gamerforEA code end

		double shotSpawnDistance = 0.5D;
		Vec3 lookVec = this.getLook(1.0F);
		entityFireball.posX = this.posX + lookVec.xCoord * shotSpawnDistance;
		entityFireball.posY = this.posY + (double) (this.height / 2.0F) + lookVec.yCoord * shotSpawnDistance;
		entityFireball.posZ = this.posZ + lookVec.zCoord * shotSpawnDistance;
		this.worldObj.spawnEntityInWorld(entityFireball);
		if (this.rand.nextInt(6) == 0)
			this.isAggressive = false;

	}

	protected EntityLivingBase findPlayerInRange()
	{
		EntityPlayer closest = this.worldObj.getClosestVulnerablePlayerToEntity(this, (double) this.aggroRange);
		if (closest != null)
		{
			float range = this.getDistanceToEntity(closest);
			if (range < this.stareRange || this.shouldAttackPlayer(closest))
				return closest;
		}

		return null;
	}

	protected void checkToIncreaseAggro(EntityPlayer par1EntityPlayer)
	{
		if (this.shouldAttackPlayer(par1EntityPlayer))
		{
			if (this.aggroCounter == 0)
				this.worldObj.playSoundAtEntity(this, "mob.ghast.moan", 1.0F, 1.0F);

			if (this.aggroCounter++ >= 20)
			{
				this.aggroCounter = 0;
				this.isAggressive = true;
			}
		}
		else
			this.aggroCounter = 0;

	}

	protected boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer)
	{
		int dx = MathHelper.floor_double(par1EntityPlayer.posX);
		int dy = MathHelper.floor_double(par1EntityPlayer.posY);
		int dz = MathHelper.floor_double(par1EntityPlayer.posZ);
		return this.worldObj.canBlockSeeTheSky(dx, dy, dz) && par1EntityPlayer.canEntityBeSeen(this);
	}

	protected boolean isCourseTraversable(double par1, double par3, double par5, double par7)
	{
		double var9 = (this.waypointX - this.posX) / par7;
		double var11 = (this.waypointY - this.posY) / par7;
		double var13 = (this.waypointZ - this.posZ) / par7;
		AxisAlignedBB var15 = this.boundingBox.copy();

		for (int var16 = 1; (double) var16 < par7; ++var16)
		{
			var15.offset(var9, var11, var13);
			if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		boolean wasAttacked = super.attackEntityFrom(par1DamageSource, par2);
		if (wasAttacked && par1DamageSource.getSourceOfDamage() instanceof EntityLivingBase)
		{
			this.field_70792_g = (EntityLivingBase) par1DamageSource.getSourceOfDamage();
			this.isAggressive = true;
			return true;
		}
		return wasAttacked;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox) && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.isValidLightLevel();
	}

	protected boolean isValidLightLevel()
	{
		return true;
	}

	protected void checkForTowerHome()
	{
		if (!this.hasHome())
		{
			int chunkX = MathHelper.floor_double(this.posX) >> 4;
			int chunkZ = MathHelper.floor_double(this.posZ) >> 4;
			TFFeature nearFeature = TFFeature.getFeatureForRegion(chunkX, chunkZ, this.worldObj);
			if (nearFeature != TFFeature.darkTower)
			{
				this.detachHome();
				this.entityAge += 5;
			}
			else
			{
				ChunkCoordinates cc = TFFeature.getNearestCenterXYZ(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ), this.worldObj);
				this.setHomeArea(cc.posX, cc.posY + 128, cc.posZ, 64);
			}
		}

	}

	public boolean isWithinHomeDistance(int x, int y, int z)
	{
		if (this.getMaximumHomeDistance() == -1.0F)
			return true;
		ChunkCoordinates home = this.getHomePosition();
		return y > 64 && y < 210 && home.getDistanceSquared(x, home.posY, z) < this.getMaximumHomeDistance() * this.getMaximumHomeDistance();
	}

	public void setInTrap()
	{
		this.inTrapCounter = 10;
	}

	public void setHomeArea(int par1, int par2, int par3, int par4)
	{
		this.homePosition.set(par1, par2, par3);
		this.maximumHomeDistance = (float) par4;
	}

	public ChunkCoordinates getHomePosition()
	{
		return this.homePosition;
	}

	public float getMaximumHomeDistance()
	{
		return this.maximumHomeDistance;
	}

	public void detachHome()
	{
		this.maximumHomeDistance = -1.0F;
	}

	public boolean hasHome()
	{
		return this.maximumHomeDistance != -1.0F;
	}
}
