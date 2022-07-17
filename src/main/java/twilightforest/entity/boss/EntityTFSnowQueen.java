package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.entity.IBreathAttacker;
import twilightforest.entity.ai.EntityAITFHoverBeam;
import twilightforest.entity.ai.EntityAITFHoverSummon;
import twilightforest.entity.ai.EntityAITFHoverThenDrop;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

import java.util.List;

public class EntityTFSnowQueen extends EntityMob implements IBossDisplayData, IEntityMultiPart, IBreathAttacker
{
	private static final int MAX_SUMMONS = 6;
	private static final int BEAM_FLAG = 21;
	private static final int PHASE_FLAG = 22;
	private static final int MAX_DAMAGE_WHILE_BEAMING = 25;
	private static final float BREATH_DAMAGE = 4.0F;
	public Entity[] iceArray;
	private int summonsRemaining = 0;
	private int successfulDrops;
	private int maxDrops;
	private int damageWhileBeaming;

	public EntityTFSnowQueen(World par1World)
	{
		super(par1World);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAITFHoverSummon(this, EntityPlayer.class, 1.0D));
		this.tasks.addTask(2, new EntityAITFHoverThenDrop(this, EntityPlayer.class, 80, 20));
		this.tasks.addTask(3, new EntityAITFHoverBeam(this, EntityPlayer.class, 80, 100));
		this.tasks.addTask(6, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.setSize(0.7F, 2.2F);
		this.iceArray = new Entity[7];

		for (int i = 0; i < this.iceArray.length; ++i)
		{
			this.iceArray[i] = new EntityTFSnowQueenIceShield(this);
		}

		this.setCurrentPhase(EntityTFSnowQueen.Phase.SUMMON);
		this.isImmuneToFire = true;
		this.experienceValue = 317;
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(21, (byte) 0);
		this.dataWatcher.addObject(22, (byte) 0);
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected String getLivingSound()
	{
		return "TwilightForest:mob.ice.noise";
	}

	@Override
	protected String getHurtSound()
	{
		return "TwilightForest:mob.ice.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return "TwilightForest:mob.ice.death";
	}

	@Override
	protected Item getDropItem()
	{
		return Items.snowball;
	}

	@Override
	protected void enchantEquipment()
	{
		super.enchantEquipment();
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
	{
		return super.onSpawnWithEgg(par1EntityLivingData);
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		for (int i = 0; i < 3; ++i)
		{
			float px = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
			float py = this.getEyeHeight() + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
			float pz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
			TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowguardian", this.lastTickPosX + (double) px, this.lastTickPosY + (double) py, this.lastTickPosZ + (double) pz, 0.0D, 0.0D, 0.0D);
		}

		if (this.getCurrentPhase() == EntityTFSnowQueen.Phase.DROP)
			for (int i = 0; i < this.iceArray.length; ++i)
			{
				float px = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
				float py = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
				float pz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
				TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowwarning", this.iceArray[i].lastTickPosX + (double) px, this.iceArray[i].lastTickPosY + (double) py, this.iceArray[i].lastTickPosZ + (double) pz, 0.0D, 0.0D, 0.0D);
			}

		if (this.isBreathing() && this.isEntityAlive())
		{
			Vec3 look = this.getLookVec();
			double dist = 0.5D;
			double px = this.posX + look.xCoord * dist;
			double py = this.posY + 1.7000000476837158D + look.yCoord * dist;
			double pz = this.posZ + look.zCoord * dist;

			for (int i = 0; i < 10; ++i)
			{
				double dx = look.xCoord;
				double dy = 0.0D;
				double dz = look.zCoord;
				double spread = 2.0D + this.getRNG().nextDouble() * 2.5D;
				double velocity = 2.0D + this.getRNG().nextDouble() * 0.15D;
				dx = dx + this.getRNG().nextGaussian() * 0.0075D * spread;
				dy = dy + this.getRNG().nextGaussian() * 0.0075D * spread;
				dz = dz + this.getRNG().nextGaussian() * 0.0075D * spread;
				dx = dx * velocity;
				dy = dy * velocity;
				dz = dz * velocity;
				TwilightForestMod.proxy.spawnParticle(this.worldObj, "icebeam", px, py, pz, dx, dy, dz);
			}
		}

	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		for (int i = 0; i < this.iceArray.length; ++i)
		{
			this.iceArray[i].onUpdate();
			if (i < this.iceArray.length - 1)
			{
				Vec3 blockPos = this.getIceShieldPosition(i);
				this.iceArray[i].setPosition(blockPos.xCoord, blockPos.yCoord, blockPos.zCoord);
				this.iceArray[i].rotationYaw = this.getIceShieldAngle(i);
			}
			else
			{
				this.iceArray[i].setPosition(this.posX, this.posY - 1.0D, this.posZ);
				this.iceArray[i].rotationYaw = this.getIceShieldAngle(i);
			}

			if (!this.worldObj.isRemote)
				this.applyShieldCollisions(this.iceArray[i]);
		}

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
	protected void dropFewItems(boolean par1, int par2)
	{
		this.dropBow();
		int totalDrops = this.rand.nextInt(4 + par2) + 1;

		for (int i = 0; i < totalDrops; ++i)
		{
			this.dropItem(Item.getItemFromBlock(Blocks.packed_ice), 7);
		}

		totalDrops = this.rand.nextInt(5 + par2) + 5;

		for (int i = 0; i < totalDrops; ++i)
		{
			this.dropItem(Items.snowball, 16);
		}

		this.entityDropItem(new ItemStack(TFItems.trophy, 1, 4), 0.0F);
	}

	private void dropBow()
	{
		int bowType = this.rand.nextInt(2);
		if (bowType == 0)
			this.entityDropItem(new ItemStack(TFItems.tripleBow), 0.0F);
		else
			this.entityDropItem(new ItemStack(TFItems.seekerBow), 0.0F);

	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
		{
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightProgressGlacier);
		}

		if (!this.worldObj.isRemote)
		{
			int dx = MathHelper.floor_double(this.posX);
			int dy = MathHelper.floor_double(this.posY);
			int dz = MathHelper.floor_double(this.posZ);
			if (this.worldObj.provider instanceof WorldProviderTwilightForest)
			{
				ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) this.worldObj.provider).getChunkProvider();
				TFFeature nearbyFeature = ((TFWorldChunkManager) this.worldObj.provider.worldChunkMgr).getFeatureAt(dx, dz, this.worldObj);
				if (nearbyFeature == TFFeature.lichTower)
					chunkProvider.setStructureConquered(dx, dy, dz, true);
			}
		}

	}

