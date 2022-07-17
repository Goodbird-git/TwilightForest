package twilightforest.entity.boss;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import twilightforest.TwilightForestMod;

import java.util.List;

public class HydraHeadContainer
{
	private static int FLAME_BURN_FACTOR = 3;
	private static int FLAME_DAMAGE = 19;
	private static int BITE_DAMAGE = 48;
	private static double FLAME_BREATH_TRACKING_SPEED = 0.04D;
	public static final int NEXT_AUTOMATIC = -1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_BITE_BEGINNING = 1;
	public static final int STATE_BITE_READY = 2;
	public static final int STATE_BITE_BITING = 3;
	public static final int STATE_BITE_ENDING = 4;
	public static final int STATE_FLAME_BEGINNING = 5;
	public static final int STATE_FLAME_BREATHING = 6;
	public static final int STATE_FLAME_ENDING = 7;
	public static final int STATE_MORTAR_BEGINNING = 8;
	public static final int STATE_MORTAR_LAUNCH = 9;
	public static final int STATE_MORTAR_ENDING = 10;
	public static final int STATE_DYING = 11;
	public static final int STATE_DEAD = 12;
	public static final int STATE_ATTACK_COOLDOWN = 13;
	public static final int STATE_BORN = 14;
	public static final int STATE_ROAR_START = 15;
	public static final int STATE_ROAR_RAWR = 16;
	public static final int NUM_STATES = 17;
	public EntityTFHydraHead headEntity;
	public EntityTFHydraNeck necka;
	public EntityTFHydraNeck neckb;
	public EntityTFHydraNeck neckc;
	public EntityTFHydraNeck neckd;
	public EntityTFHydraNeck necke;
	public Entity targetEntity;
	public double targetX;
	public double targetY;
	public double targetZ;
	public int prevState;
	public int currentState;
	public int nextState = -1;
	public boolean didRoar;
	public boolean isSecondaryAttacking;
	public int ticksNeeded;
	public int ticksProgress;
	public final int headNum;
	public int damageTaken;
	public int respawnCounter;
	public final EntityTFHydra hydraObj;
	public int[] nextStates;
	public int[] stateDurations;
	public float[][] stateNeckLength;
	public float[][] stateXRotations;
	public float[][] stateYRotations;
	public float[][] stateMouthOpen;

	public HydraHeadContainer(EntityTFHydra hydra, int number, boolean startActive)
	{
		this.headNum = number;
		this.hydraObj = hydra;
		this.damageTaken = 0;
		this.respawnCounter = -1;
		this.didRoar = false;
		this.necka = new EntityTFHydraNeck(this.hydraObj, "neck" + this.headNum + "a", 2.0F, 2.0F);
		this.neckb = new EntityTFHydraNeck(this.hydraObj, "neck" + this.headNum + "b", 2.0F, 2.0F);
		this.neckc = new EntityTFHydraNeck(this.hydraObj, "neck" + this.headNum + "c", 2.0F, 2.0F);
		this.neckd = new EntityTFHydraNeck(this.hydraObj, "neck" + this.headNum + "d", 2.0F, 2.0F);
		this.necke = new EntityTFHydraNeck(this.hydraObj, "neck" + this.headNum + "e", 2.0F, 2.0F);
		this.nextStates = new int[17];
		this.nextStates[0] = 0;
		this.nextStates[1] = 2;
		this.nextStates[2] = 3;
		this.nextStates[3] = 4;
		this.nextStates[4] = 13;
		this.nextStates[5] = 6;
		this.nextStates[6] = 7;
		this.nextStates[7] = 13;
		this.nextStates[8] = 9;
		this.nextStates[9] = 10;
		this.nextStates[10] = 13;
		this.nextStates[13] = 0;
		this.nextStates[11] = 12;
		this.nextStates[12] = 12;
		this.nextStates[14] = 15;
		this.nextStates[15] = 16;
		this.nextStates[16] = 0;
		this.stateDurations = new int[17];
		this.setupStateDurations();
		this.stateNeckLength = new float[this.hydraObj.numHeads][17];
		this.stateXRotations = new float[this.hydraObj.numHeads][17];
		this.stateYRotations = new float[this.hydraObj.numHeads][17];
		this.stateMouthOpen = new float[this.hydraObj.numHeads][17];
		this.setupStateRotations();
		if (startActive)
		{
			this.prevState = 0;
			this.currentState = 0;
			this.nextState = -1;
			this.ticksNeeded = 60;
			this.ticksProgress = 60;
		}
		else
		{
			this.prevState = 12;
			this.currentState = 12;
			this.nextState = -1;
			this.ticksNeeded = 20;
			this.ticksProgress = 20;
		}

	}

	protected void setupStateDurations()
	{
		this.stateDurations[0] = 10;
		this.stateDurations[1] = 40;
		this.stateDurations[2] = 80;
		this.stateDurations[3] = 7;
		this.stateDurations[4] = 40;
		this.stateDurations[5] = 40;
		this.stateDurations[6] = 100;
		this.stateDurations[7] = 30;
		this.stateDurations[8] = 40;
		this.stateDurations[9] = 25;
		this.stateDurations[10] = 30;
		this.stateDurations[13] = 80;
		this.stateDurations[11] = 70;
		this.stateDurations[12] = 20;
		this.stateDurations[14] = 20;
		this.stateDurations[15] = 10;
		this.stateDurations[16] = 50;
	}

