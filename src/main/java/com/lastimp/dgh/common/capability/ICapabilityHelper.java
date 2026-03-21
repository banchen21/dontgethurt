package com.lastimp.dgh.common.capability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface ICapabilityHelper {
    boolean hasHealth(Entity entity);
    Optional<HealthCapability> getHealth(LivingEntity entity);
    boolean hasNutrient(Entity entity);
    Optional<NutrientCapability> getNutrient(Player player);
    void saveHealth(LivingEntity entity);
    boolean hasDisease(Entity entity);
    Optional<DiseaseCapability> getDisease(Player player);
}
