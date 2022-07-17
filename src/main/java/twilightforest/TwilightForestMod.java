package twilightforest;

import com.gamerforea.twilightforest.ModUtils;
import com.gamerforea.twilightforest.balance.BalanceConfig;
import com.gamerforea.twilightforest.tile.OwnerTileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.entity.*;
import twilightforest.entity.boss.*;
import twilightforest.entity.passive.*;
import twilightforest.item.BehaviorTFMobEggDispense;
import twilightforest.item.TFItems;
import twilightforest.item.TFRecipes;
import twilightforest.structures.StructureTFMajorFeatureStart;
import twilightforest.tileentity.*;
import twilightforest.world.WorldProviderTwilightForest;

@Mod(modid = "TwilightForest", name = "The Twilight Forest", version = "2.3.7")
public class TwilightForestMod
{
	public static final String ID = "TwilightForest";
	public static final String VERSION = "2.3.7";
	public static final String MODEL_DIR = "twilightforest:textures/model/";
	public static final String GUI_DIR = "twilightforest:textures/gui/";
	public static final String ENVRIO_DIR = "twilightforest:textures/environment/";
	public static final String ARMOR_DIR = "twilightforest:textures/armor/";
	public static final String ENFORCED_PROGRESSION_RULE = "tfEnforcedProgression";
	public static int dimensionID;
	public static int backupdimensionID = -777;
	public static int dimensionProviderID;
	public static boolean creatureCompatibility;
	public static boolean silentCicadas;
	public static boolean allowPortalsInOtherDimensions;
	public static boolean adminOnlyPortals;
	public static String twilightForestSeed;
	public static boolean disablePortalCreation;
	public static boolean disableUncrafting;
	public static boolean oldMapGen;
	public static String portalCreationItemString;
	public static float canopyCoverage;
	public static int twilightOakChance;
	public static int idMobWildBoar;
	public static int idMobBighornSheep;
	public static int idMobWildDeer;
	public static int idMobRedcap;
	public static int idMobSwarmSpider;
	public static int idMobNaga;
	public static int idMobNagaSegment;
	public static int idMobSkeletonDruid;
	public static int idMobHostileWolf;
	public static int idMobTwilightWraith;
	public static int idMobHedgeSpider;
	public static int idMobHydra;
	public static int idMobLich;
	public static int idMobPenguin;
	public static int idMobLichMinion;
	public static int idMobLoyalZombie;
	public static int idMobTinyBird;
	public static int idMobSquirrel;
	public static int idMobBunny;
	public static int idMobRaven;
	public static int idMobQuestRam;
	public static int idMobKobold;
	public static int idMobBoggard;
	public static int idMobMosquitoSwarm;
	public static int idMobDeathTome;
	public static int idMobMinotaur;
	public static int idMobMinoshroom;
	public static int idMobFireBeetle;
	public static int idMobSlimeBeetle;
	public static int idMobPinchBeetle;
	public static int idMobMazeSlime;
	public static int idMobRedcapSapper;
	public static int idMobMistWolf;
	public static int idMobKingSpider;
	public static int idMobFirefly;
	public static int idMobMiniGhast;
	public static int idMobTowerGhast;
	public static int idMobTowerGolem;
	public static int idMobTowerTermite;
	public static int idMobTowerBroodling;
	public static int idMobTowerBoss;
	public static int idMobBlockGoblin;
	public static int idMobGoblinKnightUpper;
	public static int idMobGoblinKnightLower;
	public static int idMobHelmetCrab;
	public static int idMobKnightPhantom;
	public static int idMobYeti;
	public static int idMobYetiBoss;
	public static int idMobWinterWolf;
	public static int idMobSnowGuardian;
	public static int idMobStableIceCore;
	public static int idMobUnstableIceCore;
	public static int idMobSnowQueen;
	public static int idMobTroll;
	public static int idMobGiantMiner;
	public static int idMobArmoredGiant;
	public static int idMobIceCrystal;
	public static int idMobApocalypseCube;
	public static int idMobAdherent;
	public static int idVehicleSpawnNatureBolt = 1;
	public static int idVehicleSpawnLichBolt = 2;
	public static int idVehicleSpawnTwilightWandBolt = 3;
	public static int idVehicleSpawnTomeBolt = 4;
	public static int idVehicleSpawnHydraMortar = 5;
	public static int idVehicleSpawnLichBomb = 6;
	public static int idVehicleSpawnMoonwormShot = 7;
	public static int idVehicleSpawnSlimeBlob = 8;
	public static int idVehicleSpawnCharmEffect = 9;
	public static int idVehicleSpawnThrownAxe = 10;
	public static int idVehicleSpawnThrownPick = 13;
	public static int idVehicleSpawnFallingIce = 14;
	public static int idVehicleSpawnThrownIce = 15;
	public static int idVehicleSpawnSeekerArrow = 16;
	public static int idVehicleSpawnIceSnowball = 17;
	public static int idVehicleSpawnChainBlock = 18;
	public static int idVehicleSpawnCubeOfAnnihilation = 19;
	public static int idVehicleSpawnSlideBlock = 20;
	public static int idBiomeLake;
	public static int idBiomeTwilightForest;
	public static int idBiomeTwilightForestVariant;
	public static int idBiomeHighlands;
	public static int idBiomeMushrooms;
	public static int idBiomeSwamp;
	public static int idBiomeStream;
	public static int idBiomeSnowfield;
	public static int idBiomeGlacier;
	public static int idBiomeClearing;
	public static int idBiomeOakSavanna;
	public static int idBiomeFireflyForest;
	public static int idBiomeDeepMushrooms;
	public static int idBiomeDarkForestCenter;
	public static int idBiomeHighlandsCenter;
	public static int idBiomeDarkForest;
	public static int idBiomeEnchantedForest;
	public static int idBiomeFireSwamp;
	public static int idBiomeThornlands;
	public static boolean hasBiomeIdConflicts = false;
	public static boolean hasAssignedBiomeID = false;
	public static final TFEventListener eventListener = new TFEventListener();
	public static final TFTickHandler tickHandler = new TFTickHandler();
	public static FMLEventChannel genericChannel;
	@Instance("TwilightForest")
	public static TwilightForestMod instance;
	@SidedProxy(clientSide = "twilightforest.client.TFClientProxy", serverSide = "twilightforest.TFCommonProxy")
	public static TFCommonProxy proxy;