	private void applyShieldCollisions(Entity collider)
	{
		for (Entity collided : (Iterable<? extends Entity>) this.worldObj.getEntitiesWithinAABBExcludingEntity(collider, collider.boundingBox.expand(-0.20000000298023224D, -0.20000000298023224D, -0.20000000298023224D)))
		{
			if (collided.canBePushed())
				this.applyShieldCollision(collider, collided);
		}

	}

	protected void applyShieldCollision(Entity collider, Entity collided)
	{
		if (collided != this)
		{
			collided.applyEntityCollision(collider);
			if (collided instanceof EntityLivingBase)
			{
				boolean attackSuccess = this.attackEntityAsMob(collided);
				if (attackSuccess)
				{
					collided.motionY += 0.4000000059604645D;
					this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
				}
			}
		}

	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();
		if (this.getCurrentPhase() == EntityTFSnowQueen.Phase.SUMMON && this.getSummonsRemaining() == 0 && this.countMyMinions() <= 0)
			this.setCurrentPhase(Phase.DROP);

		if (this.getCurrentPhase() == EntityTFSnowQueen.Phase.DROP && this.successfulDrops >= this.maxDrops)
			this.setCurrentPhase(Phase.BEAM);

		if (this.getCurrentPhase() == EntityTFSnowQueen.Phase.BEAM && this.damageWhileBeaming >= 25)
			this.setCurrentPhase(Phase.SUMMON);

	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float damage)
	{
		boolean result = super.attackEntityFrom(par1DamageSource, damage);
		if (result && this.getCurrentPhase() == EntityTFSnowQueen.Phase.BEAM)
			this.damageWhileBeaming = (int) ((float) this.damageWhileBeaming + damage);

		return result;
	}

	private Vec3 getIceShieldPosition(int i)
	{
		return this.getIceShieldPosition(this.getIceShieldAngle(i), 1.0F);
	}

	private float getIceShieldAngle(int i)
	{
		return 60.0F * (float) i + (float) this.ticksExisted * 5.0F;
	}

	public Vec3 getIceShieldPosition(float angle, float distance)
	{
		double var1 = Math.cos((double) angle * 3.141592653589793D / 180.0D) * (double) distance;
		double var3 = Math.sin((double) angle * 3.141592653589793D / 180.0D) * (double) distance;
		return Vec3.createVectorHelper(this.posX + var1, this.posY + this.getShieldYOffset(), this.posZ + var3);
	}

	public double getShieldYOffset()
	{
		return 0.10000000149011612D;
	}

	@Override
	protected void fall(float par1)
	{
	}

	@Override
	public World func_82194_d()
	{
		return this.worldObj;
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart entitydragonpart, DamageSource damagesource, float i)
	{
		return false;
	}

	@Override
	public Entity[] getParts()
	{
		return this.iceArray;
	}

