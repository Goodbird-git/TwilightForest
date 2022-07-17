package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityTFLichBolt extends EntityThrowable
{
	public EntityLivingBase playerReflects = null;

	public EntityTFLichBolt(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFLichBolt(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
	}

	public EntityTFLichBolt(World par1World)
	{
		super(par1World);
	}

	@Override
	protected float func_70182_d()
	{
		return 0.5F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.makeTrail();
	}

	public void makeTrail()
	{
		for (int i = 0; i < 5; ++i)
		{
			double dx = this.posX + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dy = this.posY + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dz = this.posZ + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double s1 = (double) ((this.rand.nextFloat() * 0.5F + 0.5F) * 0.17F);
			double s2 = (double) ((this.rand.nextFloat() * 0.5F + 0.5F) * 0.8F);
			double s3 = (double) ((this.rand.nextFloat() * 0.5F + 0.5F) * 0.69F);
			this.worldObj.spawnParticle("mobSpell", dx, dy, dz, s1, s2, s3);
		}
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public float getCollisionBorderSize()
	{
		return 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i)
	{
		this.setBeenAttacked();
		if (damagesource.getEntity() != null)
		{
			Vec3 vec3d = damagesource.getEntity().getLookVec();
			if (vec3d != null)
				this.setThrowableHeading(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.5F, 0.1F);

			if (damagesource.getEntity() instanceof EntityLivingBase)
				this.playerReflects = (EntityLivingBase) damagesource.getEntity();

			return true;
		}
		return false;
	}

	@Override
	public EntityLivingBase getThrower()
	{
		return this.playerReflects != null ? this.playerReflects : super.getThrower();
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.001F;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		boolean passThrough = false;
		if (mop.entityHit instanceof EntityTFLichBolt || mop.entityHit instanceof EntityTFLichBomb)
			passThrough = true;

		if (mop.entityHit instanceof EntityLivingBase)
		{
			if (mop.entityHit instanceof EntityTFLich)
			{
				EntityTFLich lich = (EntityTFLich) mop.entityHit;
				if (lich.isShadowClone())
					passThrough = true;
			}

			if (!passThrough)
				// TODO gamerforEA code replace, old code:
				// mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), 6.0F);
				mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), BalanceConfig.lichBoltDamage);
			// TODO gamerforEA code end
		}

		if (!passThrough)
		{
			for (int i = 0; i < 8; ++i)
			{
				this.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(Items.ender_pearl), this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, this.rand.nextDouble() * 0.2D, this.rand.nextGaussian() * 0.05D);
			}

			if (!this.worldObj.isRemote)
				this.setDead();
		}
	}
}
