package twilightforest.item;

import com.gamerforea.eventhelper.util.EventUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class ItemTFPeacockFan extends ItemTF
{
	protected ItemTFPeacockFan()
	{
		this.setCreativeTab(TFItems.creativeTab);
		this.maxStackSize = 1;
		this.setMaxDamage(1024);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (!player.onGround)
				player.addPotionEffect(new PotionEffect(Potion.jump.id, 45, 0));
			else
			{
				boolean fanBox = false;
				int var7 = this.doFan(world, player);
				if (var7 > 0)
					par1ItemStack.damageItem(var7, player);
			}
		}
		else
		{
			if (!player.onGround && !player.isPotionActive(Potion.jump.id))
			{
				player.motionX *= 3.0D;
				player.motionY = 1.5D;
				player.motionZ *= 3.0D;
				player.fallDistance = 0.0F;
			}
			else
			{
				AxisAlignedBB var8 = this.getEffectAABB(world, player);
				Vec3 lookVec = player.getLookVec();

				for (int i = 0; i < 30; ++i)
				{
					world.spawnParticle("cloud", var8.minX + world.rand.nextFloat() * (var8.maxX - var8.minX), var8.minY + world.rand.nextFloat() * (var8.maxY - var8.minY), var8.minZ + world.rand.nextFloat() * (var8.maxZ - var8.minZ), lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
				}
			}

			world.playSound(player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, "random.breath", 1.0F + Item.itemRand.nextFloat(), Item.itemRand.nextFloat() * 0.7F + 0.3F, false);
		}

		player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.block;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 20;
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	private int doFan(World world, EntityPlayer player)
	{
		AxisAlignedBB fanBox = this.getEffectAABB(world, player);
		this.fanBlocksInAABB(world, player, fanBox);
		this.fanEntitiesInAABB(world, player, fanBox);
		return 1;
	}

	private void fanEntitiesInAABB(World world, EntityPlayer player, AxisAlignedBB fanBox)
	{
		Vec3 moveVec = player.getLookVec();
		List<Entity> inBox = world.getEntitiesWithinAABB(Entity.class, fanBox);
		float force = 2.0F;
		Iterator<Entity> iterator = inBox.iterator();

		while (true)
		{
			Entity entity;
			do
			{
				if (!iterator.hasNext())
					return;

				entity = iterator.next();
			}
			while (!entity.canBePushed() && !(entity instanceof EntityItem));

			// TODO gamerforEA code start
			if (EventUtils.cantDamage(player, entity))
				continue;
			// TODO gamerforEA code end

			entity.motionX = moveVec.xCoord * force;
			entity.motionY = moveVec.yCoord * force;
			entity.motionZ = moveVec.zCoord * force;
		}
	}

	private AxisAlignedBB getEffectAABB(World world, EntityPlayer player)
	{
		double range = 3.0D;
		double radius = 2.0D;
		Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 lookVec = player.getLookVec();
		Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
		return AxisAlignedBB.getBoundingBox(destVec.xCoord - radius, destVec.yCoord - radius, destVec.zCoord - radius, destVec.xCoord + radius, destVec.yCoord + radius, destVec.zCoord + radius);
	}

	private int fanBlocksInAABB(World world, EntityPlayer player, AxisAlignedBB par1AxisAlignedBB)
	{
		int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
		int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
		int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
		int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
		int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
		int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
		int fan = 0;

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dy = minY; dy <= maxY; ++dy)
			{
				for (int dz = minZ; dz <= maxZ; ++dz)
				{
					fan += this.fanBlock(world, player, dx, dy, dz);
				}
			}
		}

		return fan;
	}

	private int fanBlock(World world, EntityPlayer player, int dx, int dy, int dz)
	{
		byte cost = 0;
		Block currentID = world.getBlock(dx, dy, dz);
		if (currentID != Blocks.air)
		{
			int currentMeta = world.getBlockMetadata(dx, dy, dz);
			if (currentID instanceof BlockFlower && currentID.canHarvestBlock(player, currentMeta) && Item.itemRand.nextInt(3) == 0)
			{
				// TODO gamerforEA code start
				if (EventUtils.cantBreak(player, dx, dy, dz))
					return cost;
				// TODO gamerforEA code end

				currentID.harvestBlock(world, player, dx, dy, dz, currentMeta);
				world.setBlock(dx, dy, dz, Blocks.air, 0, 3);
				world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
			}
		}

		return cost;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("TwilightForest:" + this.getUnlocalizedName().substring(5));
	}
}
