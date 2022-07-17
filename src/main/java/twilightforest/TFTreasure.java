package twilightforest;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.EventConfig;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

import javax.annotation.Nullable;
import java.util.Random;

public class TFTreasure
{
	int type;
	Random treasureRNG;
	protected TFTreasureTable useless;
	protected TFTreasureTable common;
	protected TFTreasureTable uncommon;
	protected TFTreasureTable rare;
	protected TFTreasureTable ultrarare;
	public static TFTreasure hill1 = new TFTreasure(1);
	public static TFTreasure hill2 = new TFTreasure(2);
	public static TFTreasure hill3 = new TFTreasure(3);
	public static TFTreasure hedgemaze = new TFTreasure(4);
	public static TFTreasure labyrinth_room = new TFTreasure(5);
	public static TFTreasure labyrinth_deadend = new TFTreasure(6);
	public static TFTreasure tower_room = new TFTreasure(7);
	public static TFTreasure tower_library = new TFTreasure(8);
	public static TFTreasure basement = new TFTreasure(9);
	public static TFTreasure labyrinth_vault = new TFTreasure(10);
	public static TFTreasure darktower_cache = new TFTreasure(11);
	public static TFTreasure darktower_key = new TFTreasure(12);
	public static TFTreasure darktower_boss = new TFTreasure(13);
	public static TFTreasure tree_cache = new TFTreasure(14);
	public static TFTreasure stronghold_cache = new TFTreasure(15);
	public static TFTreasure stronghold_room = new TFTreasure(16);
	public static TFTreasure stronghold_boss = new TFTreasure(17);
	public static TFTreasure aurora_cache = new TFTreasure(18);
	public static TFTreasure aurora_room = new TFTreasure(19);
	public static TFTreasure aurora_boss = new TFTreasure(20);
	public static TFTreasure troll_garden = new TFTreasure(21);
	public static TFTreasure troll_vault = new TFTreasure(22);

	public TFTreasure(int i)
	{
		this.type = i;
		this.useless = new TFTreasureTable();
		this.common = new TFTreasureTable();
		this.uncommon = new TFTreasureTable();
		this.rare = new TFTreasureTable();
		this.ultrarare = new TFTreasureTable();
		this.treasureRNG = new Random();
		this.fill(i);
	}

	// TODO gamerforEA code start
	public boolean generate(World world, Random rand, int cx, int cy, int cz)
	{
		return this.generate(world, rand, cx, cy, cz, (EntityLivingBase) null);
	}

	public boolean generate(World world, Random rand, int cx, int cy, int cz, Block chestBlock)
	{
		return this.generate(world, rand, cx, cy, cz, chestBlock, null);
	}
	// TODO gamerforEA code end

	// TODO gamerforEA code add victim:EntityLivingBase
	public boolean generate(World world, Random rand, int cx, int cy, int cz, @Nullable EntityLivingBase victim)
	{
		// TODO gamerforEA code add victim:EntityLivingBase
		return this.generate(world, rand, cx, cy, cz, Blocks.chest, victim);
	}

