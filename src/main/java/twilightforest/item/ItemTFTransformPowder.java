package twilightforest.item;

import com.gamerforea.eventhelper.util.EventUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.entity.*;
import twilightforest.entity.passive.*;

import java.util.HashMap;

public class ItemTFTransformPowder extends ItemTF
{
	HashMap<Class<? extends EntityLivingBase>, Class<? extends EntityLivingBase>> transformMap;

	protected ItemTFTransformPowder()
	{
		this.maxStackSize = 64;
		this.setCreativeTab(TFItems.creativeTab);
		this.transformMap = new HashMap();
		this.addTwoWayTransformation(EntityTFMinotaur.class, EntityPigZombie.class);
		this.addTwoWayTransformation(EntityTFDeer.class, EntityCow.class);
		this.addTwoWayTransformation(EntityTFBighorn.class, EntitySheep.class);
		this.addTwoWayTransformation(EntityTFBoar.class, EntityPig.class);
		this.addTwoWayTransformation(EntityTFRaven.class, EntityBat.class);
		this.addTwoWayTransformation(EntityTFHostileWolf.class, EntityWolf.class);
		this.addTwoWayTransformation(EntityTFPenguin.class, EntityChicken.class);
		this.addTwoWayTransformation(EntityTFHedgeSpider.class, EntitySpider.class);
		this.addTwoWayTransformation(EntityTFSwarmSpider.class, EntityCaveSpider.class);
		this.addTwoWayTransformation(EntityTFWraith.class, EntityBlaze.class);
		this.addTwoWayTransformation(EntityTFRedcap.class, EntityVillager.class);
		this.addTwoWayTransformation(EntityTFSkeletonDruid.class, EntityWitch.class);
	}

	public void addTwoWayTransformation(Class<? extends EntityLivingBase> class1, Class<? extends EntityLivingBase> class2)
	{
		this.transformMap.put(class1, class2);
		this.transformMap.put(class2, class1);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, EntityLivingBase target)
	{
		Class transformClass = this.getMonsterTransform(target.getClass());
		if (transformClass != null)
		{
			// TODO gamerforEA code start
			if (EventUtils.cantDamage(par2EntityPlayer, target))
				return false;
			// TODO gamerforEA code end

			if (target.worldObj.isRemote)
			{
				if (target instanceof EntityLiving)
				{
					((EntityLiving) target).spawnExplosionParticle();
					((EntityLiving) target).spawnExplosionParticle();
				}

				target.worldObj.playSound(target.posX + 0.5D, target.posY + 0.5D, target.posZ + 0.5D, "mob.zombie.remedy", 1.0F + Item.itemRand.nextFloat(), Item.itemRand.nextFloat() * 0.7F + 0.3F, false);
			}
			else
			{
				EntityLivingBase newMonster = null;

				try
				{
					newMonster = (EntityLivingBase) transformClass.getConstructor(World.class).newInstance(new Object[] { target.worldObj });
				}
				catch (Exception var7)
				{
					var7.printStackTrace();
				}

				if (newMonster == null)
					return false;

				newMonster.setPositionAndRotation(target.posX, target.posY, target.posZ, target.rotationYaw, target.rotationPitch);
				target.worldObj.spawnEntityInWorld(newMonster);
				target.setDead();
			}

			--par1ItemStack.stackSize;
			return true;
		}
		else
			return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			AxisAlignedBB fanBox = this.getEffectAABB(world, player);

			for (int i = 0; i < 30; ++i)
			{
				world.spawnParticle("magicCrit", fanBox.minX + world.rand.nextFloat() * (fanBox.maxX - fanBox.minX), fanBox.minY + world.rand.nextFloat() * (fanBox.maxY - fanBox.minY), fanBox.minZ + world.rand.nextFloat() * (fanBox.maxZ - fanBox.minZ), 0.0D, 0.0D, 0.0D);
			}
		}

		return par1ItemStack;
	}

	private AxisAlignedBB getEffectAABB(World world, EntityPlayer player)
	{
		double range = 2.0D;
		double radius = 1.0D;
		Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 lookVec = player.getLookVec();
		Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
		return AxisAlignedBB.getBoundingBox(destVec.xCoord - radius, destVec.yCoord - radius, destVec.zCoord - radius, destVec.xCoord + radius, destVec.yCoord + radius, destVec.zCoord + radius);
	}

	public Class<? extends EntityLivingBase> getMonsterTransform(Class<? extends EntityLivingBase> originalMonster)
	{
		return this.transformMap.get(originalMonster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("TwilightForest:" + this.getUnlocalizedName().substring(5));
	}
}
