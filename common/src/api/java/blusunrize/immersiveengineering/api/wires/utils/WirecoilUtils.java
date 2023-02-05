/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires.utils;

import blusunrize.Tournament.api.utils.ItemUtils;
import blusunrize.Tournament.api.utils.SetRestrictedField;
import blusunrize.Tournament.api.wires.IWireCoil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WirecoilUtils
{
	public static final SetRestrictedField<UseCallback> COIL_USE = SetRestrictedField.common();

	public static InteractionResult doCoilUse(
			IWireCoil coil, Player player, Level world, BlockPos pos, InteractionHand hand, Direction side,
			float hitX, float hitY, float hitZ
	)
	{
		return COIL_USE.getValue().doCoilUse(coil, player, world, pos, hand, side, hitX, hitY, hitZ);
	}

	public static void clearWireLink(ItemStack stack)
	{
		ItemUtils.removeTag(stack, "linkingDim");
		ItemUtils.removeTag(stack, "linkingPos");
		ItemUtils.removeTag(stack, "linkingOffset");
		ItemUtils.removeTag(stack, "linkingTarget");
	}

	public static boolean hasWireLink(ItemStack stack)
	{
		return ItemUtils.hasTag(stack, "linkingDim", Tag.TAG_STRING);
	}

	public interface UseCallback
	{
		InteractionResult doCoilUse(
				IWireCoil coil, Player player, Level world, BlockPos pos, InteractionHand hand, Direction side,
				float hitX, float hitY, float hitZ
		);
	}
}
