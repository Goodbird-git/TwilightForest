package twilightforest.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twilightforest.TFAchievementPage;
import twilightforest.TFTeleporter;
import twilightforest.TwilightForestMod;

import java.util.List;
import java.util.Random;

public class BlockTFPortal extends BlockBreakable
{
	public BlockTFPortal()
	{
		super("TFPortal", Material.portal, false);
		this.setHardness(-1.0F);
		this.setStepSound(Block.soundTypeGlass);
		this.setLightLevel(0.75F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
	{
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.portal.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
	}

	public boolean tryToCreatePortal(World world, int dx, int dy, int dz)
	{
		if (this.isGoodPortalPool(world, dx, dy, dz))
		{
			world.addWeatherEffect(new EntityLightningBolt(world, dx, dy, dz));
			this.transmuteWaterToPortal(world, dx, dy, dz);
			return true;
		}
		else
			return false;
	}

	public void transmuteWaterToPortal(World world, int dx, int dy, int dz)
	{
		int px = dx;
		int pz = dz;
		if (world.getBlock(dx - 1, dy, dz).getMaterial() == Material.water)
			px = dx - 1;

		if (world.getBlock(px, dy, dz - 1).getMaterial() == Material.water)
			pz = dz - 1;

		world.setBlock(px, dy, pz, TFBlocks.portal, 0, 2);
		world.setBlock(px + 1, dy, pz, TFBlocks.portal, 0, 2);
		world.setBlock(px + 1, dy, pz + 1, TFBlocks.portal, 0, 2);
		world.setBlock(px, dy, pz + 1, TFBlocks.portal, 0, 2);
	}

	public boolean isGoodPortalPool(World world, int dx, int dy, int dz)
	{
		boolean flag = false;
		flag |= this.isGoodPortalPoolStrict(world, dx, dy, dz);
		flag |= this.isGoodPortalPoolStrict(world, dx - 1, dy, dz - 1);
		flag |= this.isGoodPortalPoolStrict(world, dx, dy, dz - 1);
		flag |= this.isGoodPortalPoolStrict(world, dx + 1, dy, dz - 1);
		flag |= this.isGoodPortalPoolStrict(world, dx - 1, dy, dz);
		flag |= this.isGoodPortalPoolStrict(world, dx + 1, dy, dz);
		flag |= this.isGoodPortalPoolStrict(world, dx - 1, dy, dz + 1);
		flag |= this.isGoodPortalPoolStrict(world, dx, dy, dz + 1);
		flag |= this.isGoodPortalPoolStrict(world, dx + 1, dy, dz + 1);
		return flag;
	}

	public boolean isGoodPortalPoolStrict(World world, int dx, int dy, int dz)
	{
		boolean flag = true;
		flag &= world.getBlock(dx, dy, dz).getMaterial() == Material.water;
		flag &= world.getBlock(dx + 1, dy, dz).getMaterial() == Material.water;
		flag &= world.getBlock(dx + 1, dy, dz + 1).getMaterial() == Material.water;
		flag &= world.getBlock(dx, dy, dz + 1).getMaterial() == Material.water;
		flag &= this.isGrassOrDirt(world, dx - 1, dy, dz - 1);
		flag &= this.isGrassOrDirt(world, dx - 1, dy, dz);
		flag &= this.isGrassOrDirt(world, dx - 1, dy, dz + 1);
		flag &= this.isGrassOrDirt(world, dx - 1, dy, dz + 2);
		flag &= this.isGrassOrDirt(world, dx, dy, dz - 1);
		flag &= this.isGrassOrDirt(world, dx + 1, dy, dz - 1);
		flag &= this.isGrassOrDirt(world, dx, dy, dz + 2);
		flag &= this.isGrassOrDirt(world, dx + 1, dy, dz + 2);
		flag &= this.isGrassOrDirt(world, dx + 2, dy, dz - 1);
		flag &= this.isGrassOrDirt(world, dx + 2, dy, dz);
		flag &= this.isGrassOrDirt(world, dx + 2, dy, dz + 1);
		flag &= this.isGrassOrDirt(world, dx + 2, dy, dz + 2);
		flag &= world.getBlock(dx, dy - 1, dz).getMaterial().isSolid();
		flag &= world.getBlock(dx + 1, dy - 1, dz).getMaterial().isSolid();
		flag &= world.getBlock(dx + 1, dy - 1, dz + 1).getMaterial().isSolid();
		flag &= world.getBlock(dx, dy - 1, dz + 1).getMaterial().isSolid();
		flag &= this.isNatureBlock(world, dx - 1, dy + 1, dz - 1);
		flag &= this.isNatureBlock(world, dx - 1, dy + 1, dz);
		flag &= this.isNatureBlock(world, dx - 1, dy + 1, dz + 1);
		flag &= this.isNatureBlock(world, dx - 1, dy + 1, dz + 2);
		flag &= this.isNatureBlock(world, dx, dy + 1, dz - 1);
		flag &= this.isNatureBlock(world, dx + 1, dy + 1, dz - 1);
		flag &= this.isNatureBlock(world, dx, dy + 1, dz + 2);
		flag &= this.isNatureBlock(world, dx + 1, dy + 1, dz + 2);
		flag &= this.isNatureBlock(world, dx + 2, dy + 1, dz - 1);
		flag &= this.isNatureBlock(world, dx + 2, dy + 1, dz);
		flag &= this.isNatureBlock(world, dx + 2, dy + 1, dz + 1);
		flag &= this.isNatureBlock(world, dx + 2, dy + 1, dz + 2);
		return flag;
	}

	public boolean isNatureBlock(World world, int dx, int dy, int dz)
	{
		Material mat = world.getBlock(dx, dy, dz).getMaterial();
		return mat == Material.plants || mat == Material.vine || mat == Material.leaves;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block notUsed)
	{
		boolean good = true;
		if (world.getBlock(x - 1, y, z) == this)
			good &= this.isGrassOrDirt(world, x + 1, y, z);
		else if (world.getBlock(x + 1, y, z) == this)
			good &= this.isGrassOrDirt(world, x - 1, y, z);
		else
			good = false;

		if (world.getBlock(x, y, z - 1) == this)
			good &= this.isGrassOrDirt(world, x, y, z + 1);
		else if (world.getBlock(x, y, z + 1) == this)
			good &= this.isGrassOrDirt(world, x, y, z - 1);
		else
			good = false;

		if (!good)
			world.setBlock(x, y, z, Blocks.water, 0, 3);

	}

	protected boolean isGrassOrDirt(World world, int dx, int dy, int dz)
	{
		return world.getBlock(dx, dy, dz).getMaterial() == Material.grass || world.getBlock(dx, dy, dz).getMaterial() == Material.ground;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
	{
		if (entity.ridingEntity == null && entity.riddenByEntity == null && entity.timeUntilPortal <= 0)
		{
			// TODO gamerforEA code start
			if (!(entity instanceof EntityPlayer))
				return;
			// TODO gamerforEA code end

			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP playerMP = (EntityPlayerMP) entity;
				if (playerMP.timeUntilPortal > 0)
					playerMP.timeUntilPortal = 10;
				else if (playerMP.dimension != TwilightForestMod.dimensionID)
				{
					playerMP.triggerAchievement(TFAchievementPage.twilightPortal);
					playerMP.triggerAchievement(TFAchievementPage.twilightArrival);
					System.out.println("Player touched the portal block.  Sending the player to dimension 7");
					playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, TwilightForestMod.dimensionID, new TFTeleporter(playerMP.mcServer.worldServerForDimension(TwilightForestMod.dimensionID)));
					playerMP.addExperienceLevel(0);
					playerMP.triggerAchievement(TFAchievementPage.twilightPortal);
					playerMP.triggerAchievement(TFAchievementPage.twilightArrival);
					int spawnX = MathHelper.floor_double(playerMP.posX);
					int spawnY = MathHelper.floor_double(playerMP.posY);
					int spawnZ = MathHelper.floor_double(playerMP.posZ);
					playerMP.setSpawnChunk(new ChunkCoordinates(spawnX, spawnY, spawnZ), true, TwilightForestMod.dimensionID);
				}
				else
				{
					playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0, new TFTeleporter(playerMP.mcServer.worldServerForDimension(0)));
					playerMP.addExperienceLevel(0);
				}
			}
			else if (entity.dimension == TwilightForestMod.dimensionID)
				this.sendEntityToDimension(entity, 0);
		}
	}

