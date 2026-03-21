package com.lastimp.dgh.forge.capability;

import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.ICapabilityHelper;
import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.config.impl.HealthLivingEntityList;
import com.lastimp.dgh.common.config.impl.PlayerBlackList;
import com.lastimp.dgh.forge.entry.register.ModCapabilities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CapabilityHelperNF implements ICapabilityHelper {
    @Override
    public boolean hasHealth(Entity entity) {
        if (entity instanceof Player player)
            return HealthLivingEntityList.isEntityWhitelisted(player.getType()) && !PlayerBlackList.isPlayerBlacklisted(player);
        return HealthLivingEntityList.isEntityWhitelisted(entity.getType());
    }

    @Override
    public Optional<HealthCapability> getHealth(LivingEntity entity) {
        try {
            return entity.getCapability(ModCapabilities.HEALTH, null).resolve();
        } catch (Throwable ignored) {
            // Some entities query capability during constructor before synced data is ready.
            return Optional.empty();
        }
    }

    @Override
    public boolean hasNutrient(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public Optional<NutrientCapability> getNutrient(Player player) {
        return player.getCapability(ModCapabilities.NUTRIENT, null).resolve();
    }

    @Override
    public boolean hasDisease(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public Optional<DiseaseCapability> getDisease(Player player) {
        return player.getCapability(ModCapabilities.DISEASE, null).resolve();
    }

    @Override
    public void saveHealth(LivingEntity entity) {
    }
}
