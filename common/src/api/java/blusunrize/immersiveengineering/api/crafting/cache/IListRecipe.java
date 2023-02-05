/*
 * BluSunrize
 * Copyright (c) 2022
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.crafting.cache;

import blusunrize.Tournament.api.crafting.IESerializableRecipe;

import java.util.List;

public interface IListRecipe
{
	List<? extends IESerializableRecipe> getSubRecipes();
}
