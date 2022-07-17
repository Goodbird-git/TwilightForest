package com.gamerforea.twilightforest.balance;

import com.gamerforea.eventhelper.config.Config;
import com.gamerforea.eventhelper.config.ConfigFloat;
import com.gamerforea.eventhelper.config.ConfigUtils;
import com.gamerforea.twilightforest.ModUtils;
import com.google.common.collect.ListMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.config.Configuration;
import twilightforest.TwilightForestMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.common.config.Configuration.CATEGORY_SPLITTER;

@Config(name = BalanceConfig.CONFIG_NAME)
public final class BalanceConfig
{
	protected static final String CONFIG_NAME = "TwilightForestBalance";
	private static final String CATEGORY_ENTITY_ATTRIBUTES = "entityAttributes";
	private static final String CATEGORY_SPECIALS = "specials";
	private static final Map<Class<? extends EntityLivingBase>, EntityBalanceInfo> ENTITY_BALANCE_MAP = new HashMap<>();

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float towerGhastFireballDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float miniGhastFireballDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float urGhastFireballDamage = 16;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float urGhastTantrumDamage = 3;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float hydraMortarDamage = 18;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float hydraHeadBiteDamage = 48;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float hydraHeadFlameDamage = 19;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float lichBoltDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float lichBombExplosionPower = 2;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float snowQueenBreathDamage = 4;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float fireBeetleBreathDamage = 2;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float knightPhantomDamagePlayerAddition = 7;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float yetiAlphaFallDamage = 5;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float natureBoltDamage = 2;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float tomeBoltDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float slimeProjectileDamage = 8;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float mistWolfDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float winterWolfDamage = 6;

	@ConfigFloat(category = CATEGORY_SPECIALS, min = 0)
	public static float iceSnowballDamage = 8;

	@SuppressWarnings("unused")
	public static void applyTo(EntityLivingBase entity)
	{
		if (entity != null)
		{
			EntityBalanceInfo balanceInfo = ENTITY_BALANCE_MAP.get(entity.getClass());
			if (balanceInfo != null)
				balanceInfo.applyTo(entity);
		}
	}

	public static void init(boolean readEntityAttributes)
	{
		ConfigUtils.readConfig(BalanceConfig.class);

		if (readEntityAttributes)
			try
			{
				ModContainer modContainer = FMLCommonHandler.instance().findContainerFor(TwilightForestMod.instance);
				if (modContainer == null)
					throw new IllegalStateException("Twilight Forest mod instance not found");

				List<EntityRegistry.EntityRegistration> entityRegistrations = getEntityRegistrations().get(modContainer);
				if (entityRegistrations.isEmpty())
					throw new IllegalStateException("Twilight Forest mod entities not found");

				Configuration cfg = ConfigUtils.getConfig(CONFIG_NAME);
				cfg.setCategoryComment(CATEGORY_ENTITY_ATTRIBUTES, "Custom default attribute values for living entities (warning: some entities can ignore it)");

				for (EntityRegistry.EntityRegistration entityRegistration : entityRegistrations)
				{
					Class<? extends Entity> entityClass = entityRegistration.getEntityClass();
					if (EntityLivingBase.class.isAssignableFrom(entityClass))
						ENTITY_BALANCE_MAP.put((Class<? extends EntityLivingBase>) entityClass, new EntityBalanceInfo(cfg, makeCategoryPath(CATEGORY_ENTITY_ATTRIBUTES, entityRegistration.getEntityName())));
				}

				cfg.save();
			}
			catch (Throwable throwable)
			{
				ModUtils.LOGGER.error("Twilight Forest balance config can't be loaded", throwable);
			}
	}

	private static ListMultimap<ModContainer, EntityRegistry.EntityRegistration> getEntityRegistrations()
	{
		return ReflectionHelper.getPrivateValue(EntityRegistry.class, EntityRegistry.instance(), "entityRegistrations");
	}

	private static String makeCategoryPath(String parentCategory, String childCategory)
	{
		return parentCategory.replace(CATEGORY_SPLITTER, "@") + CATEGORY_SPLITTER + childCategory.replace(CATEGORY_SPLITTER, "@");
	}
}
