package twilightforest.item;

import com.gamerforea.eventhelper.util.EventUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;

import java.util.Random;

public class ItemBlockTFHugeLilyPad extends ItemLilyPad
{
	Random rand = new Random();

	public ItemBlockTFHugeLilyPad(Block block)
	{
		super(block);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
		if (mop == null)
			return itemStack;
		else
		{
			if (mop.typeOfHit == MovingObjectType.BLOCK)
			{
				int x = mop.blockX;
				int y = mop.blockY;
				int z = mop.blockZ;
				int bx = x >> 1 << 1;
				int bz = z >> 1 << 1;
				if (this.canPlacePadOn(itemStack, world, player, bx, y, bz) && this.canPlacePadOn(itemStack, world, player, bx + 1, y, bz) && this.canPlacePadOn(itemStack, world, player, bx, y, bz + 1) && this.canPlacePadOn(itemStack, world, player, bx + 1, y, bz + 1))
				{
					this.rand.setSeed(8890919293L);
					this.rand.setSeed(bx * this.rand.nextLong() ^ bz * this.rand.nextLong() ^ 8890919293L);
					int orient = this.rand.nextInt(4) << 2;

					// TODO gamerofrEA code start
					if (EventUtils.cantBreak(player, bx, y + 1, bz))
						return itemStack;
					if (EventUtils.cantBreak(player, bx + 1, y + 1, bz))
						return itemStack;
					if (EventUtils.cantBreak(player, bx + 1, y + 1, bz + 1))
						return itemStack;
					if (EventUtils.cantBreak(player, bx, y + 1, bz + 1))
						return itemStack;
					// TODO gamerforEA code end

					world.setBlock(bx, y + 1, bz, TFBlocks.hugeLilyPad, 0 | orient, 2);
					world.setBlock(bx + 1, y + 1, bz, TFBlocks.hugeLilyPad, 1 | orient, 2);
					world.setBlock(bx + 1, y + 1, bz + 1, TFBlocks.hugeLilyPad, 2 | orient, 2);
					world.setBlock(bx, y + 1, bz + 1, TFBlocks.hugeLilyPad, 3 | orient, 2);
					if (!player.capabilities.isCreativeMode)
						--itemStack.stackSize;
				}
			}

			return itemStack;
		}
	}

	public boolean canPlacePadOn(ItemStack itemStack, World world, EntityPlayer player, int x, int y, int z)
	{
		return world.canMineBlock(player, x, y, z) && player.canPlayerEdit(x, y, z, 1, itemStack) && world.getBlock(x, y, z).getMaterial() == Material.water && world.getBlockMetadata(x, y, z) == 0 && world.isAirBlock(x, y + 1, z);
	}
}