	protected void setupStateRotations()
	{
		this.setAnimation(0, 0, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 0, 10.0F, 60.0F, 9.0F, 0.0F);
		this.setAnimation(2, 0, 10.0F, -60.0F, 9.0F, 0.0F);
		this.setAnimation(3, 0, 50.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 0, 50.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 0, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 0, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 13, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 13, 10.0F, 60.0F, 9.0F, 0.0F);
		this.setAnimation(2, 13, 10.0F, -60.0F, 9.0F, 0.0F);
		this.setAnimation(3, 13, 50.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 13, 50.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 13, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 13, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 5, 50.0F, 0.0F, 8.0F, 0.75F);
		this.setAnimation(1, 5, 30.0F, 45.0F, 9.0F, 0.75F);
		this.setAnimation(2, 5, 30.0F, -45.0F, 9.0F, 0.75F);
		this.setAnimation(3, 5, 50.0F, 90.0F, 8.0F, 0.75F);
		this.setAnimation(4, 5, 50.0F, -90.0F, 8.0F, 0.75F);
		this.setAnimation(5, 5, -10.0F, 90.0F, 9.0F, 0.75F);
		this.setAnimation(6, 5, -10.0F, -90.0F, 9.0F, 0.75F);
		this.setAnimation(0, 6, 45.0F, 0.0F, 8.0F, 1.0F);
		this.setAnimation(1, 6, 30.0F, 60.0F, 9.0F, 1.0F);
		this.setAnimation(2, 6, 30.0F, -60.0F, 9.0F, 1.0F);
		this.setAnimation(3, 6, 50.0F, 90.0F, 8.0F, 1.0F);
		this.setAnimation(4, 6, 50.0F, -90.0F, 8.0F, 1.0F);
		this.setAnimation(5, 6, -10.0F, 90.0F, 9.0F, 1.0F);
		this.setAnimation(6, 6, -10.0F, -90.0F, 9.0F, 1.0F);
		this.setAnimation(0, 7, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 7, 10.0F, 45.0F, 9.0F, 0.0F);
		this.setAnimation(2, 7, 10.0F, -45.0F, 9.0F, 0.0F);
		this.setAnimation(3, 7, 50.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 7, 50.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 7, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 7, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 1, -5.0F, 60.0F, 5.0F, 0.25F);
		this.setAnimation(1, 1, -10.0F, 60.0F, 9.0F, 0.25F);
		this.setAnimation(2, 1, -10.0F, -60.0F, 9.0F, 0.25F);
		this.setAnimation(0, 2, -5.0F, 60.0F, 5.0F, 1.0F);
		this.setAnimation(1, 2, -10.0F, 60.0F, 9.0F, 1.0F);
		this.setAnimation(2, 2, -10.0F, -60.0F, 9.0F, 1.0F);
		this.setAnimation(0, 3, -5.0F, -30.0F, 5.0F, 0.2F);
		this.setAnimation(1, 3, -10.0F, -30.0F, 5.0F, 0.2F);
		this.setAnimation(2, 3, -10.0F, 30.0F, 5.0F, 0.2F);
		this.setAnimation(0, 4, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 4, -10.0F, 60.0F, 9.0F, 0.0F);
		this.setAnimation(2, 4, -10.0F, -60.0F, 9.0F, 0.0F);
		this.setAnimation(0, 8, 50.0F, 0.0F, 8.0F, 0.75F);
		this.setAnimation(1, 8, 30.0F, 45.0F, 9.0F, 0.75F);
		this.setAnimation(2, 8, 30.0F, -45.0F, 9.0F, 0.75F);
		this.setAnimation(3, 8, 50.0F, 90.0F, 8.0F, 0.75F);
		this.setAnimation(4, 8, 50.0F, -90.0F, 8.0F, 0.75F);
		this.setAnimation(5, 8, -10.0F, 90.0F, 9.0F, 0.75F);
		this.setAnimation(6, 8, -10.0F, -90.0F, 9.0F, 0.75F);
		this.setAnimation(0, 9, 45.0F, 0.0F, 8.0F, 1.0F);
		this.setAnimation(1, 9, 30.0F, 60.0F, 9.0F, 1.0F);
		this.setAnimation(2, 9, 30.0F, -60.0F, 9.0F, 1.0F);
		this.setAnimation(3, 9, 50.0F, 90.0F, 8.0F, 1.0F);
		this.setAnimation(4, 9, 50.0F, -90.0F, 8.0F, 1.0F);
		this.setAnimation(5, 9, -10.0F, 90.0F, 9.0F, 1.0F);
		this.setAnimation(6, 9, -10.0F, -90.0F, 9.0F, 1.0F);
		this.setAnimation(0, 10, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 10, 10.0F, 45.0F, 9.0F, 0.0F);
		this.setAnimation(2, 10, 10.0F, -45.0F, 9.0F, 0.0F);
		this.setAnimation(3, 10, 50.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 10, 50.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 10, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 10, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 11, -20.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 11, -20.0F, 60.0F, 9.0F, 0.0F);
		this.setAnimation(2, 11, -20.0F, -60.0F, 9.0F, 0.0F);
		this.setAnimation(3, 11, -20.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 11, -20.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 11, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 11, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 12, 0.0F, 179.0F, 4.0F, 0.0F);
		this.setAnimation(1, 12, 0.0F, 179.0F, 4.0F, 0.0F);
		this.setAnimation(2, 12, 0.0F, -180.0F, 4.0F, 0.0F);
		this.setAnimation(3, 12, 0.0F, 179.0F, 4.0F, 0.0F);
		this.setAnimation(4, 12, 0.0F, -180.0F, 4.0F, 0.0F);
		this.setAnimation(5, 12, 0.0F, 179.0F, 4.0F, 0.0F);
		this.setAnimation(6, 12, 0.0F, -180.0F, 4.0F, 0.0F);
		this.setAnimation(0, 14, 60.0F, 0.0F, 7.0F, 0.0F);
		this.setAnimation(1, 14, 10.0F, 60.0F, 9.0F, 0.0F);
		this.setAnimation(2, 14, 10.0F, -60.0F, 9.0F, 0.0F);
		this.setAnimation(3, 14, 50.0F, 90.0F, 8.0F, 0.0F);
		this.setAnimation(4, 14, 50.0F, -90.0F, 8.0F, 0.0F);
		this.setAnimation(5, 14, -10.0F, 90.0F, 9.0F, 0.0F);
		this.setAnimation(6, 14, -10.0F, -90.0F, 9.0F, 0.0F);
		this.setAnimation(0, 15, 60.0F, 0.0F, 7.0F, 0.25F);
		this.setAnimation(1, 15, 10.0F, 60.0F, 9.0F, 0.25F);
		this.setAnimation(2, 15, 10.0F, -60.0F, 9.0F, 0.25F);
		this.setAnimation(3, 15, 50.0F, 90.0F, 8.0F, 0.25F);
		this.setAnimation(4, 15, 50.0F, -90.0F, 8.0F, 0.25F);
		this.setAnimation(5, 15, -10.0F, 90.0F, 9.0F, 0.25F);
		this.setAnimation(6, 15, -10.0F, -90.0F, 9.0F, 0.25F);
		this.setAnimation(0, 16, 60.0F, 0.0F, 9.0F, 1.0F);
		this.setAnimation(1, 16, 10.0F, 60.0F, 11.0F, 1.0F);
		this.setAnimation(2, 16, 10.0F, -60.0F, 11.0F, 1.0F);
		this.setAnimation(3, 16, 50.0F, 90.0F, 10.0F, 1.0F);
		this.setAnimation(4, 16, 50.0F, -90.0F, 10.0F, 1.0F);
		this.setAnimation(5, 16, -10.0F, 90.0F, 11.0F, 1.0F);
		this.setAnimation(6, 16, -10.0F, -90.0F, 11.0F, 1.0F);
	}

