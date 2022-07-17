package com.gamerforea.twilightforest;

import com.gamerforea.eventhelper.nexus.ModNexus;
import com.gamerforea.eventhelper.nexus.ModNexusFactory;
import com.gamerforea.eventhelper.nexus.NexusUtils;
import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twilightforest.TwilightForestMod;

@ModNexus(name = "TwilightForest", uuid = "b6cedf85-7aac-4d18-a01b-9a8ea1df42fc")
public final class ModUtils
{
	public static final Logger LOGGER = LogManager.getLogger("TwilightForest");
	public static final ModNexusFactory NEXUS_FACTORY = NexusUtils.getFactory();
	private static final ThreadLocal<EntityPlayer> TEMP_PLAYER = new ThreadLocal<>();

	public static FakePlayer getModFake(World world)
	{
		return NEXUS_FACTORY.getFake(world);
	}

	public static EntityPlayer getTempPlayer()
	{
		return TEMP_PLAYER.get();
	}

	public static EntityPlayer setTempPlayer(EntityPlayer player)
	{
		EntityPlayer prevPlayer = TEMP_PLAYER.get();
		TEMP_PLAYER.set(player);
		return prevPlayer;
	}

	public static boolean isTFWorld(Entity entity)
	{
		return isTFWorld(entity.worldObj);
	}

	public static boolean isTFWorld(World world)
	{
		return world != null && world.provider.dimensionId == TwilightForestMod.dimensionID;
	}

	public static boolean canMobGrief(World world)
	{
		return world != null && world.getGameRules().getGameRuleBooleanValue("mobGriefing");
	}

	public static boolean canTreeGrowAt(World world, int x, int y, int z)
	{
		if (EventConfig.enableAdvTreeRegionCheck)
		{
			EntityPlayer player = getTempPlayer();
			if (player != null)
				return !EventUtils.cantBreak(player, x, y, z);
		}
		return !EventConfig.enableTreeRegionCheck || !EventUtils.isInPrivate(world, x, y, z);
	}

	public static void init()
	{
		EventConfig.init();
		BalanceConfig.init(false);
		FixEventHandler.init();
	}
}
