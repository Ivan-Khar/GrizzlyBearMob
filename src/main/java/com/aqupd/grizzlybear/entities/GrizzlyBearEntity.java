//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aqupd.grizzlybear.entities;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import com.aqupd.grizzlybear.Main;
import com.aqupd.grizzlybear.ai.GrizzlyBearFishGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class GrizzlyBearEntity extends AnimalEntity implements Angerable {
    private static final TrackedData<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final IntRange ANGER_TIME_RANGE;
    private static final Ingredient LOVINGFOOD;
    private int angerTime;
    private UUID targetUuid;

    public GrizzlyBearEntity(EntityType<? extends GrizzlyBearEntity> entityType, World world) {
        super(entityType, world);
    }

    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return Main.GRIZZLYBEAR.create(world);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return LOVINGFOOD.test(stack);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        boolean bl = this.isBreedingItem(player.getStackInHand(hand));
        if (!bl && !player.shouldCancelInteraction()) {
            return ActionResult.success(this.world.isClient);
        } else {
            ActionResult actionResult = super.interactMob(player, hand);
            return actionResult;
        }
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GrizzlyBearEntity.AttackGoal());
        this.goalSelector.add(1, new GrizzlyBearEntity.GrizzlyBearEscapeDangerGoal());
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, false, LOVINGFOOD));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new GrizzlyBearFishGoal(((GrizzlyBearEntity)(Object)this),1.0D,20));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new GrizzlyBearEntity.GrizzlyBearRevengeGoal());
        this.targetSelector.add(2, new GrizzlyBearEntity.FollowPlayersGoal());
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(4, new FollowTargetGoal(this, FoxEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(4, new FollowTargetGoal(this, RabbitEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(4, new FollowTargetGoal(this, ChickenEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(5, new UniversalAngerGoal(this, false));
    }

    public static Builder createGrizzlyBearAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.angerFromTag((ServerWorld)this.world, tag);
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.angerToTag(tag);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
    }

    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngryAt(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    public UUID getAngryAt() {
        return this.targetUuid;
    }

    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? Main.GRIZZLY_BEAR_AMBIENT_BABY : Main.GRIZZLY_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return Main.GRIZZLY_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return Main.GRIZZLY_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(Main.GRIZZLY_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(Main.GRIZZLY_BEAR_WARNING, 1.0F, this.getSoundPitch());
            this.warningSoundCooldown = 40;
        }

    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WARNING, false);
    }

    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
                this.calculateDimensions();
            }

            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            if (this.isWarning()) {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress + 1.0F, 0.0F, 6.0F);
            } else {
                this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundCooldown > 0) {
            --this.warningSoundCooldown;
        }

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }

    }

    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.warningAnimationProgress > 0.0F) {
            float f = this.warningAnimationProgress / 6.0F;
            float g = 1.0F + f;
            return super.getDimensions(pose).scaled(1.0F, g);
        } else {
            return super.getDimensions(pose);
        }
    }

    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        if (bl) {
            this.dealDamage(this, target);
        }

        return bl;
    }

    public boolean isWarning() {
        return (Boolean)this.dataTracker.get(WARNING);
    }

    public void setWarning(boolean warning) {
        this.dataTracker.set(WARNING, warning);
    }

    @Environment(EnvType.CLIENT)
    public float getWarningAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0F;
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.98F;
    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (entityData == null) {
            entityData = new PassiveData(1.0F);
        }

        return super.initialize(world, difficulty, spawnReason, (EntityData)entityData, entityTag);
    }

    static {
        WARNING = DataTracker.registerData(GrizzlyBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
        LOVINGFOOD = Ingredient.ofItems(Items.COD, Items.SALMON, Items.SWEET_BERRIES);
    }

    class GrizzlyBearEscapeDangerGoal extends EscapeDangerGoal {
        public GrizzlyBearEscapeDangerGoal() {
            super(GrizzlyBearEntity.this, 2.0D);
        }

        public boolean canStart() {
            return !GrizzlyBearEntity.this.isBaby() && !GrizzlyBearEntity.this.isOnFire() ? false : super.canStart();
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(GrizzlyBearEntity.this, 1.25D, true);
        }

        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.method_28347()) {
                this.method_28346();
                this.mob.tryAttack(target);
                GrizzlyBearEntity.this.setWarning(false);
            } else if (squaredDistance <= d * 2.0D) {
                if (this.method_28347()) {
                    GrizzlyBearEntity.this.setWarning(false);
                    this.method_28346();
                }

                if (this.method_28348() <= 10) {
                    GrizzlyBearEntity.this.setWarning(true);
                    GrizzlyBearEntity.this.playWarningSound();
                }
            } else {
                this.method_28346();
                GrizzlyBearEntity.this.setWarning(false);
            }

        }

        public void stop() {
            GrizzlyBearEntity.this.setWarning(false);
            super.stop();
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return (double)(4.0F + entity.getWidth());
        }
    }

    class FollowPlayersGoal extends FollowTargetGoal<PlayerEntity> {
        public FollowPlayersGoal() {
            super(GrizzlyBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate)null);
        }

        public boolean canStart() {
            if (GrizzlyBearEntity.this.isBaby()) {
                return false;
            } else {
                if (super.canStart()) {
                    List<GrizzlyBearEntity> list = GrizzlyBearEntity.this.world.getNonSpectatingEntities(GrizzlyBearEntity.class, GrizzlyBearEntity.this.getBoundingBox().expand(8.0D, 4.0D, 8.0D));
                    Iterator var2 = list.iterator();

                    while(var2.hasNext()) {
                        GrizzlyBearEntity grizzlyBearEntity = (GrizzlyBearEntity)var2.next();
                        if (grizzlyBearEntity.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        protected double getFollowRange() {
            return super.getFollowRange() * 0.5D;
        }
    }

    class GrizzlyBearRevengeGoal extends RevengeGoal {
        public GrizzlyBearRevengeGoal() {
            super(GrizzlyBearEntity.this, new Class[0]);
        }

        public void start() {
            super.start();
            if (GrizzlyBearEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }

        }

        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof GrizzlyBearEntity && !mob.isBaby()) {
                super.setMobEntityTarget(mob, target);
            }

        }
    }
}
