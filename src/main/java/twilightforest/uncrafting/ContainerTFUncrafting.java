package twilightforest.uncrafting;

import com.gamerforea.twilightforest.EventConfig;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import twilightforest.TwilightForestMod;

import java.util.ArrayList;
import java.util.Map;

public class ContainerTFUncrafting extends Container
{
	public InventoryTFGoblinUncrafting uncraftingMatrix = new InventoryTFGoblinUncrafting(this);
	public InventoryCrafting assemblyMatrix = new InventoryCrafting(this, 3, 3);
	public InventoryCrafting combineMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory tinkerInput = new InventoryTFGoblinInput(this);
	public IInventory tinkerResult = new InventoryCraftResult();
	private World worldObj;

	public ContainerTFUncrafting(InventoryPlayer inventory, World world, int x, int y, int z)
	{
		this.worldObj = world;
		this.addSlotToContainer(new Slot(this.tinkerInput, 0, 13, 35));
		this.addSlotToContainer(new SlotTFGoblinCraftResult(inventory.player, this.tinkerInput, this.uncraftingMatrix, this.assemblyMatrix, this.tinkerResult, 0, 147, 35));

		for (int invX = 0; invX < 3; ++invX)
		{
			for (int invY = 0; invY < 3; ++invY)
			{
				this.addSlotToContainer(new SlotTFGoblinUncrafting(inventory.player, this.tinkerInput, this.uncraftingMatrix, this.assemblyMatrix, invY + invX * 3, 300000 + invY * 18, 17 + invX * 18));
			}
		}

		for (int var8 = 0; var8 < 3; ++var8)
		{
			for (int invY = 0; invY < 3; ++invY)
			{
				this.addSlotToContainer(new SlotTFGoblinAssembly(inventory.player, this.tinkerInput, this.assemblyMatrix, this.uncraftingMatrix, invY + var8 * 3, 62 + invY * 18, 17 + var8 * 18));
			}
		}

		for (int var9 = 0; var9 < 3; ++var9)
		{
			for (int invY = 0; invY < 9; ++invY)
			{
				this.addSlotToContainer(new Slot(inventory, invY + var9 * 9 + 9, 8 + invY * 18, 84 + var9 * 18));
			}
		}

		for (int var10 = 0; var10 < 9; ++var10)
		{
			this.addSlotToContainer(new Slot(inventory, var10, 8 + var10 * 18, 142));
		}

		this.onCraftMatrixChanged(this.assemblyMatrix);
	}

	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory)
	{
		if (par1IInventory == this.tinkerInput)
		{
			ItemStack inputStack = this.tinkerInput.getStackInSlot(0);
			IRecipe recipe = this.getRecipeFor(inputStack);
			if (recipe != null)
			{
				int recipeWidth = this.getRecipeWidth(recipe);
				int recipeHeight = this.getRecipeHeight(recipe);
				ItemStack[] recipeItems = this.getRecipeItems(recipe);

				for (int i = 0; i < this.uncraftingMatrix.getSizeInventory(); ++i)
				{
					this.uncraftingMatrix.setInventorySlotContents(i, null);
				}

				for (int invY = 0; invY < recipeHeight; ++invY)
				{
					for (int invX = 0; invX < recipeWidth; ++invX)
					{
						ItemStack ingredient = ItemStack.copyItemStack(recipeItems[invX + invY * recipeWidth]);
						if (ingredient != null && ingredient.stackSize > 1)
							ingredient.stackSize = 1;

						if (ingredient != null && (ingredient.getItemDamageForDisplay() == -1 || ingredient.getItemDamageForDisplay() == 32767))
							ingredient.setItemDamage(0);

						this.uncraftingMatrix.setInventorySlotContents(invX + invY * 3, ingredient);
					}
				}

				if (inputStack.isItemDamaged())
				{
					int damagedParts = this.countDamagedParts(inputStack);

					for (int i = 0; i < 9 && damagedParts > 0; ++i)
					{
						if (this.isDamageableComponent(this.uncraftingMatrix.getStackInSlot(i)))
						{
							this.uncraftingMatrix.getStackInSlot(i).stackSize = 0;
							--damagedParts;
						}
					}
				}

				for (int i = 0; i < 9; ++i)
				{
					ItemStack ingredient = this.uncraftingMatrix.getStackInSlot(i);
					if (this.isIngredientProblematic(ingredient))
						ingredient.stackSize = 0;
				}

				this.uncraftingMatrix.numberOfInputItems = recipe.getRecipeOutput().stackSize;
				this.uncraftingMatrix.uncraftingCost = this.calculateUncraftingCost();
				this.uncraftingMatrix.recraftingCost = 0;
			}
			else
			{
				for (int i = 0; i < 9; ++i)
				{
					this.uncraftingMatrix.setInventorySlotContents(i, null);
				}

				this.uncraftingMatrix.numberOfInputItems = 0;
				this.uncraftingMatrix.uncraftingCost = 0;
			}
		}

		if (par1IInventory == this.assemblyMatrix || par1IInventory == this.tinkerInput)
			if (this.isMatrixEmpty(this.tinkerInput))
			{
				this.tinkerResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.assemblyMatrix, this.worldObj));
				this.uncraftingMatrix.recraftingCost = 0;
			}
			else
			{
				this.tinkerResult.setInventorySlotContents(0, null);
				this.uncraftingMatrix.uncraftingCost = this.calculateUncraftingCost();
				this.uncraftingMatrix.recraftingCost = 0;
			}

		if (par1IInventory != this.combineMatrix && !this.isMatrixEmpty(this.uncraftingMatrix) && !this.isMatrixEmpty(this.assemblyMatrix))
		{
			for (int i = 0; i < 9; ++i)
			{
				if (this.assemblyMatrix.getStackInSlot(i) != null)
					this.combineMatrix.setInventorySlotContents(i, this.assemblyMatrix.getStackInSlot(i));
				else if (this.uncraftingMatrix.getStackInSlot(i) != null && this.uncraftingMatrix.getStackInSlot(i).stackSize > 0)
					this.combineMatrix.setInventorySlotContents(i, this.uncraftingMatrix.getStackInSlot(i));
				else
					this.combineMatrix.setInventorySlotContents(i, null);
			}

			ItemStack result = CraftingManager.getInstance().findMatchingRecipe(this.combineMatrix, this.worldObj);
			ItemStack input = this.tinkerInput.getStackInSlot(0);
			if (result != null && this.isValidMatchForInput(input, result))
			{
				NBTTagCompound inputTags = input.getTagCompound();
				if (inputTags != null)
					inputTags = (NBTTagCompound) inputTags.copy();

				Map resultInnateEnchantments = null;
				if (result.isItemEnchanted())
					resultInnateEnchantments = EnchantmentHelper.getEnchantments(result);

				Map inputEnchantments = null;
				if (input.isItemEnchanted())
				{
					inputEnchantments = EnchantmentHelper.getEnchantments(input);

					for (Object key : inputEnchantments.keySet())
					{
						int enchID = (Integer) key;
						Enchantment ench = Enchantment.enchantmentsList[enchID];
						if (!ench.canApply(result))
							inputEnchantments.remove(key);
					}
				}

				if (inputTags != null)
				{
					inputTags.removeTag("ench");
					result.setTagCompound((NBTTagCompound) inputTags.copy());
					if (inputEnchantments != null)
						EnchantmentHelper.setEnchantments(inputEnchantments, result);
				}

				this.tinkerResult.setInventorySlotContents(0, result);
				this.uncraftingMatrix.uncraftingCost = 0;
				this.uncraftingMatrix.recraftingCost = this.calculateRecraftingCost();
				if (this.uncraftingMatrix.recraftingCost > 0 && !result.hasDisplayName())
					result.setRepairCost(input.getRepairCost() + 2);

				if (resultInnateEnchantments != null && resultInnateEnchantments.size() > 0)
					for (Object key : resultInnateEnchantments.keySet())
					{
						int enchID = (Integer) key;
						int level = (Integer) resultInnateEnchantments.get(key);
						Enchantment ench = Enchantment.enchantmentsList[enchID];
						if (EnchantmentHelper.getEnchantmentLevel(enchID, result) > level)
							level = EnchantmentHelper.getEnchantmentLevel(enchID, result);

						if (EnchantmentHelper.getEnchantmentLevel(enchID, result) < level)
							result.addEnchantment(ench, level);
					}
			}
		}

	}

	protected boolean isIngredientProblematic(ItemStack ingredient)
	{
		return ingredient != null && (ingredient.getItem().hasContainerItem(ingredient) || ingredient.getUnlocalizedName().contains("itemMatter"));
	}

	public IRecipe getRecipeFor(ItemStack inputStack)
	{
		if (inputStack != null)
		{
			// TODO gamerforEA code start
			if (EventConfig.uncraftingBlackList.contains(inputStack))
				return null;
			if (EventConfig.uncraftingCheckNbtIgnoreEnchantments)
			{
				inputStack = inputStack.copy();
				if (inputStack.hasTagCompound())
				{
					NBTTagCompound nbt = inputStack.getTagCompound();
					nbt.removeTag("ench");
					if (nbt.hasNoTags())
						inputStack.setTagCompound(null);
				}
			}
			// TODO gamerforEA code end

			for (IRecipe recipe : (Iterable<IRecipe>) CraftingManager.getInstance().getRecipeList())
			{
				/* TODO gamerforEA code replace, old code:
				if ((recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) && recipe.getRecipeOutput().getItem() == inputStack.getItem() && inputStack.stackSize >= recipe.getRecipeOutput().stackSize && (!recipe.getRecipeOutput().getHasSubtypes() || recipe.getRecipeOutput().getItemDamage() == inputStack.getItemDamage()))
					return recipe; */
				if (recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe)
				{
					ItemStack out = recipe.getRecipeOutput();
					if (out.getItem() == inputStack.getItem() && inputStack.stackSize >= out.stackSize && (!out.getHasSubtypes() || out.getItemDamage() == inputStack.getItemDamage()))
					{
						if (EventConfig.uncraftingCheckNbt)
						{
							ItemStack outCopy = out;

							if (EventConfig.uncraftingCheckNbtIgnoreEnchantments)
							{
								outCopy = outCopy.copy();
								if (outCopy.hasTagCompound())
								{
									NBTTagCompound nbt = outCopy.getTagCompound();
									nbt.removeTag("ench");
									if (nbt.hasNoTags())
										outCopy.setTagCompound(null);
								}
							}

							if (!ItemStack.areItemStackTagsEqual(inputStack, outCopy))
								continue;
						}
						return recipe;
					}
				}
				// TODO gamerforEA code end
			}
		}

		return null;
	}

	public boolean isValidMatchForInput(ItemStack inputStack, ItemStack resultStack)
	{
		if (inputStack.getItem() instanceof ItemPickaxe && resultStack.getItem() instanceof ItemPickaxe)
			return true;
		else if (inputStack.getItem() instanceof ItemAxe && resultStack.getItem() instanceof ItemAxe)
			return true;
		else if (inputStack.getItem() instanceof ItemSpade && resultStack.getItem() instanceof ItemSpade)
			return true;
		else if (inputStack.getItem() instanceof ItemHoe && resultStack.getItem() instanceof ItemHoe)
			return true;
		else if (inputStack.getItem() instanceof ItemSword && resultStack.getItem() instanceof ItemSword)
			return true;
		else if (inputStack.getItem() instanceof ItemBow && resultStack.getItem() instanceof ItemBow)
			return true;
		else if (inputStack.getItem() instanceof ItemArmor && resultStack.getItem() instanceof ItemArmor)
		{
			ItemArmor inputArmor = (ItemArmor) inputStack.getItem();
			ItemArmor resultArmor = (ItemArmor) resultStack.getItem();
			return inputArmor.armorType == resultArmor.armorType;
		}
		else
			return false;
	}

	public int getUncraftingCost()
	{
		return this.uncraftingMatrix.uncraftingCost;
	}

	public int getRecraftingCost()
	{
		return this.uncraftingMatrix.recraftingCost;
	}

	public int calculateUncraftingCost()
	{
		return !this.isMatrixEmpty(this.assemblyMatrix) ? 0 : this.countDamageableParts(this.uncraftingMatrix);
	}

	public int calculateRecraftingCost()
	{
		if (this.tinkerInput.getStackInSlot(0) != null && this.tinkerInput.getStackInSlot(0).isItemEnchanted() && this.tinkerResult.getStackInSlot(0) != null)
		{
			ItemStack input = this.tinkerInput.getStackInSlot(0);
			ItemStack output = this.tinkerResult.getStackInSlot(0);
			int cost = 0;
			cost = cost + input.getRepairCost();
			int enchantCost = this.countTotalEnchantmentCost(input);
			cost = cost + enchantCost;
			int damagedCost = (1 + this.countDamagedParts(input)) * EnchantmentHelper.getEnchantments(output).size();
			cost = cost + damagedCost;
			int enchantabilityDifference = input.getItem().getItemEnchantability() - output.getItem().getItemEnchantability();
			cost = cost + enchantabilityDifference;
			cost = Math.max(1, cost);
			return cost;
		}
		else
			return 0;
	}

	public int countHighestEnchantmentCost(ItemStack itemStack)
	{
		int count = 0;

		for (Enchantment ench : Enchantment.enchantmentsList)
		{
			if (ench != null)
			{
				int level = EnchantmentHelper.getEnchantmentLevel(ench.effectId, itemStack);
				if (level > count)
					count += this.getWeightModifier(ench) * level;
			}
		}

		return count;
	}

	public int countTotalEnchantmentCost(ItemStack itemStack)
	{
		int count = 0;

		for (Enchantment ench : Enchantment.enchantmentsList)
		{
			if (ench != null)
			{
				int level = EnchantmentHelper.getEnchantmentLevel(ench.effectId, itemStack);
				if (level > 0)
				{
					count += this.getWeightModifier(ench) * level;
					++count;
				}
			}
		}

		return count;
	}

	public int getWeightModifier(Enchantment ench)
	{
		switch (ench.getWeight())
		{
			case 1:
				return 8;
			case 2:
				return 4;
			case 3:
			case 4:
			case 5:
				return 2;
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			default:
				return 1;
		}
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int shiftHeld, EntityPlayer par4EntityPlayer)
	{
		if (slotNum > 0 && par4EntityPlayer.inventory.getItemStack() == null && ((Slot) this.inventorySlots.get(slotNum)).inventory == this.assemblyMatrix && !((Slot) this.inventorySlots.get(slotNum)).getHasStack() && this.isMatrixEmpty(this.assemblyMatrix))
			slotNum -= 9;

		if (slotNum > 0 && ((Slot) this.inventorySlots.get(slotNum)).inventory == this.tinkerResult && this.calculateRecraftingCost() > par4EntityPlayer.experienceLevel && !par4EntityPlayer.capabilities.isCreativeMode)
			return null;
		else if (slotNum > 0 && ((Slot) this.inventorySlots.get(slotNum)).inventory == this.uncraftingMatrix && this.calculateUncraftingCost() > par4EntityPlayer.experienceLevel && !par4EntityPlayer.capabilities.isCreativeMode)
			return null;
		else if (slotNum > 0 && ((Slot) this.inventorySlots.get(slotNum)).inventory == this.uncraftingMatrix && TwilightForestMod.disableUncrafting)
			return null;
		else if (slotNum <= 0 || ((Slot) this.inventorySlots.get(slotNum)).inventory != this.uncraftingMatrix || ((Slot) this.inventorySlots.get(slotNum)).getStack() != null && ((Slot) this.inventorySlots.get(slotNum)).getStack().stackSize != 0)
		{
			ItemStack ret = super.slotClick(slotNum, mouseButton, shiftHeld, par4EntityPlayer);
			if (slotNum > 0 && ((Slot) this.inventorySlots.get(slotNum)).inventory instanceof InventoryTFGoblinInput)
				this.onCraftMatrixChanged(this.tinkerInput);

			return ret;
		}
		else
			return null;
	}

	@Override
	protected void retrySlotClick(int slotNum, int mouseButton, boolean par3, EntityPlayer par4EntityPlayer)
	{
		if (((Slot) this.inventorySlots.get(slotNum)).inventory == this.uncraftingMatrix)
			slotNum += 9;

		this.slotClick(slotNum, mouseButton, 1, par4EntityPlayer);
	}

	private boolean isMatrixEmpty(IInventory matrix)
	{
		boolean matrixEmpty = true;

		for (int i = 0; i < matrix.getSizeInventory(); ++i)
		{
			if (matrix.getStackInSlot(i) != null)
				matrixEmpty = false;
		}

		return matrixEmpty;
	}

	public boolean isDamageableComponent(ItemStack itemStack)
	{
		return itemStack != null && itemStack.getItem() != Items.stick;
	}

	public int countDamageableParts(IInventory matrix)
	{
		int count = 0;

		for (int i = 0; i < matrix.getSizeInventory(); ++i)
		{
			if (this.isDamageableComponent(matrix.getStackInSlot(i)))
				++count;
		}

		return count;
	}

	public int countDamagedParts(ItemStack input)
	{
		int totalMax4 = Math.max(4, this.countDamageableParts(this.uncraftingMatrix));
		float damage = (float) input.getItemDamage() / (float) input.getMaxDamage();
		return (int) Math.ceil(totalMax4 * damage);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack copyItem = null;
		Slot transferSlot = (Slot) this.inventorySlots.get(slotNum);
		if (transferSlot != null && transferSlot.getHasStack())
		{
			ItemStack transferStack = transferSlot.getStack();
			copyItem = transferStack.copy();
			if (slotNum != 0 && slotNum != 1)
			{
				if (slotNum >= 20 && slotNum < 47)
				{
					if (!this.mergeItemStack(transferStack, 47, 56, false))
						return null;
				}
				else if (slotNum >= 47 && slotNum < 56)
				{
					if (!this.mergeItemStack(transferStack, 20, 47, false))
						return null;
				}
				else if (!this.mergeItemStack(transferStack, 20, 56, false))
					return null;
			}
			else
			{
				if (!this.mergeItemStack(transferStack, 20, 56, true))
					return null;

				transferSlot.onSlotChange(transferStack, copyItem);
			}

			if (transferStack.stackSize == 0)
				transferSlot.putStack(null);
			else
				transferSlot.onSlotChanged();

			if (transferStack.stackSize == copyItem.stackSize)
				return null;

			transferSlot.onPickupFromSlot(player, transferStack);
		}

		return copyItem;
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < 9; ++i)
			{
				ItemStack assemblyStack = this.assemblyMatrix.getStackInSlotOnClosing(i);
				if (assemblyStack != null)
					par1EntityPlayer.dropPlayerItemWithRandomChoice(assemblyStack, false);
			}

			ItemStack inputStack = this.tinkerInput.getStackInSlotOnClosing(0);
			if (inputStack != null)
				par1EntityPlayer.dropPlayerItemWithRandomChoice(inputStack, false);
		}

	}

	public ItemStack[] getRecipeItems(IRecipe recipe)
	{
		return recipe instanceof ShapedRecipes ? this.getRecipeItemsShaped((ShapedRecipes) recipe) : recipe instanceof ShapedOreRecipe ? this.getRecipeItemsOre((ShapedOreRecipe) recipe) : null;
	}

	public ItemStack[] getRecipeItemsShaped(ShapedRecipes shaped)
	{
		return shaped.recipeItems;
	}

	public ItemStack[] getRecipeItemsOre(ShapedOreRecipe shaped)
	{
		try
		{
			Object[] objects = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 3);
			ItemStack[] items = new ItemStack[objects.length];

			for (int i = 0; i < objects.length; ++i)
			{
				if (objects[i] instanceof ItemStack)
					items[i] = (ItemStack) objects[i];

				if (objects[i] instanceof ArrayList && ((ArrayList) objects[i]).size() > 0)
					items[i] = (ItemStack) ((ArrayList) objects[i]).get(0);
			}

			return items;
		}
		catch (IllegalArgumentException | SecurityException var5)
		{
			var5.printStackTrace();
		}

		return null;
	}

	public int getRecipeWidth(IRecipe recipe)
	{
		return recipe instanceof ShapedRecipes ? this.getRecipeWidthShaped((ShapedRecipes) recipe) : recipe instanceof ShapedOreRecipe ? this.getRecipeWidthOre((ShapedOreRecipe) recipe) : -1;
	}

	public int getRecipeWidthShaped(ShapedRecipes shaped)
	{
		return shaped.recipeWidth;
	}

	public int getRecipeWidthOre(ShapedOreRecipe shaped)
	{
		try
		{
			return (Integer) ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 4);
		}
		catch (IllegalArgumentException | SecurityException var3)
		{
			var3.printStackTrace();
		}

		return 0;
	}

	public int getRecipeHeight(IRecipe recipe)
	{
		return recipe instanceof ShapedRecipes ? this.getRecipeHeightShaped((ShapedRecipes) recipe) : recipe instanceof ShapedOreRecipe ? this.getRecipeHeightOre((ShapedOreRecipe) recipe) : -1;
	}

	public int getRecipeHeightShaped(ShapedRecipes shaped)
	{
		return shaped.recipeHeight;
	}

	public int getRecipeHeightOre(ShapedOreRecipe shaped)
	{
		try
		{
			return (Integer) ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 5);
		}
		catch (IllegalArgumentException | SecurityException var3)
		{
			var3.printStackTrace();
		}

		return 0;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
}
