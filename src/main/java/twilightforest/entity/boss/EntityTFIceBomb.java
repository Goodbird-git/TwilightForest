package twilightforest.entity.boss;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.EventConfig;
import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import twilightforest.TwilightForestMod;
import twilightforest.entity.EntityTFYeti;

import java.util.List;

public class EntityTFIceBomb extends EntityThrowable
{
	private int zoneTimer = 80;
	private boolean hasHit;

	// TODO gamerforEA code start
	public final FakePlayerContainer fake = ModUtils.NEXUS_FACTORY.wrapFake(this);
	private boolean isYetiAplha;
	// TODO gamerforEA code end

	public EntityTFIceBomb(World par1World)
	{
		super(par1World);
	}

	public EntityTFIceBomb(World par1World, EntityLivingBase thrower)
	{
		super(par1World, thrower);

		// TODO gamerforEA code start
		if (thrower instanceof EntityPlayer)
			this.fake.setRealPlayer((EntityPlayer) thrower);
		else if (thrower instanceof EntityTFYetiAlpha)
			this.isYetiAplha = true;
		// TODO gamerforEA code end
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		// TODO gamerforEA code start
		if (this.isDead)
			return;
		// TODO gamerforEA code end

		if (this.getThrower() != null && this.getThrower() instanceof EntityTFYetiAlpha)
		{
			double dist = this.getDistanceSqToEntity(this.getThrower());
			if (dist <= 100.0D)
				this.setDead();
		}

		this.motionY = 0.0D;
		this.hasHit = true;
		if (!this.worldObj.isRemote)
			this.doTerrainEffects();

		// TODO gamerforEA code start
		this.setDead();
		// TODO gamerforEA code end
	}

	private void doTerrainEffects()
	{
		// TODO gamerforEA code start
		if (this.isYetiAplha && (!EventConfig.enableYetiAlphaGrief || !ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj)))
			return;
		// TODO gamerforEA code end

		int ix = MathHelper.floor_double(this.lastTickPosX);
		int iy = MathHelper.floor_double(this.lastTickPosY);
		int iz = MathHelper.floor_double(this.lastTickPosZ);

		for (int x = -3; x <= 3; ++x)
		{
			for (int y = -3; y <= 3; ++y)
			{
				for (int z = -3; z <= 3; ++z)
				{
					this.doTerrainEffect(ix + x, iy + y, iz + z);
				}
			}
		}
	}

	private void doTerrainEffect(int x, int y, int z)
	{
		// TODO gamerforEA code start
		if (!ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj))
			return;
		// TODO gamerforEA code end

		Block block = null;
		if (this.worldObj.getBlock(x, y, z).getMaterial() == Material.water)
			block = Blocks.ice;
		else if (this.worldObj.getBlock(x, y, z).getMaterial() == Material.lava)
			block = Blocks.obsidian;
		else if (this.worldObj.isAirBlock(x, y, z) && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, x, y, z))
			block = Blocks.snow_layer;

		if (block != null)
		{
			// TODO gamerforEA code start
			if (this.fake.cantBreak(x, y, z))
				return;
			// TODO gamerforEA code end

			this.worldObj.setBlock(x, y, z, block);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.hasHit)
		{
			if (!this.worldObj.isRemote)
			{
				this.motionX *= 0.1D;
				this.motionY *= 0.1D;
				this.motionZ *= 0.1D;
			}

			--this.zoneTimer;
			this.makeIceZone();
			if (this.zoneTimer <= 0)
				this.detonate();
		}
		else
			this.makeTrail();

	}

	public void makeTrail()
	{
		for (int i = 0; i < 10; ++i)
		{
			double dx = this.posX + 0.75F * (this.rand.nextFloat() - 0.5F);
			double dy = this.posY + 0.75F * (this.rand.nextFloat() - 0.5F);
			double dz = this.posZ + 0.75F * (this.rand.nextFloat() - 0.5F);
			TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}

	}

	private void makeIceZone()
	{
		if (this.worldObj.isRemote)
			for (int i = 0; i < 20; ++i)
			{
				double dx = this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 3.0F;
				double dy = this.posY + (this.rand.nextFloat() - this.rand.nextFloat()) * 3.0F;
				double dz = this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 3.0F;
				TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", dx, dy, dz, 0.0D, 0.0D, 0.0D);
			}
		else if (this.zoneTimer % 10 == 0)
			this.hitNearbyEntities();
	}

	private void hitNearbyEntities()
	{
		List<Entity> nearby = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(3.0D, 2.0D, 3.0D));
		for (Entity entity : nearby)
		{
			if (entity instanceof EntityLivingBase && entity != this.getThrower())
				if (entity instanceof EntityTFYeti)
				{
					entity.setDead();
					int x = MathHelper.floor_double(entity.lastTickPosX);
					int y = MathHelper.floor_double(entity.lastTickPosY);
					int z = MathHelper.floor_double(entity.lastTickPosZ);

					// TODO gamerforEA code start
					if (!ModUtils.isTFWorld(this) || !ModUtils.canMobGrief(this.worldObj))
						return;
					if (this.fake.cantBreak(x, y, z) || this.fake.cantBreak(x, y + 1, z))
						return;
					// TODO gamerforEA code end

					this.worldObj.setBlock(x, y, z, Blocks.ice);
					this.worldObj.setBlock(x, y + 1, z, Blocks.ice);
				}
				else
				{
					// TODO gamerforEA code start
					if (this.fake.cantDamage(entity))
						return;
					// TODO gamerforEA code end

					entity.attackEntityFrom(DamageSource.magic, 1.0F);
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2, true));
				}
		}
	}

	private void detonate()
	{
		this.setDead();
	}

	public Block getBlock()
	{
		return Blocks.packed_ice;
	}

	@Override
	protected float func_70182_d()
	{
		return 0.75F;
	}

	@Override
	protected float getGravityVelocity()
	{
		return this.hasHit ? 0.0F : 0.025F;
	}

	@Override
	protected float func_70183_g()
	{
		return -20.0F;
	}
}
