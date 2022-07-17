package twilightforest.entity;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.TFAchievementPage;
import twilightforest.entity.ai.EntityAITFBreathAttack;

public class EntityTFFireBeetle extends EntityMob implements IBreathAttacker
{
	public static final int BREATH_DURATION = 10;
	public static final int BREATH_DAMAGE = 2;

	public EntityTFFireBeetle(World world)
	{
		super(world);
		this.setSize(1.1F, 0.75F);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAITFBreathAttack(this, 1.0F, 5.0F, 30, 0.1F));
		this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	public EntityTFFireBeetle(World world, double x, double y, double z)
	{
		this(world);
		this.setPosition(x, y, z);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(17, (byte) 0);
	}

	@Override
	protected boolean isAIEnabled()
	{
		return true;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
	}

	@Override
	protected String getLivingSound()
	{
		return null;
	}

	@Override
	protected String getHurtSound()
	{
		return "mob.spider.say";
	}

	@Override
	protected String getDeathSound()
	{
		return "mob.spider.death";
	}

	@Override
	protected void func_145780_a(int var1, int var2, int var3, Block var4)
	{
		this.worldObj.playSoundAtEntity(this, "mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getDropItem()
	{
		return Items.gunpowder;
	}

	@Override
	public boolean isBreathing()
	{
		return this.dataWatcher.getWatchableObjectByte(17) != 0;
	}

	@Override
	public void setBreathing(boolean flag)
	{
		if (flag)
			this.dataWatcher.updateObject(17, (byte) 127);
		else
			this.dataWatcher.updateObject(17, (byte) 0);

	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if (this.isBreathing())
		{
			Vec3 look = this.getLookVec();
			double dist = 0.9D;
			double px = this.posX + look.xCoord * dist;
			double py = this.posY + 0.25D + look.yCoord * dist;
			double pz = this.posZ + look.zCoord * dist;

			for (int i = 0; i < 2; ++i)
			{
				double dx = look.xCoord;
				double dy = look.yCoord;
				double dz = look.zCoord;
				double spread = 5.0D + this.getRNG().nextDouble() * 2.5D;
				double velocity = 0.15D + this.getRNG().nextDouble() * 0.15D;
				dx = dx + this.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dy = dy + this.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dz = dz + this.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dx = dx * velocity;
				dy = dy * velocity;
				dz = dz * velocity;
				this.worldObj.spawnParticle(this.getFlameParticle(), px, py, pz, dx, dy, dz);
			}

			this.playBreathSound();
		}

	}

	public String getFlameParticle()
	{
		return "flame";
	}

	public void playBreathSound()
	{
		this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "mob.ghast.fireball", this.rand.nextFloat() * 0.5F, this.rand.nextFloat() * 0.5F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1)
	{
		return this.isBreathing() ? 15728880 : super.getBrightnessForRender(par1);
	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer)
			((EntityPlayer) par1DamageSource.getSourceOfDamage()).triggerAchievement(TFAchievementPage.twilightHunter);
	}

	@Override
	public int getVerticalFaceSpeed()
	{
		return 500;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 1.1F;
	}

	@Override
	public float getEyeHeight()
	{
		return 0.25F;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	public void doBreathAttack(Entity target)
	{
		// TODO gamerforEA code replace, old code:
		// if (!target.isImmuneToFire() && target.attackEntityFrom(DamageSource.inFire, 2.0F))
		if (!target.isImmuneToFire() && target.attackEntityFrom(DamageSource.inFire, BalanceConfig.fireBeetleBreathDamage))
			// TODO gamerforEA code end
			target.setFire(10);

	}
}
