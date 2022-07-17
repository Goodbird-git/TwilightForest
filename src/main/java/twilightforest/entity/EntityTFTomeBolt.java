package twilightforest.entity;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityTFTomeBolt extends EntityThrowable
{
	public EntityTFTomeBolt(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFTomeBolt(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
	}

	public EntityTFTomeBolt(World par1World)
	{
		super(par1World);
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
		return 0.003F;
	}

	public void makeTrail()
	{
		for (int i = 0; i < 5; ++i)
		{
			double dx = this.posX + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dy = this.posY + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			double dz = this.posZ + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
			this.worldObj.spawnParticle("crit", dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		// TODO gamerforEA code replace, old code:
		// if (mop.entityHit instanceof EntityLivingBase && mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 6.0F))
		if (mop.entityHit instanceof EntityLivingBase && mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), BalanceConfig.tomeBoltDamage))
		// TODO gamerforEA code end
		{
			byte potionStrength = (byte) (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL ? 3 : this.worldObj.difficultySetting == EnumDifficulty.NORMAL ? 7 : 9);
			if (potionStrength > 0)
				((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, potionStrength * 20, 1));
		}

		for (int i = 0; i < 8; ++i)
		{
			this.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(Items.fire_charge), this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, this.rand.nextDouble() * 0.2D, this.rand.nextGaussian() * 0.05D);
		}

		if (!this.worldObj.isRemote)
			this.setDead();

	}
}
