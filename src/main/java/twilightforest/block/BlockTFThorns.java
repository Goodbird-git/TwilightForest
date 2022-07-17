package twilightforest.block;

import com.gamerforea.twilightforest.EventConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

import java.util.List;
import java.util.Random;

public class BlockTFThorns extends BlockRotatedPillar
{
	private static final float THORN_DAMAGE = 4.0F;
	private String[] names;
	private IIcon[] sideIcons;
	private IIcon[] topIcons;

	protected BlockTFThorns()
	{
		super(Material.wood);
		this.setNames(new String[] { "brown", "green" });
		this.setHardness(50.0F);
		this.setResistance(2000.0F);
		this.setStepSound(Block.soundTypeWood);
		this.setCreativeTab(TFItems.creativeTab);
	}

	@Override
	public int getRenderType()
	{
		return TwilightForestMod.proxy.getThornsBlockRenderID();
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 12;
		float pixel = 0.0625F;
		switch (rotation)
		{
			case 0:
			default:
				return AxisAlignedBB.getBoundingBox((double) ((float) x + pixel * 3.0F), (double) y, (double) ((float) z + pixel * 3.0F), (double) ((float) x + 1.0F - pixel * 3.0F), (double) ((float) y + 1.0F), (double) ((float) z + 1.0F - pixel * 3.0F));
			case 4:
				return AxisAlignedBB.getBoundingBox((double) x, (double) ((float) y + pixel * 3.0F), (double) ((float) z + pixel * 3.0F), (double) ((float) x + 1.0F), (double) ((float) y + 1.0F - pixel * 3.0F), (double) ((float) z + 1.0F - pixel * 3.0F));
			case 8:
				return AxisAlignedBB.getBoundingBox((double) ((float) x + pixel * 3.0F), (double) ((float) y + pixel * 3.0F), (double) z, (double) ((float) x + 1.0F - pixel * 3.0F), (double) ((float) y + 1.0F - pixel * 3.0F), (double) ((float) z + 1.0F));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		entity.attackEntityFrom(DamageSource.cactus, 4.0F);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		// TODO gamerforEA code start
		if (!EventConfig.enableThornsBurst)
			return super.removedByPlayer(world, player, x, y, z);
		// TODO gamerforEA code end

		int meta = world.getBlockMetadata(x, y, z);
		if (!player.capabilities.isCreativeMode)
		{
			if (!world.isRemote)
			{
				world.setBlock(x, y, z, this, meta & 12 | 1, 2);
				this.doThornBurst(world, x, y, z, meta);
			}
		}
		else
			world.setBlockToAir(x, y, z);

		return true;
	}

	@Override
	public int getMobilityFlag()
	{
		return 2;
	}

	private void doThornBurst(World world, int x, int y, int z, int meta)
	{
		int rotation = meta & 12;
		switch (rotation)
		{
			case 0:
				this.growThorns(world, x, y, z, ForgeDirection.UP);
				this.growThorns(world, x, y, z, ForgeDirection.DOWN);
				break;
			case 4:
				this.growThorns(world, x, y, z, ForgeDirection.EAST);
				this.growThorns(world, x, y, z, ForgeDirection.WEST);
				break;
			case 8:
				this.growThorns(world, x, y, z, ForgeDirection.NORTH);
				this.growThorns(world, x, y, z, ForgeDirection.SOUTH);
		}

		this.growThorns(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[world.rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)]);
		this.growThorns(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[world.rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)]);
		this.growThorns(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[world.rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)]);
	}

	private void growThorns(World world, int x, int y, int z, ForgeDirection dir)
	{
		int length = 1 + world.rand.nextInt(3);

		for (int i = 1; i < length; ++i)
		{
			int dx = x + dir.offsetX * i;
			int dy = y + dir.offsetY * i;
			int dz = z + dir.offsetZ * i;
			if (!world.isAirBlock(dx, dy, dz))
				break;

			world.setBlock(dx, dy, dz, this, getMetaFor(dir) | 1, 2);
		}

	}

	public static int getMetaFor(ForgeDirection dir)
	{
		switch (dir)
		{
			case UNKNOWN:
			case UP:
			case DOWN:
			default:
				return 0;
			case EAST:
			case WEST:
				return 4;
			case NORTH:
			case SOUTH:
				return 8;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block logBlock, int metadata)
	{
		byte range = 4;
		int exRange = range + 1;
		if (world.checkChunksExist(x - exRange, y - exRange, z - exRange, x + exRange, y + exRange, z + exRange))
			for (int dx = -range; dx <= range; ++dx)
			{
				for (int dy = -range; dy <= range; ++dy)
				{
					for (int dz = -range; dz <= range; ++dz)
					{
						Block block = world.getBlock(x + dx, y + dy, z + dz);
						if (block.isLeaves(world, x + dx, y + dy, z + dz))
							block.beginLeavesDecay(world, x + dx, y + dy, z + dz);
					}
				}
			}

	}

	@Override
	public int quantityDropped(Random p_149745_1_)
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int meta)
	{
		return this.sideIcons[meta & 3];
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getTopIcon(int meta)
	{
		return this.topIcons[meta & 3];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.sideIcons = new IIcon[this.getNames().length];
		this.topIcons = new IIcon[this.getNames().length];

		for (int i = 0; i < this.getNames().length; ++i)
		{
			this.sideIcons[i] = iconRegister.registerIcon("TwilightForest:" + this.getNames()[i] + "_thorns_side");
			this.topIcons[i] = iconRegister.registerIcon("TwilightForest:" + this.getNames()[i] + "_thorns_top");
		}

	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < this.getNames().length; ++i)
		{
			par3List.add(new ItemStack(par1, 1, i));
		}

	}

	public String[] getNames()
	{
		return this.names;
	}

	public void setNames(String[] names)
	{
		this.names = names;
	}
}
