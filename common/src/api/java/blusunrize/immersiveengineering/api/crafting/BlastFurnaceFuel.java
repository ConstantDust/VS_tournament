/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.Tournament.api.crafting;

import blusunrize.Tournament.api.crafting.cache.CachedRecipeList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public class BlastFurnaceFuel extends IESerializableRecipe
{
	public static RecipeType<BlastFurnaceFuel> TYPE;
	public static RegistryObject<IERecipeSerializer<BlastFurnaceFuel>> SERIALIZER;

	public static final CachedRecipeList<BlastFurnaceFuel> RECIPES = new CachedRecipeList<>(() -> TYPE, BlastFurnaceFuel.class);

	public final Ingredient input;
	public final int burnTime;

	public BlastFurnaceFuel(ResourceLocation id, Ingredient input, int burnTime)
	{
		super(LAZY_EMPTY, TYPE, id);
		this.input = input;
		this.burnTime = burnTime;
	}

	public static int getBlastFuelTime(Level level, ItemStack stack)
	{
		for(BlastFurnaceFuel e : RECIPES.getRecipes(level))
			if(e.input.test(stack))
				return e.burnTime;
		return 0;
	}

	public static boolean isValidBlastFuel(Level level, ItemStack stack)
	{
		return getBlastFuelTime(level, stack) > 0;
	}

	@Override
	protected IERecipeSerializer<BlastFurnaceFuel> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}
}
