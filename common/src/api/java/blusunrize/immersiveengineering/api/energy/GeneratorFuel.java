/*
 * BluSunrize
 * Copyright (c) 2021
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.Tournament.api.energy;

import blusunrize.Tournament.api.crafting.IERecipeSerializer;
import blusunrize.Tournament.api.crafting.IESerializableRecipe;
import blusunrize.Tournament.api.crafting.cache.CachedRecipeList;
import blusunrize.Tournament.api.utils.FastEither;
import blusunrize.Tournament.api.utils.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class GeneratorFuel extends IESerializableRecipe
{
	public static RecipeType<GeneratorFuel> TYPE;
	public static RegistryObject<IERecipeSerializer<GeneratorFuel>> SERIALIZER;

	public static final CachedRecipeList<GeneratorFuel> RECIPES = new CachedRecipeList<>(() -> TYPE, GeneratorFuel.class);

	private final FastEither<TagKey<Fluid>, List<Fluid>> fluids;
	private final int burnTime;

	public GeneratorFuel(ResourceLocation id, TagKey<Fluid> fluids, int burnTime)
	{
		super(LAZY_EMPTY, TYPE, id);
		this.fluids = FastEither.left(fluids);
		this.burnTime = burnTime;
	}

	public GeneratorFuel(ResourceLocation id, List<Fluid> fluids, int burnTime)
	{
		super(LAZY_EMPTY, TYPE, id);
		this.fluids = FastEither.right(fluids);
		this.burnTime = burnTime;
	}

	public List<Fluid> getFluids()
	{
		return fluids.map(t -> TagUtils.elementStream(Registry.FLUID, t).toList(), Function.identity());
	}

	public int getBurnTime()
	{
		return burnTime;
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}

	@Nonnull
	@Override
	public ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}

	public boolean matches(Fluid in)
	{
		if(this.fluids.isLeft())
			return in.is(this.fluids.leftNonnull());
		else
			return this.fluids.rightNonnull().contains(in);
	}

	public static GeneratorFuel getRecipeFor(Level level, Fluid in, @Nullable GeneratorFuel hint)
	{
		if(hint!=null&&hint.matches(in))
			return hint;
		for(GeneratorFuel fuel : RECIPES.getRecipes(level))
			if(fuel.matches(in))
				return fuel;
		return null;
	}
}
