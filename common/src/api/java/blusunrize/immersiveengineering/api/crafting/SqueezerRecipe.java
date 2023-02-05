/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.crafting;

import blusunrize.Tournament.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * @author BluSunrize - 20.02.2016
 * <p>
 * The recipe for the Squeezer
 */
public class SqueezerRecipe extends MultiblockRecipe
{
	public static RecipeType<SqueezerRecipe> TYPE;
	public static RegistryObject<IERecipeSerializer<SqueezerRecipe>> SERIALIZER;
	public static final CachedRecipeList<SqueezerRecipe> RECIPES = new CachedRecipeList<>(() -> TYPE, SqueezerRecipe.class);

	public IngredientWithSize input;
	public final FluidStack fluidOutput;
	@Nonnull
	public final Lazy<ItemStack> itemOutput;

	public SqueezerRecipe(ResourceLocation id, FluidStack fluidOutput, @Nonnull Lazy<ItemStack> itemOutput, IngredientWithSize input, int energy)
	{
		super(itemOutput, TYPE, id);
		this.fluidOutput = fluidOutput;
		this.itemOutput = itemOutput;
		this.input = input;
		setTimeAndEnergy(80, energy);

		setInputListWithSizes(Lists.newArrayList(this.input));
		this.fluidOutputList = Lists.newArrayList(this.fluidOutput);
		this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.itemOutput.get()));
	}

	@Override
	protected IERecipeSerializer<SqueezerRecipe> getIESerializer()
	{
		return SERIALIZER.get();
	}

	public SqueezerRecipe setInputSize(int size)
	{
		this.input = this.input.withSize(size);
		return this;
	}

	public static SqueezerRecipe findRecipe(Level level, ItemStack input)
	{
		if(input.isEmpty())
			return null;
		for(SqueezerRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.input.test(input))
				return recipe;
		return null;
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}

	public static SortedMap<Component, Integer> getFluidValuesSorted(Level level, Fluid f, boolean inverse)
	{
		SortedMap<Component, Integer> map = new TreeMap<>(
				Comparator.comparing(
						(Function<Component, String>) Component::getString,
						inverse?Comparator.reverseOrder(): Comparator.naturalOrder()
				)
		);
		for(SqueezerRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.fluidOutput!=null&&recipe.fluidOutput.getFluid()==f&&!recipe.input.hasNoMatchingItems())
			{
				ItemStack is = recipe.input.getMatchingStacks()[0];
				map.put(is.getHoverName(), recipe.fluidOutput.getAmount());
			}
		return map;
	}
}