package com.gamerforea.twilightforest.coremod;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.Name(CoreMod.NAME)
@IFMLLoadingPlugin.MCVersion(Loader.MC_VERSION)
@IFMLLoadingPlugin.SortingIndex(1001)
public final class CoreMod implements IFMLLoadingPlugin
{
	protected static final String NAME = "TwilightForestCoreMod";
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	private static boolean isObfuscated = false;

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "com.gamerforea.twilightforest.coremod.TwilightForestTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

	public static boolean isObfuscated()
	{
		return isObfuscated;
	}
}
