package com.gamerforea.twilightforest;

import com.gamerforea.eventhelper.util.EventUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import twilightforest.TwilightForestMod;

public final class FixEventHandler
{
	private static final FixEventHandler INSTANCE = new FixEventHandler();

	private FixEventHandler()
	{
	}

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingUpdateEvent event)
	{
		if (EventConfig.preventTwilightForestFlying)
		{
			EntityLivingBase entity = event.entityLiving;
			if (!entity.worldObj.isRemote && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				if (player.dimension == TwilightForestMod.dimensionID && player.capabilities.isFlying && !player.capabilities.isCreativeMode && player.ticksExisted % 20 == 0)
				{
					String permission = EventConfig.twilightForestFlyingPermission;
					if (permission.isEmpty() || !EventUtils.hasPermission(player, permission))
					{
						player.capabilities.isFlying = false;
						player.sendPlayerAbilities();
					}
				}
			}
		}
	}
}
