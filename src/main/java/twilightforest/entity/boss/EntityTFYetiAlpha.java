package twilightforest.entity.boss;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.EventConfig;
import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.entity.ai.EntityAIStayNearHome;
import twilightforest.entity.ai.EntityAITFThrowRider;
import twilightforest.entity.ai.EntityAITFYetiRampage;
import twilightforest.entity.ai.EntityAITFYetiTired;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

import java.util.ArrayList;

public class EntityTFYetiAlpha extends EntityMob implements IRangedAttackMob
{
	private static final int RAMPAGE_FLAG = 16;
	private static final int TIRED_FLAG = 17;
	private int collisionCounter;
	private boolean canRampage;

	public EntityTFYetiAlpha(World par1World)
	{
		super(par1World);
		this.setSize(3.8F, 5.0F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAITFYetiTired(this, 100));
		this.tasks.addTask(2, new EntityAITFThrowRider(this, 1.0F));
		this.tasks.addTask(3, new EntityAIStayNearHome(this, 2.0F));
		this.tasks.addTask(4, new EntityAITFYetiRampage(this, 10, 180));
		this.tasks.addTask(5, new EntityAIArrowAttack(this, 1.0D, 40, 40, 40.0F));
		this.tasks.addTask(6, new EntityAIWander(this, 2.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
		this.experienceValue = 317;
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, (byte) 0);
		this.dataWatcher.addObject(17, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.38D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
	}

	@Override
	public void onLivingUpdate()
	{
		if (this.riddenByEntity != null && this.riddenByEntity.isSneaking())
			this.riddenByEntity.setSneaking(false);

		super.onLivingUpdate();
		if (this.riddenByEntity != null)
			this.getLookHelper().setLookPositionWithEntity(this.riddenByEntity, 100.0F, 100.0F);

		if (this.isCollided)
			++this.collisionCounter;

		if (this.collisionCounter >= 15)
		{
			if (!this.worldObj.isRemote)
				this.destroyBlocksInAABB(this.boundingBox);

			this.collisionCounter = 0;
		}

		if (this.isRampaging())
		{
			float i = this.ticksExisted / 10.0F;

			for (int i1 = 0; i1 < 20; ++i1)
			{
				this.addSnowEffect(i + i1 * 50, i1 + i);
			}

			this.limbSwingAmount = (float) (this.limbSwingAmount + 0.6D);
		}

		if (this.isTired())
			for (int var3 = 0; var3 < 20; ++var3)
			{
				this.worldObj.spawnParticle("splash", this.posX + (this.rand.nextDouble() - 0.5D) * this.width * 0.5D, this.posY + this.getEyeHeight(), this.posZ + (this.rand.nextDouble() - 0.5D) * this.width * 0.5D, (this.rand.nextFloat() - 0.5F) * 0.75F, 0.0D, (this.rand.nextFloat() - 0.5F) * 0.75F);
			}

	}

	private void addSnowEffect(float rotation, float hgt)
	{
		double px = 3.0D * Math.cos(rotation);
		double py = hgt % 5.0F;
		double pz = 3.0D * Math.sin(rotation);
		TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", this.lastTickPosX + px, this.lastTickPosY + py, this.lastTickPosZ + pz, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		if (super.interact(par1EntityPlayer))
			return true;
		if (this.worldObj.isRemote || this.riddenByEntity != null && this.riddenByEntity != par1EntityPlayer)
			return false;
		par1EntityPlayer.mountEntity(this);
		return true;
	}

	@Override
	public boolean attackEntityAsMob(Entity par1Entity)
	{
		if (this.riddenByEntity == null && par1Entity.ridingEntity == null)
			par1Entity.mountEntity(this);

		return super.attackEntityAsMob(par1Entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		if (!this.canRampage && !this.isTired() && par1DamageSource.isProjectile())
			return false;
		boolean success = super.attackEntityFrom(par1DamageSource, par2);
		this.canRampage = true;
		return success;
	}

	@Override
	protected void dropFewItems(boolean flag, int looting)
	{
		Item fur = this.getDropItem();
		int drops;
		if (fur != null)
		{
			int bombs = 6 + this.rand.nextInt(6 + looting);

			for (drops = 0; drops < bombs; ++drops)
			{
				this.dropItem(fur, 1);
			}
		}

		Item var7 = TFItems.iceBomb;
		drops = 6 + this.rand.nextInt(6 + looting);

		for (int d = 0; d < drops; ++d)
		{
			this.dropItem(var7, 1);
		}

	}

	@Override
	protected Item getDropItem()
	{
		return TFItems.alphaFur;
	}

	@Override
	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			Vec3 riderPos = this.getRiderPosition();
			this.riddenByEntity.setPosition(riderPos.xCoord, riderPos.yCoord, riderPos.zCoord);
		}

	}

	@Override
	public double getMountedYOffset()
	{
		return 5.75D;
	}

	public Vec3 getRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			float distance = 0.4F;
			double var1 = Math.cos((this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D) * distance;
			double var3 = Math.sin((this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D) * distance;
			return Vec3.createVectorHelper(this.posX + var1, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + var3);
		}
		return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
	}

	@Override
	public boolean canRiderInteract()
	{
		return true;
	}

	public boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB)
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

		// TODO gamerforEA code start
		EntityPlayer player = ModUtils.getModFake(this.worldObj);
		// TODO gamerforEA code end

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
							// TODO gamerforEA code start
							if (!EventConfig.enableYetiAlphaGrief)
								continue;
							if (EventUtils.cantBreak(player, dx, dy, dz))
								continue;
							// TODO gamerforEA code end

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