	public boolean generate(World world, Random rand, int cx, int cy, int cz, Block chestBlock,
							@Nullable EntityLivingBase victim)
	{
		boolean flag = true;
		this.treasureRNG.setSeed(world.getSeed() * (long) cx + (long) cy ^ (long) cz);

		// TODO gamerforEA code start
		boolean doDrop = false;

		if (EventConfig.fixLootChestGrief && victim != null)
		{
			EntityLivingBase attacker = victim.func_94060_bK();
			if (attacker instanceof EntityPlayer && EventUtils.cantBreak((EntityPlayer) attacker, cx, cy, cz))
				doDrop = true;
		}

		if (!doDrop)
			// TODO gamerforEA code end
			world.setBlock(cx, cy, cz, chestBlock, 0, 2);

		for (int i = 0; i < 4; ++i)
		{
			ItemStack stack = this.getCommonItem(this.treasureRNG);

			// TODO gamerforEA code start
			if (doDrop)
				victim.entityDropItem(stack, 0);
			else
				// TODO gamerforEA code end
				flag &= this.addItemToChest(world, this.treasureRNG, cx, cy, cz, stack);
		}

		for (int i = 0; i < 2; ++i)
		{
			ItemStack stack = this.getUncommonItem(this.treasureRNG);

			// TODO gamerforEA code start
			if (doDrop)
				victim.entityDropItem(stack, 0);
			else
				// TODO gamerforEA code end
				flag &= this.addItemToChest(world, this.treasureRNG, cx, cy, cz, stack);
		}

		for (int i = 0; i < 1; ++i)
		{
			ItemStack stack = this.getRareItem(this.treasureRNG);

			// TODO gamerforEA code start
			if (doDrop)
				victim.entityDropItem(stack, 0);
			else
				// TODO gamerforEA code end
				flag &= this.addItemToChest(world, this.treasureRNG, cx, cy, cz, stack);
		}

		return flag;
	}

	public ItemStack getCommonItem(Random rand)
	{
		return !this.useless.isEmpty() && rand.nextInt(4) == 0 ? this.useless.getRandomItem(rand) : this.common.getRandomItem(rand);
	}

	public ItemStack getUncommonItem(Random rand)
	{
		return this.uncommon.getRandomItem(rand);
	}

	public ItemStack getRareItem(Random rand)
	{
		return !this.ultrarare.isEmpty() && rand.nextInt(4) == 0 ? this.ultrarare.getRandomItem(rand) : this.rare.getRandomItem(rand);
	}

	protected boolean addItemToChest(World world, Random rand, int cx, int cy, int cz, ItemStack itemStack)
	{
		TileEntityChest chest = (TileEntityChest) world.getTileEntity(cx, cy, cz);
		if (chest != null)
		{
			int slot = this.findRandomInventorySlot(chest, rand);
			if (slot != -1)
			{
				chest.setInventorySlotContents(slot, itemStack);
				return true;
			}
		}

		return false;
	}

	protected int findRandomInventorySlot(TileEntityChest chest, Random rand)
	{
		for (int i = 0; i < 100; ++i)
		{
			int slot = rand.nextInt(chest.getSizeInventory());
			if (chest.getStackInSlot(slot) == null)
				return slot;
		}

		return -1;
	}

