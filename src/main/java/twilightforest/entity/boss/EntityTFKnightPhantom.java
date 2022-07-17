package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TFTreasure;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

import java.util.List;

public class EntityTFKnightPhantom extends EntityFlying implements IMob
{
	private static final float CIRCLE_SMALL_RADIUS = 2.5F;
	private static final float CIRCLE_LARGE_RADIUS = 8.5F;
	private static final int FLAG_CHARGING = 17;
	int number;
	int ticksProgress;
	EntityTFKnightPhantom.Formation currentFormation;
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
	private float maximumHomeDistance = -1.0F;
	private int chargePosX;
	private int chargePosY;
	private int chargePosZ;

	public EntityTFKnightPhantom(World par1World)
	{
		super(par1World);
		this.setSize(1.5F, 3.0F);
		this.noClip = true;
		this.isImmuneToFire = true;
		this.currentFormation = EntityTFKnightPhantom.Formation.HOVER;
		this.experienceValue = 93;
		this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlySword));
		this.setCurrentItemOrArmor(3, new ItemStack(TFItems.phantomPlate));
		this.setCurrentItemOrArmor(4, new ItemStack(TFItems.phantomHelm));
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(17, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(35.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		return !this.isEntityInvulnerable() && par1DamageSource != DamageSource.inWall && super.attackEntityFrom(par1DamageSource, par2);
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if (this.isChargingAtPlayer())
			for (int i = 0; i < 4; ++i)
			{
				Item particleID = this.rand.nextBoolean() ? TFItems.phantomHelm : TFItems.knightlySword;
				this.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(particleID), this.posX + ((double) (this.rand.nextFloat() * this.rand.nextFloat()) - 0.5D) * (double) this.width, this.posY + (double) this.rand.nextFloat() * ((double) this.height - 0.75D) + 0.5D, this.posZ + ((double) (this.rand.nextFloat() * this.rand.nextFloat()) - 0.5D) * (double) this.width, 0.0D, -0.1D, 0.0D);
				this.worldObj.spawnParticle("smoke", this.posX + ((double) (this.rand.nextFloat() * this.rand.nextFloat()) - 0.5D) * (double) this.width, this.posY + (double) this.rand.nextFloat() * ((double) this.height - 0.75D) + 0.5D, this.posZ + ((double) (this.rand.nextFloat() * this.rand.nextFloat()) - 0.5D) * (double) this.width, 0.0D, 0.1D, 0.0D);
			}

	}

	@Override
	protected void onDeathUpdate()
	{
		super.onDeathUpdate();

		for (int i = 0; i < 20; ++i)
		{
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle("explode", this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
		}

	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
		{
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightProgressKnights);
		}

		if (!this.worldObj.isRemote && this.worldObj.provider instanceof WorldProviderTwilightForest)
		{
			int dx = this.getHomePosition().posX;
			int dy = this.getHomePosition().posY;
			int dz = this.getHomePosition().posZ;
			ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) this.worldObj.provider).getChunkProvider();
			TFFeature nearbyFeature = ((TFWorldChunkManager) this.worldObj.provider.worldChunkMgr).getFeatureAt(dx, dz, this.worldObj);
			if (nearbyFeature == TFFeature.tfStronghold)
				chunkProvider.setStructureConquered(dx, dy, dz, true);
		}

		if (!this.worldObj.isRemote)
		{
			List<EntityTFKnightPhantom> nearbyKnights = this.getNearbyKnights();
			if (nearbyKnights.size() <= 1)
				this.makeATreasure();
		}

	}

	private void makeATreasure()
	{
		int treasureX;
		int treasureY;
		int treasureZ;

		ChunkCoordinates homePosition = this.getHomePosition();
		if (homePosition.posX != 0)
		{
			treasureX = homePosition.posX;
			treasureY = homePosition.posY - 1;
			treasureZ = homePosition.posZ;
		}
		else
		{
			treasureX = MathHelper.floor_double(this.lastTickPosX);
			treasureY = MathHelper.floor_double(this.lastTickPosY);
			treasureZ = MathHelper.floor_double(this.lastTickPosZ);
		}

		// TODO gamerforEA code add victim:EntityLivingBase
		TFTreasure.stronghold_boss.generate(this.worldObj, null, treasureX, treasureY, treasureZ, this);
	}

	@Override
	protected void updateEntityActionState()
	{
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			this.setDead();

		this.despawnEntity();
		this.noClip = this.ticksProgress % 20 != 0;
		++this.ticksProgress;
		if (this.ticksProgress >= this.getMaxTicksForFormation())
			this.switchToNextFormation();

		float seekRange = this.isChargingAtPlayer() ? 24.0F : 9.0F;
		EntityPlayer target = this.worldObj.getClosestVulnerablePlayerToEntity(this, (double) seekRange);
		if (target != null && this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_START)
		{
			int targetX = MathHelper.floor_double(target.lastTickPosX);
			int targetY = MathHelper.floor_double(target.lastTickPosY);
			int targetZ = MathHelper.floor_double(target.lastTickPosZ);
			if (this.isWithinHomeArea(targetX, targetY, targetZ))
			{
				this.chargePosX = targetX;
				this.chargePosY = targetY;
				this.chargePosZ = targetZ;
			}
			else
			{
				this.chargePosX = this.getHomePosition().posX;
				this.chargePosY = this.getHomePosition().posY;
				this.chargePosZ = this.getHomePosition().posZ;
			}
		}

		Vec3 dest = this.getDestination();
		double moveX = dest.xCoord - this.posX;
		double moveY = dest.yCoord - this.posY;
		double moveZ = dest.zCoord - this.posZ;
		double factor = moveX * moveX + moveY * moveY + moveZ * moveZ;
		factor = (double) MathHelper.sqrt_double(factor);
		double speed = 0.1D;
		this.motionX += moveX / factor * speed;
		this.motionY += moveY / factor * speed;
		this.motionZ += moveZ / factor * speed;
		if (target != null)
		{
			this.faceEntity(target, 10.0F, 500.0F);
			if (target.isEntityAlive())
			{
				float f1 = target.getDistanceToEntity(this);
				if (this.canEntityBeSeen(target))
					this.attackEntity(target, f1);
			}

			if (this.isAxeKnight() && this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_ATTACK && this.ticksProgress % 4 == 0)
				this.launchAxeAt(target);

			if (this.isPickKnight() && this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_ATTACK && this.ticksProgress % 4 == 0)
				this.launchPicks();
		}

	}

	protected void attackEntity(Entity par1Entity, float par2)
	{
		if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY)
		{
			this.attackTime = 20;
			this.attackEntityAsMob(par1Entity);
		}

	}

	@Override
	public boolean attackEntityAsMob(Entity par1Entity)
	{
		float f = this.getAttackDamage();
		int i = 0;
		if (par1Entity instanceof EntityLivingBase)
		{
			f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) par1Entity);
			i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) par1Entity);
		}

		boolean flag = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
		if (flag)
		{
			if (i > 0)
			{
				par1Entity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F) * (float) i * 0.5F));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);
			if (j > 0)
				par1Entity.setFire(j * 4);

			if (par1Entity instanceof EntityLivingBase)
				;
		}

		return flag;
	}

	private float getAttackDamage()
	{
		float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		if (this.isChargingAtPlayer())
			// TODO gamerforEA code replace, old code:
			// damage += 7.0F;
			damage += BalanceConfig.knightPhantomDamagePlayerAddition;
		// TODO gamerforEA code end

		return damage;
	}

	protected void launchAxeAt(Entity targetedEntity)
	{
		float bodyFacingAngle = this.renderYawOffset * 3.141593F / 180.0F;
		double sx = this.posX + (double) (MathHelper.cos(bodyFacingAngle) * 1.0F);
		double sy = this.posY + (double) this.height * 0.82D;
		double sz = this.posZ + (double) (MathHelper.sin(bodyFacingAngle) * 1.0F);
		double tx = targetedEntity.posX - sx;
		double ty = targetedEntity.boundingBox.minY + (double) (targetedEntity.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
		double tz = targetedEntity.posZ - sz;
		this.worldObj.playSoundAtEntity(this, "random.bow", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.4F);
		EntityTFThrownAxe projectile = new EntityTFThrownAxe(this.worldObj, this);
		float speed = 0.75F;
		projectile.setThrowableHeading(tx, ty, tz, speed, 1.0F);
		projectile.setLocationAndAngles(sx, sy, sz, this.rotationYaw, this.rotationPitch);
		this.worldObj.spawnEntityInWorld(projectile);
	}

	protected void launchPicks()
	{
		this.worldObj.playSoundAtEntity(this, "random.bow", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.4F);

		for (int i = 0; i < 8; ++i)
		{
			float throwAngle = (float) i * 3.1415915F / 4.0F;
			double sx = this.posX + (double) (MathHelper.cos(throwAngle) * 1.0F);
			double sy = this.posY + (double) this.height * 0.82D;
			double sz = this.posZ + (double) (MathHelper.sin(throwAngle) * 1.0F);
			double vx = (double) MathHelper.cos(throwAngle);
			double vy = 0.0D;
			double vz = (double) MathHelper.sin(throwAngle);
			EntityTFThrownPick projectile = new EntityTFThrownPick(this.worldObj, this);
			projectile.setLocationAndAngles(sx, sy, sz, (float) i * 45.0F, this.rotationPitch);
			float speed = 0.5F;
			projectile.setThrowableHeading(vx, vy, vz, speed, 1.0F);
			this.worldObj.spawnEntityInWorld(projectile);
		}

	}

	@Override
	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public void knockBack(Entity par1Entity, float damage, double par3, double par5)
	{
		this.isAirBorne = true;
		float f = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
		float distance = 0.2F;
		this.motionX /= 2.0D;
		this.motionY /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= par3 / (double) f * (double) distance;
		this.motionY += (double) distance;
		this.motionZ -= par5 / (double) f * (double) distance;
		if (this.motionY > 0.4000000059604645D)
			this.motionY = 0.4000000059604645D;

	}

	public void switchToNextFormation()
	{
		List<EntityTFKnightPhantom> nearbyKnights = this.getNearbyKnights();
		if (this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_START)
			this.switchToFormation(Formation.ATTACK_PLAYER_ATTACK);
		else if (this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_ATTACK)
			if (nearbyKnights.size() > 1)
				this.switchToFormation(Formation.WAITING_FOR_LEADER);
			else
			{
				switch (this.rand.nextInt(3))
				{
					case 0:
						this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlySword));
						break;
					case 1:
						this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlyAxe));
						break;
					case 2:
						this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlyPick));
				}

				this.switchToFormation(Formation.ATTACK_PLAYER_START);
			}
		else if (this.currentFormation == EntityTFKnightPhantom.Formation.WAITING_FOR_LEADER)
			if (nearbyKnights.size() > 1)
			{
				this.switchToFormation(nearbyKnights.get(1).currentFormation);
				this.ticksProgress = nearbyKnights.get(1).ticksProgress;
			}
			else
				this.switchToFormation(Formation.ATTACK_PLAYER_START);
		else if (this.isThisTheLeader(nearbyKnights))
		{
			this.pickRandomFormation();
			this.broadcastMyFormation(nearbyKnights);
			if (this.isNobodyCharging(nearbyKnights))
				this.makeARandomKnightCharge(nearbyKnights);
		}

	}

	private List<EntityTFKnightPhantom> getNearbyKnights()
	{
		return (List<EntityTFKnightPhantom>) this.worldObj.getEntitiesWithinAABB(EntityTFKnightPhantom.class, AxisAlignedBB.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(32.0D, 8.0D, 32.0D));
	}

	protected void pickRandomFormation()
	{
		switch (this.rand.nextInt(8))
		{
			case 0:
				this.currentFormation = EntityTFKnightPhantom.Formation.SMALL_CLOCKWISE;
				break;
			case 1:
				this.currentFormation = EntityTFKnightPhantom.Formation.SMALL_ANTICLOCKWISE;
				break;
			case 2:
				this.currentFormation = EntityTFKnightPhantom.Formation.SMALL_ANTICLOCKWISE;
				break;
			case 3:
				this.currentFormation = EntityTFKnightPhantom.Formation.CHARGE_PLUSX;
				break;
			case 4:
				this.currentFormation = EntityTFKnightPhantom.Formation.CHARGE_MINUSX;
				break;
			case 5:
				this.currentFormation = EntityTFKnightPhantom.Formation.CHARGE_PLUSZ;
				break;
			case 6:
				this.currentFormation = EntityTFKnightPhantom.Formation.CHARGE_MINUSZ;
				break;
			case 7:
				this.currentFormation = EntityTFKnightPhantom.Formation.SMALL_CLOCKWISE;
		}

		this.switchToFormation(this.currentFormation);
	}

	private boolean isThisTheLeader(List<EntityTFKnightPhantom> nearbyKnights)
	{
		boolean iAmTheLowest = true;

		for (EntityTFKnightPhantom knight : nearbyKnights)
		{
			if (knight.getNumber() < this.getNumber())
			{
				iAmTheLowest = false;
				break;
			}
		}

		return iAmTheLowest;
	}

	private boolean isNobodyCharging(List<EntityTFKnightPhantom> nearbyKnights)
	{
		boolean noCharge = true;

		for (EntityTFKnightPhantom knight : nearbyKnights)
		{
			if (knight.isChargingAtPlayer())
			{
				noCharge = false;
				break;
			}
		}

		return noCharge;
	}

	private void makeARandomKnightCharge(List<EntityTFKnightPhantom> nearbyKnights)
	{
		int randomNum = this.rand.nextInt(nearbyKnights.size());
		nearbyKnights.get(randomNum).switchToFormation(EntityTFKnightPhantom.Formation.ATTACK_PLAYER_START);
	}

	private void broadcastMyFormation(List<EntityTFKnightPhantom> nearbyKnights)
	{
		for (EntityTFKnightPhantom knight : nearbyKnights)
		{
			if (!knight.isChargingAtPlayer())
				knight.switchToFormation(this.currentFormation);
		}

	}

	public boolean isChargingAtPlayer()
	{
		return this.dataWatcher.getWatchableObjectByte(17) != 0;
	}

	public void setChargingAtPlayer(boolean flag)
	{
		if (flag)
			this.dataWatcher.updateObject(17, (byte) 127);
		else
			this.dataWatcher.updateObject(17, (byte) 0);

	}

	@Override
	protected String getLivingSound()
	{
		return "TwilightForest:mob.wraith.wraith";
	}

	@Override
	protected String getHurtSound()
	{
		return "TwilightForest:mob.wraith.wraith";
	}

	@Override
	protected String getDeathSound()
	{
		return "TwilightForest:mob.wraith.wraith";
	}

	private void switchToFormationByNumber(int formationNumber)
	{
		this.currentFormation = EntityTFKnightPhantom.Formation.values()[formationNumber];
		this.ticksProgress = 0;
	}

	public void switchToFormation(EntityTFKnightPhantom.Formation formation)
	{
		this.currentFormation = formation;
		this.ticksProgress = 0;
		this.setChargingAtPlayer(this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_START || this.currentFormation == EntityTFKnightPhantom.Formation.ATTACK_PLAYER_ATTACK);
	}

	public int getFormationAsNumber()
	{
		return this.currentFormation.ordinal();
	}

	public int getTicksProgress()
	{
		return this.ticksProgress;
	}

	public void setTicksProgress(int ticksProgress)
	{
		this.ticksProgress = ticksProgress;
	}

	public int getMaxTicksForFormation()
	{
		switch (this.currentFormation)
		{
			case HOVER:
			default:
				return 90;
			case LARGE_CLOCKWISE:
				return 180;
			case SMALL_CLOCKWISE:
				return 90;
			case LARGE_ANTICLOCKWISE:
				return 180;
			case SMALL_ANTICLOCKWISE:
				return 90;
			case CHARGE_PLUSX:
				return 180;
			case CHARGE_MINUSX:
				return 180;
			case CHARGE_PLUSZ:
				return 180;
			case CHARGE_MINUSZ:
				return 180;
			case ATTACK_PLAYER_START:
				return 50;
			case ATTACK_PLAYER_ATTACK:
				return 50;
			case WAITING_FOR_LEADER:
				return 10;
		}
	}

	private Vec3 getDestination()
	{
		if (!this.hasHome())
			;

		switch (this.currentFormation)
		{
			case HOVER:
			case ATTACK_PLAYER_START:
				return this.getHoverPosition(8.5F);
			case LARGE_CLOCKWISE:
				return this.getCirclePosition(8.5F, true);
			case SMALL_CLOCKWISE:
				return this.getCirclePosition(2.5F, true);
			case LARGE_ANTICLOCKWISE:
				return this.getCirclePosition(8.5F, false);
			case SMALL_ANTICLOCKWISE:
				return this.getCirclePosition(2.5F, false);
			case CHARGE_PLUSX:
				return this.getMoveAcrossPosition(true, true);
			case CHARGE_MINUSX:
				return this.getMoveAcrossPosition(false, true);
			case CHARGE_PLUSZ:
				return this.getMoveAcrossPosition(true, false);
			case CHARGE_MINUSZ:
				return this.getMoveAcrossPosition(false, false);
			case ATTACK_PLAYER_ATTACK:
				return this.getAttackPlayerPosition();
			case WAITING_FOR_LEADER:
				return this.getLoiterPosition();
			default:
				return this.getLoiterPosition();
		}
	}

	private Vec3 getMoveAcrossPosition(boolean plus, boolean alongX)
	{
		float offset0 = (float) this.getNumber() * 3.0F - 7.5F;
		float offset1;
		if (this.ticksProgress < 60)
			offset1 = -7.0F;
		else
			offset1 = -7.0F + (float) (this.ticksProgress - 60) / 120.0F * 14.0F;

		if (!plus)
			offset1 *= -1.0F;

		double dx = (double) ((float) this.getHomePosition().posX + (alongX ? offset0 : offset1));
		double dy = (double) this.getHomePosition().posY + Math.cos((double) ((float) this.ticksProgress / 7.0F + (float) this.getNumber()));
		double dz = (double) ((float) this.getHomePosition().posZ + (alongX ? offset1 : offset0));
		return Vec3.createVectorHelper(dx, dy, dz);
	}

	protected Vec3 getCirclePosition(float distance, boolean clockwise)
	{
		float angle = (float) this.ticksProgress * 2.0F;
		if (!clockwise)
			angle *= -1.0F;

		angle = angle + 60.0F * (float) this.getNumber();
		double dx = (double) this.getHomePosition().posX + Math.cos((double) angle * 3.141592653589793D / 180.0D) * (double) distance;
		double dy = (double) this.getHomePosition().posY + Math.cos((double) ((float) this.ticksProgress / 7.0F + (float) this.getNumber()));
		double dz = (double) this.getHomePosition().posZ + Math.sin((double) angle * 3.141592653589793D / 180.0D) * (double) distance;
		return Vec3.createVectorHelper(dx, dy, dz);
	}

	private Vec3 getHoverPosition(float distance)
	{
		double dx = this.lastTickPosX;
		double dy = (double) this.getHomePosition().posY + Math.cos((double) ((float) this.ticksProgress / 7.0F + (float) this.getNumber()));
		double dz = this.lastTickPosZ;
		double ox = (double) this.getHomePosition().posX - dx;
		double oz = (double) this.getHomePosition().posZ - dz;
		double dDist = Math.sqrt(ox * ox + oz * oz);
		if (dDist > (double) distance)
		{
			dx = (double) this.getHomePosition().posX + ox / dDist * (double) distance;
			dz = (double) this.getHomePosition().posZ + oz / dDist * (double) distance;
		}

		return Vec3.createVectorHelper(dx, dy, dz);
	}

	private Vec3 getLoiterPosition()
	{
		double dx = (double) this.getHomePosition().posX;
		double dy = (double) this.getHomePosition().posY + Math.cos((double) ((float) this.ticksProgress / 7.0F + (float) this.getNumber()));
		double dz = (double) this.getHomePosition().posZ;
		return Vec3.createVectorHelper(dx, dy, dz);
	}

	private Vec3 getAttackPlayerPosition()
	{
		return this.isSwordKnight() ? Vec3.createVectorHelper((double) this.chargePosX, (double) this.chargePosY, (double) this.chargePosZ) : this.getHoverPosition(8.5F);
	}

	public boolean isSwordKnight()
	{
		return this.getEquipmentInSlot(0) != null && this.getEquipmentInSlot(0).getItem() == TFItems.knightlySword;
	}

	public boolean isAxeKnight()
	{
		return this.getEquipmentInSlot(0) != null && this.getEquipmentInSlot(0).getItem() == TFItems.knightlyAxe;
	}

	public boolean isPickKnight()
	{
		return this.getEquipmentInSlot(0) != null && this.getEquipmentInSlot(0).getItem() == TFItems.knightlyPick;
	}

	public int getNumber()
	{
		return this.number;
	}

	public void setNumber(int number)
	{
		this.number = number;
		switch (number % 3)
		{
			case 0:
				this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlySword));
				break;
			case 1:
				this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlyAxe));
				break;
			case 2:
				this.setCurrentItemOrArmor(0, new ItemStack(TFItems.knightlyPick));
		}

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		ChunkCoordinates home = this.getHomePosition();
		nbttagcompound.setTag("Home", this.newDoubleNBTList((double) home.posX, (double) home.posY, (double) home.posZ));
		nbttagcompound.setBoolean("HasHome", this.hasHome());
		nbttagcompound.setInteger("MyNumber", this.getNumber());
		nbttagcompound.setInteger("Formation", this.getFormationAsNumber());
		nbttagcompound.setInteger("TicksProgress", this.getTicksProgress());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		if (nbttagcompound.hasKey("Home", 9))
		{
			NBTTagList nbttaglist = nbttagcompound.getTagList("Home", 6);
			int hx = (int) nbttaglist.func_150309_d(0);
			int hy = (int) nbttaglist.func_150309_d(1);
			int hz = (int) nbttaglist.func_150309_d(2);
			this.setHomeArea(hx, hy, hz, 20);
		}

		if (!nbttagcompound.getBoolean("HasHome"))
			this.detachHome();

		this.setNumber(nbttagcompound.getInteger("MyNumber"));
		this.switchToFormationByNumber(nbttagcompound.getInteger("Formation"));
		this.setTicksProgress(nbttagcompound.getInteger("TicksProgress"));
	}

	public boolean isWithinHomeArea(int par1, int par2, int par3)
	{
		return this.maximumHomeDistance == -1.0F || this.homePosition.getDistanceSquared(par1, par2, par3) < this.maximumHomeDistance * this.maximumHomeDistance;
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

	public enum Formation
	{
		HOVER,
		LARGE_CLOCKWISE,
		SMALL_CLOCKWISE,
		LARGE_ANTICLOCKWISE,
		SMALL_ANTICLOCKWISE,
		CHARGE_PLUSX,
		CHARGE_MINUSX,
		CHARGE_PLUSZ,
		CHARGE_MINUSZ,
		WAITING_FOR_LEADER,
		ATTACK_PLAYER_START,
		ATTACK_PLAYER_ATTACK
	}
}
