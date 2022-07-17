package twilightforest.entity.boss;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class EntityTFNaga extends EntityMob implements IMob, IBossDisplayData, IEntityMultiPart
{
	private static int TICKS_BEFORE_HEALING = 600;
	private static int MAX_SEGMENTS = 12;
	int currentSegments = 0;
	float segmentHealth;
	int LEASH_X = 46;
	int LEASH_Y = 7;
	int LEASH_Z = 46;
	EntityTFNagaSegment[] body;
	protected PathEntity field_70786_d;
	protected Entity targetEntity;
	int circleCount;
	int intimidateTimer;
	int crumblePlayerTimer;
	int chargeCount;
	boolean clockwise;
	public int ticksSinceDamaged = 0;

	public EntityTFNaga(World world)
	{
		super(world);
		this.setSize(1.75F, 3.0F);
		this.stepHeight = 2.0F;
		this.setHealth(this.getMaxHealth());
		this.segmentHealth = this.getMaxHealth() / 10.0F;
		this.setSegmentsPerHealth();
		this.experienceValue = 217;
		this.ignoreFrustumCheck = true;
		this.circleCount = 15;
		this.body = new EntityTFNagaSegment[MAX_SEGMENTS];

		for (int i = 0; i < this.body.length; ++i)
		{
			this.body[i] = new EntityTFNagaSegment(this, i);
			world.spawnEntityInWorld(this.body[i]);
		}

		this.goNormal();
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	public float getMaxHealthPerDifficulty()
	{
		return this.worldObj != null ? this.worldObj.difficultySetting == EnumDifficulty.EASY ? 120.0F : this.worldObj.difficultySetting == EnumDifficulty.NORMAL ? 200.0F : this.worldObj.difficultySetting == EnumDifficulty.HARD ? 250.0F : 200.0F : 200.0F;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getMaxHealthPerDifficulty());
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
	}

	protected int setSegmentsPerHealth()
	{
		int oldSegments = this.currentSegments;
		int newSegments = (int) (this.getHealth() / this.segmentHealth + (this.getHealth() > 0.0F ? 2 : 0));
		if (newSegments < 0)
			newSegments = 0;

		if (newSegments > MAX_SEGMENTS)
			newSegments = MAX_SEGMENTS;

		if (newSegments != oldSegments)
			if (newSegments < oldSegments)
				for (int i = newSegments; i < oldSegments; ++i)
				{
					if (this.body != null && this.body[i] != null)
						this.body[i].selfDestruct();
				}
			else
				this.spawnBodySegments();

		this.currentSegments = newSegments;
		this.setMovementFactorPerSegments();
		return this.currentSegments;
	}

	protected void setMovementFactorPerSegments()
	{
		float movementFactor = 0.6F - this.currentSegments / 12.0F * 0.2F;
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(movementFactor);
	}

	@Override
	public boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean handleLavaMovement()
	{
		return false;
	}

	@Override
	public void onUpdate()
	{
		this.despawnIfInvalid();
		int i;
		if (this.deathTime > 0)
			for (i = 0; i < 5; ++i)
			{
				double d = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				String explosionType = this.rand.nextBoolean() ? "hugeexplosion" : "explode";
				this.worldObj.spawnParticle(explosionType, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d, d1, d2);
			}

		++this.ticksSinceDamaged;
		if (!this.worldObj.isRemote && this.ticksSinceDamaged > TICKS_BEFORE_HEALING && this.ticksSinceDamaged % 20 == 0)
			this.heal(1.0F);

		this.setSegmentsPerHealth();
		super.onUpdate();
		this.moveSegments();

		for (i = 0; i < this.body.length; ++i)
		{
			if (!this.body[i].addedToChunk && !this.worldObj.isRemote)
				this.worldObj.spawnEntityInWorld(this.body[i]);
		}

	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();
		if (this.isCollidedHorizontally && this.hasTarget())
			this.breakNearbyBlocks();

		if (this.targetEntity != null && !this.isEntityWithinHomeArea(this.targetEntity))
			this.targetEntity = null;

		if (this.targetEntity == null)
		{
			this.targetEntity = this.findTarget();
			if (this.targetEntity != null)
				this.acquireNewPath();
		}
		else if (!this.targetEntity.isEntityAlive())
			this.targetEntity = null;
		else
		{
			float inWater = this.targetEntity.getDistanceToEntity(this);
			if (inWater > 80.0F)
				this.targetEntity = null;
			else if (this.canEntityBeSeen(this.targetEntity))
				this.attackEntity(this.targetEntity, inWater);
		}

		if (!this.hasPath())
			this.acquireNewPath();

		boolean inWater1 = this.isInWater();
		boolean inLava = this.handleLavaMovement();
		Vec3 vec3d = this.hasPath() ? this.field_70786_d.getPosition(this) : null;
		double d1 = this.width * 4.0F;

		while (vec3d != null && vec3d.squareDistanceTo(this.posX, vec3d.yCoord, this.posZ) < d1 * d1)
		{
			this.field_70786_d.incrementPathIndex();
			if (this.field_70786_d.isFinished())
			{
				vec3d = null;
				this.field_70786_d = null;
			}
			else
				vec3d = this.field_70786_d.getPosition(this);
		}

		this.isJumping = false;
		if (vec3d != null)
		{
			d1 = vec3d.xCoord - this.posX;
			double d2 = vec3d.zCoord - this.posZ;
			double dist = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
			int i = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
			double d3 = vec3d.yCoord - i;
			float f2 = (float) (Math.atan2(d2, d1) * 180.0D / 3.1415927410125732D) - 90.0F;
			float f3 = f2 - this.rotationYaw;
			this.moveForward = this.getMoveSpeed();
			this.setAIMoveSpeed(0.5F);
			if (dist > 4.0D && this.chargeCount == 0)
				this.moveStrafing = MathHelper.cos(this.ticksExisted * 0.3F) * this.getMoveSpeed() * 0.6F;

			while (f3 < -180.0F)
			{
				f3 += 360.0F;
			}

			while (f3 >= 180.0F)
			{
				f3 -= 360.0F;
			}

			if (f3 > 30.0F)
				f3 = 30.0F;

			if (f3 < -30.0F)
				f3 = -30.0F;

			this.rotationYaw += f3;
			if (d3 > 0.6D)
				this.isJumping = true;
		}

		if (this.intimidateTimer > 0 && this.hasTarget())
		{
			this.faceEntity(this.targetEntity, 30.0F, 30.0F);
			this.moveForward = 0.1F;
		}

		if (this.intimidateTimer > 0 && this.hasTarget())
		{
			this.faceEntity(this.targetEntity, 30.0F, 30.0F);
			this.moveForward = 0.1F;
		}

		if (this.rand.nextFloat() < 0.8F && (inWater1 || inLava))
			this.isJumping = true;

	}

	private float getMoveSpeed()
	{
		return 0.5F;
	}

	private void setMoveSpeed(float f)
	{
		this.setAIMoveSpeed(f);
	}

	protected void breakNearbyBlocks()
	{
		int minx = MathHelper.floor_double(this.boundingBox.minX - 0.5D);
		int miny = MathHelper.floor_double(this.boundingBox.minY + 1.01D);
		int minz = MathHelper.floor_double(this.boundingBox.minZ - 0.5D);
		int maxx = MathHelper.floor_double(this.boundingBox.maxX + 0.5D);
		int maxy = MathHelper.floor_double(this.boundingBox.maxY + 0.001D);
		int maxz = MathHelper.floor_double(this.boundingBox.maxZ + 0.5D);
		if (this.worldObj.checkChunksExist(minx, miny, minz, maxx, maxy, maxz))
			for (int dx = minx; dx <= maxx; ++dx)
			{
				for (int dy = miny; dy <= maxy; ++dy)
				{
					for (int dz = minz; dz <= maxz; ++dz)
					{
						Block i5 = this.worldObj.getBlock(dx, dy, dz);
						if (i5 != Blocks.air)
							this.breakBlock(dx, dy, dz);
					}
				}
			}

	}

	@Override
	protected String getLivingSound()
	{
		return this.rand.nextInt(3) != 0 ? "TwilightForest:mob.naga.hiss" : "TwilightForest:mob.naga.rattle";
	}

	@Override
	protected String getHurtSound()
	{
		return "TwilightForest:mob.naga.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return "TwilightForest:mob.naga.hurt";
	}

	protected void acquireNewPath()
	{
		if (!this.hasTarget())
			this.wanderRandomly();
		else if (this.intimidateTimer > 0)
		{
			this.field_70786_d = null;
			--this.intimidateTimer;
			if (this.intimidateTimer == 0)
			{
				this.clockwise = !this.clockwise;
				if (this.targetEntity.boundingBox.minY > this.boundingBox.maxY)
					this.doCrumblePlayer();
				else
					this.doCharge();
			}

		}
		else
		{
			if (this.crumblePlayerTimer > 0)
			{
				this.field_70786_d = null;
				--this.crumblePlayerTimer;
				this.crumbleBelowTarget(2);
				this.crumbleBelowTarget(3);
				if (this.crumblePlayerTimer == 0)
					this.doCharge();
			}

			if (this.chargeCount > 0)
			{
				--this.chargeCount;
				Vec3 radius = this.findCirclePoint(this.targetEntity, 14.0D, 3.141592653589793D);
				this.field_70786_d = this.worldObj.getEntityPathToXYZ(this, MathHelper.floor_double(radius.xCoord), MathHelper.floor_double(radius.yCoord), MathHelper.floor_double(radius.zCoord), 40.0F, true, true, true, true);
				if (this.chargeCount == 0)
					this.doCircle();
			}

			if (this.circleCount > 0)
			{
				--this.circleCount;
				double var6 = this.circleCount % 2 == 0 ? 12.0D : 14.0D;
				double rotation = 1.0D;
				if (this.circleCount > 1 && this.circleCount < 3)
					var6 = 16.0D;

				if (this.circleCount == 1)
					rotation = 0.1D;

				Vec3 tpoint = this.findCirclePoint(this.targetEntity, var6, rotation);
				this.field_70786_d = this.worldObj.getEntityPathToXYZ(this, (int) tpoint.xCoord, (int) tpoint.yCoord, (int) tpoint.zCoord, 40.0F, true, true, true, true);
				if (this.circleCount == 0)
					this.doIntimidate();
			}

		}
	}

	protected void crumbleBelowTarget(int range)
	{
		int floor = (int) this.boundingBox.minY;
		int targetY = (int) this.targetEntity.boundingBox.minY;
		if (targetY > floor)
		{
			int dx = (int) this.targetEntity.posX + this.rand.nextInt(range) - this.rand.nextInt(range);
			int dz = (int) this.targetEntity.posZ + this.rand.nextInt(range) - this.rand.nextInt(range);
			int dy = targetY - this.rand.nextInt(range) + this.rand.nextInt(range > 1 ? range - 1 : range);
			if (dy <= floor)
				dy = targetY;

			if (this.worldObj.getBlock(dx, dy, dz) != Blocks.air)
			{
				this.breakBlock(dx, dy, dz);

				for (int k = 0; k < 20; ++k)
				{
					double d = this.rand.nextGaussian() * 0.02D;
					double d1 = this.rand.nextGaussian() * 0.02D;
					double d2 = this.rand.nextGaussian() * 0.02D;
					this.worldObj.spawnParticle("crit", this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d, d1, d2);
				}
			}
		}

	}

	protected void breakBlock(int dx, int dy, int dz)
	{
		/* TODO gamerforEA code clear:
		Block whatsThere = super.worldObj.getBlock(dx, dy, dz);
		int whatsMeta = super.worldObj.getBlockMetadata(dx, dy, dz);
		if (whatsThere != Blocks.air)
		{
			whatsThere.dropBlockAsItem(super.worldObj, dx, dy, dz, whatsMeta, 0);
			super.worldObj.setBlock(dx, dy, dz, Blocks.air, 0, 2);
			super.worldObj.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(whatsThere) + (whatsMeta << 12));
		} */
	}

	protected void doCircle()
	{
		this.circleCount += 10 + this.rand.nextInt(10);
		this.goNormal();
	}

	protected void doCrumblePlayer()
	{
		this.crumblePlayerTimer = 20 + this.rand.nextInt(20);
		this.goSlow();
	}

	protected void doCharge()
	{
		this.chargeCount = 4;
		this.goFast();
	}

	protected void doIntimidate()
	{
		this.intimidateTimer += 15 + this.rand.nextInt(10);
		this.goSlow();
	}

	protected void goSlow()
	{
		this.moveStrafing = 0.0F;
		this.setMoveSpeed(0.1F);
		this.field_70786_d = null;
	}

	protected void goNormal()
	{
		this.setMoveSpeed(0.6F);
	}

	protected void goFast()
	{
		this.setMoveSpeed(1.0F);
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	protected Vec3 findCirclePoint(Entity toCircle, double radius, double rotation)
	{
		double vecx = this.posX - toCircle.posX;
		double vecz = this.posZ - toCircle.posZ;
		float rangle = (float) Math.atan2(vecz, vecx);
		rangle = (float) (rangle + (this.clockwise ? rotation : -rotation));
		double dx = MathHelper.cos(rangle) * radius;
		double dz = MathHelper.sin(rangle) * radius;
		double dy = Math.min(this.boundingBox.minY, toCircle.posY);
		return Vec3.createVectorHelper(toCircle.posX + dx, dy, toCircle.posZ + dz);
	}

	public boolean hasTarget()
	{
		return this.targetEntity != null;
	}

	protected Entity findTarget()
	{
		EntityPlayer entityplayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 32.0D);
		return entityplayer != null && this.canEntityBeSeen(entityplayer) && this.isEntityWithinHomeArea(entityplayer) ? entityplayer : null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i)
	{
		if (damagesource.getSourceOfDamage() != null && !this.isEntityWithinHomeArea(damagesource.getSourceOfDamage()))
			return false;
		if (damagesource.getEntity() != null && !this.isEntityWithinHomeArea(damagesource.getEntity()))
			return false;
		if (super.attackEntityFrom(damagesource, i))
		{
			this.setSegmentsPerHealth();
			Entity entity = damagesource.getEntity();
			if (entity != this)
				this.targetEntity = entity;

			this.ticksSinceDamaged = 0;
			return true;
		}
		return false;
	}

	@Override
	protected void attackEntity(Entity toAttack, float f)
	{
		if (this.attackTime <= 0 && f < 4.0F && toAttack.boundingBox.maxY > this.boundingBox.minY - 2.5D && toAttack.boundingBox.minY < this.boundingBox.maxY + 2.5D)
		{
			this.attackTime = 20;
			this.attackEntityAsMob(toAttack);
			if (this.getMoveSpeed() > 0.8D)
				toAttack.addVelocity(-MathHelper.sin(this.rotationYaw * 3.141593F / 180.0F) * 1.0F, 0.1D, MathHelper.cos(this.rotationYaw * 3.141593F / 180.0F) * 1.0F);
		}

	}

	protected void wanderRandomly()
	{
		this.goNormal();
		boolean flag = false;
		int tx = -1;
		int ty = -1;
		int tz = -1;
		float worstweight = -99999.0F;

		for (int l = 0; l < 10; ++l)
		{
			int dx = MathHelper.floor_double(this.posX + this.rand.nextInt(21) - 6.0D);
			int dy = MathHelper.floor_double(this.posY + this.rand.nextInt(7) - 3.0D);
			int dz = MathHelper.floor_double(this.posZ + this.rand.nextInt(21) - 6.0D);
			if (!this.isWithinHomeDistance(dx, dy, dz))
			{
				dx = this.getHomePosition().posX + this.rand.nextInt(21) - this.rand.nextInt(21);
				dy = this.getHomePosition().posY + this.rand.nextInt(7) - this.rand.nextInt(7);
				dz = this.getHomePosition().posZ + this.rand.nextInt(21) - this.rand.nextInt(21);
			}

			float weight = this.getBlockPathWeight(dx, dy, dz);
			if (weight > worstweight)
			{
				worstweight = weight;
				tx = dx;
				ty = dy;
				tz = dz;
				flag = true;
			}
		}

		if (flag)
			this.field_70786_d = this.worldObj.getEntityPathToXYZ(this, tx, ty, tz, 80.0F, true, true, true, true);

	}

	@Override
	public float getBlockPathWeight(int i, int j, int k)
	{
		return !this.isWithinHomeDistance(i, j, k) ? Float.MIN_VALUE : 0.0F;
	}

	@Override
	public boolean hasPath()
	{
		return this.field_70786_d != null;
	}

	@Override
	protected Item getDropItem()
	{
		return TFItems.nagaScale;
	}

	@Override
	protected void dropFewItems(boolean flag, int z)
	{
		Item i = this.getDropItem();
		if (i != null)
		{
			int j = 6 + this.rand.nextInt(6);

			for (int k = 0; k < j; ++k)
			{
				this.dropItem(i, 1);
			}
		}

		this.entityDropItem(new ItemStack(TFItems.trophy, 1, 1), 0.0F);
	}

	protected void despawnIfInvalid()
	{
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			this.despawnMe();

	}

	protected void despawnMe()
	{
		if (this.isLeashed())
		{
			ChunkCoordinates home = this.getHomePosition();

			// TODO gamerforEA code start
			if (ModUtils.isTFWorld(this))
				// TODO gamerforEA code end
				this.worldObj.setBlock(home.posX, home.posY, home.posZ, TFBlocks.bossSpawner, 0, 2);
		}

		this.setDead();
	}

	public boolean isLeashed()
	{
		return this.getMaximumHomeDistance() > -1.0F;
	}

	@Override
	public boolean isWithinHomeDistance(int x, int y, int z)
	{
		if (this.getMaximumHomeDistance() == -1.0F)
			return true;
		int distX = Math.abs(this.getHomePosition().posX - x);
		int distY = Math.abs(this.getHomePosition().posY - y);
		int distZ = Math.abs(this.getHomePosition().posZ - z);
		return distX <= this.LEASH_X && distY <= this.LEASH_Y && distZ <= this.LEASH_Z;
	}

	public boolean isEntityWithinHomeArea(Entity entity)
	{
		return this.isWithinHomeDistance(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
	}

	protected void spawnBodySegments()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.body == null)
				this.body = new EntityTFNagaSegment[MAX_SEGMENTS];

			for (int i = 0; i < this.currentSegments; ++i)
			{
				if (this.body[i] == null || this.body[i].isDead)
				{
					this.body[i] = new EntityTFNagaSegment(this, i);
					this.body[i].setLocationAndAngles(this.posX + 0.1D * i, this.posY + 0.5D, this.posZ + 0.1D * i, this.rand.nextFloat() * 360.0F, 0.0F);
					this.worldObj.spawnEntityInWorld(this.body[i]);
				}
			}
		}

	}

	protected void moveSegments()
	{
		for (int i = 0; i < this.currentSegments; ++i)
		{
			Entity leader;
			if (i == 0)
				leader = this;
			else
				leader = this.body[i - 1];

			double followX = leader.posX;
			double followY = leader.posY;
			double followZ = leader.posZ;
			float angle = (leader.rotationYaw + 180.0F) * 3.141593F / 180.0F;
			double straightenForce = 0.05D + 1.0D / (i + 1) * 0.5D;
			double idealX = -MathHelper.sin(angle) * straightenForce;
			double idealZ = MathHelper.cos(angle) * straightenForce;
			Vec3 diff = Vec3.createVectorHelper(this.body[i].posX - followX, this.body[i].posY - followY, this.body[i].posZ - followZ);
			diff = diff.normalize();
			diff = diff.addVector(idealX, 0.0D, idealZ);
			diff = diff.normalize();
			double f = 2.0D;
			double destX = followX + f * diff.xCoord;
			double destY = followY + f * diff.yCoord;
			double destZ = followZ + f * diff.zCoord;
			this.body[i].setPosition(destX, destY, destZ);
			this.body[i].motionX = f * diff.xCoord;
			this.body[i].motionY = f * diff.yCoord;
			this.body[i].motionZ = f * diff.zCoord;
			double distance = MathHelper.sqrt_double(diff.xCoord * diff.xCoord + diff.zCoord * diff.zCoord);
			if (i == 0)
				diff.yCoord -= 0.15D;

			this.body[i].setRotation((float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0D / 3.141592653589793D) + 90.0F, -((float) (Math.atan2(diff.yCoord, distance) * 180.0D / 3.141592653589793D)));
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
			this.setHomeArea(hx, hy, hz, 20);
		}

		if (!nbttagcompound.getBoolean("HasHome"))
			this.detachHome();

		this.setSegmentsPerHealth();
	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
		{
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightKillNaga);
		}

		if (!this.worldObj.isRemote && this.worldObj.provider instanceof WorldProviderTwilightForest)
		{
			int dx = MathHelper.floor_double(this.posX);
			int dy = MathHelper.floor_double(this.posY);
			int dz = MathHelper.floor_double(this.posZ);
			ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) this.worldObj.provider).getChunkProvider();
			TFFeature nearbyFeature = ((TFWorldChunkManager) this.worldObj.provider.worldChunkMgr).getFeatureAt(dx, dz, this.worldObj);
			if (nearbyFeature == TFFeature.nagaCourtyard)
				chunkProvider.setStructureConquered(dx, dy, dz, true);
		}

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
		return this.body;
	}

	public float getMaximumHomeDistance()
	{
		return this.func_110174_bM();
	}
}
