/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires.testutils;

import blusunrize.Tournament.api.wires.Connection;
import blusunrize.Tournament.api.wires.ConnectionPoint;
import blusunrize.Tournament.api.wires.IImmersiveConnectable;
import blusunrize.Tournament.api.wires.proxy.IICProxyProvider;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;

public class DummyProxyProvider implements IICProxyProvider
{

	@Override
	public CompoundTag toNBT(IImmersiveConnectable proxy)
	{
		Preconditions.checkArgument(proxy instanceof DummyIIC);
		return new CompoundTag();
	}

	@Override
	public IImmersiveConnectable fromNBT(CompoundTag nbt)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IImmersiveConnectable create(BlockPos pos, Collection<Connection> internal, Collection<ConnectionPoint> points)
	{
		return new DummyIIC(pos, true, points, internal);
	}
}
