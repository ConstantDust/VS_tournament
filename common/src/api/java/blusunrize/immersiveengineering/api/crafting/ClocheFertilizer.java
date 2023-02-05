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

public class ClocheFertilizer extends IESerializableRecipe
{
	public static RecipeType<ClocheFertilizer> TYPE;
	public static RegistryObject<IERecipeSerializer<ClocheFertilizer>> SERIALIZER;

	public static final CachedRecipeList<ClocheFertilizer> RECIPES = new CachedRecipeList<>(() -> TYPE, ClocheFertilizer.class);

	public final Ingredient input;
	public final float growthModifier;

	public ClocheFertilizer(ResourceLocation id, Ingredient input, float growthModifier)
	{
		super(LAZY_EMPTY, TYPE, id);
		this.input = input;
		this.growthModifier = growthModifier;
	}

	public float getGrowthModifier()
	{
		return growthModifier;
	}

	@Override
	protected IERecipeSerializer<ClocheFertilizer> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}

	public static float getFertilizerGrowthModifier(Level level, ItemStack stack)
	{
		for(ClocheFertilizer e : RECIPES.getRecipes(level))
			if(e.input.test(stack))
				return e.getGrowthModifier();
		return 0;
	}

	public static boolean isValidFertilizer(Level level, ItemStack stack)
	{
		return getFertilizerGrowthModifier(level, stack) > 0;
	}
}
