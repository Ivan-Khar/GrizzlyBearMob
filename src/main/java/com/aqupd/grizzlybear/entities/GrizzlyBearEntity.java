//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aqupd.grizzlybear.entities;

import java.util.List;
import java.util.UUID;

import com.aqupd.grizzlybear.Main;
import com.aqupd.grizzlybear.ai.*;
import com.aqupd.grizzlybear.utils.AqConfig;
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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrizzlyBearEntity extends AnimalEntity implements Angerable {
    private static final TrackedData<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    private static final Ingredient LOVINGFOOD;
    private int angerTime;
    private UUID targetUuid;

    private static double health = AqConfig.INSTANCE.getDoubleProperty("entity.health");
    private static double speed = AqConfig.INSTANCE.getDoubleProperty("entity.speed");
    private static double follow = AqConfig.INSTANCE.getDoubleProperty("entity.follow");
    private static double damage = AqConfig.INSTANCE.getDoubleProperty("entity.damage");
    private static int angermin = AqConfig.INSTANCE.getNumberProperty("entity.angertimemin");
    private static int angermax = AqConfig.INSTANCE.getNumberProperty("entity.angertimemax");
    private static boolean friendly = AqConfig.INSTANCE.getBooleanProperty("entity.friendlytoplayer");

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
            return super.interactMob(player, hand);
        }
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GrizzlyBearEntity.AttackGoal());
        this.goalSelector.add(1, new GrizzlyBearEntity.GrizzlyBearEscapeDangerGoal());
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, LOVINGFOOD, false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new GrizzlyBearFishGoal(this,1.0D,20));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new GrizzlyBearEntity.GrizzlyBearRevengeGoal());
        if (!friendly) {
            this.targetSelector.add(2, new GrizzlyBearEntity.ProtectBabiesGoal());
            this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
            this.targetSelector.add(4, new FollowTargetGoal<>(this, FoxEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new FollowTargetGoal<>(this, RabbitEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new FollowTargetGoal<>(this, ChickenEntity.class, 10, true, true, null));
            this.targetSelector.add(4, new FollowTargetGoal<>(this, BeeEntity.class, 10, true, true, null));
            this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
        }
    }

    public static Builder createGrizzlyBearAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, health)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, follow)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, damage);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.world, nbt);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
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
            this.applyDamageEffects(this, target);
        }

        return bl;
    }

    public boolean isWarning() {
        return this.dataTracker.get(WARNING);
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

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (entityData == null) {
            entityData = new PassiveData(1.0F);
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    static {
        WARNING = DataTracker.registerData(GrizzlyBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME_RANGE = Durations.betweenSeconds(angermin, angermax);
        LOVINGFOOD = Ingredient.ofItems(Items.COD, Items.SALMON, Items.SWEET_BERRIES);
    }

    class GrizzlyBearEscapeDangerGoal extends EscapeDangerGoal {
        public GrizzlyBearEscapeDangerGoal() {
            super(GrizzlyBearEntity.this, 2.0D);
        }

        public boolean canStart() {
            return (GrizzlyBearEntity.this.isBaby() || GrizzlyBearEntity.this.isOnFire()) && super.canStart();
        }
    }

    private class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(GrizzlyBearEntity.this, 1.25D, true);
        }

        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.isCooledDown()) {
                this.resetCooldown();
                this.mob.tryAttack(target);
                GrizzlyBearEntity.this.setWarning(false);
            } else if (squaredDistance <= d * 2.0D) {
                if (this.isCooledDown()) {
                    GrizzlyBearEntity.this.setWarning(false);
                    this.resetCooldown();
                }

                if (this.getCooldown() <= 10) {
                    GrizzlyBearEntity.this.setWarning(true);
                    GrizzlyBearEntity.this.playWarningSound();
                }
            } else {
                this.resetCooldown();
                GrizzlyBearEntity.this.setWarning(false);
            }

        }

        public void stop() {
            GrizzlyBearEntity.this.setWarning(false);
            super.stop();
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return 4.0F + entity.getWidth();
        }
    }

    class ProtectBabiesGoal extends FollowTargetGoal<PlayerEntity> {
        public ProtectBabiesGoal() {
            super(GrizzlyBearEntity.this, PlayerEntity.class, 20, true, true, null);
        }

        public boolean canStart() {
            if (!GrizzlyBearEntity.this.isBaby()) {
                if (super.canStart()) {
                    List<GrizzlyBearEntity> list = GrizzlyBearEntity.this.world.getNonSpectatingEntities(GrizzlyBearEntity.class, GrizzlyBearEntity.this.getBoundingBox().expand(8.0D, 4.0D, 8.0D));

                    for (GrizzlyBearEntity grizzlyBearEntity : list) {
                        if (grizzlyBearEntity.isBaby()) {
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        protected double getFollowRange() {
            return super.getFollowRange() * 0.5D;
        }
    }

    class GrizzlyBearRevengeGoal extends RevengeGoal {
        public GrizzlyBearRevengeGoal() {
            super(GrizzlyBearEntity.this);
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