	public void sendEntityToDimension(Entity entity, int par1)
	{
		if (!entity.worldObj.isRemote && !entity.isDead)
		{
			entity.worldObj.theProfiler.startSection("changeDimension");
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			int dim = entity.dimension;
			WorldServer worldserver = minecraftserver.worldServerForDimension(dim);
			WorldServer worldserver1 = minecraftserver.worldServerForDimension(par1);
			entity.dimension = par1;
			entity.worldObj.removeEntity(entity);
			entity.isDead = false;
			entity.worldObj.theProfiler.startSection("reposition");
			minecraftserver.getConfigurationManager().transferEntityToWorld(entity, dim, worldserver, worldserver1, new TFTeleporter(worldserver1));
			entity.worldObj.theProfiler.endStartSection("reloading");
			Entity transferEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), worldserver1);
			if (transferEntity != null)
			{
				transferEntity.copyDataFrom(entity, true);
				worldserver1.spawnEntityInWorld(transferEntity);
			}

			entity.isDead = true;
			entity.worldObj.theProfiler.endSection();
			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
			entity.worldObj.theProfiler.endSection();
		}

	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random)
	{
		if (random.nextInt(100) == 0)
			world.playSoundEffect(i + 0.5D, j + 0.5D, k + 0.5D, "portal.portal", 1.0F, random.nextFloat() * 0.4F + 0.8F);

		for (int l = 0; l < 4; ++l)
		{
			double d = i + random.nextFloat();
			double d1 = j + random.nextFloat();
			double d2 = k + random.nextFloat();
			double d3 = 0.0D;
			double d4 = 0.0D;
			double d5 = 0.0D;
			int i1 = random.nextInt(2) * 2 - 1;
			d3 = (random.nextFloat() - 0.5D) * 0.5D;
			d4 = (random.nextFloat() - 0.5D) * 0.5D;
			d5 = (random.nextFloat() - 0.5D) * 0.5D;
			if (world.getBlock(i - 1, j, k) != this && world.getBlock(i + 1, j, k) != this)
			{
				d = i + 0.5D + 0.25D * i1;
				d3 = random.nextFloat() * 2.0F * i1;
			}
			else
			{
				d2 = k + 0.5D + 0.25D * i1;
				d5 = random.nextFloat() * 2.0F * i1;
			}

			world.spawnParticle("portal", d, d1, d2, d3, d4, d5);
		}

	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
	}
}
