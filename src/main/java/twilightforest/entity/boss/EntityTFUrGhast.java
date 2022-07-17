package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TFTreasure;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;
import twilightforest.entity.EntityTFTowerGhast;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

import java.util.ArrayList;
import java.util.List;

public class EntityTFUrGhast extends EntityTFTowerGhast implements IBossDisplayData
{
	private static final int DATA_TANTRUM = 18;
	private static final int HOVER_ALTITUDE = 20;
	public double courseX;
	public double courseY;
	public double courseZ;
	ArrayList<ChunkCoordinates> trapLocations;
	ArrayList<ChunkCoordinates> travelCoords;
	int currentTravelCoordIndex;
	int travelPathRepetitions;
	int desiredRepetitions;
	int nextTantrumCry;
	float damageUntilNextPhase;
	boolean noTrapMode;

	public EntityTFUrGhast(World par1World)
	{
		super(par1World);
		this.setSize(14.0F, 18.0F);
		this.aggroRange = 128.0F;
		this.wanderFactor = 32.0F;
		this.noClip = true;
		this.trapLocations = new ArrayList<>();
		this.travelCoords = new ArrayList<>();
		this.setInTantrum(false);
		this.damageUntilNextPhase = 45.0F;
		this.experienceValue = 317;
		this.noTrapMode = false;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250.0D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(18, (byte) 0);
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.deathTime > 0)
			for (int k = 0; k < 5; ++k)
			{
				double d = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				String explosionType = this.rand.nextBoolean() ? "hugeexplosion" : "explode";
				this.worldObj.spawnParticle(explosionType, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d, d1, d2);
			}

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (source == DamageSource.inWall)
			return false;
		boolean attackSuccessful = false;
		if (this.isInTantrum())
			damage /= 4.0F;

