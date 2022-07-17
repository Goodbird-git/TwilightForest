package com.gamerforea.twilightforest.balance;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.EnumMap;
import java.util.Map;

public final class EntityBalanceInfo
{
	private final Map<EntityAttribute, Double> attributes = new EnumMap<>(EntityAttribute.class);

	public EntityBalanceInfo(Configuration cfg, String category)
	{
		for (EntityAttribute attribute : EntityAttribute.values())
		{
			this.attributes.put(attribute, attribute.getFromConfig(cfg, category));
		}
	}

	public void applyTo(EntityLivingBase entity)
	{
		if (entity != null)
			this.attributes.forEach((attribute, value) -> {
				if (value > 0)
				{
					IAttributeInstance entityAttribute = entity.getEntityAttribute(attribute.attribute);
					if (entityAttribute != null)
						entityAttribute.setBaseValue(attribute.attribute.clampValue(value));
				}
			});
	}

	private enum EntityAttribute
	{
		MAX_HEALTH("maxHealth", SharedMonsterAttributes.maxHealth),
		ATTACK_DAMAGE("attackDamage", SharedMonsterAttributes.attackDamage),
		FOLLOW_RANGE("followRange", SharedMonsterAttributes.followRange),
		MOVEMENT_SPEED("movementSpeed", SharedMonsterAttributes.movementSpeed);

		private final String configKey;
		private final IAttribute attribute;

		EntityAttribute(String configKey, IAttribute attribute)
		{
			this.configKey = configKey;
			this.attribute = attribute;
		}

		public double getFromConfig(Configuration cfg, String category)
		{
			Property property = cfg.get(category, this.configKey, 0.0D);

			if (this.attribute instanceof RangedAttribute)
			{
				RangedAttribute rangedAttribute = (RangedAttribute) this.attribute;
				double minValue = ReflectionHelper.getPrivateValue(RangedAttribute.class, rangedAttribute, "field_111120_a", "minimumValue");
				double maxValue = ReflectionHelper.getPrivateValue(RangedAttribute.class, rangedAttribute, "field_111118_b", "maximumValue");
				property.setMinValue(minValue);
				property.setMaxValue(maxValue);
				property.comment = "[range: " + minValue + " ~ " + maxValue + ", default: " + this.attribute.getDefaultValue() + "]";
			}

			return property.getDouble(0.0D);
		}
	}
}
