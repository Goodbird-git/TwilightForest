package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFLichBomb extends EntityThrowable
{
	public EntityTFLichBomb(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFLichBomb(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
	}

	public EntityTFLichBomb(World par1World)
	{
		super(par1World);
	}

	protected float func_40077_c()
	{
		return 0.35F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.makeTrail();
	}

	public void makeTrail()
	{
		for (int i = 0; i < 1; ++i)
		{
			double sx = 0.5D * (this.rand.nextDouble() - this.rand.nextDouble()) + this.motionX;
			double sy = 0.5D * (this.rand.nextDouble() - this.rand.nextDouble()) + this.motionY;
			double sz = 0.5D * (this.rand.nextDouble() - this.rand.nextDouble()) + this.motionZ;
			double dx = this.posX + sx;
			double dy = this.posY + sy;
			double dz = this.posZ + sz;
			this.worldObj.spawnParticle("flame", dx, dy, dz, sx * -0.25D, sy * -0.25D, sz * -0.25D);
		}

	}

	@Override
	public boolean isBurning()
	{
		return true;
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
			this.explode();
			return true;
		}
		return false;
	}

	protected void explode()
	{
		// TODO gamerforEA code replace, old code:
		// float explosionPower = 2.0F;
		float explosionPower = BalanceConfig.lichBombExplosionPower;
		// TODO gamerforEA code end

		this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, explosionPower, false, false);
		this.setDead();
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.001F;
	}

	@Override
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
	{
		boolean passThrough = false;
		if (par1MovingObjectPosition.entityHit instanceof EntityTFLichBolt || par1MovingObjectPosition.entityHit instanceof EntityTFLichBomb)
			passThrough = true;

		if (par1MovingObjectPosition.entityHit instanceof EntityTFLich)
			passThrough = true;

		if (!passThrough)
			this.explode();

	}
}
