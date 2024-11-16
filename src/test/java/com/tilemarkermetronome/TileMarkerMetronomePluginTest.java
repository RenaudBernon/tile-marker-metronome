package com.tilemarkermetronome;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TileMarkerMetronomePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TileMarkerMetronomePlugin.class);
		RuneLite.main(args);
	}
}