	protected void setAnimation(int head, int state, float xRotation, float yRotation, float neckLength, float mouthOpen)
	{
		this.stateXRotations[head][state] = xRotation;
		this.stateYRotations[head][state] = yRotation;
		this.stateNeckLength[head][state] = neckLength;
		this.stateMouthOpen[head][state] = mouthOpen;
	}

	public EntityTFHydraNeck[] getNeckArray()
	{
		return new EntityTFHydraNeck[] { this.necka, this.neckb, this.neckc, this.neckd, this.necke };
	}

	public void onUpdate()
	{
		this.necka.onUpdate();
		this.neckb.onUpdate();
		this.neckc.onUpdate();
		this.neckd.onUpdate();
		this.necke.onUpdate();
		if (this.headEntity == null)
			this.headEntity = this.findNearbyHead("head" + this.headNum);

		this.setDifficultyVariables();
		if (this.headEntity != null)
		{
			this.headEntity.width = this.headEntity.height = this.isActive() ? 4.0F : 1.0F;
			if (!this.hydraObj.worldObj.isRemote)
			{
				this.advanceRespawnCounter();
				this.advanceHeadState();
				this.setHeadPosition();
				this.setHeadFacing();
				this.executeAttacks();
			}
			else
				this.clientAnimateHeadDeath();

			this.setNeckPosition();
			this.addMouthParticles();
			this.playSounds();
		}

	}

	protected void advanceRespawnCounter()
	{
		if (this.currentState == 12 && this.respawnCounter > -1 && --this.respawnCounter <= 0)
		{
			this.setNextState(14);
			this.damageTaken = 0;
			this.endCurrentAction();
			this.respawnCounter = -1;
		}

	}

