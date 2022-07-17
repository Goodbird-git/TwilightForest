package twilightforest.entity;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityTFNatureBolt extends EntityThrowable
{
	private EntityPlayer playerTarget;

	public EntityTFNatureBolt(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFNatureBolt(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);
	}

	public EntityTFNatureBolt(World par1World)
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
			this.worldObj.spawnParticle("happyVillager", dx, dy, dz, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		EntityLivingBase thrower = this.getThrower();

		if (mop.entityHit instanceof EntityLivingBase)
		{
			// TODO gamerforEA code start
			if (thrower instanceof EntityPlayer && !EventUtils.cantDamage(thrower, mop.entityHit))
			{
				this.setDead();
				return;
			}
			// TODO gamerforEA code end

			// TODO gamerforEA code replace, old code:
			// if (mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, thrower), 2.0F))
			if (mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, thrower), BalanceConfig.natureBoltDamage))
			// TODO gamerforEA code end
			{
				byte dx = (byte) (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL ? 0 : this.worldObj.difficultySetting == EnumDifficulty.NORMAL ? 3 : 7);
				if (dx > 0)
					((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(Potion.poison.id, dx * 20, 0));
			}
		}

		for (int i = 0; i < 8; ++i)
		{
			this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(Blocks.leaves) + "_0", this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, this.rand.nextDouble() * 0.2D, this.rand.nextGaussian() * 0.05D);
		}

		if (!this.worldObj.isRemote)
		{
			this.setDead();
			int x = MathHelper.floor_double(mop.blockX);
			int y = MathHelper.floor_double(mop.blockY);
			int z = MathHelper.floor_double(mop.blockZ);
			Material materialHit = this.worldObj.getBlock(x, y, z).getMaterial();
			if (materialHit == Material.grass && this.playerTarget != null)
				Items.dye.onItemUse(new ItemStack(Items.dye, 1, 15), this.playerTarget, this.worldObj, x, y, z, 0, 0.0F, 0.0F, 0.0F);
			else if (materialHit.isSolid() && this.canReplaceBlock(this.worldObj, x, y, z))
			{
				// TODO gamerforEA code start
				if (thrower instanceof EntityPlayer)
				{
					if (EventUtils.cantBreak((EntityPlayer) thrower, x, y, z))
						return;
				}
				else if (!ModUtils.isTFWorld(this.worldObj))
					return;
				// TODO gamerforEA code end

				this.worldObj.setBlock(x, y, z, Blocks.leaves, 2, 3);
			}
		}
	}

	private boolean canReplaceBlock(World worldObj, int dx, int dy, int dz)
	{
		Block blockID = worldObj.getBlock(dx, dy, dz);
		float hardness = blockID == null ? -1.0F : blockID.getBlockHardness(worldObj, dx, dy, dz);
		return hardness >= 0.0F && hardness < 50.0F;
	}

	public void setTarget(EntityLivingBase attackTarget)
	{
		if (attackTarget instanceof EntityPlayer)
			this.playerTarget = (EntityPlayer) attackTarget;
	}
}
