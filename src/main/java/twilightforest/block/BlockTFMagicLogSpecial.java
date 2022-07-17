package twilightforest.block;

import com.gamerforea.twilightforest.EventConfig;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.item.ItemTFOreMagnet;
import twilightforest.item.TFItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BlockTFMagicLogSpecial extends BlockTFMagicLog
{
	protected BlockTFMagicLogSpecial()
	{
		this.setCreativeTab(TFItems.creativeTab);
	}

	@Override
	public int tickRate(World par1World)
	{
		return 20;
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return Item.getItemFromBlock(TFBlocks.magicLog);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		int orient = meta & 12;
		int woodType = meta & 3;
		if (orient == 12)
			switch (woodType)
			{
				case 0:
				default:
					return side != 1 && side != 0 ? BlockTFMagicLog.SPR_TIMECLOCKOFF : BlockTFMagicLog.SPR_TIMETOP;
				case 1:
					return side != 1 && side != 0 ? BlockTFMagicLog.SPR_TRANSHEARTOFF : BlockTFMagicLog.SPR_TRANSTOP;
				case 2:
					return side != 1 && side != 0 ? BlockTFMagicLog.SPR_MINEGEMOFF : BlockTFMagicLog.SPR_MINETOP;
				case 3:
					return side != 1 && side != 0 ? BlockTFMagicLog.SPR_SORTEYEOFF : BlockTFMagicLog.SPR_SORTTOP;
			}
		else
			switch (woodType)
			{
				case 0:
				default:
					return orient == 0 && (side == 1 || side == 0) ? BlockTFMagicLog.SPR_TIMETOP : orient == 4 && (side == 5 || side == 4) ? BlockTFMagicLog.SPR_TIMETOP : orient != 8 || side != 2 && side != 3 ? BlockTFMagicLog.SPR_TIMECLOCK : BlockTFMagicLog.SPR_TIMETOP;
				case 1:
					return orient != 0 || side != 1 && side != 0 ? orient == 4 && (side == 5 || side == 4) ? BlockTFMagicLog.SPR_TRANSTOP : orient != 8 || side != 2 && side != 3 ? BlockTFMagicLog.SPR_TRANSHEART : BlockTFMagicLog.SPR_TRANSTOP : BlockTFMagicLog.SPR_TRANSTOP;
				case 2:
					return orient == 0 && (side == 1 || side == 0) ? BlockTFMagicLog.SPR_MINETOP : orient != 4 || side != 5 && side != 4 ? orient == 8 && (side == 2 || side == 3) ? BlockTFMagicLog.SPR_MINETOP : BlockTFMagicLog.SPR_MINEGEM : BlockTFMagicLog.SPR_MINETOP;
				case 3:
					return orient != 0 || side != 1 && side != 0 ? orient == 4 && (side == 5 || side == 4) ? BlockTFMagicLog.SPR_SORTTOP : orient != 8 || side != 2 && side != 3 ? BlockTFMagicLog.SPR_SORTEYE : BlockTFMagicLog.SPR_SORTTOP : BlockTFMagicLog.SPR_SORTTOP;
			}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if ((meta & 12) != 12)
		{
			if ((meta & 3) == 0 && !world.isRemote)
			{
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.1F, 0.5F);
				this.doTreeOfTimeEffect(world, x, y, z, rand);
			}
			else if ((meta & 3) == 1 && !world.isRemote)
				this.doTreeOfTransformationEffect(world, x, y, z, rand);
			else if ((meta & 3) == 2 && !world.isRemote)
				this.doMinersTreeEffect(world, x, y, z, rand);
			else if ((meta & 3) == 3 && !world.isRemote)
				this.doSortingTreeEffect(world, x, y, z, rand);

			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int orient = meta & 12;
		int woodType = meta & 3;
		if (orient == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, woodType | 12, 3);
			return true;
		}
		else if (orient == 12)
		{
			world.setBlockMetadataWithNotify(x, y, z, woodType | 0, 3);
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
			return true;
		}
		else
			return false;
	}

	private void doTreeOfTimeEffect(World world, int x, int y, int z, Random rand)
	{
		int numticks = 24 * this.tickRate(world);
		int successes = 0;

		for (int i = 0; i < numticks; ++i)
		{
			int dx = rand.nextInt(32) - 16;
			int dy = rand.nextInt(32) - 16;
			int dz = rand.nextInt(32) - 16;
			Block thereID = world.getBlock(x + dx, y + dy, z + dz);
			if (thereID != Blocks.air && thereID.getTickRandomly())
			{
				thereID.updateTick(world, x + dx, y + dy, z + dz, rand);
				++successes;
			}
		}

	}

	private void doTreeOfTransformationEffect(World world, int x, int y, int z, Random rand)
	{
		for (int i = 0; i < 1; ++i)
		{
			int dx = rand.nextInt(32) - 16;
			int dz = rand.nextInt(32) - 16;
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note.harp", 0.1F, rand.nextFloat() * 2.0F);
			if (Math.sqrt(dx * dx + dz * dz) < 16.0D)
			{
				BiomeGenBase biomeAt = world.getBiomeGenForCoords(x + dx, z + dz);
				if (biomeAt != TFBiomeBase.enchantedForest)
				{
					Chunk chunkAt = world.getChunkFromBlockCoords(x + dx, z + dz);
					chunkAt.getBiomeArray()[(z + dz & 15) << 4 | x + dx & 15] = (byte) TFBiomeBase.enchantedForest.biomeID;
					world.markBlockForUpdate(x + dx, y, z + dz);
					if (world instanceof WorldServer)
						this.sendChangedBiome(world, x + dx, z + dz, chunkAt);
				}
			}
		}

	}

	private void sendChangedBiome(World world, int x, int z, Chunk chunkAt)
	{
		FMLProxyPacket message = TFGenericPacketHandler.makeBiomeChangePacket(x, z, (byte) TFBiomeBase.enchantedForest.biomeID);
		TargetPoint targetPoint = new TargetPoint(world.provider.dimensionId, x, 128.0D, z, 128.0D);
		TwilightForestMod.genericChannel.sendToAllAround(message, targetPoint);
	}

	private void doMinersTreeEffect(World world, int x, int y, int z, Random rand)
	{
		int dx = rand.nextInt(64) - 32;
		int dy = rand.nextInt(64) - 32;
		int dz = rand.nextInt(64) - 32;
		int moved = ItemTFOreMagnet.doMagnet(world, x, y, z, x + dx, y + dy, z + dz);
		if (moved > 0)
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.endermen.portal", 0.1F, 1.0F);

	}

	private void doSortingTreeEffect(World world, int x, int y, int z, Random rand)
	{
		// TODO gamerforEA code start
		if (!EventConfig.enableSortingTree)
			return;
		// TODO gamerforEA code end

		byte XSEARCH = 16;
		byte YSEARCH = 16;
		byte ZSEARCH = 16;
		ArrayList<IInventory> chests = new ArrayList();
		int itemCount = 0;

		int sortedChestNum;
		int sortedSlotNum;
		int matchCount;
		int moveChest;
		for (int beingSorted = x - XSEARCH; beingSorted < x + XSEARCH; ++beingSorted)
		{
			for (sortedChestNum = y - YSEARCH; sortedChestNum < y + YSEARCH; ++sortedChestNum)
			{
				for (sortedSlotNum = z - ZSEARCH; sortedSlotNum < z + ZSEARCH; ++sortedSlotNum)
				{
					if (world.getBlock(beingSorted, sortedChestNum, sortedSlotNum) == Blocks.chest)
					{
						IInventory matchChestNum = Blocks.chest.func_149951_m(world, beingSorted, sortedChestNum, sortedSlotNum);
						if (matchChestNum != null && !this.checkIfChestsContains(chests, (IInventory) world.getTileEntity(beingSorted, sortedChestNum, sortedSlotNum)))
						{
							matchCount = 0;

							for (moveChest = 0; moveChest < matchChestNum.getSizeInventory(); ++moveChest)
							{
								if (matchChestNum.getStackInSlot(moveChest) != null)
								{
									++matchCount;
									++itemCount;
								}
							}

							if (matchCount > 0)
								chests.add(matchChestNum);
						}
					}
				}
			}
		}

		ItemStack var21 = null;
		sortedChestNum = -1;
		sortedSlotNum = -1;
		int slotNum;
		ItemStack currentItem;
		int var22;
		if (itemCount > 0)
		{
			var22 = rand.nextInt(itemCount);
			matchCount = 0;

			for (moveChest = 0; moveChest < chests.size(); ++moveChest)
			{
				IInventory chest = chests.get(moveChest);

				for (slotNum = 0; slotNum < chest.getSizeInventory(); ++slotNum)
				{
					currentItem = chest.getStackInSlot(slotNum);
					if (currentItem != null && matchCount++ == var22)
					{
						var21 = currentItem;
						sortedChestNum = moveChest;
						sortedSlotNum = slotNum;
					}
				}
			}
		}

		if (var21 != null)
		{
			var22 = -1;
			matchCount = 0;

			for (moveChest = 0; moveChest < chests.size(); ++moveChest)
			{
				IInventory chest = chests.get(moveChest);
				slotNum = 0;

				for (int var25 = 0; var25 < chest.getSizeInventory(); ++var25)
				{
					ItemStack currentItem1 = chest.getStackInSlot(var25);
					if (currentItem1 != null && this.isSortingMatch(var21, currentItem1))
						slotNum += currentItem1.stackSize;
				}

				if (slotNum > matchCount)
				{
					matchCount = slotNum;
					var22 = moveChest;
				}
			}

			if (var22 >= 0 && var22 != sortedChestNum)
			{
				IInventory var23 = chests.get(var22);
				IInventory chest = chests.get(sortedChestNum);
				slotNum = this.getEmptySlotIn(var23);
				if (slotNum >= 0)
				{
					chest.setInventorySlotContents(sortedSlotNum, null);
					var23.setInventorySlotContents(slotNum, var21);
				}
			}

			if (var21.stackSize < var21.getMaxStackSize())
			{
				for (IInventory chest : chests)
				{
					for (slotNum = 0; slotNum < chest.getSizeInventory(); ++slotNum)
					{
						currentItem = chest.getStackInSlot(slotNum);
						if (currentItem != null && currentItem != var21 && var21.isItemEqual(currentItem) && currentItem.stackSize <= var21.getMaxStackSize() - var21.stackSize)
						{
							// TODO gamerforEA code start
							if (!ItemStack.areItemStackTagsEqual(var21, currentItem))
								continue;
							// TODO gamerforEA code end

							chest.setInventorySlotContents(slotNum, null);
							var21.stackSize += currentItem.stackSize;
							currentItem.stackSize = 0;
						}
					}
				}
			}
		}

	}

	private boolean isSortingMatch(ItemStack beingSorted, ItemStack currentItem)
	{
		return this.getCreativeTab(currentItem.getItem()) == this.getCreativeTab(beingSorted.getItem());
	}

	private Object getCreativeTab(Item item)
	{
		try
		{
			return ObfuscationReflectionHelper.getPrivateValue(Item.class, item, 0);
		}
		catch (IllegalArgumentException | SecurityException var3)
		{
			var3.printStackTrace();
		}

		return null;
	}

	private boolean checkIfChestsContains(ArrayList<IInventory> chests, IInventory testChest)
	{
		Iterator var3 = chests.iterator();

		IInventory chest;
		do
		{
			if (!var3.hasNext())
				return false;

			chest = (IInventory) var3.next();
			if (chest == testChest)
				return true;
		}
		while (!(chest instanceof InventoryLargeChest) || !((InventoryLargeChest) chest).isPartOfLargeChest(testChest));

		return true;
	}

	private int getEmptySlotIn(IInventory chest)
	{
		for (int i = 0; i < chest.getSizeInventory(); ++i)
		{
			if (chest.getStackInSlot(i) == null)
				return i;
		}

		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		return 15;
	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
	}
}
