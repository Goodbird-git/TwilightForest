package com.gamerforea.twilightforest;

import com.gamerforea.eventhelper.config.*;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

@Config(name = "TwilightForest")
public final class EventConfig
{
	private static final String CATEGORY_BLACKLIST = "blacklist";
	private static final String CATEGORY_OTHER = "other";
	private static final String CATEGORY_PERMISSION = "permission";
	private static final String CATEGORY_PERFORMANCE = "performance";

	@ConfigItemBlockList(name = "uncrafting",
						 category = CATEGORY_BLACKLIST,
						 comment = "Чёрный список блоков для Анти-верстака",
						 oldName = "uncraftingBlackList",
						 oldCategory = CATEGORY_GENERAL)
	public static final ItemBlockList uncraftingBlackList = new ItemBlockList(true);

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Проверять NBT в Анти-верстаке (защита от дюпа)",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean uncraftingCheckNbt = true;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Игнорировать зачарования при проверке NBT",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean uncraftingCheckNbtIgnoreEnchantments = true;

	@ConfigBoolean(category = CATEGORY_OTHER, comment = "Включить Дерево сортировки", oldCategory = CATEGORY_GENERAL)
	public static boolean enableSortingTree = true;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить установку блоков Альфа-Йети",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableYetiAlphaGrief = false;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить проверку наличия привата при росте деревьев",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableTreeRegionCheck = false;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить продвинутую проверку наличия привата при росте деревьев (при возможности используется информация об игрока, посадившем дерево)",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableAdvTreeRegionCheck = false;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Принудительно включать проверку наличия привата при росте деревьев (если информации о владельце нет, то будет взят общий фейковый игрок)",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableForceAdvTreeRegionCheck = false;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить автоматическую генерацию парного портала",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableAutoPortalGen = true;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить спавн Гастов Карминитовым реактором",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableReactorSpawnGhast = true;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить распространение Шипов и выключить их разрушение",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableThornsBurst = true;

	@ConfigBoolean(category = CATEGORY_OTHER,
				   comment = "Включить проверку наличия привата при спавне дропа Тёмного гаста и Призрака рыцаря")
	public static boolean fixLootChestGrief = false;

	@ConfigBoolean(category = CATEGORY_PERMISSION,
				   comment = "Включить проверку наличия разрешения на сохранение инвентаря",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean enableCharmOfKeepingPermission = false;

	@ConfigString(category = CATEGORY_PERMISSION,
				  comment = "Разрешение на сохранение инвентаря после смерти",
				  oldCategory = CATEGORY_GENERAL)
	public static String charmOfKeepingPermission = "scavenger.scavenge";

	@ConfigBoolean(category = CATEGORY_PERMISSION, comment = "Запрет на полёты в Сумеречном лесу")
	public static boolean preventTwilightForestFlying = false;

	@ConfigString(category = CATEGORY_PERMISSION, comment = "Разрешение на полёты в Сумеречном лесу")
	public static String twilightForestFlyingPermission = "twilightforest.flying";

	@ConfigBoolean(category = CATEGORY_PERFORMANCE,
				   comment = "Кэшировать координаты портала (может повысить производительность) (на весь игровой мир будет использоваться один общий портал)",
				   oldCategory = CATEGORY_GENERAL)
	public static boolean cachePortalCoords = true;

	public static void init()
	{
		ConfigUtils.readConfig(EventConfig.class);
	}

	static
	{
		init();
	}
}
