/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.client.ieobj;

import blusunrize.Tournament.api.utils.SetRestrictedField;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import java.util.List;

public interface ItemCallback<Key> extends IEOBJCallback<Key>
{
	SetRestrictedField<BlockEntityWithoutLevelRenderer> DYNAMIC_IEOBJ_RENDERER = SetRestrictedField.client();
	IItemRenderProperties USE_IEOBJ_RENDER = new IItemRenderProperties()
	{
		@Override
		public BlockEntityWithoutLevelRenderer getItemStackRenderer()
		{
			return DYNAMIC_IEOBJ_RENDERER.getValue();
		}
	};

	default List<List<String>> getSpecialGroups(ItemStack stack, TransformType transform, LivingEntity entity)
	{
		return List.of();
	}

	@Nonnull
	default Transformation getTransformForGroups(
			ItemStack stack, List<String> groups, TransformType transform, LivingEntity entity, float partialTicks
	)
	{
		return Transformation.identity();
	}

	default boolean areGroupsFullbright(ItemStack stack, List<String> groups)
	{
		return false;
	}

	Key extractKey(ItemStack stack, LivingEntity owner);

	default void handlePerspective(Key key, LivingEntity holder, TransformType cameraTransformType, PoseStack mat)
	{
	}

	static <T> ItemCallback<T> castOrDefault(IEOBJCallback<T> generic)
	{
		if(generic instanceof ItemCallback<T> itemCB)
			return itemCB;
		else
			return DefaultCallback.cast();
	}
}
