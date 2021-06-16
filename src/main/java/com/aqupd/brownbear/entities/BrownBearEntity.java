//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aqupd.brownbear.entities;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import com.aqupd.brownbear.Main;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BrownBearEntity extends AnimalEntity implements Angerable {
    private static final TrackedData<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final IntRange ANGER_TIME_RANGE;
    private int angerTime;
    private UUID targetUuid;

    public BrownBearEntity(EntityType<? extends BrownBearEntity> entityType, World world) {
        super(entityType, world);
    }

    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return (PassiveEntity)Main.BROWNBEAR.create(world);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new BrownBearEntity.AttackGoal());
        this.goalSelector.add(1, new BrownBearEntity.BrownBearEscapeDangerGoal());
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new BrownBearEntity.BrownBearRevengeGoal());
        this.targetSelector.add(2, new BrownBearEntity.FollowPlayersGoal());
        this.targetSelector.add(3, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(4, new FollowTargetGoal(this, FoxEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(4, new FollowTargetGoal(this, RabbitEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(4, new FollowTargetGoal(this, ChickenEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(4, new FollowTargetGoal(this, BeeEntity.class, 10, true, true, (Predicate) null));
        this.targetSelector.add(5, new UniversalAngerGoal(this, false));
    }

    public static Builder createBrownBearAttributes() {
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
        return this.isBaby() ? Main.BROWN_BEAR_AMBIENT_BABY : Main.BROWN_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return Main.BROWN_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return Main.BROWN_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(Main.BROWN_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(Main.BROWN_BEAR_WARNING, 1.0F, this.getSoundPitch());
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
        WARNING = DataTracker.registerData(BrownBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    }

    class BrownBearEscapeDangerGoal extends EscapeDangerGoal {
        public BrownBearEscapeDangerGoal() {
            super(BrownBearEntity.this, 2.0D);
        }

        public boolean canStart() {
            return !BrownBearEntity.this.isBaby() && !BrownBearEntity.this.isOnFire() ? false : super.canStart();
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(BrownBearEntity.this, 1.25D, true);
        }

        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.method_28347()) {
                this.method_28346();
                this.mob.tryAttack(target);
                BrownBearEntity.this.setWarning(false);
            } else if (squaredDistance <= d * 2.0D) {
                if (this.method_28347()) {
                    BrownBearEntity.this.setWarning(false);
                    this.method_28346();
                }

                if (this.method_28348() <= 10) {
                    BrownBearEntity.this.setWarning(true);
                    BrownBearEntity.this.playWarningSound();
                }
            } else {
                this.method_28346();
                BrownBearEntity.this.setWarning(false);
            }

        }

        public void stop() {
            BrownBearEntity.this.setWarning(false);
            super.stop();
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return (double)(4.0F + entity.getWidth());
        }
    }

    class FollowPlayersGoal extends FollowTargetGoal<PlayerEntity> {
        public FollowPlayersGoal() {
            super(BrownBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate)null);
        }

        public boolean canStart() {
            if (BrownBearEntity.this.isBaby()) {
                return false;
            } else {
                if (super.canStart()) {
                    List<BrownBearEntity> list = BrownBearEntity.this.world.getNonSpectatingEntities(BrownBearEntity.class, BrownBearEntity.this.getBoundingBox().expand(8.0D, 4.0D, 8.0D));
                    Iterator var2 = list.iterator();

                    while(var2.hasNext()) {
                        BrownBearEntity brownBearEntity = (BrownBearEntity)var2.next();
                        if (brownBearEntity.isBaby()) {
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

    class BrownBearRevengeGoal extends RevengeGoal {
        public BrownBearRevengeGoal() {
            super(BrownBearEntity.this, new Class[0]);
        }

        public void start() {
            super.start();
            if (BrownBearEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }

        }

        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof BrownBearEntity && !mob.isBaby()) {
                super.setMobEntityTarget(mob, target);
            }

        }
    }
}
