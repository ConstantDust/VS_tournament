/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires.localhandlers;

import blusunrize.Tournament.api.wires.GlobalWireNetwork;
import blusunrize.Tournament.api.wires.LocalWireNetwork;

public interface ILocalHandlerConstructor
{
	LocalNetworkHandler create(LocalWireNetwork local, GlobalWireNetwork global) throws Exception;
}