	protected void fill(int i)
	{
		this.useless.add(Blocks.red_flower, 4);
		this.useless.add(Blocks.yellow_flower, 4);
		this.useless.add(Items.feather, 3);
		this.useless.add(Items.wheat_seeds, 2);
		this.useless.add(Items.flint, 2);
		this.useless.add(Blocks.cactus, 2);
		this.useless.add(Items.reeds, 4);
		this.useless.add(Blocks.sand, 4);
		this.useless.add(Items.flower_pot, 1);
		this.useless.add(new ItemStack(Items.dye, 1, 0));
		if (i == 1)
		{
			this.common.add(Items.iron_ingot, 4);
			this.common.add(Items.wheat, 4);
			this.common.add(Items.string, 4);
			this.common.add(Items.bucket, 1);
			this.uncommon.add(Items.bread, 1);
			this.uncommon.add(TFItems.oreMagnet, 1);
			this.uncommon.add(Items.gunpowder, 4);
			this.uncommon.add(Items.arrow, 12);
			this.uncommon.add(Blocks.torch, 12);
			this.rare.add(Items.gold_ingot, 3);
			this.rare.add(Items.iron_pickaxe, 1);
			this.rare.add(TFItems.liveRoot, 3);
			this.ultrarare.add(TFItems.transformPowder, 12);
			this.ultrarare.add(Items.diamond, 1);
			this.ultrarare.add(TFItems.steeleafIngot, 3);
		}

		if (i == 2)
		{
			this.common.add(Items.iron_ingot, 4);
			this.common.add(Items.carrot, 4);
			this.common.add(Blocks.ladder, 6);
			this.common.add(Items.bucket, 1);
			this.uncommon.add(Items.baked_potato, 2);
			this.uncommon.add(TFItems.oreMagnet, 1);
			this.uncommon.add(TFItems.ironwoodIngot, 4);
			this.uncommon.add(Items.arrow, 12);
			this.uncommon.add(Blocks.torch, 12);
			this.rare.add(TFItems.nagaScale, 1);
			this.rare.add(TFBlocks.uncraftingTable, 1);
			this.rare.add(TFItems.transformPowder, 12);
			this.ultrarare.add(TFItems.peacockFan, 1);
			this.ultrarare.add(Items.emerald, 6);
			this.ultrarare.add(Items.diamond, 1);
			this.ultrarare.add(TFItems.charmOfLife1, 1);
		}

		if (i == 3)
		{
			this.common.add(Items.gold_nugget, 9);
			this.common.add(Items.potato, 4);
			this.common.add(Items.fish, 4);
			this.common.add(TFItems.torchberries, 5);
			this.uncommon.add(Items.pumpkin_pie, 1);
			this.uncommon.add(TFItems.oreMagnet, 1);
			this.uncommon.add(Items.gunpowder, 4);
			this.uncommon.add(Items.arrow, 12);
			this.uncommon.add(Blocks.torch, 12);
			this.uncommon.add(TFItems.steeleafIngot, 4);
			this.rare.add(TFItems.nagaScale, 1);
			this.rare.addEnchanted(new ItemStack(TFItems.ironwoodPick, 1), Enchantment.efficiency, 1, Enchantment.fortune, 1);
			this.rare.add(TFItems.transformPowder, 12);
			this.ultrarare.add(TFItems.moonwormQueen, 1);
			this.ultrarare.add(TFBlocks.sapling, 1, 4);
			this.ultrarare.add(Items.diamond, 2);
			this.ultrarare.add(TFItems.charmOfLife1, 1);
			this.ultrarare.add(TFItems.charmOfKeeping1, 1);
		}

		if (i == 4)
		{
			this.common.add(Blocks.planks, 4);
			this.common.add(Blocks.brown_mushroom, 4);
			this.common.add(Blocks.red_mushroom, 4);
			this.common.add(Items.wheat, 4);
			this.common.add(Items.string, 4);
			this.common.add(Items.stick, 6);
			this.uncommon.add(Items.melon, 4);
			this.uncommon.add(Items.melon_seeds, 4);
			this.uncommon.add(Items.pumpkin_seeds, 4);
			this.uncommon.add(Items.arrow, 12);
			this.uncommon.add(TFBlocks.firefly, 4);
			this.rare.add(Blocks.web, 3);
			this.rare.add(Items.shears, 1);
			this.rare.add(Items.saddle, 1);
			this.rare.add(Items.bow, 1);
			this.rare.add(Items.apple, 2);
			this.ultrarare.add(Items.diamond_hoe, 1);
			this.ultrarare.add(Items.diamond, 1);
			this.ultrarare.add(Items.mushroom_stew, 1);
			this.ultrarare.add(Items.golden_apple, 1);
		}

		if (i == 5)
		{
			this.useless.clear();
			this.common.add(Items.iron_ingot, 4);
			this.common.add(TFItems.mazeWafer, 12);
			this.common.add(Items.gunpowder, 4);
			this.common.add(TFItems.ironwoodIngot, 4);
			this.common.add(TFBlocks.firefly, 5);
			this.common.add(Items.milk_bucket, 1);
			this.uncommon.add(TFItems.steeleafIngot, 6);
			this.uncommon.add(TFItems.steeleafLegs, 1);
			this.uncommon.add(TFItems.steeleafPlate, 1);
			this.uncommon.add(TFItems.steeleafHelm, 1);
			this.uncommon.add(TFItems.steeleafBoots, 1);
			this.uncommon.add(TFItems.steeleafPick, 1);
			this.uncommon.add(TFItems.ironwoodPlate, 1);
			this.uncommon.add(TFItems.ironwoodSword, 1);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.rare.add(TFItems.mazeMapFocus, 1);
			this.rare.add(Blocks.tnt, 3);
			this.rare.add(new ItemStack(Items.potionitem, 1, 16373));
		}

		if (i == 6)
		{
			this.common.add(Items.stick, 12);
			this.common.add(Items.coal, 12);
			this.common.add(Items.arrow, 12);
			this.common.add(TFItems.mazeWafer, 9);
			this.common.add(Items.paper, 12);
			this.common.add(Items.leather, 4);
			this.common.add(Items.mushroom_stew, 1);
			this.uncommon.add(Items.milk_bucket, 1);
			this.uncommon.add(Items.paper, 5);
			this.uncommon.add(Items.iron_ingot, 6);
			this.uncommon.add(TFItems.ironwoodIngot, 8);
			this.uncommon.add(TFBlocks.firefly, 5);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.rare.add(TFItems.steeleafIngot, 8);
			this.rare.add(Items.golden_apple, 1);
			this.rare.add(Items.blaze_rod, 2);
		}

		if (i == 10)
		{
			this.useless.clear();
			this.common.add(Items.iron_ingot, 9);
			this.common.add(Items.emerald, 5);
			this.common.add(TFItems.mazeWafer, 12);
			this.common.add(TFItems.ironwoodIngot, 9);
			this.common.add(new ItemStack(Items.potionitem, 1, 16369));
			this.common.add(new ItemStack(Items.potionitem, 1, 16373));
			this.common.add(new ItemStack(Items.potionitem, 1, 16370));
			this.uncommon.addEnchanted(new ItemStack(Items.bow), Enchantment.infinity, 1, Enchantment.punch, 2);
			this.uncommon.addEnchanted(new ItemStack(Items.bow), Enchantment.power, 3, Enchantment.flame, 1);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafShovel), Enchantment.efficiency, 4, Enchantment.unbreaking, 2);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafAxe), Enchantment.efficiency, 5);
			this.uncommon.add(TFItems.steeleafIngot, 12);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafPlate), Enchantment.protection, 3);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafLegs), Enchantment.fireProtection, 4);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafBoots), Enchantment.protection, 2);
			this.uncommon.addEnchanted(new ItemStack(TFItems.steeleafHelm), Enchantment.respiration, 3);
			this.rare.add(Blocks.emerald_block, 1);
			this.rare.add(Blocks.ender_chest, 1);
			this.rare.addEnchanted(new ItemStack(TFItems.steeleafPick), Enchantment.efficiency, 4, Enchantment.silkTouch, 1);
			this.rare.addEnchanted(new ItemStack(TFItems.steeleafSword), Enchantment.sharpness, 4, Enchantment.knockback, 2);
			this.rare.addEnchanted(new ItemStack(TFItems.steeleafSword), Enchantment.baneOfArthropods, 5, Enchantment.fireAspect, 2);
			this.rare.addEnchanted(new ItemStack(TFItems.mazebreakerPick), Enchantment.efficiency, 4, Enchantment.unbreaking, 3, Enchantment.fortune, 2);
		}

		if (i == 7)
		{
			this.common.add(Items.glass_bottle, 6);
			this.common.add(new ItemStack(Items.potionitem, 1, 0));
			this.common.add(Items.sugar, 5);
			this.common.add(Items.spider_eye, 3);
			this.common.add(Items.ghast_tear, 1);
			this.common.add(Items.magma_cream, 2);
			this.common.add(Items.fermented_spider_eye, 1);
			this.common.add(Items.speckled_melon, 2);
			this.common.add(Items.blaze_powder, 3);
			this.common.add(Items.paper, 6);
			this.uncommon.addRandomEnchanted(Items.golden_sword, 10);
			this.uncommon.addRandomEnchanted(Items.golden_boots, 7);
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16274));
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16341));
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16307));
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16348));
			this.rare.addRandomEnchanted(Items.golden_helmet, 18);
			this.rare.add(new ItemStack(Items.potionitem, 1, 16306));
			this.rare.add(new ItemStack(Items.potionitem, 1, 16305));
			this.rare.add(new ItemStack(Items.potionitem, 1, 32725));
			this.rare.add(new ItemStack(Items.potionitem, 1, 32764));
			this.rare.add(TFItems.transformPowder, 12);
			this.rare.add(TFItems.charmOfLife1, 1);
			this.rare.add(TFItems.charmOfKeeping1, 1);
			this.ultrarare.addRandomEnchanted(Items.golden_axe, 20);
			this.ultrarare.add(Items.ender_pearl, 1);
			this.ultrarare.add(Blocks.obsidian, 4);
			this.ultrarare.add(Items.diamond, 1);
			this.ultrarare.add(TFItems.moonwormQueen, 1);
			this.ultrarare.add(TFItems.peacockFan, 1);
		}

		if (i == 8)
		{
			this.common.add(Items.glass_bottle, 6);
			this.common.add(new ItemStack(Items.potionitem, 1, 0));
			this.common.add(Blocks.ladder, 6);
			this.common.add(Items.paper, 6);
			this.common.add(Items.bone, 6);
			this.common.add(Items.gold_nugget, 6);
			this.common.add(Items.clay_ball, 12);
			this.uncommon.addRandomEnchanted(Items.iron_leggings, 5);
			this.uncommon.add(Items.fire_charge, 3);
			this.uncommon.add(Items.book, 5);
			this.uncommon.add(Items.map, 1);
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16));
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16276));
			this.uncommon.add(new ItemStack(Items.potionitem, 1, 16312));
			this.rare.addRandomEnchanted(Items.bow, 5);
			this.rare.addRandomEnchanted(Items.stone_sword, 10);
			this.rare.addRandomEnchanted(Items.wooden_sword, 15);
			this.rare.add(new ItemStack(Items.potionitem, 1, 32696));
			this.rare.add(new ItemStack(Items.potionitem, 1, 16369));
			this.rare.add(new ItemStack(Items.potionitem, 1, 16373));
			this.rare.add(new ItemStack(Items.potionitem, 1, 16370));
			this.rare.add(TFItems.transformPowder, 12);
			this.rare.add(TFItems.charmOfKeeping1, 1);
			this.ultrarare.addRandomEnchanted(Items.golden_pickaxe, 10);
			this.ultrarare.addRandomEnchanted(Items.iron_sword, 20);
			this.ultrarare.addRandomEnchanted(Items.bow, 30);
			this.ultrarare.add(Blocks.bookshelf, 5);
			this.ultrarare.add(Items.ender_pearl, 2);
			this.ultrarare.add(Items.experience_bottle, 6);
		}

		if (i == 9)
		{
			this.common.add(new ItemStack(Items.potionitem, 1, 0));
			this.common.add(Items.rotten_flesh, 6);
			this.common.add(Items.poisonous_potato, 2);
			this.common.add(Items.wheat, 6);
			this.common.add(Items.potato, 6);
			this.common.add(Items.carrot, 6);
			this.common.add(Items.melon, 6);
			this.common.add(Items.water_bucket, 1);
			this.common.add(Blocks.torch, 12);
			this.common.add(Items.mushroom_stew, 1);
			this.common.add(Items.milk_bucket, 1);
			this.common.add(Items.melon_seeds, 5);
			this.uncommon.add(Items.bread, 8);
			this.uncommon.add(Items.cooked_beef, 6);
			this.uncommon.add(Items.cooked_porkchop, 8);
			this.uncommon.add(Items.baked_potato, 8);
			this.uncommon.add(Items.cooked_chicken, 10);
			this.uncommon.add(Items.cooked_fished, 8);
			this.rare.add(Items.speckled_melon, 12);
			this.rare.add(Items.apple, 12);
			this.rare.add(Items.map, 1);
			this.rare.add(TFItems.charmOfKeeping1, 1);
			this.ultrarare.add(Items.golden_apple, 2);
			this.ultrarare.add(Items.golden_carrot, 2);
			this.ultrarare.add(Items.cake, 1);
			this.ultrarare.add(Items.boat, 1);
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 4));
		}

		if (i == 11)
		{
			this.common.add(Items.stick, 12);
			this.common.add(new ItemStack(Items.coal, 12, 1));
			this.common.add(Items.arrow, 12);
			this.common.add(TFItems.experiment115, 9);
			this.common.add(new ItemStack(Blocks.wool, 1, 14));
			this.common.add(Items.redstone, 6);
			this.uncommon.add(Blocks.redstone_lamp, 3);
			this.uncommon.add(Items.iron_ingot, 6);
			this.uncommon.add(TFItems.ironwoodIngot, 8);
			this.uncommon.add(TFBlocks.firefly, 5);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.rare.add(TFItems.steeleafIngot, 8);
			this.rare.add(Items.diamond, 2);
		}

		if (i == 12)
		{
			this.useless.clear();
			this.common.add(Items.iron_ingot, 4);
			this.common.add(TFItems.experiment115, 12);
			this.common.add(Items.gunpowder, 4);
			this.common.add(TFItems.ironwoodIngot, 4);
			this.common.add(TFBlocks.firefly, 5);
			this.common.add(Items.redstone, 12);
			this.common.add(Items.glowstone_dust, 12);
			this.uncommon.add(TFItems.steeleafIngot, 6);
			this.uncommon.add(TFItems.steeleafLegs, 1);
			this.uncommon.add(TFItems.steeleafPlate, 1);
			this.uncommon.add(TFItems.steeleafHelm, 1);
			this.uncommon.add(TFItems.steeleafBoots, 1);
			this.uncommon.add(TFItems.steeleafPick, 1);
			this.uncommon.add(TFItems.ironwoodPlate, 1);
			this.uncommon.add(TFItems.ironwoodSword, 1);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.rare.add(TFItems.charmOfLife1, 1);
			this.rare.addEnchantedBook(Enchantment.featherFalling, 3);
			this.rare.addEnchantedBook(Enchantment.knockback, 2);
			this.rare.addEnchantedBook(Enchantment.efficiency, 3);
		}

		if (i == 13)
		{
			this.useless.clear();
			this.common.add(TFItems.carminite, 3);
			this.uncommon.add(TFItems.fieryTears, 5);
			this.rare.add(new ItemStack(TFItems.trophy, 1, 3));
		}

		if (i == 14)
		{
			this.common.add(Items.poisonous_potato, 2);
			this.common.add(Items.wheat, 6);
			this.common.add(Items.potato, 6);
			this.common.add(Items.carrot, 6);
			this.common.add(Items.melon, 6);
			this.common.add(Items.water_bucket, 1);
			this.common.add(Items.milk_bucket, 1);
			this.common.add(Items.melon_seeds, 5);
			this.uncommon.add(new ItemStack(TFBlocks.firefly, 12));
			this.uncommon.add(new ItemStack(TFBlocks.sapling, 4, 0));
			this.uncommon.add(new ItemStack(TFBlocks.sapling, 4, 1));
			this.uncommon.add(new ItemStack(TFBlocks.sapling, 4, 2));
			this.uncommon.add(new ItemStack(TFBlocks.sapling, 4, 3));
			this.rare.add(Items.pumpkin_pie, 12);
			this.rare.add(Items.apple, 12);
			this.rare.add(TFItems.charmOfLife1, 1);
			this.rare.add(TFItems.charmOfKeeping1, 1);
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 4));
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 5));
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 6));
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 7));
			this.ultrarare.add(new ItemStack(TFBlocks.sapling, 1, 8));
		}

		if (i == 15)
		{
			this.common.add(Items.stick, 12);
			this.common.add(new ItemStack(Items.coal, 12));
			this.common.add(Items.arrow, 12);
			this.common.add(TFItems.mazeWafer, 9);
			this.common.add(new ItemStack(Blocks.wool, 1, 11));
			this.common.add(Items.iron_ingot, 2);
			this.uncommon.add(Items.bucket, 1);
			this.uncommon.add(Items.iron_ingot, 6);
			this.uncommon.add(TFItems.ironwoodIngot, 6);
			this.uncommon.add(TFBlocks.firefly, 5);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.uncommon.add(TFItems.armorShard, 3);
			this.rare.add(TFItems.knightMetal, 8);
			this.rare.addRandomEnchanted(Items.bow, 20);
			this.rare.addRandomEnchanted(Items.iron_sword, 20);
			this.rare.addRandomEnchanted(TFItems.ironwoodSword, 15);
			this.rare.addRandomEnchanted(TFItems.steeleafSword, 10);
			this.ultrarare.addEnchantedBook(Enchantment.baneOfArthropods, 4);
			this.ultrarare.addEnchantedBook(Enchantment.sharpness, 4);
			this.ultrarare.addEnchantedBook(Enchantment.smite, 4);
			this.ultrarare.addEnchantedBook(Enchantment.unbreaking, 2);
			this.ultrarare.addEnchantedBook(Enchantment.unbreaking, 2);
			this.ultrarare.addEnchantedBook(Enchantment.protection, 3);
			this.ultrarare.addEnchantedBook(Enchantment.projectileProtection, 3);
			this.ultrarare.addEnchantedBook(Enchantment.featherFalling, 3);
		}

		if (i == 16)
		{
			this.useless.clear();
			this.common.add(Items.iron_ingot, 4);
			this.common.add(TFItems.mazeWafer, 12);
			this.common.add(Items.gunpowder, 4);
			this.common.add(TFItems.ironwoodIngot, 4);
			this.common.add(TFBlocks.firefly, 5);
			this.common.add(Items.milk_bucket, 1);
			this.uncommon.add(TFItems.steeleafIngot, 6);
			this.uncommon.add(TFItems.steeleafLegs, 1);
			this.uncommon.add(TFItems.steeleafPlate, 1);
			this.uncommon.add(TFItems.steeleafHelm, 1);
			this.uncommon.add(TFItems.steeleafBoots, 1);
			this.uncommon.add(TFItems.steeleafPick, 1);
			this.uncommon.add(TFItems.ironwoodPlate, 1);
			this.uncommon.add(TFItems.ironwoodSword, 1);
			this.uncommon.add(TFItems.charmOfLife1, 1);
			this.rare.add(TFItems.mazeMapFocus, 1);
			this.rare.addRandomEnchanted(Items.bow, 30);
			this.rare.addRandomEnchanted(Items.iron_sword, 30);
			this.rare.addRandomEnchanted(TFItems.ironwoodSword, 25);
			this.rare.addRandomEnchanted(TFItems.steeleafSword, 20);
			this.rare.addRandomEnchanted(Items.diamond_sword, 15);
		}

		if (i == 17)
		{
			this.useless.clear();
			this.common.addRandomEnchanted(TFItems.knightlySword, 20);
			this.common.addRandomEnchanted(TFItems.knightlyPick, 20);
			this.common.addRandomEnchanted(TFItems.knightlyAxe, 20);
			this.uncommon.addRandomEnchanted(TFItems.phantomHelm, 20);
			this.uncommon.addRandomEnchanted(TFItems.phantomPlate, 20);
			this.rare.addRandomEnchanted(TFItems.phantomHelm, 30);
			this.rare.addRandomEnchanted(TFItems.phantomPlate, 30);
		}

		if (i == 18)
		{
			this.common.add(Items.stick, 12);
			this.common.add(new ItemStack(Items.coal, 12));
			this.common.add(Items.arrow, 12);
			this.common.add(TFItems.mazeWafer, 9);
			this.common.add(Blocks.ice, 4);
			this.common.add(Blocks.packed_ice, 4);
			this.common.add(TFItems.ironwoodIngot, 2);
			this.uncommon.add(TFBlocks.auroraBlock, 12);
			this.uncommon.add(TFItems.ironwoodIngot, 6);
			this.uncommon.add(TFBlocks.firefly, 5);
			this.uncommon.add(TFItems.charmOfKeeping1, 1);
			this.uncommon.add(TFItems.arcticFur, 3);
			this.rare.add(TFItems.arcticFur, 8);
			this.rare.add(TFItems.iceBow, 1);
			this.rare.add(TFItems.enderBow, 1);
			this.rare.add(TFItems.iceSword, 1);
			this.ultrarare.addEnchantedBook(Enchantment.sharpness, 4);
			this.ultrarare.addEnchantedBook(Enchantment.power, 4);
			this.ultrarare.addEnchantedBook(Enchantment.punch, 2);
			this.ultrarare.addEnchantedBook(Enchantment.unbreaking, 2);
			this.ultrarare.addEnchantedBook(Enchantment.unbreaking, 2);
			this.ultrarare.addEnchantedBook(Enchantment.infinity, 1);
		}

		if (i == 19)
		{
			this.useless.clear();
			this.common.add(Blocks.ice, 4);
			this.common.add(Blocks.packed_ice, 4);
			this.common.add(TFItems.mazeWafer, 12);
			this.common.add(TFItems.iceBomb, 4);
			this.common.add(TFBlocks.firefly, 5);
			this.uncommon.add(TFItems.arcticFur, 6);
			this.uncommon.add(TFItems.arcticLegs, 1);
			this.uncommon.add(TFItems.arcticPlate, 1);
			this.uncommon.add(TFItems.arcticHelm, 1);
			this.uncommon.add(TFItems.arcticBoots, 1);
			this.uncommon.add(TFItems.knightlyPlate, 1);
			this.uncommon.add(TFItems.knightlySword, 1);
			this.uncommon.add(TFItems.charmOfLife1, 1);
			this.rare.addRandomEnchanted(TFItems.iceBow, 30);
			this.rare.addRandomEnchanted(TFItems.enderBow, 5);
			this.rare.addRandomEnchanted(TFItems.iceSword, 25);
			this.rare.addRandomEnchanted(TFItems.glassSword, 20);
		}

		if (i == 21)
		{
			this.useless.clear();
			this.common.add(Blocks.red_mushroom, 4);
			this.common.add(Blocks.brown_mushroom, 4);
			this.common.add(Items.wheat_seeds, 6);
			this.common.add(Items.carrot, 6);
			this.common.add(Items.potato, 6);
			this.common.add(Items.melon_seeds, 6);
			this.common.add(new ItemStack(Items.dye, 12, 15));
			this.uncommon.add(TFBlocks.uberousSoil, 6);
			this.rare.add(TFItems.magicBeans, 1);
		}

		if (i == 22)
		{
			this.useless.clear();
			this.useless.clear();
			this.common.add(Items.coal, 32);
			this.common.add(TFItems.torchberries, 16);
			this.common.add(Items.emerald, 6);
			this.uncommon.add(TFBlocks.trollSteinn, 6);
			this.uncommon.add(Blocks.obsidian, 6);
			this.rare.add(TFItems.lampOfCinders, 1);
		}

	}
}
