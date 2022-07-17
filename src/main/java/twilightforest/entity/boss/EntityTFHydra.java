package twilightforest.entity.boss;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityTFHydra extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob
{
	private static int TICKS_BEFORE_HEALING = 1000;
	private static int HEAD_RESPAWN_TICKS = 100;
	private static int HEAD_MAX_DAMAGE = 120;
	private static float ARMOR_MULTIPLIER = 8.0F;
	private static int MAX_HEALTH = 360;
	private static float HEADS_ACTIVITY_FACTOR = 0.3F;
	private static int SECONDARY_FLAME_CHANCE = 10;
	private static int SECONDARY_MORTAR_CHANCE = 16;
	private static final int DATA_SPAWNHEADS = 17;
	private static final int DATA_BOSSHEALTH = 18;
	public Entity[] partArray;
	public EntityDragonPart body;
	public HydraHeadContainer[] hc;
	public int numHeads;
	public EntityDragonPart leftLeg;
	public EntityDragonPart rightLeg;
	public EntityDragonPart tail;
	Entity field_70776_bF;
	public int ticksSinceDamaged;

	public EntityTFHydra(World world)
	{
		super(world);
		this.numHeads = 7;
		this.field_70776_bF = null;
		this.ticksSinceDamaged = 0;
		this.partArray = new Entity[] { this.body = new EntityDragonPart(this, "body", 4.0F, 4.0F), this.leftLeg = new EntityDragonPart(this, "leg", 2.0F, 3.0F), this.rightLeg = new EntityDragonPart(this, "leg", 2.0F, 3.0F), this.tail = new EntityDragonPart(this, "tail", 4.0F, 4.0F) };
		this.hc = new HydraHeadContainer[this.numHeads];

		for (int i = 0; i < this.numHeads; ++i)
		{
			this.hc[i] = new HydraHeadContainer(this, i, i < 3);
		}

		ArrayList<Entity> partList = new ArrayList<>();
		Collections.addAll(partList, this.partArray);

		for (int i = 0; i < this.numHeads; ++i)
		{
			Collections.addAll(partList, this.hc[i].getNeckArray());
		}

		this.partArray = partList.toArray(this.partArray);
		this.setSize(16.0F, 12.0F);
		this.ignoreFrustumCheck = true;
		this.isImmuneToFire = true;
		this.experienceValue = 511;
		this.setSpawnHeads(true);
	}

	public EntityTFHydra(World world, double x, double y, double z)
	{
		this(world);
		this.setPosition(x, y, z);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double) MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
	}

	@Override
	public void onLivingUpdate()
	{
		if ((this.hc[0].headEntity == null || this.hc[1].headEntity == null || this.hc[2].headEntity == null) && this.shouldSpawnHeads() && !this.worldObj.isRemote)
		{
			for (int i = 0; i < this.numHeads; ++i)
			{
				this.hc[i].headEntity = new EntityTFHydraHead(this, "head" + i, 3.0F, 3.0F);
				this.hc[i].headEntity.setPosition(this.posX, this.posY, this.posZ);
				this.worldObj.spawnEntityInWorld(this.hc[i].headEntity);
			}

			this.setSpawnHeads(false);
		}

		this.body.onUpdate();

		for (int i = 0; i < this.numHeads; ++i)
		{
			this.hc[i].onUpdate();
		}

		if (!this.worldObj.isRemote)
			this.dataWatcher.updateObject(18, (int) this.getHealth());
		else if (this.getHealth() > 0.0F)
			this.deathTime = 0;

		if (this.hurtTime > 0)
			for (int i = 0; i < this.numHeads; ++i)
			{
				this.hc[i].setHurtTime(this.hurtTime);
			}

		++this.ticksSinceDamaged;
		if (!this.worldObj.isRemote && this.ticksSinceDamaged > TICKS_BEFORE_HEALING && this.ticksSinceDamaged % 5 == 0)
			this.heal(1.0F);

		this.setDifficultyVariables();
		if (this.newPosRotationIncrements > 0)
		{
			double var1 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
			double var3 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
			double var5 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
			double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + var7 / (double) this.newPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.newRotationPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(var1, var3, var5);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		if (Math.abs(this.motionX) < 0.005D)
			this.motionX = 0.0D;

		if (Math.abs(this.motionY) < 0.005D)
			this.motionY = 0.0D;

		if (Math.abs(this.motionZ) < 0.005D)
			this.motionZ = 0.0D;

		this.worldObj.theProfiler.startSection("ai");
		if (this.isMovementBlocked())
		{
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		}
		else if (this.isClientWorld())
		{
			this.worldObj.theProfiler.startSection("oldAi");
			this.updateEntityActionState();
			this.worldObj.theProfiler.endSection();
			this.rotationYawHead = this.rotationYaw;
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("jump");
		if (this.isJumping)
			if (!this.isInWater() && !this.handleLavaMovement())
			{
				if (this.onGround)
					this.jump();
			}
			else
				this.motionY += 0.03999999910593033D;

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("travel");
		this.moveStrafing *= 0.98F;
		this.moveForward *= 0.98F;
		this.randomYawVelocity *= 0.9F;
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
		this.worldObj.theProfiler.endSection();
		this.body.width = this.body.height = 6.0F;
		this.tail.width = 6.0F;
		this.tail.height = 2.0F;
		float angle = (this.renderYawOffset + 180.0F) * 3.141593F / 180.0F;
		double dx = this.posX - (double) MathHelper.sin(angle) * 3.0D;
		double dy = this.posY + 0.1D;
		double dz = this.posZ + (double) MathHelper.cos(angle) * 3.0D;
		this.body.setPosition(dx, dy, dz);
		dx = this.posX - (double) MathHelper.sin(angle) * 10.5D;
		dy = this.posY + 0.1D;
		dz = this.posZ + (double) MathHelper.cos(angle) * 10.5D;
		this.tail.setPosition(dx, dy, dz);
		this.worldObj.theProfiler.startSection("push");
		if (!this.worldObj.isRemote && this.hurtTime == 0)
		{
			this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.body.boundingBox), this.body);
			this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.tail.boundingBox), this.tail);
		}

		this.worldObj.theProfiler.endSection();
		if (!this.worldObj.isRemote)
		{
			this.destroyBlocksInAABB(this.body.boundingBox);
			this.destroyBlocksInAABB(this.tail.boundingBox);

			for (int i = 0; i < this.numHeads; ++i)
			{
				if (this.hc[i].headEntity != null && this.hc[i].isActive())
					this.destroyBlocksInAABB(this.hc[i].headEntity.boundingBox);
			}

			if (this.ticksExisted % 20 == 0 && this.isUnsteadySurfaceBeneath())
				this.destroyBlocksInAABB(this.boundingBox.offset(0.0D, -1.0D, 0.0D));
		}

	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(17, (byte) 0);
		this.dataWatcher.addObject(18, MAX_HEALTH);
	}

	public boolean shouldSpawnHeads()
	{
		return this.dataWatcher.getWatchableObjectByte(17) != 0;
	}

	public void setSpawnHeads(boolean flag)
	{
		if (flag)
			this.dataWatcher.updateObject(17, (byte) 127);
		else
			this.dataWatcher.updateObject(17, (byte) 0);

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("SpawnHeads", this.shouldSpawnHeads());
		nbttagcompound.setByte("NumHeads", (byte) this.countActiveHeads());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		this.setSpawnHeads(nbttagcompound.getBoolean("SpawnHeads"));
		this.activateNumberOfHeads(nbttagcompound.getByte("NumHeads"));
	}

	@Override
	protected void updateEntityActionState()
	{
		++this.entityAge;
		this.despawnEntity();
		this.moveStrafing = 0.0F;
		this.moveForward = 0.0F;
		float f = 48.0F;

		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].isActive() && this.hc[i].getDamageTaken() > HEAD_MAX_DAMAGE)
			{
				this.hc[i].setNextState(11);
				this.hc[i].endCurrentAction();
				this.hc[i].setRespawnCounter(HEAD_RESPAWN_TICKS);
				int otherHead = this.getRandomDeadHead();
				if (otherHead != -1)
					this.hc[otherHead].setRespawnCounter(HEAD_RESPAWN_TICKS);
			}
		}

		if (this.rand.nextFloat() < 0.7F)
		{
			EntityPlayer entityplayer1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, (double) f);
			if (entityplayer1 != null)
			{
				this.field_70776_bF = entityplayer1;
				this.numTicksToChaseTarget = 100 + this.rand.nextInt(20);
			}
			else
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
		}

		if (this.field_70776_bF != null)
		{
			this.faceEntity(this.field_70776_bF, 10.0F, (float) this.getVerticalFaceSpeed());

			for (int i = 0; i < this.numHeads; ++i)
			{
				if (!this.isHeadAttacking(this.hc[i]) && !this.hc[i].isSecondaryAttacking)
					this.hc[i].setTargetEntity(this.field_70776_bF);
			}

			if (this.field_70776_bF.isEntityAlive())
			{
				float distance = this.field_70776_bF.getDistanceToEntity(this);
				if (this.canEntityBeSeen(this.field_70776_bF))
					this.attackEntity(this.field_70776_bF, distance);
			}

			if (this.numTicksToChaseTarget-- <= 0 || this.field_70776_bF.isDead || this.field_70776_bF.getDistanceSqToEntity(this) > (double) (f * f))
				this.field_70776_bF = null;
		}
		else
		{
			if (this.rand.nextFloat() < 0.05F)
				this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;

			this.rotationYaw += this.randomYawVelocity;
			this.rotationPitch = this.defaultPitch;

			for (int i = 0; i < this.numHeads; ++i)
			{
				if (this.hc[i].currentState == 0)
					this.hc[i].setTargetEntity(null);
			}
		}

		this.secondaryAttacks();
		boolean flag = this.isInWater();
		boolean flag1 = this.handleLavaMovement();
		if (flag || flag1)
			this.isJumping = this.rand.nextFloat() < 0.8F;

	}

	private void setDifficultyVariables()
	{
		if (this.worldObj.difficultySetting != EnumDifficulty.HARD)
			HEADS_ACTIVITY_FACTOR = 0.3F;
		else
			HEADS_ACTIVITY_FACTOR = 0.5F;

	}

	private int getRandomDeadHead()
	{
		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].currentState == 12 && this.hc[i].respawnCounter == -1)
				return i;
		}

		return -1;
	}

	private void activateNumberOfHeads(int howMany)
	{
		int moreHeads = howMany - this.countActiveHeads();

		for (int i = 0; i < moreHeads; ++i)
		{
			int otherHead = this.getRandomDeadHead();
			if (otherHead != -1)
			{
				this.hc[otherHead].currentState = 0;
				this.hc[otherHead].setNextState(0);
				this.hc[otherHead].endCurrentAction();
			}
		}

	}

	private void attackEntity(Entity target, float distance)
	{
		int BITE_CHANCE = 10;
		int FLAME_CHANCE = 100;
		int MORTAR_CHANCE = 160;
		boolean targetAbove = target.boundingBox.minY > this.boundingBox.maxY;

		for (int i = 0; i < 3; ++i)
		{
			if (this.hc[i].currentState == 0 && !this.areTooManyHeadsAttacking(target, i))
				if (distance > 4.0F && distance < 10.0F && this.rand.nextInt(BITE_CHANCE) == 0 && this.countActiveHeads() > 2 && !this.areOtherHeadsBiting(target, i))
					this.hc[i].setNextState(1);
				else if (distance > 0.0F && distance < 20.0F && this.rand.nextInt(FLAME_CHANCE) == 0)
					this.hc[i].setNextState(5);
				else if (distance > 8.0F && distance < 32.0F && !targetAbove && this.rand.nextInt(MORTAR_CHANCE) == 0)
					this.hc[i].setNextState(8);
		}

		for (int i = 3; i < this.numHeads; ++i)
		{
			if (this.hc[i].currentState == 0 && !this.areTooManyHeadsAttacking(target, i))
				if (distance > 0.0F && distance < 20.0F && this.rand.nextInt(FLAME_CHANCE) == 0)
					this.hc[i].setNextState(5);
				else if (distance > 8.0F && distance < 32.0F && !targetAbove && this.rand.nextInt(MORTAR_CHANCE) == 0)
					this.hc[i].setNextState(8);
		}

	}

	protected boolean areTooManyHeadsAttacking(Entity target, int testHead)
	{
		int otherAttacks = 0;

		for (int i = 0; i < this.numHeads; ++i)
		{
			if (i != testHead && this.isHeadAttacking(this.hc[i]))
			{
				++otherAttacks;
				if (this.isHeadBiting(this.hc[i]))
					otherAttacks += 2;
			}
		}

		return (float) otherAttacks >= 1.0F + (float) this.countActiveHeads() * HEADS_ACTIVITY_FACTOR;
	}

	public int countActiveHeads()
	{
		int count = 0;

		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].isActive())
				++count;
		}

		return count;
	}

	private boolean isHeadAttacking(HydraHeadContainer head)
	{
		return head.currentState == 1 || head.currentState == 2 || head.currentState == 3 || head.currentState == 5 || head.currentState == 6 || head.currentState == 8 || head.currentState == 9;
	}

	protected boolean areOtherHeadsBiting(Entity target, int testHead)
	{
		for (int i = 0; i < this.numHeads; ++i)
		{
			if (i != testHead && this.isHeadBiting(this.hc[i]))
				return true;
		}

		return false;
	}

	protected boolean isHeadBiting(HydraHeadContainer head)
	{
		return head.currentState == 1 || head.currentState == 2 || head.currentState == 3 || head.nextState == 1;
	}

	private void secondaryAttacks()
	{
		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].headEntity == null)
				return;
		}

		EntityLivingBase secondaryTarget = this.findSecondaryTarget(20.0D);
		if (secondaryTarget != null)
		{
			float distance = secondaryTarget.getDistanceToEntity(this);

			for (int i = 1; i < this.numHeads; ++i)
			{
				if (this.hc[i].isActive() && this.hc[i].currentState == 0 && this.isTargetOnThisSide(i, secondaryTarget))
					if (distance > 0.0F && distance < 20.0F && this.rand.nextInt(SECONDARY_FLAME_CHANCE) == 0)
					{
						this.hc[i].setTargetEntity(secondaryTarget);
						this.hc[i].isSecondaryAttacking = true;
						this.hc[i].setNextState(5);
					}
					else if (distance > 8.0F && distance < 32.0F && this.rand.nextInt(SECONDARY_MORTAR_CHANCE) == 0)
					{
						this.hc[i].setTargetEntity(secondaryTarget);
						this.hc[i].isSecondaryAttacking = true;
						this.hc[i].setNextState(8);
					}
			}
		}

	}

	public boolean isTargetOnThisSide(int headNum, Entity target)
	{
		double headDist = this.distanceSqXZ(this.hc[headNum].headEntity, target);
		double middleDist = this.distanceSqXZ(this, target);
		return headDist < middleDist;
	}

	private double distanceSqXZ(Entity headEntity, Entity target)
	{
		double distX = headEntity.posX - target.posX;
		double distZ = headEntity.posZ - target.posZ;
		return distX * distX + distZ * distZ;
	}

	public EntityLivingBase findSecondaryTarget(double range)
	{
		double closestRange = -1.0D;
		EntityLivingBase closestEntity = null;

		for (EntityLivingBase nearbyLiving : (Iterable<? extends EntityLivingBase>) this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(range, range, range)))
		{
			if (!(nearbyLiving instanceof EntityTFHydra) && !(nearbyLiving instanceof EntityTFHydraPart) && nearbyLiving != this.field_70776_bF && !this.isAnyHeadTargeting(nearbyLiving) && this.canEntityBeSeen(nearbyLiving))
			{
				double curDist = nearbyLiving.getDistanceSq(this.posX, this.posY, this.posZ);
				if ((range < 0.0D || curDist < range * range) && (closestRange == -1.0D || curDist < closestRange))
				{
					closestRange = curDist;
					closestEntity = nearbyLiving;
				}
			}
		}

		return closestEntity;
	}

	boolean isAnyHeadTargeting(Entity targetEntity)
	{
		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].targetEntity != null && this.hc[i].targetEntity.equals(targetEntity))
				return true;
		}

		return false;
	}

	private void collideWithEntities(List<Entity> par1List, Entity part)
	{
		double pushPower = 4.0D;
		double centerX = (part.boundingBox.minX + part.boundingBox.maxX) / 2.0D;
		double centerY = (part.boundingBox.minZ + part.boundingBox.maxZ) / 2.0D;

		for (Entity entity : par1List)
		{
			if (entity instanceof EntityLivingBase)
			{
				double distX = entity.posX - centerX;
				double distZ = entity.posZ - centerY;
				double sqDist = distX * distX + distZ * distZ;
				entity.addVelocity(distX / sqDist * pushPower, 0.20000000298023224D, distZ / sqDist * pushPower);
			}
		}

	}

	private boolean isUnsteadySurfaceBeneath()
	{
		int minX = MathHelper.floor_double(this.boundingBox.minX);
		int minZ = MathHelper.floor_double(this.boundingBox.minZ);
		int maxX = MathHelper.floor_double(this.boundingBox.maxX);
		int maxZ = MathHelper.floor_double(this.boundingBox.maxZ);
		int minY = MathHelper.floor_double(this.boundingBox.minY);
		int solid = 0;
		int total = 0;
		int dy = minY - 1;

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dz = minZ; dz <= maxZ; ++dz)
			{
				++total;
				if (this.worldObj.getBlock(dx, dy, dz).getMaterial().isSolid())
					++solid;
			}
		}

		return (float) solid / (float) total < 0.6F;
	}

	private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB)
	{
		// TODO gamerforEA code start
		if (!ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj))
			return false;
		// TODO gamerforEA code end

		int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
		int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
		int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
		int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
		int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
		int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
		boolean wasBlocked = false;

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dy = minY; dy <= maxY; ++dy)
			{
				for (int dz = minZ; dz <= maxZ; ++dz)
				{
					Block currentID = this.worldObj.getBlock(dx, dy, dz);
					if (currentID != Blocks.air)
					{
						int currentMeta = this.worldObj.getBlockMetadata(dx, dy, dz);
						if (currentID != Blocks.obsidian && currentID != Blocks.end_stone && currentID != Blocks.bedrock)
						{
							this.worldObj.setBlock(dx, dy, dz, Blocks.air, 0, 2);
							this.worldObj.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
						}
						else
							wasBlocked = true;
					}
				}
			}
		}

		return wasBlocked;
	}

	@Override
	public int getVerticalFaceSpeed()
	{
		return 500;
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart dragonpart, DamageSource damagesource, float i)
	{
		double range = this.calculateRange(damagesource);
		return !(range > 400.0D) && this.superAttackFrom(damagesource, (float) Math.round(i / 8.0F));
	}

	protected boolean superAttackFrom(DamageSource par1DamageSource, float par2)
	{
		return super.attackEntityFrom(par1DamageSource, par2);
	}

	public boolean attackEntityFromPart(EntityTFHydraPart part, DamageSource damagesource, float damageAmount)
	{
		if (!this.worldObj.isRemote && damagesource == DamageSource.inWall && part.getBoundingBox() != null)
			this.destroyBlocksInAABB(part.getBoundingBox());

		HydraHeadContainer headCon = null;

		for (int i = 0; i < this.numHeads; ++i)
		{
			if (this.hc[i].headEntity == part)
				headCon = this.hc[i];
		}

		double range = this.calculateRange(damagesource);
		if (range > 400.0D)
			return false;
		else if (headCon != null && !headCon.isActive())
			return false;
		else
		{
			boolean tookDamage;
			if (headCon != null && (double) headCon.getCurrentMouthOpen() > 0.5D)
			{
				tookDamage = this.superAttackFrom(damagesource, damageAmount);
				headCon.addDamage(damageAmount);
			}
			else
			{
				int armoredDamage = Math.round(damageAmount / ARMOR_MULTIPLIER);
				tookDamage = this.superAttackFrom(damagesource, (float) armoredDamage);
				if (headCon != null)
					headCon.addDamage((float) armoredDamage);
			}

			if (tookDamage)
				this.ticksSinceDamaged = 0;

			return tookDamage;
		}
	}

	protected double calculateRange(DamageSource damagesource)
	{
		double range = -1.0D;
		if (damagesource.getSourceOfDamage() != null)
			range = this.getDistanceSqToEntity(damagesource.getSourceOfDamage());

		if (damagesource.getEntity() != null)
			range = this.getDistanceSqToEntity(damagesource.getEntity());

		return range;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		return false;
	}

	@Override
	public Entity[] getParts()
	{
		return this.partArray;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public void knockBack(Entity entity, float i, double d, double d1)
	{
	}

	@Override
	protected String getLivingSound()
	{
		return "TwilightForest:mob.hydra.growl";
	}

	@Override
	protected String getHurtSound()
	{
		return "TwilightForest:mob.hydra.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return "TwilightForest:mob.hydra.death";
	}

	@Override
	protected float getSoundVolume()
	{
		return 2.0F;
	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
		{
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightKillHydra);
		}

		if (!this.worldObj.isRemote && this.worldObj.provider instanceof WorldProviderTwilightForest)
		{
			int dx = MathHelper.floor_double(this.posX);
			int dy = MathHelper.floor_double(this.posY);
			int dz = MathHelper.floor_double(this.posZ);
			ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) this.worldObj.provider).getChunkProvider();
			TFFeature nearbyFeature = ((TFWorldChunkManager) this.worldObj.provider.worldChunkMgr).getFeatureAt(dx, dz, this.worldObj);
			if (nearbyFeature == TFFeature.hydraLair)
				chunkProvider.setStructureConquered(dx, dy, dz, true);
		}

	}

	@Override
	protected void dropFewItems(boolean par1, int par2)
	{
		int totalDrops = this.rand.nextInt(3 + par2) + 5;

		for (int i = 0; i < totalDrops; ++i)
		{
			this.dropItem(TFItems.hydraChop, 5);
		}

		totalDrops = this.rand.nextInt(4 + par2) + 7;

		for (int i = 0; i < totalDrops; ++i)
		{
			this.dropItem(TFItems.fieryBlood, 1);
		}

		this.dropItem(TFItems.trophy, 1);
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean isBurning()
	{
		return false;
	}

	@Override
	protected void onDeathUpdate()
	{
		++this.deathTime;
		if (this.deathTime == 1)
			for (int i = 0; i < this.numHeads; ++i)
			{
				this.hc[i].setRespawnCounter(-1);
				if (this.hc[i].isActive())
				{
					this.hc[i].setNextState(0);
					this.hc[i].endCurrentAction();
					this.hc[i].setHurtTime(200);
				}
			}

		if (this.deathTime <= 140 && this.deathTime % 20 == 0)
		{
			int headToDie = this.deathTime / 20 - 1;
			if (this.hc[headToDie].isActive())
			{
				this.hc[headToDie].setNextState(11);
				this.hc[headToDie].endCurrentAction();
			}
		}

		if (this.deathTime == 200)
		{
			if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && !this.isChild())
			{
				int var1 = this.getExperiencePoints(this.attackingPlayer);

				while (var1 > 0)
				{
					int var2 = EntityXPOrb.getXPSplit(var1);
					var1 -= var2;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
				}
			}

			this.setDead();
		}

		for (int var1 = 0; var1 < 20; ++var1)
		{
			double var8 = this.rand.nextGaussian() * 0.02D;
			double var4 = this.rand.nextGaussian() * 0.02D;
			double var6 = this.rand.nextGaussian() * 0.02D;
			String particle = this.rand.nextInt(2) == 0 ? "largeexplode" : "explode";
			this.worldObj.spawnParticle(particle, this.posX + (double) (this.rand.nextFloat() * this.body.width * 2.0F) - (double) this.body.width, this.posY + (double) (this.rand.nextFloat() * this.body.height), this.posZ + (double) (this.rand.nextFloat() * this.body.width * 2.0F) - (double) this.body.width, var8, var4, var6);
		}

	}

	@Override
	public World func_82194_d()
	{
		return this.worldObj;
	}
}
