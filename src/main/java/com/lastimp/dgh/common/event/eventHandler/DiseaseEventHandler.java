package com.lastimp.dgh.common.event.eventHandler;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.system.disease.DiseaseManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class DiseaseEventHandler {
    private static final DiseaseManager DISEASE_MANAGER = new DiseaseManager();

    public static void onPlayerTick(Player player) {
        DiseaseCapability.getAndApply(player, capability -> DISEASE_MANAGER.updateDisease(player, capability));
        DrugEventHandler.onPlayerTick(player);
    }

    public static void onRainAction(Player player) {
        DiseaseCapability.getAndApply(player, capability -> DISEASE_MANAGER.triggerRainInfection(player, capability));
    }

    public static void onPlayerWakeUp(Player player) {
        DiseaseCapability.getAndApply(player, capability -> DISEASE_MANAGER.onPlayerWakeUp(player, capability));
    }

    public static void onPlayerDamage(Player player, DamageSource source, float amount) {
        DiseaseCapability.getAndApply(player, capability -> DISEASE_MANAGER.onPlayerDamage(player, capability, source, amount));
    }

    public static void onPlayerDeath(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        DiseaseCapability.getAndApply(player, capability -> {
            DISEASE_MANAGER.recordDeath(player, capability);
            persistedTag.put(DiseaseCapability.DISEASE_RECORD, capability.serializeRespawnPersistent());
        });
        data.put(Player.PERSISTED_NBT_TAG, persistedTag);
    }

    public static void onPlayerFall(Player player, float distance) {
        DiseaseCapability.getAndApply(player, capability -> DISEASE_MANAGER.triggerFallTrauma(player, capability, distance));
    }
}