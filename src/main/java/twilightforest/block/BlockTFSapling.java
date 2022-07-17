package twilightforest.block;

import com.gamerforea.twilightforest.EventConfig;
import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.tile.OwnerTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import twilightforest.item.TFItems;
import twilightforest.world.*;

import java.util.List;
import java.util.Random;

// TODO gamerforEA implement ITileEntityProvider
public class BlockTFSapling extends BlockSapling implements ITileEntityProvider
{
	private IIcon[] icons;
	private String[] iconNames = { "sapling_oak", "sapling_canopy", "sapling_mangrove", "sapling_darkwood", "sapling_hollow_oak", "sapling_time", "sapling_transformation", "sapling_mining", "sapling_sorting", "sapling_rainboak" };

	// TODO gamerforEA code start
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new OwnerTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
	{
		if (placer instanceof EntityPlayer)
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof OwnerTileEntity)
				((OwnerTileEntity) tile).fake.setProfile(((EntityPlayer) placer).getGameProfile());
		}
		super.onBlockPlacedBy(world, x, y, z, placer, stack);
	}
	// TODO gamerforEA code end

	protected BlockTFSapling()
	{
		float var3 = 0.4F;
		this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 2.0F, 0.5F + var3);
		this.setCreativeTab(TFItems.creativeTab);
	}

	@Override
	public void updateTick(World par1World, int x, int y, int z, Random par5Random)
	{
		if (!par1World.isRemote && par1World.getBlockLightValue(x, y + 1, z) >= 9 && par5Random.nextInt(7) == 0)
			this.func_149878_d(par1World, x, y, z, par5Random);
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random rand)
	{
		int yOffset = 0;
		int zOffset = 0;
		boolean largeTree = false;

		// TODO gamerforEA code start
		EntityPlayer saplingOwner = null;
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof OwnerTileEntity && ((OwnerTileEntity) tile).fake.getProfile() != null)
			saplingOwner = ((OwnerTileEntity) tile).fake.get();
		else if (EventConfig.enableForceAdvTreeRegionCheck)
			saplingOwner = ModUtils.getModFake(world);
		// TODO gamerforEA code end

		int meta = world.getBlockMetadata(x, y, z);
		WorldGenerator treeGenerator;
		if (meta == 1)
			treeGenerator = new TFGenCanopyTree(true);
		else if (meta == 2)
			treeGenerator = new TFGenMangroveTree(true);
		else if (meta == 3)
			treeGenerator = new TFGenDarkCanopyTree(true);
		else if (meta == 4)
			treeGenerator = new TFGenHollowTree(true);
		else if (meta == 5)
			treeGenerator = new TFGenTreeOfTime(true);
		else if (meta == 6)
			treeGenerator = new TFGenTreeOfTransformation(true);
		else if (meta == 7)
			treeGenerator = new TFGenMinersTree(true);
		else if (meta == 8)
			treeGenerator = new TFGenSortingTree(true);
		else if (meta == 9)
			treeGenerator = rand.nextInt(7) == 0 ? new TFGenLargeRainboak(true) : new TFGenSmallRainboak(true);
		else
			treeGenerator = new TFGenSmallTwilightOak(true);

		if (largeTree)
		{
			world.setBlock(x + yOffset, y, z + zOffset, Blocks.air, 0, 4);
			world.setBlock(x + yOffset + 1, y, z + zOffset, Blocks.air, 0, 4);
			world.setBlock(x + yOffset, y, z + zOffset + 1, Blocks.air, 0, 4);
			world.setBlock(x + yOffset + 1, y, z + zOffset + 1, Blocks.air, 0, 4);
		}
		else
			world.setBlock(x, y, z, Blocks.air, 0, 4);

		// TODO gamerforEA code replace, old code:
		// boolean generateSuccess = treeGenerator.generate(world, rand, x + yOffset, y, z + zOffset);
		EntityPlayer prevTempPlayer = ModUtils.setTempPlayer(saplingOwner);
		boolean generateSuccess;
		try
		{
			generateSuccess = treeGenerator.generate(world, rand, x + yOffset, y, z + zOffset);
		}
		finally
		{
			ModUtils.setTempPlayer(prevTempPlayer);
		}
		// TODO gamerforEA code end

		if (!generateSuccess)
			if (largeTree)
			{
				world.setBlock(x + yOffset, y, z + zOffset, this, meta, 4);
				world.setBlock(x + yOffset + 1, y, z + zOffset, this, meta, 4);
				world.setBlock(x + yOffset, y, z + zOffset + 1, this, meta, 4);
				world.setBlock(x + yOffset + 1, y, z + zOffset + 1, this, meta, 4);
			}
			else
				world.setBlock(x, y, z, this, meta, 4);

	}

	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return metadata < this.icons.length ? this.icons[metadata] : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.icons = new IIcon[this.iconNames.length];

		for (int i = 0; i < this.icons.length; ++i)
		{
			this.icons[i] = par1IconRegister.registerIcon("TwilightForest:" + this.iconNames[i]);
		}

	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
		par3List.add(new ItemStack(par1, 1, 4));
		par3List.add(new ItemStack(par1, 1, 5));
		par3List.add(new ItemStack(par1, 1, 6));
		par3List.add(new ItemStack(par1, 1, 7));
		par3List.add(new ItemStack(par1, 1, 8));
		par3List.add(new ItemStack(par1, 1, 9));
	}
}
