package twilightforest.block;

import com.gamerforea.twilightforest.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import twilightforest.item.TFItems;

import java.util.Random;

public class BlockTFUberousSoil extends Block implements IGrowable
{
	protected BlockTFUberousSoil()
	{
		super(Material.ground);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
		this.setLightOpacity(255);
		this.setHardness(0.6F);
		this.setStepSound(Block.soundTypeGravel);
		this.setTickRandomly(true);
		this.setBlockTextureName("TwilightForest:uberous_soil");
		this.setCreativeTab(TFItems.creativeTab);
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
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Blocks.dirt.getItemDropped(0, p_149650_2_, p_149650_3_);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		Material aboveMaterial = world.getBlock(x, y + 1, z).getMaterial();
		if (aboveMaterial.isSolid())
			world.setBlock(x, y, z, Blocks.dirt);

	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);
		return plantType == EnumPlantType.Crop || plantType == EnumPlantType.Plains || plantType == EnumPlantType.Cave;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		Block above = world.getBlock(x, y + 1, z);
		Material aboveMaterial = above.getMaterial();
		if (aboveMaterial.isSolid())
			world.setBlock(x, y, z, Blocks.dirt);

		if (above instanceof IPlantable)
		{
			IPlantable plant = (IPlantable) above;
			if (plant.getPlantType(world, x, y + 1, z) == EnumPlantType.Crop)
				world.setBlock(x, y, z, Blocks.farmland, 2, 2);
			else if (plant.getPlantType(world, x, y + 1, z) == EnumPlantType.Plains)
				world.setBlock(x, y, z, Blocks.grass);
			else
				world.setBlock(x, y, z, Blocks.dirt);

			/* TODO gamerforEA code replace, old code:
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, null);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, null);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, null);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, null); */
			EntityPlayer player = ModUtils.getModFake(world);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, player);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, player);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, player);
			ItemDye.applyBonemeal(new ItemStack(Items.dye), world, x, y + 1, z, player);
			// TODO gamerforEA code end

			if (!world.isRemote)
				world.playAuxSFX(2005, x, y + 1, z, 0);
		}

	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean var5)
	{
		return true;
	}

	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z)
	{
		return true;
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z)
	{
		int gx = x;
		int gz = z;
		if (rand.nextBoolean())
			gx = x + (rand.nextBoolean() ? 1 : -1);
		else
			gz = z + (rand.nextBoolean() ? 1 : -1);

		Block blockAt = world.getBlock(gx, y, gz);
		if (world.isAirBlock(gx, y + 1, gz) && (blockAt == Blocks.dirt || blockAt == Blocks.grass || blockAt == Blocks.farmland))
			world.setBlock(gx, y, gz, this);

	}
}