	public TwilightForestMod()
	{
		instance = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		this.loadConfiguration(config);
		proxy.doPreLoadRegistration();
		TFBlocks.registerBlocks();
		TFItems.registerItems();
		AchievementPage.registerAchievementPage(new TFAchievementPage());
		new StructureTFMajorFeatureStart();
		TFBiomeBase.assignBlankBiomeIds();
		if (hasAssignedBiomeID)
		{
			FMLLog.info("[TwilightForest] Twilight Forest mod has auto-assigned some biome IDs.  This will break any existing Twilight Forest saves.");
			this.saveBiomeIds(config);
		}

		hasBiomeIdConflicts = TFBiomeBase.areThereBiomeIdConflicts();

		// TODO gamerforEA code start
		ModUtils.init();
		// TODO gamerforEA code end
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		this.registerCreatures();
		TFRecipes.registerRecipes();
		this.registerTileEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		MinecraftForge.EVENT_BUS.register(eventListener);
		FMLCommonHandler.instance().bus().register(eventListener);
		FMLCommonHandler.instance().bus().register(tickHandler);
		Item portalItem;
		if (Item.itemRegistry.containsKey(portalCreationItemString))
		{
			portalItem = (Item) Item.itemRegistry.getObject(portalCreationItemString);
			if (portalItem != Items.diamond)
				FMLLog.info("Set Twilight Forest portal item to %s", portalItem.getUnlocalizedName());
		}
		else if (Block.blockRegistry.containsKey(portalCreationItemString))
		{
			portalItem = Item.getItemFromBlock((Block) Block.blockRegistry.getObject(portalCreationItemString));
			FMLLog.info("Set Twilight Forest portal item to %s", portalItem.getUnlocalizedName());
		}
		else
		{
			FMLLog.info("Twilight Forest config lists portal item as \'%s\'.  Not found, defaulting to diamond.", portalCreationItemString);
			portalItem = Items.diamond;
		}

		tickHandler.portalItem = portalItem;
		TFMapPacketHandler mapPacketHandler = new TFMapPacketHandler();
		NetworkRegistry.INSTANCE.newEventDrivenChannel("magicmap").register(mapPacketHandler);
		NetworkRegistry.INSTANCE.newEventDrivenChannel("mazemap").register(mapPacketHandler);
		genericChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("TwilightForest");
		proxy.doOnLoadRegistration();
		DimensionManager.registerProviderType(dimensionProviderID, WorldProviderTwilightForest.class, false);
		TFBiomeBase.registerWithBiomeDictionary();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		if (!DimensionManager.isDimensionRegistered(dimensionID))
			DimensionManager.registerDimension(dimensionID, dimensionProviderID);
		else
		{
			FMLLog.warning("[TwilightForest] Twilight Forest detected that the configured dimension id \'%d\' is being used.  Using backup ID.  It is recommended that you configure this mod to use a unique dimension ID.", dimensionID);
			DimensionManager.registerDimension(backupdimensionID, dimensionProviderID);
			dimensionID = backupdimensionID;
		}

		hasBiomeIdConflicts = TFBiomeBase.areThereBiomeIdConflicts();
	}

