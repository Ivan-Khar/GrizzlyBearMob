package com.aqupd.grizzlybear.ai;


import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.List;
import java.util.Objects;


public class GrizzlyBearFishGoal extends MoveToTargetPosGoal {
    private int upTick;
    private boolean fished = false;
    private int stayTick;
    public GrizzlyBearFishGoal(GrizzlyBearEntity mob, double speed, int range) {
        super(mob, speed, range,4);
        upTick = mob.getRandom().nextInt(20)+10;
        stayTick = mob.getRandom().nextInt(100)+40;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return ((world.getBlockState(pos).isOf(Blocks.WATER)) && world.isAir(pos.up()));
    }

    @Override
    protected int getInterval(PathAwareEntity mob) {
        return 1;
    }


    @Override
    public boolean canStart() {
        return !this.mob.isBaby() && this.mob.getRandom().nextInt(6500)==1 && super.canStart();
    }

    @Override
    public void start() {
        upTick = mob.getRandom().nextInt(5)+10;
        stayTick = mob.getRandom().nextInt(40)+100;
        super.start();
    }


    public double getDesiredSquaredDistanceToTarget() {
        return 2D;
    }

    @Override
    public boolean shouldContinue() {
        if (fished){
            fished = false;
            return false;
        }
        return super.shouldContinue() ;
    }

    @Override
    public void tick() {
        if (this.hasReached() && upTick <= 0){
            ((GrizzlyBearEntity)this.mob).setWarning(false);
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.mob.world)).parameter(LootContextParameters.ORIGIN, this.mob.getPos()).parameter(LootContextParameters.THIS_ENTITY, this.mob).random(this.mob.getRandom());
            LootTable lootTable = Objects.requireNonNull(this.mob.world.getServer()).getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
            List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.COMMAND));
            for (ItemStack itemStack: list) {
                if (!itemStack.isIn(ItemTags.FISHES) || this.mob.getRandom().nextInt(3)==1) {
                    ItemEntity itemEntity = new ItemEntity(this.mob.world, this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), itemStack);
                    double d = this.mob.getX() - this.targetPos.getX();
                    double e = this.mob.getY() - this.targetPos.getY();
                    double f = this.mob.getZ() - this.targetPos.getZ();
                    itemEntity.setVelocity(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);
                    this.mob.world.spawnEntity(itemEntity);
                    itemEntity.playSound(SoundEvents.ENTITY_ITEM_PICKUP,0.2F,2);
                }else {
                    this.mob.heal(2F);
                    for(int i = 0; i < 8; ++i) {
                        double x = this.mob.getX() +this.targetPos.getX();
                        double y = 2.25 + this.mob.getY();
                        double z = this.targetPos.getZ() + this.mob.getZ();
                        ((ServerWorld) this.mob.world).spawnParticles(ParticleTypes.END_ROD,x/2,y,z/2,1,0,-0.2F,0,0.2f);
                    }
                }
            }
            fished = true;
        } else if (this.hasReached() ){
            if (stayTick <=0) {
                ((GrizzlyBearEntity) this.mob).setWarning(true);
                upTick--;
            }else {
                this.mob.getLookControl().lookAt(targetPos.getX(),targetPos.getY(),targetPos.getZ());
                stayTick--;
            }
        }
        super.tick();
    }
}
