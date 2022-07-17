package twilightforest.entity.boss;

import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFUrGhastFireball extends EntityLargeFireball
{
	public EntityTFUrGhastFireball(World worldObj, EntityTFUrGhast entityTFTowerBoss, double x, double y, double z)
	{
		super(worldObj, entityTFTowerBoss, x, y, z);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (!this.worldObj.isRemote && !(mop.entityHit instanceof EntityFireball))
		{
			if (mop.entityHit != null)
				// TODO gamerforEA code replace, old code:
				// mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 16.0F);
				mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), BalanceConfig.urGhastFireballDamage);
			// TODO gamerforEA code end

			boolean canMobGrief = ModUtils.canMobGrief(this.worldObj);

			// TODO gamerforEA code start
			canMobGrief &= ModUtils.isTFWorld(this.worldObj);
			// TODO gamerforEA code end

			this.worldObj.newExplosion(null, this.posX, this.posY, this.posZ, (float) this.field_92057_e, true, canMobGrief);
			this.setDead();
		}

	}
}
