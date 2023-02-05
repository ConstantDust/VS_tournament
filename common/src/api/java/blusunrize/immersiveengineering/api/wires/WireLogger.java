/*
 * BluSunrize
 * Copyright (c) 2019
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.Tournament.api.wires;

import blusunrize.Tournament.api.Lib;
import blusunrize.Tournament.api.utils.GatedLogger;
import org.apache.logging.log4j.LogManager;

public class WireLogger
{
	public static GatedLogger logger = new GatedLogger(LogManager.getLogger(Lib.MODID+"-wires"), false);
}
