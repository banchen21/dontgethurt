package com.lastimp.dgh.compact.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidWalkToLivingEntityTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.item.tool.StretcherItem;
import com.lastimp.dgh.common.entry.register.ModItems;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TaskBringToBed implements IMaidTask {
    public static final ResourceLocation ID = ResourceHelper.ModResource("bring_to_bed");

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ModItems.STRETCHER.get());
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return List.of(Pair.of(5, this.createWalkToLivingEntityTask()));
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
        return List.of(Pair.of(5, this.createWalkToLivingEntityTask()));
    }

    private MaidWalkToLivingEntityTask createWalkToLivingEntityTask() {
        return new MaidWalkToLivingEntityTask(
                0.6f, 1.5f,
                (maid) -> (maid.getOwner() != null && HealthCapability.isDown(maid.getOwner()) && maid.getOwner().getVehicle() == null),
                (maid, livingEntity) -> livingEntity.is(maid.getOwner()),
                this::startCarry
        );
    }

    private void startCarry(EntityMaid maid, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player)) return;
        var inv = maid.getAvailableInv(true);
        for(int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.is(ModItems.STRETCHER.get())) {
                stack.shrink(1);

                ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
                BlockPos blockPos = player.getRespawnPosition();
                ServerLevel finalLevel = targetLevel == null ? player.server.overworld() : targetLevel;
                BlockPos finalPos = blockPos == null ? player.server.overworld().getSharedSpawnPos() : blockPos;

                var location = Player.findRespawnPositionAndUseSpawnBlock(finalLevel, finalPos, player.getRespawnAngle(), player.isRespawnForced(), true);
                location.ifPresent((loc) -> {
                    maid.teleportTo(finalLevel, loc.x, loc.y, loc.z, Set.of(), maid.getXRot(), maid.getYRot());
                    var playerSuccess = player.teleportTo(finalLevel, loc.x, loc.y, loc.z, Set.of(), maid.getXRot(), maid.getYRot());

                    if (playerSuccess) {
                        Player newPlayer = (Player) finalLevel.getEntity(player.getUUID());

                        ItemStack newStack = ModItems.STRETCHER.get().getDefaultInstance();
                        ((StretcherItem)newStack.getItem()).interactLivingEntity(newStack, newPlayer, newPlayer);
                    }
                });
            }
        }
    }
}
