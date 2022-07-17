package twilightforest.entity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class EntityTFMoonwormShot extends EntityThrowable
{
	// TODO gamerforEA code start
	public final FakePlayerContainer fake = ModUtils.NEXUS_FACTORY.wrapFake(this);

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		this.fake.writeToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.fake.readFromNBT(nbt);
	}
	// TODO gamerforEA code end

	public EntityTFMoonwormShot(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	public EntityTFMoonwormShot(World par1World, EntityLivingBase par2EntityLiving)
	{
		super(par1World, par2EntityLiving);

		// TODO gamerforEA code start
		if (par2EntityLiving instanceof EntityPlayer)
			this.fake.setRealPlayer((EntityPlayer) par2EntityLiving);
		// TODO gamerforEA code end
	}

	public EntityTFMoonwormShot(World par1World)
	{
		super(par1World);
	}

	protected float func_40077_c()
	{
		return 0.5F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.makeTrail();
	}

	@Override
	public float getBrightness(float par1)
	{
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1)
	{
		return 15728880;
	}

	public void makeTrail()
	{
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
	protected float getGravityVelocity()
	{
		return 0.03F;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (mop.typeOfHit == MovingObjectType.BLOCK && !this.worldObj.isRemote)
			TFItems.moonwormQueen.onItemUse(null, (EntityPlayer) this.getThrower(), this.worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, 0.0F, 0.0F, 0.0F);

		// TODO gamerforEA add condition [2]
		if (mop.entityHit != null && !this.fake.cantDamage(mop.entityHit))
			mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);

		for (int var3 = 0; var3 < 8; ++var3)
		{
			this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(TFBlocks.moonworm) + "_0", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!this.worldObj.isRemote)
			this.setDead();

	}
}
