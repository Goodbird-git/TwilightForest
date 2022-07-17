package twilightforest.entity.boss;

import com.gamerforea.eventhelper.util.ExplosionByPlayer;
import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityTFHydraMortar extends EntityThrowable
{
	private static final int BURN_FACTOR = 5;
	private static final int DIRECT_DAMAGE = 18;
	public EntityLivingBase playerReflects = null;
	public int fuse = 80;
	public boolean megaBlast = false;

	public EntityTFHydraMortar(World par1World)
	{
		super(par1World);
		this.setSize(0.75F, 0.75F);
	}

	public EntityTFHydraMortar(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
		this.setSize(0.75F, 0.75F);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		if (this.onGround)
		{
			if (!this.worldObj.isRemote)
			{
				this.motionX *= 0.9D;
				this.motionY *= 0.9D;
				this.motionZ *= 0.9D;
			}

			if (this.fuse-- <= 0)
				this.detonate();
		}

	}

	public void setToBlasting()
	{
		this.megaBlast = true;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (mop.entityHit == null && !this.megaBlast)
		{
			this.motionY = 0.0D;
			this.onGround = true;
		}
		else
			this.detonate();

	}

	@Override
	public float func_145772_a(Explosion par1Explosion, World par2World, int par3, int par4, int par5, Block par6Block)
	{
		float var6 = super.func_145772_a(par1Explosion, par2World, par3, par4, par5, par6Block);
		if (this.megaBlast && par6Block != Blocks.bedrock && par6Block != Blocks.end_portal && par6Block != Blocks.end_portal_frame)
			var6 = Math.min(0.8F, var6);

		return var6;
	}

	protected void detonate()
	{
		float explosionPower = this.megaBlast ? 4.0F : 0.1F;

		// TODO gamerforEA code clear:
		// this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, explosionPower, true, true);

		if (!this.worldObj.isRemote)
			for (Entity nearby : new ArrayList<Entity>(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 1.0D, 1.0D))))
			{
				// TODO gamerforEA code replace, old code:
				// if (nearby.attackEntityFrom(DamageSource.causeFireballDamage(null, this.getThrower()), 18.0F) && !nearby.isImmuneToFire())
				Entity thrower = this.getThrower();
				if (thrower == null)
					thrower = this;
				if (nearby.attackEntityFrom(DamageSource.causeFireballDamage(null, thrower), BalanceConfig.hydraMortarDamage) && !nearby.isImmuneToFire())
					// TODO gamerforEA code end
					nearby.setFire(5);
			}

		// TODO gamerforEA code start
		if (ModUtils.isTFWorld(this))
			ExplosionByPlayer.newExplosion(ModUtils.NEXUS_FACTORY.getProfile(), null, this.worldObj, this, this.posX, this.posY, this.posZ, explosionPower, true, ModUtils.canMobGrief(this.worldObj));
		// TODO gamerforEA code end

		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i)
	{
		this.setBeenAttacked();
		if (damagesource.getEntity() != null && !this.worldObj.isRemote)
		{
			Vec3 vec3d = damagesource.getEntity().getLookVec();
			if (vec3d != null)
			{
				this.setThrowableHeading(vec3d.xCoord, vec3d.yCoord + 1.0D, vec3d.zCoord, 1.5F, 0.1F);
				this.onGround = false;
				this.fuse += 20;
			}

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
		return 1.5F;
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.05F;
	}

	@Override
	protected float func_70182_d()
	{
		return 0.75F;
	}

	@Override
	protected float func_70183_g()
	{
		return -20.0F;
	}
}
