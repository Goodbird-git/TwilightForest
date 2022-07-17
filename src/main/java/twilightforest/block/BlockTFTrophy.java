package twilightforest.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFTrophy;

import java.util.ArrayList;
import java.util.Random;

public class BlockTFTrophy extends BlockContainer
{
	public BlockTFTrophy()
	{
		super(Material.circuits);
		this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z) & 7;

		TileEntity tile = world.getTileEntity(x, y, z);

		// TODO gamerforEA code start
		if (!(tile instanceof TileEntityTFTrophy))
			return;
		// TODO gamerforEA code end

		TileEntityTFTrophy trophy = (TileEntityTFTrophy) tile;
		if (trophy != null && trophy.func_145904_a() == 0)
			switch (meta)
			{
				case 1:
				default:
					this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
					break;
				case 2:
				case 3:
					this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
					break;
				case 4:
				case 5:
					this.setBlockBounds(0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
			}
		else if (trophy != null && trophy.func_145904_a() == 3)
			this.setBlockBounds(0.25F, 0.5F, 0.25F, 0.75F, 1.0F, 0.75F);
		else
			switch (meta)
			{
				case 1:
				default:
					this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
					break;
				case 2:
					this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
					break;
				case 3:
					this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
					break;
				case 4:
					this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
					break;
				case 5:
					this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
			}

	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack itemStack)
	{
		int rotation = MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		par1World.setBlockMetadataWithNotify(par2, par3, par4, rotation, 2);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityTFTrophy();
	}

	@SideOnly(Side.CLIENT)
	public Item idPicked(World par1World, int par2, int par3, int par4)
	{
		return TFItems.trophy;
	}

	@Override
	public int getDamageValue(World par1World, int par2, int par3, int par4)
	{
		TileEntity var5 = par1World.getTileEntity(par2, par3, par4);
		return var5 instanceof TileEntitySkull ? ((TileEntitySkull) var5).func_145904_a() : super.getDamageValue(par1World, par2, par3, par4);
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer)
	{
		if (par6EntityPlayer.capabilities.isCreativeMode)
		{
			par5 |= 8;
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par5, 2);
		}

		this.dropBlockAsItem(par1World, par2, par3, par4, par5, 0);
		super.onBlockHarvested(par1World, par2, par3, par4, par5, par6EntityPlayer);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList();
		if ((metadata & 8) == 0)
		{
			ItemStack var7 = new ItemStack(TFItems.trophy, 1, this.getDamageValue(world, x, y, z));

			TileEntity tile = world.getTileEntity(x, y, z);

			// TODO gamerforEA code start
			if (!(tile instanceof TileEntityTFTrophy))
				return drops;
			// TODO gamerforEA code end

			TileEntityTFTrophy var8 = (TileEntityTFTrophy) tile;
			drops.add(var7);
		}

		return drops;
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return TFItems.trophy;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.soul_sand.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
	}
}
