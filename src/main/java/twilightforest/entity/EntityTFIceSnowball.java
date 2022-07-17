package twilightforest.entity;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFIceSnowball extends EntityThrowable
{
	private static final int DAMAGE = 8;

	public EntityTFIceSnowball(World par1World)
	{
		super(par1World);
	}

	public EntityTFIceSnowball(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.makeTrail();
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.006F;
	}

	public void makeTrail()
	{
		for (int i = 0; i < 2; ++i)
		{
			double dx = this.posX + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dy = this.posY + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dz = this.posZ + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			this.worldObj.spawnParticle("snowballpoof", dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i)
	{
		this.setBeenAttacked();
		this.pop();
		return true;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (mop.entityHit instanceof EntityLivingBase)
			// TODO gamerforEA code replace, old code:
			// mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 8.0F);
			mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), BalanceConfig.iceSnowballDamage);
		// TODO gamerforEA code end

		this.pop();
	}

	protected void pop()
	{
		for (int i = 0; i < 8; ++i)
		{
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, this.rand.nextDouble() * 0.2D, this.rand.nextGaussian() * 0.05D);
		}

		if (!this.worldObj.isRemote)
			this.setDead();

	}
}
