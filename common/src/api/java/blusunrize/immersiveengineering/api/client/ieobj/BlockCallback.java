/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.client.ieobj;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public interface BlockCallback<T> extends IEOBJCallback<T>
{
	T extractKey(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, BlockEntity blockEntity);

	T getDefaultKey();

	default boolean dependsOnLayer()
	{
		return false;
	}

	static <T> BlockCallback<T> castOrDefault(IEOBJCallback<T> generic)
	{
		if(generic instanceof BlockCallback<T> blockCB)
			return blockCB;
		else
			return DefaultCallback.cast();
	}
}
