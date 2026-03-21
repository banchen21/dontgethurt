package com.lastimp.dgh.common.capability.healthCore.diseaseSystem;

import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DiseaseEventHandler {
    private DiseaseEventHandler() {
    }

    public static void onRainAction(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        HealthCapability.getAndApply(player, health -> health.disease().onRainAction(health, player));
    }

    public static void onInjury(LivingEntity target, net.minecraft.world.damagesource.DamageSource source) {
        if (target.level().isClientSide() || source == null) {
            return;
        }
        HealthCapability.getAndApply(target, health -> health.disease().onInjury(health, target, source.getEntity()));
    }

    public static void onWakeUp(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        HealthCapability.getAndApply(player, health -> health.disease().onWakeUp());
    }

    public static void onMedicineUsed(LivingEntity target, ItemStack usedStack) {
        if (target.level().isClientSide() || usedStack.isEmpty()) {
            return;
        }
        HealthCapability.getAndApply(target, health -> health.disease().onMedicineUsed(health, target, usedStack));
    }
}

