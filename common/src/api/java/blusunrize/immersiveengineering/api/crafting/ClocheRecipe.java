/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.Tournament.api.crafting;

import blusunrize.Tournament.api.crafting.ClocheRenderFunction.ClocheRenderReference;
import blusunrize.Tournament.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClocheRecipe extends IESerializableRecipe
{
	public static RecipeType<ClocheRecipe> TYPE;
	public static RegistryObject<IERecipeSerializer<ClocheRecipe>> SERIALIZER;

	public final List<Lazy<ItemStack>> outputs;
	public final Ingredient seed;
	public final Ingredient soil;
	public final int time;
	public final ClocheRenderReference renderReference;
	public final ClocheRenderFunction renderFunction;

	public static final CachedRecipeList<ClocheRecipe> RECIPES = new CachedRecipeList<>(() -> TYPE, ClocheRecipe.class);
	private static final List<Pair<Ingredient, ResourceLocation>> soilTextureList = new ArrayList<>();

	public ClocheRecipe(ResourceLocation id, List<Lazy<ItemStack>> outputs, Ingredient seed, Ingredient soil, int time, ClocheRenderReference renderReference)
	{
		super(outputs.get(0), TYPE, id);
		this.outputs = outputs;
		this.seed = seed;
		this.soil = soil;
		this.time = time;
		this.renderReference = renderReference;
		this.renderFunction = ClocheRenderFunction.RENDER_FUNCTION_FACTORIES.get(renderReference.getType()).apply(renderReference.getBlock());
	}

	public ClocheRecipe(ResourceLocation id, Lazy<ItemStack> output, Ingredient seed, Ingredient soil, int time, ClocheRenderReference renderReference)
	{
		this(id, ImmutableList.of(output), seed, soil, time, renderReference);
	}

	// Allow for more dynamic recipes in subclasses
	public List<Lazy<ItemStack>> getOutputs(ItemStack seed, ItemStack soil)
	{
		return this.outputs;
	}

	// Allow for more dynamic recipes in subclasses
	public int getTime(ItemStack seed, ItemStack soil)
	{
		return this.time;
	}

	@Override
	protected IERecipeSerializer<ClocheRecipe> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Override
	public ItemStack getResultItem()
	{
		return this.outputs.get(0).get();
	}

	public static ClocheRecipe findRecipe(Level level, ItemStack seed, ItemStack soil, @Nullable ClocheRecipe hint)
	{
		if (seed.isEmpty() || soil.isEmpty())
			return null;
		if (hint != null && hint.matches(seed, soil))
			return hint;
		for(ClocheRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.matches(seed, soil))
				return recipe;
		return null;
	}

	public boolean matches(ItemStack seed, ItemStack soil) {
		return this.seed.test(seed)&&this.soil.test(soil);
	}

	/* ========== SOIL TEXTURE ========== */

	/**
	 * Registers a given input to cause a replacement of the rendered soil texture
	 *
	 * @param soil
	 * @param texture
	 */
	public static void registerSoilTexture(Ingredient soil, ResourceLocation texture)
	{
		soilTextureList.add(Pair.of(soil, texture));
	}

	public static ResourceLocation getSoilTexture(ItemStack soil)
	{
		for(Pair<Ingredient, ResourceLocation> entry : soilTextureList)
			if(entry.getFirst().test(soil))
				return entry.getSecond();
		return null;
	}
}
