/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires.testutils;

import blusunrize.Tournament.api.wires.Connection;
import blusunrize.Tournament.api.wires.IWireSyncManager;

public class DummySyncManager implements IWireSyncManager
{

	@Override
	public void onConnectionAdded(Connection c)
	{
	}

	@Override
	public void onConnectionRemoved(Connection c)
	{
	}
}