	public void makeRandomBlockFall()
	{
		this.makeRandomBlockFall(30);
	}

	private void makeRandomBlockFall(int range)
	{
		int bx = MathHelper.floor_double(this.posX) + this.getRNG().nextInt(range) - this.getRNG().nextInt(range);
		int bz = MathHelper.floor_double(this.posZ) + this.getRNG().nextInt(range) - this.getRNG().nextInt(range);
		int by = MathHelper.floor_double(this.posY + this.getEyeHeight());
		this.makeBlockFallAbove(bx, bz, by);
	}

	private void makeBlockFallAbove(int x, int z, int y)
	{
		// TODO gamerforEA code start
		if (!EventConfig.enableYetiAlphaGrief)
			return;
		if (!ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj))
			return;
		// TODO gamerforEA code end

		if (this.worldObj.isAirBlock(x, y, z))
			for (int yOffset = 1; yOffset < 30; ++yOffset)
			{
				if (!this.worldObj.isAirBlock(x, y + yOffset, z))
				{
					this.makeBlockFall(x, y + yOffset, z);
					break;
				}
			}

	}

	public void makeNearbyBlockFall()
	{
		this.makeRandomBlockFall(15);
	}

	public void makeBlockAboveTargetFall()
	{
		EntityLivingBase attackTarget = this.getAttackTarget();
		if (attackTarget != null)
		{
			int bx = MathHelper.floor_double(attackTarget.posX);
			int bz = MathHelper.floor_double(attackTarget.posZ);
			int by = MathHelper.floor_double(attackTarget.posY + attackTarget.getEyeHeight());
			this.makeBlockFallAbove(bx, bz, by);
		}

	}

	private void makeBlockFall(int x, int y, int z)
	{
		// TODO gamerforEA code start
		if (!EventConfig.enableYetiAlphaGrief)
			return;
		if (!ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj))
			return;
		if (EventUtils.cantBreak(ModUtils.getModFake(this.worldObj), x, y, z))
			return;
		// TODO gamerforEA code end

		Block block = this.worldObj.getBlock(x, y, z);
		int meta = this.worldObj.getBlockMetadata(x, y, z);
		this.worldObj.setBlock(x, y, z, Blocks.packed_ice);
		this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
		EntityTFFallingIce ice = new EntityTFFallingIce(this.worldObj, x, y - 3, z);
		this.worldObj.spawnEntityInWorld(ice);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float par2)
	{
		if (!this.canRampage)
		{
			EntityTFIceBomb ice = new EntityTFIceBomb(this.worldObj, this);
			double d0 = target.posX - this.posX;
			double d1 = target.posY + target.getEyeHeight() - 1.100000023841858D - target.posY;
			double d2 = target.posZ - this.posZ;
			float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
			ice.setThrowableHeading(d0, d1 + f1, d2, 0.75F, 12.0F);
			this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
			this.worldObj.spawnEntityInWorld(ice);
		}

	}

	@Override
	public boolean canDespawn()
	{
		return false;
	}

	public boolean canRampage()
	{
		return this.canRampage;
	}

	public void setRampaging(boolean par1)
	{
		this.getDataWatcher().updateObject(16, (byte) (par1 ? 1 : 0));
	}

	public boolean isRampaging()
	{
		return this.getDataWatcher().getWatchableObjectByte(16) == 1;
	}

	public void setTired(boolean par1)
	{
		this.getDataWatcher().updateObject(17, (byte) (par1 ? 1 : 0));
		this.canRampage = false;
	}

	public boolean isTired()
	{
		return this.getDataWatcher().getWatchableObjectByte(17) == 1;
	}

	@Override
	protected void fall(float par1)
	{
		super.fall(par1);
		if (this.isRampaging())
		{
			this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY - 0.20000000298023224D - this.yOffset);
			int k = MathHelper.floor_double(this.posZ);
			this.worldObj.playAuxSFX(2006, i, j, k, 20);
			this.worldObj.playAuxSFX(2006, i, j, k, 30);
			if (!this.worldObj.isRemote)
				this.hitNearbyEntities();
		}

	}

	private void hitNearbyEntities()
	{
		ArrayList nearby = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(5.0D, 0.0D, 5.0D)));

		for (Object aNearby : nearby)
		{
			Entity entity = (Entity) aNearby;
			if (entity instanceof EntityLivingBase)
			{
				// TODO gamerforEA code replace, old code:
				// boolean hit = entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F);
				boolean hit = entity.attackEntityFrom(DamageSource.causeMobDamage(this), BalanceConfig.yetiAlphaFallDamage);
				// TODO gamerforEA code end

				if (hit)
					entity.motionY += 0.4000000059604645D;
			}
		}

	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
		{
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightProgressYeti);
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
				if (nearbyFeature == TFFeature.yetiCave)
					chunkProvider.setStructureConquered(dx, dy, dz, true);
			}
		}

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		ChunkCoordinates home = this.getHomePosition();
		nbttagcompound.setTag("Home", this.newDoubleNBTList(home.posX, home.posY, home.posZ));
		nbttagcompound.setBoolean("HasHome", this.hasHome());
		super.writeEntityToNBT(nbttagcompound);
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
			this.setHomeArea(hx, hy, hz, 30);
		}

		if (!nbttagcompound.getBoolean("HasHome"))
			this.detachHome();

	}
}
