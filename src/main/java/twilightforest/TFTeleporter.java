package twilightforest;

import com.gamerforea.twilightforest.EventConfig;
import cpw.mods.fml.common.FMLLog;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFWorld;

import java.util.Random;

public class TFTeleporter extends Teleporter
{
	protected WorldServer myWorld;
	protected Random rand;

	// TODO gamerforEA code start
	private static final TIntObjectMap<ChunkPosition> PORTAL_COORDS_CACHE = new TIntObjectHashMap<>();
	// TODO gamerforEA code end

	public TFTeleporter(WorldServer par1WorldServer)
	{
		super(par1WorldServer);
		this.myWorld = par1WorldServer;
		if (this.rand == null)
			this.rand = new Random();

	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float facing)
	{
		// TODO gamerforEA add condition [1]
		if (!EventConfig.enableAutoPortalGen || !this.placeInExistingPortal(entity, x, y, z, facing))
		{
			if (entity.worldObj.getGameRules().getGameRuleBooleanValue("tfEnforcedProgression"))
			{
				int px = MathHelper.floor_double(entity.posX);
				int pz = MathHelper.floor_double(entity.posZ);
				if (!this.isSafeBiomeAt(px, pz, entity))
				{
					System.out.println("[TwilightForest] Portal destination looks unsafe, rerouting!");
					ChunkCoordinates safeCoords = this.findSafeCoords(200, px, pz, entity);
					if (safeCoords != null)
					{
						entity.setLocationAndAngles((double) safeCoords.posX, entity.posY, (double) safeCoords.posZ, 90.0F, 0.0F);
						x = (double) safeCoords.posX;
						z = (double) safeCoords.posZ;
						System.out.println("[TwilightForest] Safely rerouted!");
					}
					else
					{
						System.out.println("[TwilightForest] Did not find a safe spot at first try, trying again with longer range.");
						safeCoords = this.findSafeCoords(400, px, pz, entity);
						if (safeCoords != null)
						{
							entity.setLocationAndAngles((double) safeCoords.posX, entity.posY, (double) safeCoords.posZ, 90.0F, 0.0F);
							x = (double) safeCoords.posX;
							z = (double) safeCoords.posZ;
							System.out.println("[TwilightForest] Safely rerouted to long range portal.  Return trip not guaranteed.");
						}
						else
							System.out.println("[TwilightForest] Did not find a safe spot.");
					}
				}
			}

			// TODO gamerforEA code start
			if (!EventConfig.enableAutoPortalGen)
			{
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, 0);
				entity.motionX = entity.motionY = entity.motionZ = 0;
				return;
			}
			// TODO gamerforEA code end

			this.makePortal(entity);
			this.placeInExistingPortal(entity, x, y, z, facing);
		}
	}

	private ChunkCoordinates findSafeCoords(int range, int x, int z, Entity par1Entity)
	{
		for (int i = 0; i < 25; ++i)
		{
			int dx = x + this.rand.nextInt(range) - this.rand.nextInt(range);
			int dz = z + this.rand.nextInt(range) - this.rand.nextInt(range);
			if (this.isSafeBiomeAt(dx, dz, par1Entity))
				return new ChunkCoordinates(dx, 100, dz);
		}

		return null;
	}

	boolean isSafeBiomeAt(int x, int z, Entity par1Entity)
	{
		BiomeGenBase biomeAt = this.myWorld.getBiomeGenForCoords(x, z);
		if (biomeAt instanceof TFBiomeBase && par1Entity instanceof EntityPlayerMP)
		{
			TFBiomeBase tfBiome = (TFBiomeBase) biomeAt;
			EntityPlayerMP player = (EntityPlayerMP) par1Entity;
			return tfBiome.doesPlayerHaveRequiredAchievement(player);
		}
		return true;
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double par3, double par5, double par7, float par9)
	{
		World world = this.myWorld;
		int range = 200;
		double portalDistanceSq = -1.0D;
		int portalBlockX = 0;
		int portalBlockY = 0;
		int portalBlockZ = 0;
		int entityX = MathHelper.floor_double(entity.posX);
		int entityZ = MathHelper.floor_double(entity.posZ);

		// TODO gamerforEA code start
		boolean needSearch = true;

		int dimensionId = world.provider.dimensionId;
		if (EventConfig.cachePortalCoords)
		{
			ChunkPosition cachedPortalCoords = PORTAL_COORDS_CACHE.get(dimensionId);
			if (cachedPortalCoords != null)
			{
				int x = cachedPortalCoords.chunkPosX;
				int y = cachedPortalCoords.chunkPosY;
				int z = cachedPortalCoords.chunkPosZ;
				if (this.isBlockPortal(world, x, y, z))
				{
					portalDistanceSq = entity.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
					portalBlockX = x;
					portalBlockY = y;
					portalBlockZ = z;
					needSearch = false;
				}
				else
					PORTAL_COORDS_CACHE.remove(dimensionId);
			}
		}
		if (needSearch)
			// TODO gamerforEA code end
			for (int x = entityX - range; x <= entityX + range; ++x)
			{
				double distanceX = (double) x + 0.5D - entity.posX;

				for (int z = entityZ - range; z <= entityZ + range; ++z)
				{
					double distanceZ = (double) z + 0.5D - entity.posZ;

					for (int y = TFWorld.MAXHEIGHT - 1; y >= 0; --y)
					{
						if (this.isBlockPortal(world, x, y, z))
						{
							while (this.isBlockPortal(world, x, y - 1, z))
							{
								--y;
							}

							double distanceY = (double) y + 0.5D - entity.posY;
							double distanceSq = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
							if (portalDistanceSq < 0.0D || distanceSq < portalDistanceSq)
							{
								portalDistanceSq = distanceSq;
								portalBlockX = x;
								portalBlockY = y;
								portalBlockZ = z;
							}
						}
					}
				}
			}

		if (portalDistanceSq < 0.0D)
			return false;

		// TODO gamerforEA code start
		if (EventConfig.cachePortalCoords && needSearch)
			PORTAL_COORDS_CACHE.put(dimensionId, new ChunkPosition(portalBlockX, portalBlockY, portalBlockZ));
		// TODO gamerforEA code end

		double portalX = (double) portalBlockX + 0.5D;
		double portalY = (double) portalBlockY + 0.5D;
		double portalZ = (double) portalBlockZ + 0.5D;
		if (this.isBlockPortal(world, portalBlockX - 1, portalBlockY, portalBlockZ))
			portalX -= 0.5D;

		if (this.isBlockPortal(world, portalBlockX + 1, portalBlockY, portalBlockZ))
			portalX += 0.5D;

		if (this.isBlockPortal(world, portalBlockX, portalBlockY, portalBlockZ - 1))
			portalZ -= 0.5D;

		if (this.isBlockPortal(world, portalBlockX, portalBlockY, portalBlockZ + 1))
			portalZ += 0.5D;

		int xOffset = 0;

		int zOffset;
		for (zOffset = 0; xOffset == zOffset && xOffset == 0; zOffset = this.rand.nextInt(3) - this.rand.nextInt(3))
		{
			xOffset = this.rand.nextInt(3) - this.rand.nextInt(3);
		}

		entity.setLocationAndAngles(portalX + (double) xOffset, portalY + 1.0D, portalZ + (double) zOffset, entity.rotationYaw, 0.0F);
		entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		return true;
	}

	public boolean isBlockPortal(World world, int x, int y, int z)
	{
		return world.getBlock(x, y, z) == TFBlocks.portal;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		ChunkCoordinates spot = this.findPortalCoords(entity, true);
		if (spot != null)
		{
			FMLLog.info("[TwilightForest] Found ideal portal spot");
			this.makePortalAt(this.myWorld, spot.posX, spot.posY, spot.posZ);
			return true;
		}
		FMLLog.info("[TwilightForest] Did not find ideal portal spot, shooting for okay one");
		spot = this.findPortalCoords(entity, false);
		if (spot != null)
		{
			FMLLog.info("[TwilightForest] Found okay portal spot");
			this.makePortalAt(this.myWorld, spot.posX, spot.posY, spot.posZ);
			return true;
		}
		FMLLog.info("[TwilightForest] Did not even find an okay portal spot, just making a random one");
		double yFactor = this.myWorld.provider.dimensionId == 0 ? 2.0D : 0.5D;
		int entityX = MathHelper.floor_double(entity.posX);
		int entityY = MathHelper.floor_double(entity.posY * yFactor);
		int entityZ = MathHelper.floor_double(entity.posZ);
		this.makePortalAt(this.myWorld, entityX, entityY, entityZ);
		return false;
	}

	public ChunkCoordinates findPortalCoords(Entity entity, boolean ideal)
	{
		double yFactor = this.myWorld.provider.dimensionId == 0 ? 2.0D : 0.5D;
		int entityX = MathHelper.floor_double(entity.posX);
		int entityZ = MathHelper.floor_double(entity.posZ);
		double spotWeight = -1.0D;
		ChunkCoordinates spot = null;
		byte range = 16;

		for (int rx = entityX - range; rx <= entityX + range; ++rx)
		{
			double xWeight = (double) rx + 0.5D - entity.posX;

			for (int rz = entityZ - range; rz <= entityZ + range; ++rz)
			{
				double zWeight = (double) rz + 0.5D - entity.posZ;

				for (int ry = 127; ry >= 0; --ry)
				{
					if (this.myWorld.isAirBlock(rx, ry, rz))
					{
						while (ry > 0 && this.myWorld.isAirBlock(rx, ry - 1, rz))
						{
							--ry;
						}

						if (ideal)
						{
							if (!this.isIdealPortal(rx, rz, ry))
								continue;
						}
						else if (!this.isOkayPortal(rx, rz, ry))
							continue;

						double yWeight = (double) ry + 0.5D - entity.posY * yFactor;
						double rPosWeight = xWeight * xWeight + yWeight * yWeight + zWeight * zWeight;
						if (spotWeight < 0.0D || rPosWeight < spotWeight)
						{
							spotWeight = rPosWeight;
							spot = new ChunkCoordinates(rx, ry, rz);
						}
					}
				}
			}
		}

		return spot;
	}

	public boolean isIdealPortal(int rx, int rz, int ry)
	{
		for (int potentialZ = 0; potentialZ < 4; ++potentialZ)
		{
			for (int potentialX = 0; potentialX < 4; ++potentialX)
			{
				for (int potentialY = -1; potentialY < 3; ++potentialY)
				{
					int tx = rx + potentialX - 1;
					int ty = ry + potentialY;
					int tz = rz + potentialZ - 1;
					if (potentialY == -1 && this.myWorld.getBlock(tx, ty, tz).getMaterial() != Material.grass || potentialY >= 0 && !this.myWorld.getBlock(tx, ty, tz).getMaterial().isReplaceable())
						return false;
				}
			}
		}

		return true;
	}

	public boolean isOkayPortal(int rx, int rz, int ry)
	{
		for (int potentialZ = 0; potentialZ < 4; ++potentialZ)
		{
			for (int potentialX = 0; potentialX < 4; ++potentialX)
			{
				for (int potentialY = -1; potentialY < 3; ++potentialY)
				{
					int tx = rx + potentialX - 1;
					int ty = ry + potentialY;
					int tz = rz + potentialZ - 1;
					if (potentialY == -1 && !this.myWorld.getBlock(tx, ty, tz).getMaterial().isSolid() || potentialY >= 0 && !this.myWorld.getBlock(tx, ty, tz).getMaterial().isReplaceable())
						return false;
				}
			}
		}

		return true;
	}

	private void makePortalAt(World world, int px, int py, int pz)
	{
		if (py < 30)
			py = 30;

		world.getClass();
		if (py > 118)
		{
			world.getClass();
			py = 118;
		}

		--py;
		world.setBlock(px - 1, py, pz - 1, Blocks.grass);
		world.setBlock(px, py, pz - 1, Blocks.grass);
		world.setBlock(px + 1, py, pz - 1, Blocks.grass);
		world.setBlock(px + 2, py, pz - 1, Blocks.grass);
		world.setBlock(px - 1, py, pz, Blocks.grass);
		world.setBlock(px + 2, py, pz, Blocks.grass);
		world.setBlock(px - 1, py, pz + 1, Blocks.grass);
		world.setBlock(px + 2, py, pz + 1, Blocks.grass);
		world.setBlock(px - 1, py, pz + 2, Blocks.grass);
		world.setBlock(px, py, pz + 2, Blocks.grass);
		world.setBlock(px + 1, py, pz + 2, Blocks.grass);
		world.setBlock(px + 2, py, pz + 2, Blocks.grass);
		world.setBlock(px, py - 1, pz, Blocks.dirt);
		world.setBlock(px + 1, py - 1, pz, Blocks.dirt);
		world.setBlock(px, py - 1, pz + 1, Blocks.dirt);
		world.setBlock(px + 1, py - 1, pz + 1, Blocks.dirt);
		world.setBlock(px, py, pz, TFBlocks.portal, 0, 2);
		world.setBlock(px + 1, py, pz, TFBlocks.portal, 0, 2);
		world.setBlock(px, py, pz + 1, TFBlocks.portal, 0, 2);
		world.setBlock(px + 1, py, pz + 1, TFBlocks.portal, 0, 2);

		for (int dx = -1; dx <= 2; ++dx)
		{
			for (int dz = -1; dz <= 2; ++dz)
			{
				for (int dy = 1; dy <= 5; ++dy)
				{
					world.setBlock(px + dx, py + dy, pz + dz, Blocks.air);
				}
			}
		}

		world.setBlock(px - 1, py + 1, pz - 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px, py + 1, pz - 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 1, py + 1, pz - 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 2, py + 1, pz - 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px - 1, py + 1, pz, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 2, py + 1, pz, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px - 1, py + 1, pz + 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 2, py + 1, pz + 1, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px - 1, py + 1, pz + 2, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px, py + 1, pz + 2, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 1, py + 1, pz + 2, this.randNatureBlock(world.rand), 0, 2);
		world.setBlock(px + 2, py + 1, pz + 2, this.randNatureBlock(world.rand), 0, 2);
	}

	public Block randNatureBlock(Random random)
	{
		Block[] block = { Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.tallgrass, Blocks.red_flower, Blocks.yellow_flower };
		return block[random.nextInt(block.length)];
	}
}
