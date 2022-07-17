package com.gamerforea.twilightforest.entity;

import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityCustomFireball extends EntityLargeFireball
{
	private float damage = 6;

	public EntityCustomFireball(World world)
	{
		super(world);
	}

	@SideOnly(Side.CLIENT)
	public EntityCustomFireball(World world, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ)
	{
		super(world, x, y, z, accelerationX, accelerationY, accelerationZ);
	}

	public EntityCustomFireball(World world, EntityLivingBase shooter, double x, double y, double z)
	{
		super(world, shooter, x, y, z);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (!this.worldObj.isRemote)
		{
			if (mop.entityHit != null)
				mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), this.getDamage());

			this.worldObj.newExplosion(null, this.posX, this.posY, this.posZ, (float) this.field_92057_e, true, ModUtils.canMobGrief(this.worldObj) && ModUtils.isTFWorld(this));
			this.setDead();
		}
	}

	public float getDamage()
	{
		return this.damage;
	}

	public void setDamage(float damage)
	{
		this.damage = damage;
	}
}
