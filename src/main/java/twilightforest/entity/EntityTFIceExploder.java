package twilightforest.entity;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.eventhelper.util.ExplosionByPlayer;
import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class EntityTFIceExploder extends EntityMob
{
	private static final float EXPLOSION_RADIUS = 1.0F;

	public EntityTFIceExploder(World par1World)
	{
		super(par1World);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.setSize(0.8F, 1.8F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected Item getDropItem()
	{
		return Items.snowball;
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
	public float getEyeHeight()
	{
		return this.height * 0.6F;
	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
	}

	@Override
	protected void onDeathUpdate()
	{
		++this.deathTime;
		if (this.deathTime == 60)
		{
			boolean canMobGrief = ModUtils.canMobGrief(this.worldObj);

			// TODO gamerforEA use ExplosionByPlayer
			ExplosionByPlayer.createExplosion(ModUtils.NEXUS_FACTORY.getProfile(), null, this.worldObj, this, this.posX, this.posY, this.posZ, 1.0F, canMobGrief);

			if (canMobGrief)
				this.detonate();

			if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
			{
				int i = this.getExperiencePoints(this.attackingPlayer);

				while (i > 0)
				{
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
				}
			}

			this.setDead();

			for (int i = 0; i < 20; ++i)
			{
				double d2 = this.rand.nextGaussian() * 0.02D;
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle("explode", this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
			}
		}

	}

	private void detonate()
	{
		// TODO gamerforEA code start
		if (!ModUtils.isTFWorld(this.worldObj) || !ModUtils.canMobGrief(this.worldObj))
			return;
		// TODO gamerforEA code end

		int range = 4;
		int sx = MathHelper.floor_double(this.posX);
		int sy = MathHelper.floor_double(this.posY);
		int sz = MathHelper.floor_double(this.posZ);

		for (int dx = -range; dx <= range; ++dx)
		{
			for (int dy = -range; dy <= range; ++dy)
			{
				for (int dz = -range; dz <= range; ++dz)
				{
					double distance = Math.sqrt((double) (dx * dx + dy * dy + dz * dz));
					float randRange = (float) range + (this.rand.nextFloat() - this.rand.nextFloat()) * 2.0F;
					if (distance < (double) randRange)
						this.transformBlock(sx + dx, sy + dy, sz + dz);
				}
			}
		}

	}

	private void transformBlock(int x, int y, int z)
	{
		// TODO gamerforEA code start
		if (!ModUtils.isTFWorld(this.worldObj) || !ModUtils.canMobGrief(this.worldObj))
			return;
		// TODO gamerforEA code end

		Block block = this.worldObj.getBlock(x, y, z);
		int meta = this.worldObj.getBlockMetadata(x, y, z);
		if (block.getExplosionResistance(this) < 8.0F && block.getBlockHardness(this.worldObj, x, y, z) >= 0.0F)
		{
			int blockColor = 16777215;

			try
			{
				blockColor = block.colorMultiplier(this.worldObj, x, y, z);
			}
			catch (NoSuchMethodError ignored)
			{
			}

			if (blockColor == 16777215)
				blockColor = block.getMapColor(meta).colorValue;

			Block targetBlock = null;
			if (this.shouldTransformGlass(block, x, y, z))
				targetBlock = Blocks.stained_glass;
			else if (this.shouldTransformClay(block, x, y, z))
				targetBlock = Blocks.stained_hardened_clay;

			// TODO gamerforEA add condition [2]
			if (targetBlock != null && !EventUtils.cantBreak(ModUtils.getModFake(this.worldObj), x, y, z))
				this.worldObj.setBlock(x, y, z, targetBlock, this.getMetaForColor(blockColor), 3);
		}

	}

	private boolean shouldTransformClay(Block block, int x, int y, int z)
	{
		return block.isNormalCube(this.worldObj, x, y, z);
	}

	private boolean shouldTransformGlass(Block block, int x, int y, int z)
	{
		return block != Blocks.air && this.isBlockNormalBounds(block, x, y, z) && (!block.getMaterial().isOpaque() || block.isLeaves(this.worldObj, x, y, z) || block == Blocks.ice || block == TFBlocks.auroraBlock);
	}

	private boolean isBlockNormalBounds(Block block, int x, int y, int z)
	{
		return block.getBlockBoundsMaxX() == 1.0D && block.getBlockBoundsMaxY() == 1.0D && block.getBlockBoundsMaxZ() == 1.0D && block.getBlockBoundsMinX() == 0.0D && block.getBlockBoundsMinY() == 0.0D && block.getBlockBoundsMinZ() == 0.0D;
	}

	private int getMetaForColor(int blockColor)
	{
		int red = blockColor >> 16 & 255;
		int green = blockColor >> 8 & 255;
		int blue = blockColor & 255;
		int bestColor = 0;
		int bestDifference = 1024;

		for (int i = 0; i < 15; ++i)
		{
			int iColor = Blocks.wool.getMapColor(i).colorValue;
			int iRed = iColor >> 16 & 255;
			int iGreen = iColor >> 8 & 255;
			int iBlue = iColor & 255;
			int difference = Math.abs(red - iRed) + Math.abs(green - iGreen) + Math.abs(blue - iBlue);
			if (difference < bestDifference)
			{
				bestColor = i;
				bestDifference = difference;
			}
		}

		return bestColor;
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 8;
	}
}
