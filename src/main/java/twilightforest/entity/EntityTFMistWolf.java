package twilightforest.entity;

import com.gamerforea.twilightforest.balance.BalanceConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityTFMistWolf extends EntityTFHostileWolf
{
	public EntityTFMistWolf(World world)
	{
		super(world);
		this.setSize(1.4F, 1.9F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
	}

	public int getAttackStrength(Entity victim)
	{
		// TODO gamerforEA code replace, old code:
		// return 6;
		return MathHelper.floor_float(BalanceConfig.mistWolfDamage);
		// TODO gamerforEA code end
	}

	@Override
	public boolean attackEntityAsMob(Entity victim)
	{
		int damage = this.getAttackStrength(victim);
		if (victim.attackEntityFrom(DamageSource.causeMobDamage(this), (float) damage))
		{
			float myBrightness = this.getBrightness(1.0F);
			if (victim instanceof EntityLivingBase && myBrightness < 0.1F)
			{
				byte effectDuration = 0;
				if (this.worldObj.difficultySetting != EnumDifficulty.EASY)
					if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL)
						effectDuration = 7;
					else if (this.worldObj.difficultySetting == EnumDifficulty.HARD)
						effectDuration = 15;

				if (effectDuration > 0)
					((EntityLivingBase) victim).addPotionEffect(new PotionEffect(Potion.blindness.id, effectDuration * 20, 0));
			}

			return true;
		}
		return false;
	}

	@Override
	protected float getSoundPitch()
	{
		return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.6F;
	}
}
