package twilightforest.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twilightforest.item.TFItems;
import twilightforest.tileentity.*;

import java.util.List;
import java.util.Random;

public class BlockTFTowerDevice extends Block
{
	private static IIcon TEX_REAPPEARING_INACTIVE;
	private static IIcon TEX_REAPPEARING_ACTIVE;
	private static IIcon TEX_VANISH_INACTIVE;
	private static IIcon TEX_VANISH_ACTIVE;
	private static IIcon TEX_VANISH_LOCKED;
	private static IIcon TEX_VANISH_UNLOCKED;
	private static IIcon TEX_BUILDER_INACTIVE;
	private static IIcon TEX_BUILDER_ACTIVE;
	private static IIcon TEX_ANTIBUILDER;
	private static IIcon TEX_BUILDER_TIMEOUT;
	private static IIcon TEX_GHASTTRAP_INACTIVE;
	private static IIcon TEX_GHASTTRAP_ACTIVE;
	private static IIcon TEX_REACTOR_INACTIVE;
	private static IIcon TEX_REACTOR_ACTIVE;
	private static IIcon TEX_GHASTTRAP_LID_INACTIVE;
	private static IIcon TEX_GHASTTRAP_LID_ACTIVE;
	private static IIcon TEX_SMOKER_ACTIVE;
	private static IIcon TEX_SMOKER_INACTIVE;
	private static IIcon TEX_FIREJET_ACTIVE;
	private static IIcon TEX_FIREJET_INACTIVE;
	public static final int META_REAPPEARING_INACTIVE = 0;
	public static final int META_REAPPEARING_ACTIVE = 1;
	public static final int META_VANISH_INACTIVE = 2;
	public static final int META_VANISH_ACTIVE = 3;
	public static final int META_VANISH_LOCKED = 4;
	public static final int META_VANISH_UNLOCKED = 5;
	public static final int META_BUILDER_INACTIVE = 6;
	public static final int META_BUILDER_ACTIVE = 7;
	public static final int META_BUILDER_TIMEOUT = 8;
	public static final int META_ANTIBUILDER = 9;
	public static final int META_GHASTTRAP_INACTIVE = 10;
	public static final int META_GHASTTRAP_ACTIVE = 11;
	public static final int META_REACTOR_INACTIVE = 12;
	public static final int META_REACTOR_ACTIVE = 13;

	public BlockTFTowerDevice()
	{
		super(Material.wood);
		this.setHardness(10.0F);
		this.setResistance(35.0F);
		this.setStepSound(Block.soundTypeWood);
		this.setCreativeTab(TFItems.creativeTab);
	}

