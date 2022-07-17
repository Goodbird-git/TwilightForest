package twilightforest.item;

import com.gamerforea.eventhelper.util.EventUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;

public class ItemTFCrumbleHorn extends ItemTF
{
	private static final int CHANCE_HARVEST = 20;
	private static final int CHANCE_CRUMBLE = 5;

	protected ItemTFCrumbleHorn()
	{
		this.setCreativeTab(TFItems.creativeTab);
		this.maxStackSize = 1;
		this.setMaxDamage(1024);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		world.playSoundAtEntity(player, "mob.sheep.say", 1.0F, 0.8F);
		return par1ItemStack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		if (count > 10 && count % 5 == 0 && !player.worldObj.isRemote)
		{
			int crumbled = this.doCrumble(player.worldObj, player);
			if (crumbled > 0)
				stack.damageItem(crumbled, player);

			player.worldObj.playSoundAtEntity(player, "mob.sheep.say", 1.0F, 0.8F);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	private int doCrumble(World world, EntityPlayer player)
	{
		double range = 3.0D;
		double radius = 2.0D;
		Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 lookVec = player.getLookVec();
		Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
		AxisAlignedBB crumbleBox = AxisAlignedBB.getBoundingBox(destVec.xCoord - radius, destVec.yCoord - radius, destVec.zCoord - radius, destVec.xCoord + radius, destVec.yCoord + radius, destVec.zCoord + radius);
		return this.crumbleBlocksInAABB(world, player, crumbleBox);
	}

	private int crumbleBlocksInAABB(World world, EntityPlayer player, AxisAlignedBB par1AxisAlignedBB)
	{
		int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
		int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
		int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
		int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
		int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
		int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
		int crumbled = 0;

		for (int dx = minX; dx <= maxX; ++dx)
		{
			for (int dy = minY; dy <= maxY; ++dy)
			{
				for (int dz = minZ; dz <= maxZ; ++dz)
				{
					crumbled += this.crumbleBlock(world, player, dx, dy, dz);
				}
			}
		}

		return crumbled;
	}

	private int crumbleBlock(World world, EntityPlayer player, int x, int y, int z)
	{
		int cost = 0;
		Block block = world.getBlock(x, y, z);
		if (block != Blocks.air)
		{
			// TODO gamerforEA code start
			if (EventUtils.cantBreak(player, x, y, z))
				return 0;
			// TODO gamerforEA code end

			int meta = world.getBlockMetadata(x, y, z);
			if (block == Blocks.stone && world.rand.nextInt(5) == 0)
			{
				world.setBlock(x, y, z, Blocks.cobblestone, 0, 3);
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
				++cost;
			}

			if (block == Blocks.stonebrick && meta == 0 && world.rand.nextInt(5) == 0)
			{
				world.setBlock(x, y, z, Blocks.stonebrick, 2, 3);
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
				++cost;
			}

			if (block == TFBlocks.mazestone && meta == 1 && world.rand.nextInt(5) == 0)
			{
				world.setBlock(x, y, z, TFBlocks.mazestone, 4, 3);
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
				++cost;
			}

			if (block == Blocks.cobblestone && world.rand.nextInt(5) == 0)
			{
				world.setBlock(x, y, z, Blocks.gravel, 0, 3);
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
				++cost;
			}

			if ((block == Blocks.gravel || block == Blocks.dirt) && block.canHarvestBlock(player, meta) && world.rand.nextInt(20) == 0)
			{
				world.setBlock(x, y, z, Blocks.air, 0, 3);
				block.harvestBlock(world, player, x, y, z, meta);
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
				++cost;
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
