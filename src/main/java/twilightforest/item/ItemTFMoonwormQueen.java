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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMoonwormShot;

public class ItemTFMoonwormQueen extends ItemTF
{
	private static final int FIRING_TIME = 12;
	private IIcon[] icons;
	private String[] iconNames = { "moonwormQueen", "moonwormQueenAlt" };

	protected ItemTFMoonwormQueen()
	{
		this.setCreativeTab(TFItems.creativeTab);
		this.maxStackSize = 1;
		this.setMaxDamage(256);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		if (par1ItemStack.getItemDamage() < this.getMaxDamage())
			player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		else
			player.stopUsingItem();

		return par1ItemStack;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		Block currentBlockID = world.getBlock(x, y, z);
		if (currentBlockID == TFBlocks.moonworm)
			return false;
		else if (par1ItemStack != null && par1ItemStack.getItemDamage() == this.getMaxDamage())
			return false;
		else
		{
			if (currentBlockID == Blocks.snow)
				side = 1;
			else if (currentBlockID != Blocks.vine && currentBlockID != Blocks.tallgrass && currentBlockID != Blocks.deadbush && (currentBlockID == Blocks.air || !currentBlockID.isReplaceable(world, x, y, z)))
			{
				if (side == 0)
					--y;

				if (side == 1)
					++y;

				if (side == 2)
					--z;

				if (side == 3)
					++z;

				if (side == 4)
					--x;

				if (side == 5)
					++x;
			}

			if (world.canPlaceEntityOnSide(TFBlocks.moonworm, x, y, z, false, side, player, par1ItemStack))
			{
				// TODO gamerforEA code start
				if (EventUtils.cantBreak(player, x, y, z))
					return false;
				// TODO gamerforEA code end

				int placementMeta = TFBlocks.moonworm.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 0);
				if (world.setBlock(x, y, z, TFBlocks.moonworm, placementMeta, 3))
				{
					if (world.getBlock(x, y, z) == TFBlocks.moonworm)
						TFBlocks.moonworm.onBlockPlacedBy(world, x, y, z, player, par1ItemStack);

					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.getSound(), TFBlocks.moonworm.stepSound.getVolume() / 2.0F, TFBlocks.moonworm.stepSound.getPitch() * 0.8F);
					if (par1ItemStack != null)
					{
						par1ItemStack.damageItem(1, player);
						player.stopUsingItem();
					}
				}

				return true;
			}
			else
				return false;
		}
	}

	public String getSound()
	{
		return "mob.slime.big";
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World world, EntityPlayer player, int useRemaining)
	{
		int useTime = this.getMaxItemUseDuration(par1ItemStack) - useRemaining;
		if (!world.isRemote && useTime > 12 && par1ItemStack.getItemDamage() + 1 < this.getMaxDamage())
		{
			boolean fired = world.spawnEntityInWorld(new EntityTFMoonwormShot(world, player));
			if (fired)
			{
				par1ItemStack.damageItem(2, player);
				world.playSoundAtEntity(player, this.getSound(), 1.0F, 1.0F);
			}
		}
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (usingItem != null && usingItem.getItem() == this)
		{
			int useTime = usingItem.getMaxItemUseDuration() - useRemaining;
			if (useTime >= 12)
				return (useTime >> 1) % 2 == 0 ? this.icons[0] : this.icons[1];
		}

		return this.icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		super.registerIcons(par1IconRegister);
		this.icons = new IIcon[this.iconNames.length];

		for (int i = 0; i < this.iconNames.length; ++i)
		{
			this.icons[i] = par1IconRegister.registerIcon("TwilightForest:" + this.iconNames[i]);
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
}