	// TODO gamerforEA code start
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTFCReactorActive && entity instanceof EntityPlayer)
			((TileEntityTFCReactorActive) tile).fake.setProfile(((EntityPlayer) entity).getGameProfile());
	}
	// TODO gamerforEA code end

	public int tickRate()
	{
		return 15;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		switch (meta)
		{
			case 0:
			default:
				return TEX_REAPPEARING_INACTIVE;
			case 1:
				return TEX_REAPPEARING_ACTIVE;
			case 2:
				return TEX_VANISH_INACTIVE;
			case 3:
				return TEX_VANISH_ACTIVE;
			case 4:
				return TEX_VANISH_LOCKED;
			case 5:
				return TEX_VANISH_UNLOCKED;
			case 6:
				return TEX_BUILDER_INACTIVE;
			case 7:
				return TEX_BUILDER_ACTIVE;
			case 8:
				return TEX_BUILDER_TIMEOUT;
			case 9:
				return TEX_ANTIBUILDER;
			case 10:
				if (side >= 2)
					return TEX_GHASTTRAP_INACTIVE;
				else
				{
					if (side == 1)
						return TEX_GHASTTRAP_LID_INACTIVE;

					return TFBlocks.towerWood.getIcon(side, 1);
				}
			case 11:
				if (side >= 2)
					return TEX_GHASTTRAP_ACTIVE;
				else
				{
					if (side == 1)
						return TEX_GHASTTRAP_LID_ACTIVE;

					return TFBlocks.towerWood.getIcon(side, 1);
				}
			case 12:
				return TEX_REACTOR_INACTIVE;
			case 13:
				return TEX_REACTOR_ACTIVE;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		TEX_REAPPEARING_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_reappearing_off");
		TEX_REAPPEARING_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_reappearing_on");
		TEX_VANISH_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_vanish_off");
		TEX_VANISH_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_vanish_on");
		TEX_VANISH_LOCKED = par1IconRegister.registerIcon("TwilightForest:towerdev_lock_on");
		TEX_VANISH_UNLOCKED = par1IconRegister.registerIcon("TwilightForest:towerdev_lock_off");
		TEX_BUILDER_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_builder_off");
		TEX_BUILDER_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_builder_on");
		TEX_ANTIBUILDER = par1IconRegister.registerIcon("TwilightForest:towerdev_antibuilder");
		TEX_BUILDER_TIMEOUT = par1IconRegister.registerIcon("TwilightForest:towerdev_builder_timeout");
		TEX_GHASTTRAP_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_ghasttrap_off");
		TEX_GHASTTRAP_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_ghasttrap_on");
		TEX_REACTOR_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_reactor_off");
		TEX_REACTOR_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_reactor_on");
		TEX_GHASTTRAP_LID_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_ghasttraplid_off");
		TEX_GHASTTRAP_LID_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_ghasttraplid_on");
		TEX_SMOKER_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_smoker_off");
		TEX_SMOKER_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_smoker_on");
		TEX_FIREJET_INACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_firejet_off");
		TEX_FIREJET_ACTIVE = par1IconRegister.registerIcon("TwilightForest:towerdev_firejet_on");
	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 4));
		par3List.add(new ItemStack(par1, 1, 5));
		par3List.add(new ItemStack(par1, 1, 6));
		par3List.add(new ItemStack(par1, 1, 9));
		par3List.add(new ItemStack(par1, 1, 10));
		par3List.add(new ItemStack(par1, 1, 12));
	}

	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		int meta = par1World.getBlockMetadata(x, y, z);
		if (meta == 2)
		{
			if (areNearbyLockBlocks(par1World, x, y, z))
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 1.0F, 0.3F);
			else
				changeToActiveVanishBlock(par1World, x, y, z, 3);

			return true;
		}
		else if (meta == 0)
		{
			if (areNearbyLockBlocks(par1World, x, y, z))
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 1.0F, 0.3F);
			else
				changeToActiveVanishBlock(par1World, x, y, z, 1);

			return true;
		}
		else
			return false;
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 2 ? 6000.0F : meta == 4 ? 6000000.0F : super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		switch (meta)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				return -1.0F;
			default:
				return super.getBlockHardness(world, x, y, z);
		}
	}

	public static boolean areNearbyLockBlocks(World world, int x, int y, int z)
	{
		boolean locked = false;

		for (int dx = x - 2; dx <= x + 2; ++dx)
		{
			for (int dy = y - 2; dy <= y + 2; ++dy)
			{
				for (int dz = z - 2; dz <= z + 2; ++dz)
				{
					if (world.getBlock(dx, dy, dz) == TFBlocks.towerDevice && world.getBlockMetadata(dx, dy, dz) == 4)
						locked = true;
				}
			}
		}

		return locked;
	}

	public static void unlockBlock(World par1World, int x, int y, int z)
	{
		Block thereBlockID = par1World.getBlock(x, y, z);
		int thereBlockMeta = par1World.getBlockMetadata(x, y, z);
		if (thereBlockID == TFBlocks.towerDevice || thereBlockMeta == 4)
		{
			changeToBlockMeta(par1World, x, y, z, 5);
			par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
		}

	}

	private static void changeToBlockMeta(World par1World, int x, int y, int z, int meta)
	{
		Block thereBlockID = par1World.getBlock(x, y, z);
		if (thereBlockID == TFBlocks.towerDevice || thereBlockID == TFBlocks.towerTranslucent)
		{
			par1World.setBlock(x, y, z, thereBlockID, meta, 3);
			par1World.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			par1World.notifyBlocksOfNeighborChange(x, y, z, thereBlockID);
		}

	}

	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		int meta = par1World.getBlockMetadata(x, y, z);
		if (!par1World.isRemote && meta == 6 && par1World.isBlockIndirectlyGettingPowered(x, y, z))
		{
			changeToBlockMeta(par1World, x, y, z, 7);
			par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
		}

	}

	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, Block myBlockID)
	{
		int meta = par1World.getBlockMetadata(x, y, z);
		if (!par1World.isRemote)
		{
			if (meta == 2 && par1World.isBlockIndirectlyGettingPowered(x, y, z) && !areNearbyLockBlocks(par1World, x, y, z))
				changeToActiveVanishBlock(par1World, x, y, z, 3);

			if (meta == 0 && par1World.isBlockIndirectlyGettingPowered(x, y, z) && !areNearbyLockBlocks(par1World, x, y, z))
				changeToActiveVanishBlock(par1World, x, y, z, 1);

			if (meta == 6 && par1World.isBlockIndirectlyGettingPowered(x, y, z))
			{
				changeToBlockMeta(par1World, x, y, z, 7);
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
				par1World.scheduleBlockUpdate(x, y, z, this, 4);
			}

			if (meta == 7 && !par1World.isBlockIndirectlyGettingPowered(x, y, z))
			{
				changeToBlockMeta(par1World, x, y, z, 6);
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
				par1World.scheduleBlockUpdate(x, y, z, this, 4);
			}

			if (meta == 8 && !par1World.isBlockIndirectlyGettingPowered(x, y, z))
				changeToBlockMeta(par1World, x, y, z, 6);

			if (meta == 10 && this.isInactiveTrapCharged(par1World, x, y, z) && par1World.isBlockIndirectlyGettingPowered(x, y, z))
			{
				changeToBlockMeta(par1World, x, y, z, 11);
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
				par1World.scheduleBlockUpdate(x, y, z, this, 4);
			}

			if (meta == 12 && this.isReactorReady(par1World, x, y, z))
				changeToBlockMeta(par1World, x, y, z, 13);
		}

	}

	@Override
	public void updateTick(World par1World, int x, int y, int z, Random par5Random)
	{
		if (!par1World.isRemote)
		{
			int meta = par1World.getBlockMetadata(x, y, z);
			if (meta == 3 || meta == 1)
			{
				if (meta == 3)
					par1World.setBlock(x, y, z, Blocks.air, 0, 3);
				else
				{
					par1World.setBlock(x, y, z, TFBlocks.towerTranslucent, 0, 3);
					par1World.scheduleBlockUpdate(x, y, z, TFBlocks.towerTranslucent, 80);
				}

				par1World.notifyBlocksOfNeighborChange(x, y, z, this);
				par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.3F, 0.5F);
				checkAndActivateVanishBlock(par1World, x - 1, y, z);
				checkAndActivateVanishBlock(par1World, x + 1, y, z);
				checkAndActivateVanishBlock(par1World, x, y + 1, z);
				checkAndActivateVanishBlock(par1World, x, y - 1, z);
				checkAndActivateVanishBlock(par1World, x, y, z + 1);
				checkAndActivateVanishBlock(par1World, x, y, z - 1);
			}

			if (meta == 7 && par1World.isBlockIndirectlyGettingPowered(x, y, z))
				this.letsBuild(par1World, x, y, z);

			if (meta == 6 || meta == 8)
			{
				checkAndActivateVanishBlock(par1World, x - 1, y, z);
				checkAndActivateVanishBlock(par1World, x + 1, y, z);
				checkAndActivateVanishBlock(par1World, x, y + 1, z);
				checkAndActivateVanishBlock(par1World, x, y - 1, z);
				checkAndActivateVanishBlock(par1World, x, y, z + 1);
				checkAndActivateVanishBlock(par1World, x, y, z - 1);
			}
		}

	}

	private void letsBuild(World par1World, int x, int y, int z)
	{
		BlockSourceImpl blockSource = new BlockSourceImpl(par1World, x, y, z);
		TileEntityTFTowerBuilder tileEntity = (TileEntityTFTowerBuilder) blockSource.getBlockTileEntity();
		if (tileEntity != null && !tileEntity.makingBlocks)
			tileEntity.startBuilding();

	}

	private boolean isInactiveTrapCharged(World par1World, int x, int y, int z)
	{
		BlockSourceImpl blockSource = new BlockSourceImpl(par1World, x, y, z);
		TileEntityTFGhastTrapInactive tileEntity = (TileEntityTFGhastTrapInactive) blockSource.getBlockTileEntity();
		return tileEntity != null && tileEntity.isCharged();
	}

	private boolean isReactorReady(World world, int x, int y, int z)
	{
		return world.getBlock(x, y + 1, z) == Blocks.redstone_block && world.getBlock(x, y - 1, z) == Blocks.redstone_block && world.getBlock(x + 1, y, z) == Blocks.redstone_block && world.getBlock(x - 1, y, z) == Blocks.redstone_block && world.getBlock(x, y, z + 1) == Blocks.redstone_block && world.getBlock(x, y, z - 1) == Blocks.redstone_block;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
	{
		int meta = par1World.getBlockMetadata(x, y, z);
		if (meta == 3 || meta == 1 || meta == 7)
			for (int i = 0; i < 1; ++i)
			{
				this.sparkle(par1World, x, y, z, par5Random);
			}

	}

	public void sparkle(World world, int x, int y, int z, Random rand)
	{
		double offset = 0.0625D;

		for (int side = 0; side < 6; ++side)
		{
			double rx = x + rand.nextFloat();
			double ry = y + rand.nextFloat();
			double rz = z + rand.nextFloat();
			if (side == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube())
				ry = y + 1 + offset;

			if (side == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube())
				ry = y - offset;

			if (side == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube())
				rz = z + 1 + offset;

			if (side == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube())
				rz = z - offset;

			if (side == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube())
				rx = x + 1 + offset;

			if (side == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube())
				rx = x - offset;

			if (rx < x || rx > x + 1 || ry < 0.0D || ry > y + 1 || rz < z || rz > z + 1)
				world.spawnParticle("reddust", rx, ry, rz, 0.0D, 0.0D, 0.0D);
		}

	}

	public static void checkAndActivateVanishBlock(World world, int x, int y, int z)
	{
		Block thereID = world.getBlock(x, y, z);
		int thereMeta = world.getBlockMetadata(x, y, z);
		if (thereID == TFBlocks.towerDevice && (thereMeta == 2 || thereMeta == 5) && !areNearbyLockBlocks(world, x, y, z))
			changeToActiveVanishBlock(world, x, y, z, 3);
		else if (thereID == TFBlocks.towerDevice && thereMeta == 0 && !areNearbyLockBlocks(world, x, y, z))
			changeToActiveVanishBlock(world, x, y, z, 1);
		else if (thereID == TFBlocks.towerTranslucent && thereMeta == 2)
			changeToActiveVanishBlock(world, x, y, z, 3);

	}

	public static void changeToActiveVanishBlock(World par1World, int x, int y, int z, int meta)
	{
		changeToBlockMeta(par1World, x, y, z, meta);
		par1World.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.3F, 0.6F);
		Block thereBlockID = par1World.getBlock(x, y, z);
		par1World.scheduleBlockUpdate(x, y, z, thereBlockID, getTickRateFor(thereBlockID, meta, par1World.rand));
	}

	private static int getTickRateFor(Block thereBlockID, int meta, Random rand)
	{
		return thereBlockID != TFBlocks.towerDevice || meta != 3 && meta != 1 ? thereBlockID == TFBlocks.towerTranslucent && meta == 3 ? 10 : 15 : 2 + rand.nextInt(5);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		Block blockID = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (blockID != this)
			return 0;
		else
			switch (meta)
			{
				case 1:
				case 3:
				case 7:
					return 4;
				case 2:
				case 4:
				case 5:
				case 6:
				case 8:
				case 10:
				case 12:
				default:
					return 0;
				case 9:
					return 10;
				case 11:
				case 13:
					return 15;
			}
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return metadata == 7 || metadata == 9 || metadata == 13 || metadata == 10 || metadata == 11;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		return metadata == 7 ? new TileEntityTFTowerBuilder() : metadata == 9 ? new TileEntityTFReverter() : metadata == 10 ? new TileEntityTFGhastTrapInactive() : metadata == 11 ? new TileEntityTFGhastTrapActive() : metadata == 13 ? new TileEntityTFCReactorActive() : null;
	}

	@Override
	public Item getItemDropped(int meta, Random par2Random, int par3)
	{
		if (meta == 9)
			return null;
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(int meta)
	{
		switch (meta)
		{
			case 1:
				return 0;
			case 2:
			case 4:
			case 5:
			case 6:
			case 9:
			case 10:
			case 12:
			default:
				return meta;
			case 3:
				return 2;
			case 7:
			case 8:
				return 6;
			case 11:
				return 10;
			case 13:
				return 12;
		}
	}
}