		if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer)
			attackSuccessful = super.attackEntityFrom(DamageSource.causeThrownDamage(source.getSourceOfDamage(), source.getEntity()), damage);
		else
			attackSuccessful = super.attackEntityFrom(source, damage);

		if (!this.worldObj.isRemote)
			if (this.hurtTime == this.maxHurtTime)
			{
				this.damageUntilNextPhase -= this.getLastDamage();
				FMLLog.info("[Urghast] Attack successful, %f damage until phase switch.", this.damageUntilNextPhase);
				if (this.damageUntilNextPhase <= 0.0F)
					this.switchPhase();
			}
			else
				FMLLog.info("[Urghast] Attack fail with %s type attack for %f damage", source.damageType, damage);

		return attackSuccessful;
	}

	private float getLastDamage()
	{
		return this.prevHealth - this.getHealth();
	}

	private void switchPhase()
	{
		if (this.isInTantrum())
			this.stopTantrum();
		else
			this.startTantrum();

		this.damageUntilNextPhase = 48.0F;
	}

	protected void startTantrum()
	{
		this.setInTantrum(true);
		int rainTime = 6000;
		WorldInfo worldInfo = MinecraftServer.getServer().worldServers[0].getWorldInfo();
		worldInfo.setRaining(true);
		worldInfo.setThundering(true);
		worldInfo.setRainTime(rainTime);
		worldInfo.setThunderTime(rainTime);
		this.spawnGhastsAtTraps();
	}

	protected void spawnGhastsAtTraps()
	{
		ArrayList<ChunkCoordinates> ghastSpawns = new ArrayList<>(this.trapLocations);
		int numSpawns = Math.min(2, ghastSpawns.size());

		for (int i = 0; i < numSpawns; ++i)
		{
			int index = this.rand.nextInt(ghastSpawns.size());
			ChunkCoordinates spawnCoord = ghastSpawns.get(index);
			ghastSpawns.remove(index);
			this.spawnMinionGhastsAt(spawnCoord.posX, spawnCoord.posY, spawnCoord.posZ);
		}

	}

	public void stopTantrum()
	{
		this.setInTantrum(false);
	}

	private void spawnMinionGhastsAt(int x, int y, int z)
	{
		int tries = 24;
		int spawns = 0;
		int maxSpawns = 6;
		int rangeXZ = 4;
		int rangeY = 8;
		this.worldObj.addWeatherEffect(new EntityLightningBolt(this.worldObj, (double) x, (double) (y + 4), (double) z));

		for (int i = 0; i < tries; ++i)
		{
			EntityTFMiniGhast minion = new EntityTFMiniGhast(this.worldObj);
			double sx = (double) x + (this.rand.nextDouble() - this.rand.nextDouble()) * (double) rangeXZ;
			double sy = (double) y + this.rand.nextDouble() * (double) rangeY;
			double sz = (double) z + (this.rand.nextDouble() - this.rand.nextDouble()) * (double) rangeXZ;
			minion.setLocationAndAngles(sx, sy, sz, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
			minion.makeBossMinion();
			if (minion.getCanSpawnHere())
			{
				this.worldObj.spawnEntityInWorld(minion);
				minion.spawnExplosionParticle();
			}

			++spawns;
			if (spawns >= maxSpawns)
				break;
		}

	}

	@Override
	protected void updateEntityActionState()
	{
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			this.setDead();

		this.despawnEntity();

		for (EntityTFMiniGhast ghast : (Iterable<? extends EntityTFMiniGhast>) this.worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, this.boundingBox.expand(1.0D, 1.0D, 1.0D)))
		{
			ghast.setDead();
			this.heal(2.0F);
		}

		if (this.trapLocations.isEmpty() && !this.noTrapMode)
			this.scanForTrapsTwice();

		if (this.trapLocations.isEmpty() && !this.noTrapMode)
		{
			FMLLog.info("[TwilightForest] Ur-ghast cannot find traps nearby, entering trap-less mode");
			this.noTrapMode = true;
		}

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

			if (this.isInTantrum())
			{
				this.shedTear();
				this.field_70792_g = null;
				if (--this.nextTantrumCry <= 0)
				{
					this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
					this.nextTantrumCry = 20 + this.rand.nextInt(30);
				}

				if (this.ticksExisted % 10 == 0)
					this.doTantrumDamageEffects();
			}

			this.checkAndChangeCourse();
			double offsetX = this.waypointX - this.posX;
			double offsetY = this.waypointY - this.posY;
			double offsetZ = this.waypointZ - this.posZ;
			double distanceToWaypoint = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
			if (distanceToWaypoint < 1.0D || distanceToWaypoint > 3600.0D)
				this.makeNewWaypoint();

			if (this.courseChangeCooldown-- <= 0)
			{
				this.courseChangeCooldown += this.rand.nextInt(5) + 0;
				distanceToWaypoint = (double) MathHelper.sqrt_double(distanceToWaypoint);
				double speed = 0.05D;
				this.motionX += offsetX / distanceToWaypoint * speed;
				this.motionY += offsetY / distanceToWaypoint * speed;
				this.motionZ += offsetZ / distanceToWaypoint * speed;
			}

			double targetRange = this.aggroCounter <= 0 && !this.isAggressive ? (double) this.stareRange : (double) this.aggroRange;
			if (this.field_70792_g != null && this.field_70792_g.getDistanceSqToEntity(this) < targetRange * targetRange && this.canEntityBeSeen(this.field_70792_g))
			{
				this.faceEntity(this.field_70792_g, 10.0F, (float) this.getVerticalFaceSpeed());
				if (this.isAggressive)
				{
					if (this.attackCounter == 10)
						this.playSound("mob.ghast.charge", this.getSoundVolume(), this.getSoundPitch());

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
				this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / 3.1415927F;
				this.rotationPitch = 0.0F;
			}

			if (this.attackCounter > 0 && !this.isAggressive)
				--this.attackCounter;

			byte currentAggroStatus = this.dataWatcher.getWatchableObjectByte(16);
			byte newAggroStatus = (byte) (this.attackCounter > 10 ? 2 : this.aggroCounter <= 0 && !this.isAggressive ? 0 : 1);
			if (currentAggroStatus != newAggroStatus)
				this.dataWatcher.updateObject(16, newAggroStatus);

		}
	}

	private void doTantrumDamageEffects()
	{
		AxisAlignedBB below = this.boundingBox.getOffsetBoundingBox(0.0D, -16.0D, 0.0D).expand(0.0D, 16.0D, 0.0D);

		for (EntityPlayer player : (Iterable<? extends EntityPlayer>) this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, below))
		{
			int dx = MathHelper.floor_double(player.posX);
			int dy = MathHelper.floor_double(player.posY);
			int dz = MathHelper.floor_double(player.posZ);
			if (this.worldObj.canBlockSeeTheSky(dx, dy, dz))
				// TODO gamerforEA code replace, old code:
				// player.attackEntityFrom(DamageSource.anvil, 3.0F);
				player.attackEntityFrom(DamageSource.anvil, BalanceConfig.urGhastTantrumDamage);
			// TODO gamerforEA code end
		}

		for (EntityTFMiniGhast ghast : (Iterable<? extends EntityTFMiniGhast>) this.worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, below))
		{
			++ghast.motionY;
		}

	}

	private void shedTear()
	{
		TwilightForestMod.proxy.spawnParticle(this.worldObj, "bosstear", this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
	}

	protected void makeNewWaypoint()
	{
		double closestDistance = this.getDistanceSq(this.courseX, this.courseY, this.courseZ);

		for (int i = 0; i < 50; ++i)
		{
			double potentialX = this.posX + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * this.wanderFactor);
			double potentialY = this.courseY + (double) (this.rand.nextFloat() * 8.0F) - 4.0D;
			double potentialZ = this.posZ + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * this.wanderFactor);
			double offsetX = this.courseX - potentialX;
			double offsetY = this.courseY - potentialY;
			double offsetZ = this.courseZ - potentialZ;
			double potentialDistanceToCourse = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
			if (potentialDistanceToCourse < closestDistance)
			{
				this.waypointX = potentialX;
				this.waypointY = potentialY;
				this.waypointZ = potentialZ;
				closestDistance = potentialDistanceToCourse;
			}
		}

	}

	protected void checkAndChangeCourse()
	{
		if (this.courseX == 0.0D && this.courseY == 0.0D && this.courseZ == 0.0D)
			this.changeCourse();

		double offsetX = this.courseX - this.posX;
		double offsetY = this.courseY - this.posY;
		double offsetZ = this.courseZ - this.posZ;
		double distanceToCourse = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
		if (distanceToCourse < 100.0D)
			this.changeCourse();

	}

	private void changeCourse()
	{
		if (this.travelCoords.isEmpty())
			this.makeTravelPath();

		if (!this.travelCoords.isEmpty())
		{
			if (this.currentTravelCoordIndex >= this.travelCoords.size())
			{
				this.currentTravelCoordIndex = 0;
				++this.travelPathRepetitions;
				if (!this.checkGhastsAtTraps())
					this.spawnGhastsAtTraps();
			}

			this.courseX = (double) this.travelCoords.get(this.currentTravelCoordIndex).posX;
			this.courseY = (double) (this.travelCoords.get(this.currentTravelCoordIndex).posY + 20);
			this.courseZ = (double) this.travelCoords.get(this.currentTravelCoordIndex).posZ;
			++this.currentTravelCoordIndex;
		}

	}

	private boolean checkGhastsAtTraps()
	{
		int trapsWithEnoughGhasts = 0;

		for (ChunkCoordinates trap : this.trapLocations)
		{
			AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox((double) trap.posX, (double) trap.posY, (double) trap.posZ, (double) (trap.posX + 1), (double) (trap.posY + 1), (double) (trap.posZ + 1)).expand(8.0D, 16.0D, 8.0D);
			List<EntityTFMiniGhast> nearbyGhasts = this.worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, aabb);
			if (nearbyGhasts.size() >= 4)
				++trapsWithEnoughGhasts;
		}

		return trapsWithEnoughGhasts >= 1;
	}

	private void makeTravelPath()
	{
		int px = MathHelper.floor_double(this.posX);
		int py = MathHelper.floor_double(this.posY);
		int pz = MathHelper.floor_double(this.posZ);
		ArrayList<ChunkCoordinates> potentialPoints;
		if (!this.noTrapMode)
			potentialPoints = new ArrayList<>(this.trapLocations);
		else
		{
			potentialPoints = new ArrayList<>();
			potentialPoints.add(new ChunkCoordinates(px + 20, py - 20, pz));
			potentialPoints.add(new ChunkCoordinates(px, py - 20, pz - 20));
			potentialPoints.add(new ChunkCoordinates(px - 20, py - 20, pz));
			potentialPoints.add(new ChunkCoordinates(px, py - 20, pz + 20));
		}

		this.travelCoords.clear();

		while (!potentialPoints.isEmpty())
		{
			int index = this.rand.nextInt(potentialPoints.size());
			this.travelCoords.add(potentialPoints.get(index));
			potentialPoints.remove(index);
		}

		if (this.noTrapMode)
			this.travelCoords.add(new ChunkCoordinates(px, py - 20, pz));

	}

	@Override
	protected void spitFireball()
	{
		double offsetX = this.field_70792_g.posX - this.posX;
		double offsetY = this.field_70792_g.boundingBox.minY + (double) (this.field_70792_g.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
		double offsetZ = this.field_70792_g.posZ - this.posZ;
		this.worldObj.playAuxSFXAtEntity(null, 1008, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
		EntityTFUrGhastFireball entityFireball = new EntityTFUrGhastFireball(this.worldObj, this, offsetX, offsetY, offsetZ);
		entityFireball.field_92057_e = 1;
		double shotSpawnDistance = 8.5D;
		Vec3 lookVec = this.getLook(1.0F);
		entityFireball.posX = this.posX + lookVec.xCoord * shotSpawnDistance;
		entityFireball.posY = this.posY + (double) (this.height / 2.0F) + lookVec.yCoord * shotSpawnDistance;
		entityFireball.posZ = this.posZ + lookVec.zCoord * shotSpawnDistance;
		this.worldObj.spawnEntityInWorld(entityFireball);

		for (int i = 0; i < 2; ++i)
		{
			entityFireball = new EntityTFUrGhastFireball(this.worldObj, this, offsetX + (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 8.0F), offsetY, offsetZ + (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 8.0F));
			entityFireball.field_92057_e = 1;
			entityFireball.posX = this.posX + lookVec.xCoord * shotSpawnDistance;
			entityFireball.posY = this.posY + (double) (this.height / 2.0F) + lookVec.yCoord * shotSpawnDistance;
			entityFireball.posZ = this.posZ + lookVec.zCoord * shotSpawnDistance;
			this.worldObj.spawnEntityInWorld(entityFireball);
		}

	}

	private void scanForTrapsTwice()
	{
		int scanRangeXZ = 48;
		int scanRangeY = 32;
		int px = MathHelper.floor_double(this.posX);
		int py = MathHelper.floor_double(this.posY);
		int pz = MathHelper.floor_double(this.posZ);
		this.scanForTraps(scanRangeXZ, scanRangeY, px, py, pz);
		if (this.trapLocations.size() > 0)
		{
			int ax = 0;
			int ay = 0;
			int az = 0;

			for (ChunkCoordinates trapCoords : this.trapLocations)
			{
				ax += trapCoords.posX;
				ay += trapCoords.posY;
				az += trapCoords.posZ;
			}

			ax = ax / this.trapLocations.size();
			ay = ay / this.trapLocations.size();
			az = az / this.trapLocations.size();
			this.scanForTraps(scanRangeXZ, scanRangeY, ax, ay, az);
		}

	}

	protected void scanForTraps(int scanRangeXZ, int scanRangeY, int px, int py, int pz)
	{
		for (int sx = -scanRangeXZ; sx <= scanRangeXZ; ++sx)
		{
			for (int sz = -scanRangeXZ; sz <= scanRangeXZ; ++sz)
			{
				for (int sy = -scanRangeY; sy <= scanRangeY; ++sy)
				{
					if (this.isTrapAt(px + sx, py + sy, pz + sz))
					{
						ChunkCoordinates trapCoords = new ChunkCoordinates(px + sx, py + sy, pz + sz);
						if (!this.trapLocations.contains(trapCoords))
							this.trapLocations.add(trapCoords);
					}
				}
			}
		}

	}

	private boolean isTrapAt(int x, int y, int z)
	{
		return this.worldObj.blockExists(x, y, z) && this.worldObj.getBlock(x, y, z) == TFBlocks.towerDevice && (this.worldObj.getBlockMetadata(x, y, z) == 10 || this.worldObj.getBlockMetadata(x, y, z) == 11);
	}

	@Override
	public boolean isBurning()
	{
		return false;
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	public boolean isInTantrum()
	{
		return this.dataWatcher.getWatchableObjectByte(18) != 0;
	}

	public void setInTantrum(boolean par1)
	{
		this.dataWatcher.updateObject(18, par1 ? Byte.valueOf((byte) -1) : Byte.valueOf((byte) 0));
		this.damageUntilNextPhase = 48.0F;
	}

	@Override
	protected float getSoundVolume()
	{
		return 16.0F;
	}

	@Override
	protected float getSoundPitch()
	{
		return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setBoolean("inTantrum", this.isInTantrum());
		super.writeEntityToNBT(nbttagcompound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		this.setInTantrum(nbttagcompound.getBoolean("inTantrum"));
	}

	@Override
	protected void onDeathUpdate()
	{
		super.onDeathUpdate();
		if (this.deathTime == 20 && !this.worldObj.isRemote)
		{
			ChunkCoordinates chestCoords = this.findChestCoords();

			// TODO gamerforEA code add victim:EntityLivingBase
			TFTreasure.darktower_boss.generate(this.worldObj, null, chestCoords.posX, chestCoords.posY, chestCoords.posZ, this);
		}
	}

	@Override
	public void onDeath(DamageSource damageSource)
	{
		super.onDeath(damageSource);
		Entity attacker = damageSource.getSourceOfDamage();
		if (attacker instanceof EntityPlayer)
		{
			EntityPlayer playerAttacker = (EntityPlayer) attacker;
			playerAttacker.triggerAchievement(TFAchievementPage.twilightHunter);
			playerAttacker.triggerAchievement(TFAchievementPage.twilightProgressUrghast);
		}

		if (!this.worldObj.isRemote && this.worldObj.provider instanceof WorldProviderTwilightForest)
		{
			ChunkCoordinates chestCoords = this.findChestCoords();
			int dx = chestCoords.posX;
			int dy = chestCoords.posY;
			int dz = chestCoords.posZ;
			ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) this.worldObj.provider).getChunkProvider();
			TFFeature nearbyFeature = ((TFWorldChunkManager) this.worldObj.provider.worldChunkMgr).getFeatureAt(dx, dz, this.worldObj);
			if (nearbyFeature == TFFeature.darkTower)
				chunkProvider.setStructureConquered(dx, dy, dz, true);
		}

	}

	private ChunkCoordinates findChestCoords()
	{
		if (this.trapLocations.size() <= 0)
			return new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
		int ax = 0;
		int ay = 0;
		int az = 0;

		for (ChunkCoordinates trapCoords : this.trapLocations)
		{
			ax += trapCoords.posX;
			ay += trapCoords.posY;
			az += trapCoords.posZ;
		}

		ax = ax / this.trapLocations.size();
		ay = ay / this.trapLocations.size();
		az = az / this.trapLocations.size();
		return new ChunkCoordinates(ax, ay + 2, az);
	}
}
