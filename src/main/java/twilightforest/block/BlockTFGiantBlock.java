package twilightforest.block;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.ModUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTFGiantBlock extends Block
{
	private IIcon[][][] giantIcon;
	private Block baseBlock;
	private boolean isSelfDestructing;

	public BlockTFGiantBlock(Block baseBlock)
	{
		super(baseBlock.getMaterial());
		this.setStepSound(baseBlock.stepSound);
		this.baseBlock = baseBlock;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.giantIcon = new GiantBlockIcon[4][4][6];

		for (int x = 0; x < 4; ++x)
		{
			for (int y = 0; y < 4; ++y)
			{
				for (int side = 0; side < 6; ++side)
				{
					this.giantIcon[x][y][side] = new GiantBlockIcon(this.baseBlock.getBlockTextureFromSide(side), x, y);
				}
			}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		switch (side)
		{
			case 0:
			default:
				return this.giantIcon[x & 3][z & 3][side];
			case 1:
				return this.giantIcon[x & 3][z & 3][side];
			case 2:
				return this.giantIcon[3 - (x & 3)][3 - (y & 3)][side];
			case 3:
				return this.giantIcon[x & 3][3 - (y & 3)][side];
			case 4:
				return this.giantIcon[z & 3][3 - (y & 3)][side];
			case 5:
				return this.giantIcon[3 - (z & 3)][3 - (y & 3)][side];
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return this.giantIcon[0][0][side];
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		int bx = x >> 2 << 2;
		int by = y >> 2 << 2;
		int bz = z >> 2 << 2;
		boolean allReplaceable = true;

		for (int dx = 0; dx < 4; ++dx)
		{
			for (int dy = 0; dy < 4; ++dy)
			{
				for (int dz = 0; dz < 4; ++dz)
				{
					allReplaceable &= world.getBlock(bx + dx, by + dy, bz + dz).isReplaceable(world, bx + dx, by + dy, bz + dz);
				}
			}
		}

		return super.canPlaceBlockAt(world, x, y, z) && allReplaceable;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int bx = x >> 2 << 2;
		int by = y >> 2 << 2;
		int bz = z >> 2 << 2;
		return AxisAlignedBB.getBoundingBox(bx + this.minX, by + this.minY, bz + this.minZ, bx + this.maxX * 4.0D, by + this.maxY * 4.0D, bz + this.maxZ * 4.0D);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack)
	{
		if (!world.isRemote)
		{
			int bx = x >> 2 << 2;
			int by = y >> 2 << 2;
			int bz = z >> 2 << 2;

			// TODO gamerforEA code start
			EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : ModUtils.getModFake(world);
			for (int dx = 0; dx < 4; ++dx)
			{
				for (int dy = 0; dy < 4; ++dy)
				{
					for (int dz = 0; dz < 4; ++dz)
					{
						if (EventUtils.cantBreak(player, bx + dx, by + dy, bz + dz))
						{
							world.setBlockToAir(bx + dx, by + dy, bz + dz);
							return;
						}
					}
				}
			}
			// TODO gamerforEA code end

			for (int dx = 0; dx < 4; ++dx)
			{
				for (int dy = 0; dy < 4; ++dy)
				{
					for (int dz = 0; dz < 4; ++dz)
					{
						world.setBlock(bx + dx, by + dy, bz + dz, this, 0, 2);
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		int bx = x >> 2 << 2;
		int by = y >> 2 << 2;
		int bz = z >> 2 << 2;
		Block blockThere = world.getBlock(x, y, z);
		int metaThere = world.getBlockMetadata(x, y, z);
		byte b0 = 16;

		for (int i1 = 0; i1 < b0; ++i1)
		{
			for (int j1 = 0; j1 < b0; ++j1)
			{
				for (int k1 = 0; k1 < b0; ++k1)
				{
					double d0 = bx + (i1 + 0.5D) / 4.0D;
					double d1 = by + (j1 + 0.5D) / 4.0D;
					double d2 = bz + (k1 + 0.5D) / 4.0D;
					effectRenderer.addEffect(new EntityDiggingFX(world, d0, d1, d2, d0 - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D, blockThere, metaThere).applyColourMultiplier(x, y, z));
				}
			}
		}

		return true;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
		this.setGiantBlockToAir(world, x, y, z);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		world.setBlockToAir(x, y, z);
		this.setGiantBlockToAir(world, x, y, z);
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if (!this.isSelfDestructing && !this.canBlockStay(world, x, y, z))
			this.setGiantBlockToAir(world, x, y, z);

	}

	private void setGiantBlockToAir(World world, int x, int y, int z)
	{
		this.isSelfDestructing = true;
		int bx = x >> 2 << 2;
		int by = y >> 2 << 2;
		int bz = z >> 2 << 2;

		for (int dx = 0; dx < 4; ++dx)
		{
			for (int dy = 0; dy < 4; ++dy)
			{
				for (int dz = 0; dz < 4; ++dz)
				{
					if ((x != bx + dx || y != by + dy || z != bz + dz) && world.getBlock(bx + dx, by + dy, bz + dz) == this)
						world.setBlockToAir(bx + dx, by + dy, bz + dz);
				}
			}
		}

		this.isSelfDestructing = false;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		boolean allThisBlock = true;
		int bx = x >> 2 << 2;
		int by = y >> 2 << 2;
		int bz = z >> 2 << 2;

		for (int dx = 0; dx < 4; ++dx)
		{
			for (int dy = 0; dy < 4; ++dy)
			{
				for (int dz = 0; dz < 4; ++dz)
				{
					allThisBlock &= world.getBlock(bx + dx, by + dy, bz + dz) == this;
				}
			}
		}

		return allThisBlock;
	}

	@Override
	public int getMobilityFlag()
	{
		return 2;
	}
}
