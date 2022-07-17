package twilightforest.item;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenerator;

import java.util.ArrayList;
import java.util.Iterator;

public class ItemTFOreMagnet extends ItemTF
{
	private static final float WIGGLE = 10.0F;
	private IIcon[] icons;
	private String[] iconNames = { "oreMagnet", "oreMagnet1", "oreMagnet2" };

	protected ItemTFOreMagnet()
	{
		this.setCreativeTab(TFItems.creativeTab);
		this.maxStackSize = 1;
		this.setMaxDamage(12);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
	{
		player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World world, EntityPlayer player, int useRemaining)
	{
		int useTime = this.getMaxItemUseDuration(par1ItemStack) - useRemaining;
		if (!world.isRemote && useTime > 10)
		{
			int moved = this.doMagnet(world, player, 0.0F, 0.0F);
			if (moved == 0)
				moved = this.doMagnet(world, player, 10.0F, 0.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, 10.0F, 10.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, 0.0F, 10.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, -10.0F, 10.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, -10.0F, 0.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, -10.0F, -10.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, 0.0F, -10.0F);

			if (moved == 0)
				moved = this.doMagnet(world, player, 10.0F, -10.0F);

			if (moved > 0)
			{
				par1ItemStack.damageItem(moved, player);
				if (par1ItemStack.stackSize == 0)
					player.destroyCurrentEquippedItem();

				world.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
			}
		}

	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (usingItem != null && usingItem.getItem() == this)
		{
			int useTime = usingItem.getMaxItemUseDuration() - useRemaining;
			if (useTime >= 20)
				return (useTime >> 2) % 2 == 0 ? this.icons[2] : this.icons[1];

			if (useTime > 10)
				return this.icons[1];
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

	protected int doMagnet(World world, EntityPlayer player, float yawOffset, float pitchOffset)
	{
		double range = 32.0D;
		Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 lookVec = this.getOffsetLook(player, yawOffset, pitchOffset);
		Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
		int useX = MathHelper.floor_double(srcVec.xCoord);
		int useY = MathHelper.floor_double(srcVec.yCoord);
		int useZ = MathHelper.floor_double(srcVec.zCoord);
		int destX = MathHelper.floor_double(destVec.xCoord);
		int destY = MathHelper.floor_double(destVec.yCoord);
		int destZ = MathHelper.floor_double(destVec.zCoord);

		// TODO gamerforEA add EntityPlayer parameter
		return doMagnet(player, world, useX, useY, useZ, destX, destY, destZ);
	}

	// TODO gamerforEA code start
	public static int doMagnet(World world, int useX, int useY, int useZ, int destX, int destY, int destZ)
	{
		return doMagnet(ModUtils.getModFake(world), world, useX, useY, useZ, destX, destY, destZ);
	}
	// TODO gamerforEA code end

	// TODO gamerforEA add EntityPlayer parameter
	public static int doMagnet(EntityPlayer player, World world, int useX, int useY, int useZ, int destX, int destY, int destZ)
	{
		int blocksMoved = 0;
		ChunkCoordinates[] lineArray = TFGenerator.getBresehnamArrayCoords(useX, useY, useZ, destX, destY, destZ);
		Block foundID = Blocks.air;
		int foundMeta = -1;
		int foundX = -1;
		int foundY = -1;
		int foundZ = -1;
		int baseX = -1;
		int baseY = -1;
		int baseZ = -1;
		boolean isNetherrack = false;

		for (ChunkCoordinates coord : lineArray)
		{
			Block block = world.getBlock(coord.posX, coord.posY, coord.posZ);
			int meta = world.getBlockMetadata(coord.posX, coord.posY, coord.posZ);
			if (baseY == -1)
				if (isReplaceable(world, block, meta, coord.posX, coord.posY, coord.posZ))
				{
					baseX = coord.posX;
					baseY = coord.posY;
					baseZ = coord.posZ;
				}
				else if (isNetherReplaceable(world, block, meta, coord.posX, coord.posY, coord.posZ))
				{
					isNetherrack = true;
					baseX = coord.posX;
					baseY = coord.posY;
					baseZ = coord.posZ;
				}

			if (block != Blocks.air && isOre(block, meta))
			{
				foundID = block;
				foundMeta = meta;
				foundX = coord.posX;
				foundY = coord.posY;
				foundZ = coord.posZ;
				break;
			}
		}

		if (baseY != -1 && foundID != Blocks.air)
		{
			ArrayList<ChunkCoordinates> veinBlocks = new ArrayList();
			findVein(world, foundX, foundY, foundZ, foundID, foundMeta, veinBlocks);
			int offX = baseX - foundX;
			int offY = baseY - foundY;
			int offZ = baseZ - foundZ;
			Iterator<ChunkCoordinates> iterCoord = veinBlocks.iterator();

			while (true)
			{
				int replaceX;
				int replaceY;
				int replaceZ;
				Block replaceID;
				ChunkCoordinates coord;
				do
				{
					if (!iterCoord.hasNext())
						return blocksMoved;

					coord = iterCoord.next();
					replaceX = coord.posX + offX;
					replaceY = coord.posY + offY;
					replaceZ = coord.posZ + offZ;
					replaceID = world.getBlock(replaceX, replaceY, replaceZ);
					int replaceMeta = world.getBlockMetadata(replaceX, replaceY, replaceZ);
					if (isNetherrack)
					{
						if (isNetherReplaceable(world, replaceID, replaceMeta, replaceX, replaceY, replaceZ))
							break;
					}
					else if (isReplaceable(world, replaceID, replaceMeta, replaceX, replaceY, replaceZ))
						break;
				}
				while (replaceID != Blocks.air);

				// TODO gamerforEA add condition
				if (!EventUtils.cantBreak(player, coord.posX, coord.posY, coord.posZ) && !EventUtils.cantBreak(player, replaceX, replaceY, replaceZ))
				{
					world.setBlock(coord.posX, coord.posY, coord.posZ, isNetherrack ? Blocks.netherrack : Blocks.stone, 0, 2);
					world.setBlock(replaceX, replaceY, replaceZ, foundID, foundMeta, 2);
				}

				++blocksMoved;
			}
		}
		else
			return blocksMoved;
	}

	private Vec3 getOffsetLook(EntityPlayer player, float yawOffset, float pitchOffset)
	{
		float var2 = MathHelper.cos(-(player.rotationYaw + yawOffset) * 0.017453292F - 3.1415927F);
		float var3 = MathHelper.sin(-(player.rotationYaw + yawOffset) * 0.017453292F - 3.1415927F);
		float var4 = -MathHelper.cos(-(player.rotationPitch + pitchOffset) * 0.017453292F);
		float var5 = MathHelper.sin(-(player.rotationPitch + pitchOffset) * 0.017453292F);
		return Vec3.createVectorHelper(var3 * var4, var5, var2 * var4);
	}

	private static boolean isReplaceable(World world, Block replaceID, int replaceMeta, int x, int y, int z)
	{
		return replaceID == Blocks.dirt || replaceID == Blocks.grass || replaceID == Blocks.gravel || replaceID != Blocks.air && replaceID.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	private static boolean isNetherReplaceable(World world, Block replaceID, int replaceMeta, int x, int y, int z)
	{
		return replaceID == Blocks.netherrack || replaceID != Blocks.air && replaceID.isReplaceableOreGen(world, x, y, z, Blocks.netherrack);
	}

	private static boolean findVein(World world, int x, int y, int z, Block oreID, int oreMeta, ArrayList<ChunkCoordinates> veinBlocks)
	{
		ChunkCoordinates here = new ChunkCoordinates(x, y, z);
		if (veinBlocks.contains(here))
			return false;
		else if (veinBlocks.size() >= 24)
			return false;
		else if (world.getBlock(x, y, z) == oreID && world.getBlockMetadata(x, y, z) == oreMeta)
		{
			veinBlocks.add(here);
			findVein(world, x + 1, y, z, oreID, oreMeta, veinBlocks);
			findVein(world, x - 1, y, z, oreID, oreMeta, veinBlocks);
			findVein(world, x, y + 1, z, oreID, oreMeta, veinBlocks);
			findVein(world, x, y - 1, z, oreID, oreMeta, veinBlocks);
			findVein(world, x, y, z + 1, oreID, oreMeta, veinBlocks);
			findVein(world, x, y, z - 1, oreID, oreMeta, veinBlocks);
			return true;
		}
		else
			return false;
	}

	public static boolean isOre(Block blockID, int meta)
	{
		return blockID != Blocks.coal_ore && (blockID == Blocks.iron_ore || blockID == Blocks.diamond_ore || blockID == Blocks.emerald_ore || blockID == Blocks.gold_ore || blockID == Blocks.lapis_ore || blockID == Blocks.redstone_ore || blockID == Blocks.lit_redstone_ore || blockID == Blocks.quartz_ore || blockID == TFBlocks.root && meta == 1 || blockID.getUnlocalizedName().toLowerCase().contains("ore"));
	}
}