	public boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB)
	{
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
					Block block = this.worldObj.getBlock(dx, dy, dz);
					if (block != Blocks.air)
					{
						int currentMeta = this.worldObj.getBlockMetadata(dx, dy, dz);
						if (block != Blocks.ice && block != Blocks.packed_ice)
							wasBlocked = true;
						else
						{
							this.worldObj.setBlock(dx, dy, dz, Blocks.air, 0, 2);
							this.worldObj.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(block) + (currentMeta << 12));
						}
					}
				}
			}
		}

		return wasBlocked;
	}

	@Override
	public boolean isBreathing()
	{
		return this.getDataWatcher().getWatchableObjectByte(21) == 1;
	}

	@Override
	public void setBreathing(boolean flag)
	{
		this.getDataWatcher().updateObject(21, (byte) (flag ? 1 : 0));
	}

	public EntityTFSnowQueen.Phase getCurrentPhase()
	{
		return EntityTFSnowQueen.Phase.values()[this.getDataWatcher().getWatchableObjectByte(22)];
	}

	public void setCurrentPhase(EntityTFSnowQueen.Phase currentPhase)
	{
		this.getDataWatcher().updateObject(22, (byte) currentPhase.ordinal());
		if (currentPhase == EntityTFSnowQueen.Phase.SUMMON)
			this.setSummonsRemaining(6);

		if (currentPhase == EntityTFSnowQueen.Phase.DROP)
		{
			this.successfulDrops = 0;
			this.maxDrops = 2 + this.rand.nextInt(3);
		}

		if (currentPhase == EntityTFSnowQueen.Phase.BEAM)
			this.damageWhileBeaming = 0;

	}

	public int getSummonsRemaining()
	{
		return this.summonsRemaining;
	}

	public void setSummonsRemaining(int summonsRemaining)
	{
		this.summonsRemaining = summonsRemaining;
	}

	public void summonMinionAt(EntityLivingBase targetedEntity)
	{
		Vec3 minionSpot = this.findVecInLOSOf(targetedEntity);
		EntityTFIceCrystal minion = new EntityTFIceCrystal(this.worldObj);
		minion.setPosition(minionSpot.xCoord, minionSpot.yCoord, minionSpot.zCoord);
		this.worldObj.spawnEntityInWorld(minion);
		minion.setAttackTarget(targetedEntity);
		minion.setToDieIn30Seconds();
		--this.summonsRemaining;
	}

	protected Vec3 findVecInLOSOf(Entity targetEntity)
	{
		if (targetEntity == null)
			return null;
		double tx = 0.0D;
		double ty = 0.0D;
		double tz = 0.0D;
		int tries = 100;

		for (int i = 0; i < tries; ++i)
		{
			tx = targetEntity.posX + this.rand.nextGaussian() * 16.0D;
			ty = targetEntity.posY + this.rand.nextGaussian() * 8.0D;
			tz = targetEntity.posZ + this.rand.nextGaussian() * 16.0D;
			boolean groundFlag = false;
			int bx = MathHelper.floor_double(tx);
			int by = MathHelper.floor_double(ty);
			int bz = MathHelper.floor_double(tz);

			while (!groundFlag && ty > 0.0D)
			{
				Block whatsThere = this.worldObj.getBlock(bx, by - 1, bz);
				if (whatsThere != Blocks.air && whatsThere.getMaterial().isSolid())
					groundFlag = true;
				else
				{
					--ty;
					--by;
				}
			}

			if (by != 0 && this.canEntitySee(targetEntity, tx, ty, tz))
			{
				float halfWidth = this.width / 2.0F;
				AxisAlignedBB destBox = AxisAlignedBB.getBoundingBox(tx - (double) halfWidth, ty - (double) this.yOffset + (double) this.ySize, tz - (double) halfWidth, tx + (double) halfWidth, ty - (double) this.yOffset + (double) this.ySize + (double) this.height, tz + (double) halfWidth);
				if (this.worldObj.getCollidingBoundingBoxes(this, destBox).size() <= 0 && !this.worldObj.isAnyLiquid(destBox))
					break;
			}
		}

		return tries == 99 ? null : Vec3.createVectorHelper(tx, ty, tz);
	}

	protected boolean canEntitySee(Entity entity, double dx, double dy, double dz)
	{
		return this.worldObj.rayTraceBlocks(Vec3.createVectorHelper(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ), Vec3.createVectorHelper(dx, dy, dz)) == null;
	}

	public int countMyMinions()
	{
		List<EntityTFIceCrystal> nearbyMinons = this.worldObj.getEntitiesWithinAABB(EntityTFIceCrystal.class, AxisAlignedBB.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(32.0D, 16.0D, 32.0D));
		return nearbyMinons.size();
	}

	public void incrementSuccessfulDrops()
	{
		++this.successfulDrops;
	}

	@Override
	public void doBreathAttack(Entity target)
	{
		// TODO gamerforEA code replace, old code:
		// target.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
		target.attackEntityFrom(DamageSource.causeMobDamage(this), BalanceConfig.snowQueenBreathDamage);
		// TODO gamerforEA code end
	}

	public enum Phase
	{
		SUMMON,
		DROP,
		BEAM
	}
}
