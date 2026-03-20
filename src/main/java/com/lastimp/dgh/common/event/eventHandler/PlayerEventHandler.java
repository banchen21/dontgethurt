package com.lastimp.dgh.common.event.eventHandler;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.system.disease.DiseaseManager;
import com.lastimp.dgh.common.config.ModConfigs;
import com.lastimp.dgh.common.item.bases.AbstractHealingItem;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.entry.register.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class PlayerEventHandler {
    public static void logIn(Player player) {
        if (player.level().isClientSide) return;

        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        var key = "dgh_new_player";
        if (!persistedTag.getBoolean(key)) {
            player.getInventory().add(new ItemStack(ModItems.HEALTH_CARE_BAG.get()));
            player.getInventory().add(new ItemStack(ModItems.BANDAGE.get(), 8));
            player.getInventory().add(new ItemStack(ModItems.MORPHINE.get(), 2));
            persistedTag.putBoolean(key, true);
            data.put(Player.PERSISTED_NBT_TAG, persistedTag);
        }
        ModConfigs.synToPlayer((ServerPlayer) player);
        // TEST: 给新玩家一次性触发所有疾病以便开发测试使用
        final String TEST_KEY = "dgh_test_given_all_diseases";
        if (!persistedTag.getBoolean(TEST_KEY)) {
            var dm = new DiseaseManager();
            String[] all = new String[]{
                    "upper_respiratory_infection",
                    "sepsis",
                    "aids",
                    "undead_infection",
                    "ptsd",
                    "fracture_dislocation",
                    "crimson_disease",
                    "hippocratic_syndrome",
                    "ender_erosion",
                    "dietary_complication",
                    "tetanus"
            };
            for (var diseaseKey : all) {
                try {
                    dm.triggerDisease(player, diseaseKey);
                } catch (Exception ignored) {
                }
            }
            persistedTag.putBoolean(TEST_KEY, true);
            data.put(Player.PERSISTED_NBT_TAG, persistedTag);
        }
    }

    public static void logOut(Player player) {
        if (player.level().isClientSide) return;

        GameRules rules = player.level().getGameRules();
        if(!player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            rules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(true, player.level().getServer());
        }
    }

    public static InteractionResult onPlayerInteractEntity(Player player, Entity target, InteractionHand hand) {
        if (!(target instanceof LivingEntity livingEntity)) return InteractionResult.PASS;
        if (!HealthCapability.isDown(livingEntity)) return InteractionResult.PASS;

        var item = player.getMainHandItem();
        if (item.getItem() instanceof AbstractHealingItem healingItem) {
            healingItem.interactLivingEntity(item, player, livingEntity, hand);
        }
        return InteractionResult.CONSUME;
    }

    public static void onPlayerRespawn(Player player) {
        if (player.level().isClientSide()) return;
        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        HealthCapability.getAndApply(player, newHealth -> {
            newHealth.deserialize(new HealthCapability().serialize());
            newHealth.respawnDeserializeNBT(persistedTag.getCompound(HealthCapability.HEALTH_RECORD));
        });
        DiseaseCapability.getAndApply(player, disease -> {
            disease.deserialize(new DiseaseCapability().serialize());
            if (persistedTag.contains(DiseaseCapability.DISEASE_RECORD)) {
                disease.deserializeRespawnPersistent(persistedTag.getCompound(DiseaseCapability.DISEASE_RECORD));
            }
        });
        persistedTag.remove(HealthCapability.HEALTH_RECORD);
        persistedTag.remove(DiseaseCapability.DISEASE_RECORD);
        data.put(Player.PERSISTED_NBT_TAG, persistedTag);
    }
}