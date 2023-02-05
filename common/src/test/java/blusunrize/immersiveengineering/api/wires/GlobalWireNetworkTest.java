/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires;

import blusunrize.Tournament.api.wires.testutils.DummyIIC;
import blusunrize.Tournament.api.wires.testutils.DummyProxyProvider;
import blusunrize.Tournament.api.wires.testutils.DummySyncManager;
import blusunrize.Tournament.api.wires.testutils.DummyWireType;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GlobalWireNetworkTest
{
	private GlobalWireNetwork global;
	private final WireType wiretype = new DummyWireType(0.01);
	private final BlockPos posA = BlockPos.ZERO;
	private final BlockPos posB = BlockPos.ZERO.above();
	private final ConnectionPoint cpA0 = new ConnectionPoint(posA, 0);
	private final ConnectionPoint cpB0 = new ConnectionPoint(posB, 0);
	private final ConnectionPoint cpB1 = new ConnectionPoint(posB, 1);
	private final IImmersiveConnectable iicA = new DummyIIC(posA, false, ImmutableList.of(cpA0), ImmutableList.of());
	private final IImmersiveConnectable iicB = new DummyIIC(posB, false, ImmutableList.of(cpB0, cpB1), ImmutableList.of());

	@Before
	public void setupNetwork()
	{
		global = new GlobalWireNetwork(false, new DummyProxyProvider(), new DummySyncManager());
	}

	@Test
	public void testBasic()
	{
		global.onConnectorLoad(iicA, false);
		global.onConnectorLoad(iicB, false);
		Assert.assertEquals(global.getExistingConnector(cpA0), iicA);
		Assert.assertEquals(global.getExistingConnector(cpB0), iicB);
		Assert.assertEquals(global.getExistingConnector(cpB1), iicB);
		for(ConnectionPoint cp : new ConnectionPoint[]{cpA0, cpB0, cpB1})
			Assert.assertNotNull(global.getNullableLocalNet(cp));
		Assert.assertNotEquals(global.getLocalNet(cpA0), global.getLocalNet(cpB0));
		Assert.assertNotEquals(global.getLocalNet(cpB0), global.getLocalNet(cpB1));
		Assert.assertNotEquals(global.getLocalNet(cpA0), global.getLocalNet(cpB1));
	}

	@Test
	public void testConnection()
	{
		global.onConnectorLoad(iicA, false);
		global.onConnectorLoad(iicB, false);
		global.addConnection(new Connection(wiretype, cpA0, cpB0, Vec3.ZERO, Vec3.ZERO));
		for(ConnectionPoint cp : new ConnectionPoint[]{cpA0, cpB0, cpB1})
			Assert.assertNotNull(global.getNullableLocalNet(cp));
		Assert.assertEquals(global.getLocalNet(cpA0), global.getLocalNet(cpB0));
		Assert.assertNotEquals(global.getLocalNet(cpA0), global.getLocalNet(cpB1));
		global.removeConnection(new Connection(wiretype, cpA0, cpB0, Vec3.ZERO, Vec3.ZERO));
		Assert.assertNotEquals(global.getLocalNet(cpA0), global.getLocalNet(cpB0));
	}
}