	protected void clientAnimateHeadDeath()
	{
		if (this.headEntity.getState() == 11)
		{
			++this.headEntity.deathTime;
			if (this.headEntity.deathTime > 0)
				if (this.headEntity.deathTime < 20)
					this.doExplosionOn(this.headEntity, true);
				else if (this.headEntity.deathTime < 30)
					this.doExplosionOn(this.necka, false);
				else if (this.headEntity.deathTime < 40)
					this.doExplosionOn(this.neckb, false);
				else if (this.headEntity.deathTime < 50)
					this.doExplosionOn(this.neckc, false);
				else if (this.headEntity.deathTime < 60)
					this.doExplosionOn(this.neckd, false);
				else if (this.headEntity.deathTime < 70)
					this.doExplosionOn(this.necke, false);

			this.necka.hurtTime = 20;
			this.neckb.hurtTime = 20;
			this.neckc.hurtTime = 20;
			this.neckd.hurtTime = 20;
			this.necke.hurtTime = 20;
		}
		else
		{
			this.headEntity.deathTime = 0;
			this.headEntity.setHealth((float) this.headEntity.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
		}

	}

	private void doExplosionOn(EntityTFHydraPart part, boolean large)
	{
		for (int i = 0; i < 10; ++i)
		{
			double var8 = part.getRNG().nextGaussian() * 0.02D;
			double var4 = part.getRNG().nextGaussian() * 0.02D;
			double var6 = part.getRNG().nextGaussian() * 0.02D;
			String particle = large && part.getRNG().nextInt(5) == 0 ? "largeexplode" : "explode";
			part.worldObj.spawnParticle(particle, part.posX + (double) (part.getRNG().nextFloat() * part.width * 2.0F) - (double) part.width, part.posY + (double) (part.getRNG().nextFloat() * part.height), part.posZ + (double) (part.getRNG().nextFloat() * part.width * 2.0F) - (double) part.width, var8, var4, var6);
		}

	}

	protected void advanceHeadState()
	{
		if (++this.ticksProgress >= this.ticksNeeded)
		{
			int myNext;
			if (this.nextState == -1)
			{
				myNext = this.nextStates[this.currentState];
				if (myNext != this.currentState && this.isSecondaryAttacking && myNext == 13)
				{
					this.isSecondaryAttacking = false;
					myNext = 0;
				}
			}
			else
			{
				myNext = this.nextState;
				this.nextState = -1;
			}

			this.ticksNeeded = this.ticksProgress = this.stateDurations[myNext];
			this.ticksProgress = 0;
			this.prevState = this.currentState;
			this.currentState = myNext;
		}

		if (this.headEntity.getState() != this.currentState)
			this.headEntity.setState(this.currentState);

	}

	protected void setHeadFacing()
	{
		if (this.currentState == 2)
		{
			this.faceEntity(this.targetEntity, 5.0F, (float) this.hydraObj.getVerticalFaceSpeed());
			float biteMaxYaw = -60.0F;
			float biteMinYaw = -90.0F;
			if (this.headNum == 2)
			{
				biteMaxYaw = 60.0F;
				biteMinYaw = 90.0F;
			}

			float yawOffOffset = MathHelper.wrapAngleTo180_float(this.headEntity.rotationYaw - this.hydraObj.renderYawOffset);
			if (yawOffOffset > biteMaxYaw)
				this.headEntity.rotationYaw = this.hydraObj.renderYawOffset + biteMaxYaw;

			if (yawOffOffset < biteMinYaw)
				this.headEntity.rotationYaw = this.hydraObj.renderYawOffset + biteMinYaw;

			Vec3 look = this.headEntity.getLookVec();
			double distance = 16.0D;
			this.targetX = this.headEntity.posX + look.xCoord * distance;
			this.targetY = this.headEntity.posY + 1.5D + look.yCoord * distance;
			this.targetZ = this.headEntity.posZ + look.zCoord * distance;
		}
		else if (this.currentState != 3 && this.currentState != 4)
			if (this.currentState == 16)
				this.faceVec(this.targetX, this.targetY, this.targetZ, 10.0F, (float) this.hydraObj.getVerticalFaceSpeed());
			else if (this.currentState != 6 && this.currentState != 5)
			{
				if (this.isActive())
					if (this.targetEntity != null)
						this.faceEntity(this.targetEntity, 5.0F, (float) this.hydraObj.getVerticalFaceSpeed());
					else
						this.faceIdle(1.5F, (float) this.hydraObj.getVerticalFaceSpeed());
			}
			else
			{
				this.moveTargetCoordsTowardsTargetEntity(FLAME_BREATH_TRACKING_SPEED);
				this.faceVec(this.targetX, this.targetY, this.targetZ, 5.0F, (float) this.hydraObj.getVerticalFaceSpeed());
			}
		else
		{
			this.faceEntity(this.targetEntity, 5.0F, (float) this.hydraObj.getVerticalFaceSpeed());
			this.headEntity.rotationPitch = (float) ((double) this.headEntity.rotationPitch + 0.7853981633974483D);
		}

	}

	protected void moveTargetCoordsTowardsTargetEntity(double distance)
	{
		if (this.targetEntity != null)
		{
			Vec3 vect = Vec3.createVectorHelper(this.targetEntity.posX - this.targetX, this.targetEntity.posY - this.targetY, this.targetEntity.posZ - this.targetZ);
			vect = vect.normalize();
			this.targetX += vect.xCoord * distance;
			this.targetY += vect.yCoord * distance;
			this.targetZ += vect.zCoord * distance;
		}

	}

	protected void addMouthParticles()
	{
		Vec3 vector = this.headEntity.getLookVec();
		double dist = 3.5D;
		double px = this.headEntity.posX + vector.xCoord * dist;
		double py = this.headEntity.posY + 1.0D + vector.yCoord * dist;
		double pz = this.headEntity.posZ + vector.zCoord * dist;
		if (this.headEntity.getState() == 5)
		{
			this.headEntity.worldObj.spawnParticle("flame", px + this.headEntity.getRNG().nextDouble() - 0.5D, py + this.headEntity.getRNG().nextDouble() - 0.5D, pz + this.headEntity.getRNG().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
			this.headEntity.worldObj.spawnParticle("smoke", px + this.headEntity.getRNG().nextDouble() - 0.5D, py + this.headEntity.getRNG().nextDouble() - 0.5D, pz + this.headEntity.getRNG().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
		}

		if (this.headEntity.getState() == 6)
		{
			Vec3 look = this.headEntity.getLookVec();

			for (int i = 0; i < 5; ++i)
			{
				double dx = look.xCoord;
				double dy = look.yCoord;
				double dz = look.zCoord;
				double spread = 5.0D + this.headEntity.getRNG().nextDouble() * 2.5D;
				double velocity = 1.0D + this.headEntity.getRNG().nextDouble();
				dx = dx + this.headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dy = dy + this.headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dz = dz + this.headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
				dx = dx * velocity;
				dy = dy * velocity;
				dz = dz * velocity;
				TwilightForestMod.proxy.spawnParticle(this.headEntity.worldObj, "largeflame", px, py, pz, dx, dy, dz);
			}
		}

		if (this.headEntity.getState() == 1 || this.headEntity.getState() == 2)
			this.headEntity.worldObj.spawnParticle("splash", px + this.headEntity.getRNG().nextDouble() - 0.5D, py + this.headEntity.getRNG().nextDouble() - 0.5D, pz + this.headEntity.getRNG().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);

		if (this.headEntity.getState() == 8)
			this.headEntity.worldObj.spawnParticle("largesmoke", px + this.headEntity.getRNG().nextDouble() - 0.5D, py + this.headEntity.getRNG().nextDouble() - 0.5D, pz + this.headEntity.getRNG().nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);

	}

	protected void playSounds()
	{
		if (this.headEntity.getState() == 6 && this.headEntity.ticksExisted % 5 == 0)
			this.headEntity.worldObj.playSoundEffect(this.headEntity.posX + 0.5D, this.headEntity.posY + 0.5D, this.headEntity.posZ + 0.5D, "mob.ghast.fireball", 0.5F + this.headEntity.getRNG().nextFloat(), this.headEntity.getRNG().nextFloat() * 0.7F + 0.3F);

		if (this.headEntity.getState() == 16)
			this.headEntity.worldObj.playSoundEffect(this.headEntity.posX + 0.5D, this.headEntity.posY + 0.5D, this.headEntity.posZ + 0.5D, "TwilightForest:mob.hydra.roar", 1.25F, this.headEntity.getRNG().nextFloat() * 0.3F + 0.7F);

		if (this.headEntity.getState() == 2 && this.ticksProgress == 60)
			this.headEntity.worldObj.playSoundEffect(this.headEntity.posX + 0.5D, this.headEntity.posY + 0.5D, this.headEntity.posZ + 0.5D, "TwilightForest:mob.hydra.warn", 2.0F, this.headEntity.getRNG().nextFloat() * 0.3F + 0.7F);

		if (this.headEntity.getState() == 0)
			this.didRoar = false;

	}

	protected void setNeckPosition()
	{
		Vec3 vector = null;
		float neckRotation = 0.0F;
		if (this.headNum == 0)
		{
			vector = Vec3.createVectorHelper(0.0D, 3.0D, -1.0D);
			neckRotation = 0.0F;
		}

		if (this.headNum == 1)
		{
			vector = Vec3.createVectorHelper(-1.0D, 3.0D, 3.0D);
			neckRotation = 90.0F;
		}

		if (this.headNum == 2)
		{
			vector = Vec3.createVectorHelper(1.0D, 3.0D, 3.0D);
			neckRotation = -90.0F;
		}

		if (this.headNum == 3)
		{
			vector = Vec3.createVectorHelper(-1.0D, 3.0D, 3.0D);
			neckRotation = 135.0F;
		}

		if (this.headNum == 4)
		{
			vector = Vec3.createVectorHelper(1.0D, 3.0D, 3.0D);
			neckRotation = -135.0F;
		}

		if (this.headNum == 5)
		{
			vector = Vec3.createVectorHelper(-1.0D, 3.0D, 5.0D);
			neckRotation = 135.0F;
		}

		if (this.headNum == 6)
		{
			vector = Vec3.createVectorHelper(1.0D, 3.0D, 5.0D);
			neckRotation = -135.0F;
		}

		vector.rotateAroundY(-(this.hydraObj.renderYawOffset + neckRotation) * 3.141593F / 180.0F);
		this.setNeckPositon(this.hydraObj.posX + vector.xCoord, this.hydraObj.posY + vector.yCoord, this.hydraObj.posZ + vector.zCoord, this.hydraObj.renderYawOffset, 0.0F);
	}

	protected void setHeadPosition()
	{
		this.setupStateDurations();
		this.setupStateRotations();
		float neckLength = this.getCurrentNeckLength();
		float xRotation = this.getCurrentHeadXRotation();
		float yRotation = this.getCurrentHeadYRotation();
		float periodX = this.headNum != 0 && this.headNum != 3 ? this.headNum != 1 && this.headNum != 4 ? 7.0F : 5.0F : 20.0F;
		float periodY = this.headNum != 0 && this.headNum != 4 ? this.headNum != 1 && this.headNum != 6 ? 5.0F : 6.0F : 10.0F;
		float xSwing = MathHelper.sin((float) this.hydraObj.ticksExisted / periodX) * 3.0F;
		float ySwing = MathHelper.sin((float) this.hydraObj.ticksExisted / periodY) * 5.0F;
		if (!this.isActive())
		{
			ySwing = 0.0F;
			xSwing = 0.0F;
		}

		Vec3 vector = Vec3.createVectorHelper(0.0D, 0.0D, (double) neckLength);
		vector.rotateAroundX((xRotation * 3.141593F + xSwing) / 180.0F);
		vector.rotateAroundY(-(this.hydraObj.renderYawOffset + yRotation + ySwing) * 3.141593F / 180.0F);
		double dx = this.hydraObj.posX + vector.xCoord;
		double dy = this.hydraObj.posY + vector.yCoord + 3.0D;
		double dz = this.hydraObj.posZ + vector.zCoord;
		this.headEntity.setPosition(dx, dy, dz);
		this.headEntity.setMouthOpen(this.getCurrentMouthOpen());
	}

	protected void executeAttacks()
	{
		if (this.currentState == 9 && this.ticksProgress % 10 == 0)
		{
			Entity lookTarget = this.getHeadLookTarget();
			if (lookTarget == null || !(lookTarget instanceof EntityTFHydraPart) && !(lookTarget instanceof EntityDragonPart))
			{
				EntityTFHydraMortar mortar = new EntityTFHydraMortar(this.headEntity.worldObj, this.headEntity);
				Vec3 vector = this.headEntity.getLookVec();
				double dist = 3.5D;
				double px = this.headEntity.posX + vector.xCoord * dist;
				double py = this.headEntity.posY + 1.0D + vector.yCoord * dist;
				double pz = this.headEntity.posZ + vector.zCoord * dist;
				mortar.setLocationAndAngles(px, py, pz, 0.0F, 0.0F);
				if (this.targetEntity != null && !this.headEntity.canEntityBeSeen(this.targetEntity))
					mortar.setToBlasting();

				this.headEntity.worldObj.playAuxSFXAtEntity(null, 1008, (int) this.headEntity.posX, (int) this.headEntity.posY, (int) this.headEntity.posZ, 0);
				this.headEntity.worldObj.spawnEntityInWorld(mortar);
			}
			else
				this.endCurrentAction();
		}

		if (this.headEntity.getState() == 3)
			for (Entity nearby : (Iterable<? extends Entity>) this.headEntity.worldObj.getEntitiesWithinAABBExcludingEntity(this.headEntity, this.headEntity.boundingBox.expand(0.0D, 1.0D, 0.0D)))
			{
				if (nearby instanceof EntityLivingBase && !(nearby instanceof EntityTFHydraPart) && !(nearby instanceof EntityTFHydra) && !(nearby instanceof EntityDragonPart))
					// TODO gamerforEA code replace, old code:
					// nearby.attackEntityFrom(DamageSource.causeMobDamage(this.hydraObj), (float) BITE_DAMAGE);
					nearby.attackEntityFrom(DamageSource.causeMobDamage(this.hydraObj), BalanceConfig.hydraHeadBiteDamage);
				// TODO gamerforEA code end
			}

		if (this.headEntity.getState() == 6)
		{
			Entity target = this.getHeadLookTarget();
			if (target != null)
				if (!(target instanceof EntityTFHydraPart) && !(target instanceof EntityDragonPart))
				{
					// TODO gamerforEA code replace, old code:
					// if (!target.isImmuneToFire() && target.attackEntityFrom(DamageSource.inFire, (float) FLAME_DAMAGE))
					if (!target.isImmuneToFire() && target.attackEntityFrom(DamageSource.inFire, BalanceConfig.hydraHeadFlameDamage))
						// TODO gamerforEA code end
						target.setFire(FLAME_BURN_FACTOR);
				}
				else
					this.endCurrentAction();
		}

	}

	protected void setDifficultyVariables()
	{
		if (this.hydraObj.worldObj.difficultySetting != EnumDifficulty.HARD)
			FLAME_BREATH_TRACKING_SPEED = 0.04D;
		else
			FLAME_BREATH_TRACKING_SPEED = 0.1D;
	}

	private Entity getHeadLookTarget()
	{
		Entity pointedEntity = null;
		double range = 30.0D;
		Vec3 srcVec = Vec3.createVectorHelper(this.headEntity.posX, this.headEntity.posY + 1.0D, this.headEntity.posZ);
		Vec3 lookVec = this.headEntity.getLook(1.0F);
		Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
		float var9 = 3.0F;
		List<Entity> possibleList = this.headEntity.worldObj.getEntitiesWithinAABBExcludingEntity(this.headEntity, this.headEntity.boundingBox.addCoord(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range).expand((double) var9, (double) var9, (double) var9));
		double hitDist = 0.0D;

		for (Entity possibleEntity : possibleList)
		{
			if (possibleEntity.canBeCollidedWith() && possibleEntity != this.headEntity && possibleEntity != this.necka && possibleEntity != this.neckb && possibleEntity != this.neckc)
			{
				float borderSize = possibleEntity.getCollisionBorderSize();
				AxisAlignedBB collisionBB = possibleEntity.boundingBox.expand((double) borderSize, (double) borderSize, (double) borderSize);
				MovingObjectPosition interceptPos = collisionBB.calculateIntercept(srcVec, destVec);
				if (collisionBB.isVecInside(srcVec))
				{
					if (0.0D < hitDist || hitDist == 0.0D)
					{
						pointedEntity = possibleEntity;
						hitDist = 0.0D;
					}
				}
				else if (interceptPos != null)
				{
					double possibleDist = srcVec.distanceTo(interceptPos.hitVec);
					if (possibleDist < hitDist || hitDist == 0.0D)
					{
						pointedEntity = possibleEntity;
						hitDist = possibleDist;
					}
				}
			}
		}

		return pointedEntity;
	}

	public void setNextState(int next)
	{
		this.nextState = next;
	}

	public void endCurrentAction()
	{
		this.ticksProgress = this.ticksNeeded;
	}

	private EntityTFHydraHead findNearbyHead(String string)
	{
		for (EntityTFHydraHead nearbyHead : (Iterable<? extends EntityTFHydraHead>) this.hydraObj.worldObj.getEntitiesWithinAABB(EntityTFHydraHead.class, AxisAlignedBB.getBoundingBox(this.hydraObj.posX, this.hydraObj.posY, this.hydraObj.posZ, this.hydraObj.posX + 1.0D, this.hydraObj.posY + 1.0D, this.hydraObj.posZ + 1.0D).expand(16.0D, 16.0D, 16.0D)))
		{
			if (nearbyHead.getPartName().equals(string))
			{
				nearbyHead.hydraObj = this.hydraObj;
				return nearbyHead;
			}
		}

		return null;
	}

	protected float getCurrentNeckLength()
	{
		float prevLength = this.stateNeckLength[this.headNum][this.prevState];
		float curLength = this.stateNeckLength[this.headNum][this.currentState];
		float progress = (float) this.ticksProgress / (float) this.ticksNeeded;
		return prevLength + (curLength - prevLength) * progress;
	}

	protected float getCurrentHeadXRotation()
	{
		float prevRotation = this.stateXRotations[this.headNum][this.prevState];
		float currentRotation = this.stateXRotations[this.headNum][this.currentState];
		float progress = (float) this.ticksProgress / (float) this.ticksNeeded;
		return prevRotation + (currentRotation - prevRotation) * progress;
	}

	protected float getCurrentHeadYRotation()
	{
		float prevRotation = this.stateYRotations[this.headNum][this.prevState];
		float currentRotation = this.stateYRotations[this.headNum][this.currentState];
		float progress = (float) this.ticksProgress / (float) this.ticksNeeded;
		return prevRotation + (currentRotation - prevRotation) * progress;
	}

	protected float getCurrentMouthOpen()
	{
		float prevOpen = this.stateMouthOpen[this.headNum][this.prevState];
		float curOpen = this.stateMouthOpen[this.headNum][this.currentState];
		float progress = (float) this.ticksProgress / (float) this.ticksNeeded;
		return prevOpen + (curOpen - prevOpen) * progress;
	}

	protected void setNeckPositon(double startX, double startY, double startZ, float startYaw, float startPitch)
	{
		double endX = this.headEntity.posX;
		double endY = this.headEntity.posY;
		double endZ = this.headEntity.posZ;
		float endYaw = this.headEntity.rotationYaw;

		float endPitch;
		for (endPitch = this.headEntity.rotationPitch; startYaw - endYaw < -180.0F; endYaw -= 360.0F)
		{
		}

		while (startYaw - endYaw >= 180.0F)
		{
			endYaw += 360.0F;
		}

		while (startPitch - endPitch < -180.0F)
		{
			endPitch -= 360.0F;
		}

		while (startPitch - endPitch >= 180.0F)
		{
			endPitch += 360.0F;
		}

		if (endPitch > 0.0F)
		{
			Vec3 vector = Vec3.createVectorHelper(0.0D, 0.0D, -1.0D);
			vector.rotateAroundY(-endYaw * 3.141593F / 180.0F);
			endX = endX + vector.xCoord;
			endY = endY + vector.yCoord;
			endZ = endZ + vector.zCoord;
		}
		else
		{
			Vec3 vector = this.headEntity.getLookVec();
			float dist = 1.0F;
			endX = endX - vector.xCoord * (double) dist;
			endY = endY - vector.yCoord * (double) dist;
			endZ = endZ - vector.zCoord * (double) dist;
		}

		float factor = 0.0F;
		factor = 0.0F;
		this.necka.setPosition(endX + (startX - endX) * (double) factor, endY + (startY - endY) * (double) factor, endZ + (startZ - endZ) * (double) factor);
		this.necka.rotationYaw = endYaw + (startYaw - endYaw) * factor;
		this.necka.rotationPitch = endPitch + (startPitch - endPitch) * factor;
		factor = 0.25F;
		this.neckb.setPosition(endX + (startX - endX) * (double) factor, endY + (startY - endY) * (double) factor, endZ + (startZ - endZ) * (double) factor);
		this.neckb.rotationYaw = endYaw + (startYaw - endYaw) * factor;
		this.neckb.rotationPitch = endPitch + (startPitch - endPitch) * factor;
		factor = 0.5F;
		this.neckc.setPosition(endX + (startX - endX) * (double) factor, endY + (startY - endY) * (double) factor, endZ + (startZ - endZ) * (double) factor);
		this.neckc.rotationYaw = endYaw + (startYaw - endYaw) * factor;
		this.neckc.rotationPitch = endPitch + (startPitch - endPitch) * factor;
		factor = 0.75F;
		this.neckd.setPosition(endX + (startX - endX) * (double) factor, endY + (startY - endY) * (double) factor, endZ + (startZ - endZ) * (double) factor);
		this.neckd.rotationYaw = endYaw + (startYaw - endYaw) * factor;
		this.neckd.rotationPitch = endPitch + (startPitch - endPitch) * factor;
		factor = 1.0F;
		this.necke.setPosition(endX + (startX - endX) * (double) factor, endY + (startY - endY) * (double) factor, endZ + (startZ - endZ) * (double) factor);
		this.necke.rotationYaw = endYaw + (startYaw - endYaw) * factor;
		this.necke.rotationPitch = endPitch + (startPitch - endPitch) * factor;
	}

	protected void faceIdle(float yawConstraint, float pitchConstraint)
	{
		float angle = this.hydraObj.rotationYaw * 3.141593F / 180.0F;
		float distance = 30.0F;
		double dx = this.hydraObj.posX - (double) (MathHelper.sin(angle) * distance);
		double dy = this.hydraObj.posY + 3.0D;
		double dz = this.hydraObj.posZ + (double) (MathHelper.cos(angle) * distance);
		this.faceVec(dx, dy, dz, yawConstraint, pitchConstraint);
	}

	public void faceEntity(Entity entity, float yawConstraint, float pitchConstraint)
	{
		// TODO gamerforEA code start
		if (entity == null)
			return;
		// TODO gamerforEA code end

		double yTarget;
		if (entity instanceof EntityLivingBase)
		{
			EntityLivingBase entityliving = (EntityLivingBase) entity;
			yTarget = entityliving.posY + (double) entityliving.getEyeHeight();
		}
		else
			yTarget = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D;

		this.faceVec(entity.posX, yTarget, entity.posZ, yawConstraint, pitchConstraint);
		this.targetX = entity.posX;
		this.targetY = entity.posY;
		this.targetZ = entity.posZ;
	}

	public void faceVec(double xCoord, double yCoord, double zCoord, float yawConstraint, float pitchConstraint)
	{
		double xOffset = xCoord - this.headEntity.posX;
		double zOffset = zCoord - this.headEntity.posZ;
		double yOffset = this.headEntity.posY + 1.0D - yCoord;
		double distance = (double) MathHelper.sqrt_double(xOffset * xOffset + zOffset * zOffset);
		float xyAngle = (float) (Math.atan2(zOffset, xOffset) * 180.0D / 3.141592653589793D) - 90.0F;
		float zdAngle = (float) -(Math.atan2(yOffset, distance) * 180.0D / 3.141592653589793D);
		this.headEntity.rotationPitch = -this.updateRotation(this.headEntity.rotationPitch, zdAngle, pitchConstraint);
		this.headEntity.rotationYaw = this.updateRotation(this.headEntity.rotationYaw, xyAngle, yawConstraint);
	}

	private float updateRotation(float current, float intended, float increment)
	{
		float delta = MathHelper.wrapAngleTo180_float(intended - current);
		if (delta > increment)
			delta = increment;

		if (delta < -increment)
			delta = -increment;

		return MathHelper.wrapAngleTo180_float(current + delta);
	}

	public Entity getTargetEntity()
	{
		return this.targetEntity;
	}

	public void setTargetEntity(Entity targetEntity)
	{
		this.targetEntity = targetEntity;
	}

	public void setHurtTime(int hurtTime)
	{
		if (this.headEntity != null)
			this.headEntity.hurtTime = hurtTime;

		this.necka.hurtTime = hurtTime;
		this.neckb.hurtTime = hurtTime;
		this.neckc.hurtTime = hurtTime;
		this.neckd.hurtTime = hurtTime;
		this.necke.hurtTime = hurtTime;
	}

	public boolean shouldRenderHead()
	{
		return this.headEntity.getState() != 12 && this.headEntity.deathTime < 20;
	}

	public boolean shouldRenderNeck(int neckNum)
	{
		int time = 30 + 10 * neckNum;
		return this.headEntity.getState() != 12 && this.headEntity.deathTime < time;
	}

	public boolean isActive()
	{
		return this.currentState != 11 && this.currentState != 12;
	}

	public void addDamage(float damageAmount)
	{
		this.damageTaken = (int) ((float) this.damageTaken + damageAmount);
	}

	public int getDamageTaken()
	{
		return this.damageTaken;
	}

	public void setRespawnCounter(int count)
	{
		this.respawnCounter = count;
	}
}