	@EventHandler
	public void startServer(FMLServerStartingEvent event)
	{
		this.registerDispenseBehaviors(event.getServer());
		event.registerServerCommand(new CommandTFProgress());
	}

	private void registerCreatures()
	{
		TFCreatures.registerTFCreature(EntityTFBoar.class, "Wild Boar", idMobWildBoar, 8611131, 16773066);
		TFCreatures.registerTFCreature(EntityTFBighorn.class, "Bighorn Sheep", idMobBighornSheep, 14405295, 14141297);
		TFCreatures.registerTFCreature(EntityTFDeer.class, "Wild Deer", idMobWildDeer, 8080686, 4924445);
		TFCreatures.registerTFCreature(EntityTFRedcap.class, "Redcap", idMobRedcap, 3881580, 11214356);
		TFCreatures.registerTFCreature(EntityTFSwarmSpider.class, "Swarm Spider", idMobSwarmSpider, 3277358, 1516830);
		TFCreatures.registerTFCreature(EntityTFNaga.class, "Naga", idMobNaga, 10801942, 1783819);
		TFCreatures.registerTFCreature(EntityTFSkeletonDruid.class, "Skeleton Druid", idMobSkeletonDruid, 10724259, 2767639);
		TFCreatures.registerTFCreature(EntityTFHostileWolf.class, "Hostile Wolf", idMobHostileWolf, 14144467, 11214356);
		TFCreatures.registerTFCreature(EntityTFWraith.class, "Twilight Wraith", idMobTwilightWraith, 5263440, 8618883);
		TFCreatures.registerTFCreature(EntityTFHedgeSpider.class, "Hedge Spider", idMobHedgeSpider, 2318099, 5645907);
		TFCreatures.registerTFCreature(EntityTFHydra.class, "Hydra", idMobHydra, 1321280, 2719851);
		TFCreatures.registerTFCreature(EntityTFLich.class, "Twilight Lich", idMobLich, 11314313, 3540082);
		TFCreatures.registerTFCreature(EntityTFPenguin.class, "Glacier Penguin", idMobPenguin, 1185051, 16379346);
		TFCreatures.registerTFCreature(EntityTFLichMinion.class, "Lich Minion", idMobLichMinion);
		TFCreatures.registerTFCreature(EntityTFLoyalZombie.class, "Loyal Zombie", idMobLoyalZombie);
		TFCreatures.registerTFCreature(EntityTFTinyBird.class, "Tiny Bird", idMobTinyBird, 3386077, 1149166);
		TFCreatures.registerTFCreature(EntityTFSquirrel.class, "Forest Squirrel", idMobSquirrel, 9457426, 15658734);
		TFCreatures.registerTFCreature(EntityTFBunny.class, "Forest Bunny", idMobBunny, 16711406, 13413017);
		TFCreatures.registerTFCreature(EntityTFRaven.class, "Forest Raven", idMobRaven, 17, 2236979);
		TFCreatures.registerTFCreature(EntityTFQuestRam.class, "Questing Ram", idMobQuestRam);
		TFCreatures.registerTFCreature(EntityTFKobold.class, "Twilight Kobold", idMobKobold, 3612822, 9002267);
		TFCreatures.registerTFCreature(EntityTFMosquitoSwarm.class, "Mosquito Swarm", idMobMosquitoSwarm, 526596, 2961185);
		TFCreatures.registerTFCreature(EntityTFDeathTome.class, "Death Tome", idMobDeathTome, 7818786, 14405054);
		TFCreatures.registerTFCreature(EntityTFMinotaur.class, "Minotaur", idMobMinotaur, 4141092, 11173222);
		TFCreatures.registerTFCreature(EntityTFMinoshroom.class, "Minoshroom", idMobMinoshroom, 11014162, 11173222);
		TFCreatures.registerTFCreature(EntityTFFireBeetle.class, "Fire Beetle", idMobFireBeetle, 1903360, 13332261);
		TFCreatures.registerTFCreature(EntityTFSlimeBeetle.class, "Slime Beetle", idMobSlimeBeetle, 792070, 6334284);
		TFCreatures.registerTFCreature(EntityTFPinchBeetle.class, "Pinch Beetle", idMobPinchBeetle, 12358439, 2364937);
		TFCreatures.registerTFCreature(EntityTFMazeSlime.class, "Maze Slime", idMobMazeSlime, 10724259, 2767639);
		TFCreatures.registerTFCreature(EntityTFRedcapSapper.class, "Redcap Sapper", idMobRedcapSapper, 5725473, 11214356);
		TFCreatures.registerTFCreature(EntityTFMistWolf.class, "Mist Wolf", idMobMistWolf, 3806225, 14862474);
		TFCreatures.registerTFCreature(EntityTFKingSpider.class, "King Spider", idMobKingSpider, 2890254, 16760855);
		TFCreatures.registerTFCreature(EntityTFMobileFirefly.class, "Firefly", idMobFirefly, 10801942, 12250626);
		TFCreatures.registerTFCreature(EntityTFMiniGhast.class, "Mini Ghast", idMobMiniGhast, 12369084, 10961731);
		TFCreatures.registerTFCreature(EntityTFTowerGhast.class, "Tower Ghast", idMobTowerGhast, 12369084, 12023928);
		TFCreatures.registerTFCreature(EntityTFTowerGolem.class, "Tower Golem", idMobTowerGolem, 7028000, 14867930);
		TFCreatures.registerTFCreature(EntityTFTowerTermite.class, "Tower Termite", idMobTowerTermite, 6105889, 11313210);
		TFCreatures.registerTFCreature(EntityTFTowerBroodling.class, "Redscale Broodling", idMobTowerBroodling, 3423252, 12250626);
		TFCreatures.registerTFCreature(EntityTFUrGhast.class, "Tower Boss", idMobTowerBoss, 12369084, 12023928);
		TFCreatures.registerTFCreature(EntityTFBlockGoblin.class, "Block&Chain Goblin", idMobBlockGoblin, 13887420, 2047999);
		TFCreatures.registerTFCreature(EntityTFGoblinKnightUpper.class, "Upper Goblin Knight", idMobGoblinKnightUpper);
		TFCreatures.registerTFCreature(EntityTFGoblinKnightLower.class, "Lower Goblin Knight", idMobGoblinKnightLower, 5660757, 13887420);
		TFCreatures.registerTFCreature(EntityTFHelmetCrab.class, "Helmet Crab", idMobHelmetCrab, 16486475, 13887420);
		TFCreatures.registerTFCreature(EntityTFKnightPhantom.class, "Knight Phantom", idMobKnightPhantom, 10905403, 13887420);
		TFCreatures.registerTFCreature(EntityTFYeti.class, "Yeti", idMobYeti, 14606046, 4617659);
		TFCreatures.registerTFCreature(EntityTFYetiAlpha.class, "Yeti Boss", idMobYetiBoss, 13487565, 2705518);
		TFCreatures.registerTFCreature(EntityTFWinterWolf.class, "WinterWolf", idMobWinterWolf, 14672869, 11713738);
		TFCreatures.registerTFCreature(EntityTFSnowGuardian.class, "SnowGuardian", idMobSnowGuardian, 13887420, 16711422);
		TFCreatures.registerTFCreature(EntityTFIceShooter.class, "Stable Ice Core", idMobStableIceCore, 10600435, 7340280);
		TFCreatures.registerTFCreature(EntityTFIceExploder.class, "Unstable Ice Core", idMobUnstableIceCore, 10136821, 10162085);
		TFCreatures.registerTFCreature(EntityTFSnowQueen.class, "Snow Queen", idMobSnowQueen, 11645652, 8847470);
		TFCreatures.registerTFCreature(EntityTFTroll.class, "Troll", idMobTroll, 10398095, 11572366);
		TFCreatures.registerTFCreature(EntityTFGiantMiner.class, "Giant Miner", idMobGiantMiner, 2169682, 10132122);
		TFCreatures.registerTFCreature(EntityTFArmoredGiant.class, "Armored Giant", idMobArmoredGiant, 2331537, 10132122);
		TFCreatures.registerTFCreature(EntityTFIceCrystal.class, "Ice Crystal", idMobIceCrystal, 14477822, 11389691);
		TFCreatures.registerTFCreature(EntityTFApocalypseCube.class, "Apocalypse Cube", idMobApocalypseCube, 10, 9109504);
		TFCreatures.registerTFCreature(EntityTFAdherent.class, "Adherent", idMobAdherent, 655360, 139);
		EntityRegistry.registerModEntity(EntityTFHydraHead.class, "HydraHead", 11, this, 150, 3, false);
		EntityRegistry.registerModEntity(EntityTFNatureBolt.class, "tfnaturebolt", idVehicleSpawnNatureBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(EntityTFLichBolt.class, "tflichbolt", idVehicleSpawnLichBolt, this, 150, 2, true);
		EntityRegistry.registerModEntity(EntityTFTwilightWandBolt.class, "tftwilightwandbolt", idVehicleSpawnTwilightWandBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(EntityTFTomeBolt.class, "tftomebolt", idVehicleSpawnTomeBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(EntityTFHydraMortar.class, "tfhydramortar", idVehicleSpawnHydraMortar, this, 150, 3, true);
		EntityRegistry.registerModEntity(EntityTFLichBomb.class, "tflichbomb", idVehicleSpawnLichBomb, this, 150, 3, true);
		EntityRegistry.registerModEntity(EntityTFMoonwormShot.class, "tfmoonwormshot", idVehicleSpawnMoonwormShot, this, 150, 3, true);
		EntityRegistry.registerModEntity(EntityTFSlimeProjectile.class, "tfslimeblob", idVehicleSpawnSlimeBlob, this, 150, 3, true);
		EntityRegistry.registerModEntity(EntityTFCharmEffect.class, "tfcharmeffect", idVehicleSpawnCharmEffect, this, 80, 3, true);
		EntityRegistry.registerModEntity(EntityTFThrownAxe.class, "tfthrownaxe", idVehicleSpawnThrownAxe, this, 80, 3, true);
		EntityRegistry.registerModEntity(EntityTFThrownPick.class, "tfthrownpick", idVehicleSpawnThrownPick, this, 80, 3, true);
		EntityRegistry.registerModEntity(EntityTFFallingIce.class, "tffallingice", idVehicleSpawnFallingIce, this, 80, 3, true);
		EntityRegistry.registerModEntity(EntityTFIceBomb.class, "tfthrownice", idVehicleSpawnThrownIce, this, 80, 2, true);
		EntityRegistry.registerModEntity(EntitySeekerArrow.class, "tfSeekerArrow", idVehicleSpawnSeekerArrow, this, 150, 1, true);
		EntityRegistry.registerModEntity(EntityTFIceSnowball.class, "tficesnowball", idVehicleSpawnIceSnowball, this, 150, 3, true);
		EntityRegistry.registerModEntity(EntityTFChainBlock.class, "tfchainBlock", idVehicleSpawnChainBlock, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityTFCubeOfAnnihilation.class, "tfcubeannihilation", idVehicleSpawnCubeOfAnnihilation, this, 80, 1, true);
		EntityRegistry.registerModEntity(EntityTFSlideBlock.class, "tfslideblock", idVehicleSpawnSlideBlock, this, 80, 1, true);

		// TODO gamerforEA code start
		BalanceConfig.init(true);
		// TODO gamerforEA code end
	}

	private void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityTFFirefly.class, "Firefly");
		GameRegistry.registerTileEntity(TileEntityTFCicada.class, "Cicada");
		GameRegistry.registerTileEntity(TileEntityTFNagaSpawner.class, "Naga Spawner");
		GameRegistry.registerTileEntity(TileEntityTFLichSpawner.class, "Lich Spawner");
		GameRegistry.registerTileEntity(TileEntityTFHydraSpawner.class, "Hydra Spawner");
		GameRegistry.registerTileEntity(TileEntityTFSmoker.class, "Swamp Smoker");
		GameRegistry.registerTileEntity(TileEntityTFPoppingJet.class, "Popping Flame Jet");
		GameRegistry.registerTileEntity(TileEntityTFFlameJet.class, "Lit Flame Jet");
		GameRegistry.registerTileEntity(TileEntityTFMoonworm.class, "Moonworm");
		GameRegistry.registerTileEntity(TileEntityTFTowerBuilder.class, "Tower Builder");
		GameRegistry.registerTileEntity(TileEntityTFReverter.class, "Tower Reverter");
		GameRegistry.registerTileEntity(TileEntityTFTrophy.class, "TF Trophy");
		GameRegistry.registerTileEntity(TileEntityTFTowerBossSpawner.class, "Tower Boss Spawner");
		GameRegistry.registerTileEntity(TileEntityTFGhastTrapInactive.class, "Inactive Ghast Trap");
		GameRegistry.registerTileEntity(TileEntityTFGhastTrapActive.class, "Active Ghast Trap");
		GameRegistry.registerTileEntity(TileEntityTFCReactorActive.class, "Active Carminite Reactor");
		GameRegistry.registerTileEntity(TileEntityTFKnightPhantomsSpawner.class, "Knight Phantom Spawner");
		GameRegistry.registerTileEntity(TileEntityTFSnowQueenSpawner.class, "Snow Queen Spawner");

		// TODO gamerforEA code start
		GameRegistry.registerTileEntity(OwnerTileEntity.class, "TwilightForestOwnerTileEntity");
		// TODO gamerforEA code end
	}

	private void registerThaumcraftIntegration()
	{
	}

	private void registerDispenseBehaviors(MinecraftServer minecraftServer)
	{
		BlockDispenser.dispenseBehaviorRegistry.putObject(TFItems.spawnEgg, new BehaviorTFMobEggDispense(minecraftServer));
	}

	private void loadConfiguration(Configuration configFile)
	{
		configFile.load();
		dimensionID = configFile.get("dimension", "dimensionID", 7).getInt();
		configFile.get("dimension", "dimensionID", 7).comment = "What ID number to assign to the Twilight Forest dimension.  Change if you are having conflicts with another mod.";
		dimensionProviderID = configFile.get("dimension", "dimensionProviderID", -777).getInt();
		configFile.get("dimension", "dimensionProviderID", 7).comment = "Dimension provider ID.  Does not normally need to be changed, but the option is provided to work around a bug in MCPC+";
		silentCicadas = configFile.get("general", "SilentCicadas", false).getBoolean(false);
		configFile.get("general", "SilentCicadas", false).comment = "Make cicadas silent  for those having sound library problems, or otherwise finding them annoying";
		allowPortalsInOtherDimensions = configFile.get("general", "AllowPortalsInOtherDimensions", false).getBoolean(false);
		configFile.get("general", "AllowPortalsInOtherDimensions", false).comment = "Allow portals to the Twilight Forest to be made outside of dimension 0.  May be considered an exploit.";
		adminOnlyPortals = configFile.get("general", "AdminOnlyPortals", false).getBoolean(false);
		configFile.get("general", "AdminOnlyPortals", false).comment = "Allow portals only for admins (ops).  This severly reduces the range in which the mod usually scans for valid portal conditions, and it scans near ops only.";
		twilightForestSeed = configFile.get("general", "TwilightForestSeed", "").getString();
		configFile.get("general", "TwilightForestSeed", "").comment = "If set, this will override the normal world seed when generating parts of the Twilight Forest Dimension.";
		disablePortalCreation = configFile.get("general", "DisablePortalCreation", false).getBoolean(false);
		configFile.get("general", "DisablePortalCreation", false).comment = "Disable Twilight Forest portal creation entirely.  Provided for server operators looking to restrict action to the dimension.";
		disableUncrafting = configFile.get("general", "DisableUncrafting", false).getBoolean(false);
		configFile.get("general", "DisableUncrafting", false).comment = "Disable the uncrafting function of the uncrafting table.  Provided as an option when interaction with other mods produces exploitable recipes.";
		oldMapGen = configFile.get("general", "OldMapGen", false).getBoolean(false);
		configFile.get("general", "OldMapGen", false).comment = "Use old (pre Minecraft 1.7) map gen.  May not be fully supported.";
		portalCreationItemString = configFile.get("general", "PortalCreationItem", "diamond").getString();
		configFile.get("general", "PortalCreationItem", "diamond").comment = "Item to create the Twilight Forest Portal.  Defaults to \'diamond\'";
		canopyCoverage = (float) configFile.get("Performance", "CanopyCoverage", 1.7D).getDouble(1.7D);
		configFile.get("performance", "CanopyCoverage", 1.7D).comment = "Amount of canopy coverage, from 0.0 on up.  Lower numbers improve chunk generation speed at the cost of a thinner forest.";
		twilightOakChance = configFile.get("Performance", "TwilightOakChance", 48).getInt(48);
		configFile.get("Performance", "TwilightOakChance", 48).comment = "Chance that a chunk in the Twilight Forest will contain a twilight oak tree.  Higher numbers reduce the number of trees, increasing performance.";
		idMobWildBoar = 177;
		idMobBighornSheep = 178;
		idMobWildDeer = 179;
		idMobRedcap = 180;
		idMobSwarmSpider = 181;
		idMobNaga = 182;
		idMobNagaSegment = 183;
		idMobSkeletonDruid = 184;
		idMobHostileWolf = 185;
		idMobTwilightWraith = 186;
		idMobHedgeSpider = 187;
		idMobHydra = 189;
		idMobLich = 190;
		idMobPenguin = 191;
		idMobLichMinion = 192;
		idMobLoyalZombie = 193;
		idMobTinyBird = 194;
		idMobSquirrel = 195;
		idMobBunny = 196;
		idMobRaven = 197;
		idMobQuestRam = 198;
		idMobKobold = 199;
		idMobBoggard = 201;
		idMobMosquitoSwarm = 202;
		idMobDeathTome = 203;
		idMobMinotaur = 204;
		idMobMinoshroom = 205;
		idMobFireBeetle = 206;
		idMobSlimeBeetle = 207;
		idMobPinchBeetle = 208;
		idMobMazeSlime = 209;
		idMobRedcapSapper = 210;
		idMobMistWolf = 211;
		idMobKingSpider = 212;
		idMobFirefly = 213;
		idMobMiniGhast = 214;
		idMobTowerGhast = 215;
		idMobTowerGolem = 216;
		idMobTowerTermite = 218;
		idMobTowerBroodling = 219;
		idMobTowerBoss = 217;
		idMobBlockGoblin = 220;
		idMobGoblinKnightUpper = 221;
		idMobGoblinKnightLower = 222;
		idMobHelmetCrab = 223;
		idMobKnightPhantom = 224;
		idMobYeti = 225;
		idMobYetiBoss = 226;
		idMobWinterWolf = 227;
		idMobSnowGuardian = 228;
		idMobStableIceCore = 229;
		idMobUnstableIceCore = 230;
		idMobSnowQueen = 231;
		idMobTroll = 232;
		idMobGiantMiner = 233;
		idMobArmoredGiant = 234;
		idMobIceCrystal = 235;
		idMobApocalypseCube = 236;
		idMobAdherent = 237;
		idBiomeLake = configFile.get("biome", "biome.id.Lake", -1).getInt();
		idBiomeTwilightForest = configFile.get("biome", "biome.id.TwilightForest", -1).getInt();
		idBiomeTwilightForestVariant = configFile.get("biome", "biome.id.TwilightForestVariant", -1).getInt();
		idBiomeHighlands = configFile.get("biome", "biome.id.Highlands", -1).getInt();
		idBiomeMushrooms = configFile.get("biome", "biome.id.Mushrooms", -1).getInt();
		idBiomeSwamp = configFile.get("biome", "biome.id.Swamp", -1).getInt();
		idBiomeStream = configFile.get("biome", "biome.id.Stream", -1).getInt();
		idBiomeSnowfield = configFile.get("biome", "biome.id.Snowfield", -1).getInt();
		idBiomeGlacier = configFile.get("biome", "biome.id.Glacier", -1).getInt();
		idBiomeClearing = configFile.get("biome", "biome.id.Clearing", -1).getInt();
		idBiomeOakSavanna = configFile.get("biome", "biome.id.OakSavanna", -1).getInt();
		idBiomeFireflyForest = configFile.get("biome", "biome.id.LightedForest", -1).getInt();
		idBiomeDeepMushrooms = configFile.get("biome", "biome.id.DeepMushrooms", -1).getInt();
		idBiomeDarkForestCenter = configFile.get("biome", "biome.id.DarkForestCenter", -1).getInt();
		idBiomeHighlandsCenter = configFile.get("biome", "biome.id.HighlandsCenter", -1).getInt();
		idBiomeDarkForest = configFile.get("biome", "biome.id.DarkForest", -1).getInt();
		idBiomeEnchantedForest = configFile.get("biome", "biome.id.EnchantedForest", -1).getInt();
		idBiomeFireSwamp = configFile.get("biome", "biome.id.FireSwamp", -1).getInt();
		idBiomeThornlands = configFile.get("biome", "biome.id.Thornlands", -1).getInt();
		if (configFile.hasChanged())
			configFile.save();

	}

	private void saveBiomeIds(Configuration config)
	{
		config.get("biome", "biome.id.Lake", -1).set(idBiomeLake);
		config.get("biome", "biome.id.TwilightForest", -1).set(idBiomeTwilightForest);
		config.get("biome", "biome.id.TwilightForestVariant", -1).set(idBiomeTwilightForestVariant);
		config.get("biome", "biome.id.Highlands", -1).set(idBiomeHighlands);
		config.get("biome", "biome.id.Mushrooms", -1).set(idBiomeMushrooms);
		config.get("biome", "biome.id.Swamp", -1).set(idBiomeSwamp);
		config.get("biome", "biome.id.Stream", -1).set(idBiomeStream);
		config.get("biome", "biome.id.Snowfield", -1).set(idBiomeSnowfield);
		config.get("biome", "biome.id.Glacier", -1).set(idBiomeGlacier);
		config.get("biome", "biome.id.Clearing", -1).set(idBiomeClearing);
		config.get("biome", "biome.id.OakSavanna", -1).set(idBiomeOakSavanna);
		config.get("biome", "biome.id.LightedForest", -1).set(idBiomeFireflyForest);
		config.get("biome", "biome.id.DeepMushrooms", -1).set(idBiomeDeepMushrooms);
		config.get("biome", "biome.id.DarkForestCenter", -1).set(idBiomeDarkForestCenter);
		config.get("biome", "biome.id.HighlandsCenter", -1).set(idBiomeHighlandsCenter);
		config.get("biome", "biome.id.DarkForest", -1).set(idBiomeDarkForest);
		config.get("biome", "biome.id.EnchantedForest", -1).set(idBiomeEnchantedForest);
		config.get("biome", "biome.id.FireSwamp", -1).set(idBiomeFireSwamp);
		config.get("biome", "biome.id.Thornlands", -1).set(idBiomeThornlands);
		config.save();
	}

	public static void setDimensionID(int dim)
	{
		if (dimensionID != dim)
		{
			FMLLog.info("[TwilightForest] Server has a different dimension ID (%d) for the Twilight Forest.  Changing this on the client.  This change will not be saved.", dim);
			DimensionManager.unregisterDimension(dimensionID);
			dimensionID = dim;
			DimensionManager.registerDimension(dimensionID, dimensionProviderID);
		}

	}
}
