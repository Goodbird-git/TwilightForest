package twilightforest.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import twilightforest.item.ItemTF;
import twilightforest.item.TFItems;

public class ItemTFScepterLifeDrain extends ItemTF {
    protected ItemTFScepterLifeDrain() {
        super.maxStackSize = 1;
        this.setMaxDamage(99);
        this.setCreativeTab(TFItems.creativeTab);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World worldObj, EntityPlayer player) {
        if(par1ItemStack.getItemDamage() < this.getMaxDamage()) {
            player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        } else {
            player.stopUsingItem();
        }

        return par1ItemStack;
    }

    public static void animateTargetShatter(World worldObj, EntityLivingBase target) {
        for(int var1 = 0; var1 < 50; ++var1) {
            double gaussX = Item.itemRand.nextGaussian() * 0.02D;
            double gaussY = Item.itemRand.nextGaussian() * 0.02D;
            double gaussZ = Item.itemRand.nextGaussian() * 0.02D;
            double gaussFactor = 10.0D;
            Item popItem = getTargetDropItemId(target) != null?getTargetDropItemId(target):Items.rotten_flesh;
            worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(popItem), target.posX + (double)(Item.itemRand.nextFloat() * target.width * 2.0F) - (double)target.width - gaussX * gaussFactor, target.posY + (double)(Item.itemRand.nextFloat() * target.height) - gaussY * gaussFactor, target.posZ + (double)(Item.itemRand.nextFloat() * target.width * 2.0F) - (double)target.width - gaussZ * gaussFactor, gaussX, gaussY, gaussZ);
        }

    }

    public static Item getTargetDropItemId(EntityLivingBase target) {
        return Items.rotten_flesh;
    }

    private Entity getPlayerLookTarget(World worldObj, EntityPlayer player) {
        Entity pointedEntity = null;
        double range = 20.0D;
        Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
        float var9 = 1.0F;
        List<Entity> possibleList = worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range).expand((double)var9, (double)var9, (double)var9));
        double hitDist = 0.0D;

        for(Entity possibleEntity : possibleList) {
            if(possibleEntity.canBeCollidedWith()) {
                float borderSize = possibleEntity.getCollisionBorderSize();
                AxisAlignedBB collisionBB = possibleEntity.boundingBox.expand((double)borderSize, (double)borderSize, (double)borderSize);
                MovingObjectPosition interceptPos = collisionBB.calculateIntercept(srcVec, destVec);
                if(collisionBB.isVecInside(srcVec)) {
                    if(0.0D < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = 0.0D;
                    }
                } else if(interceptPos != null) {
                    double possibleDist = srcVec.distanceTo(interceptPos.hitVec);
                    if(possibleDist < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = possibleDist;
                    }
                }
            }
        }

        return pointedEntity;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        World worldObj = player.worldObj;
        if(stack.getItemDamage() >= this.getMaxDamage()) {
            player.stopUsingItem();
        } else {
            if(count % 5 == 0) {
                Entity pointedEntity = this.getPlayerLookTarget(worldObj, player);
                if(pointedEntity != null && pointedEntity instanceof EntityLivingBase && !pointedEntity.isDead && ((EntityLivingBase) pointedEntity).getHealth()>0) {
                    EntityLivingBase target = (EntityLivingBase)pointedEntity;
                    if(target.getActivePotionEffect(Potion.moveSlowdown) == null && target.getHealth() >= 1.0F) {
                        this.makeRedMagicTrail(worldObj, player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
                        worldObj.playSoundAtEntity(player, "fire.ignite", 1.0F, (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F + 1.0F);
                        if(!worldObj.isRemote) {
                            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(player, player), 1.0F);
                            if(this.getMaxHealth(target) <= this.getMaxHealth(player)) {
                                target.motionX = 0.0D;
                                target.motionY = 0.2D;
                                target.motionZ = 0.0D;
                            }

                            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 2));
                        }
                    } else if(target.getHealth() <= 3.0F) {
                        this.makeRedMagicTrail(worldObj, player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
                        if(target instanceof EntityLiving) {
                            ((EntityLiving)target).spawnExplosionParticle();
                        }

                        worldObj.playSoundAtEntity(target, "game.player.hurt.fall.big", 1.0F, ((Item.itemRand.nextFloat() - Item.itemRand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        animateTargetShatter(worldObj, target);
                        if(!worldObj.isRemote) {
                            target.setDead();
                            target.onDeath(DamageSource.causeIndirectMagicDamage(player, player));
                        }

                        player.stopUsingItem();
                    } else if(!worldObj.isRemote) {
                        target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(player, player), 3.0F);
                        if(this.getMaxHealth(target) <= this.getMaxHealth(player)) {
                            target.motionX = 0.0D;
                            target.motionY = 0.2D;
                            target.motionZ = 0.0D;
                        }

                        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 2));
                        if(count % 10 == 0) {
                            player.heal(1.0F);
                            player.getFoodStats().addStats(1, 0.1F);
                        }
                    }

                    if(!worldObj.isRemote) {
                        stack.damageItem(1, player);
                    }
                }
            }

        }
    }

    private float getMaxHealth(EntityLivingBase target) {
        return (float)target.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
    }

    protected void makeRedMagicTrail(World worldObj, double srcX, double srcY, double srcZ, double destX, double destY, double destZ) {
        int particles = 32;

        for(int i = 0; i < particles; ++i) {
            double trailFactor = (double)i / ((double)particles - 1.0D);
            float f = 1.0F;
            float f1 = 0.5F;
            float f2 = 0.5F;
            double tx = srcX + (destX - srcX) * trailFactor + worldObj.rand.nextGaussian() * 0.005D;
            double ty = srcY + (destY - srcY) * trailFactor + worldObj.rand.nextGaussian() * 0.005D;
            double tz = srcZ + (destZ - srcZ) * trailFactor + worldObj.rand.nextGaussian() * 0.005D;
            worldObj.spawnParticle("mobSpell", tx, ty, tz, (double)f, (double)f1, (double)f2);
        }

    }

    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        par3List.add(par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() + " charges left");
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.itemIcon = par1IconRegister.registerIcon("TwilightForest:" + this.getUnlocalizedName().substring(5));
    }
}
