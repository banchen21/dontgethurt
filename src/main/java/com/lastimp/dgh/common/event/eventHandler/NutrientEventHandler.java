package com.lastimp.dgh.common.event.eventHandler;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition;
import com.lastimp.dgh.common.config.impl.NutrientFoodList;
import com.lastimp.dgh.common.config.record.NutrientFoodRecord;
import com.lastimp.dgh.common.enums.BodyComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.registries.ForgeRegistries;

public class NutrientEventHandler {
    private static final String PENDING_FOOD_ID = "dgh_pending_food_id";
    private static final String PENDING_FOOD_LEVEL = "dgh_pending_food_level";
    private static final String PENDING_SATURATION = "dgh_pending_saturation";
    private static final float TARGET_BALANCE = 0.5f;
    private static final float SLEEP_REBALANCE_STEP = 0.1f;
    private static final float SLEEP_HYDRATION_DECAY = 0.08f;
    private static final float HUNGER_CONSUME_STEP = 0.01f;
    private static final float HUNGER_RESTORE_STEP = 0.4f;
    private static final int HUNGER_TRIGGER_LEVEL = 6;
    private static final float LOW_HYDRATION_EXHAUSTION = 0.1f;
    private static final float CRITICAL_HYDRATION_EXHAUSTION = 0.25f;
    private static final float OVERHYDRATION_POISON_THRESHOLD = 0.90f;
    private static final int LOW_CARBOHYDRATE_BLINDNESS_DURATION = 45;
    private static final float LOW_FAT_DAMAGE_THRESHOLD = 0.20f;
    private static final float LOW_FAT_DAMAGE_MULTIPLIER = 1.25f;
    private static final float HIGH_FAT_EFFECT_THRESHOLD = 0.80f;
    private static final int HIGH_FAT_EFFECT_DURATION = 60;
    private static final float LOW_PROTEIN_LACERATION_THRESHOLD = 0.20f;
    private static final float LOW_PROTEIN_LACERATION_BASE = 0.03f;
    private static final float LOW_PROTEIN_NATURAL_HEAL_BLOCK_THRESHOLD = 0.20f;
    private static final float HIGH_PROTEIN_MALABSORPTION_THRESHOLD = 0.80f;
    private static final float HIGH_PROTEIN_ABSORPTION_FACTOR = 0.5f;
    private static final int HIGH_PROTEIN_NAUSEA_DURATION = 100;
    private static final float HIGH_VITAMINS_MALABSORPTION_THRESHOLD = 0.80f;
    private static final float HIGH_DIETARY_FIBER_MALABSORPTION_THRESHOLD = 0.80f;
    private static final float LOW_MINERALS_BLOOD_SCAN_BLOCK_THRESHOLD = 0.20f;
    private static final float HIGH_MINERALS_HUNGER_THRESHOLD = 0.80f;
    private static final int HIGH_MINERALS_HUNGER_DURATION = 60;

    public static void onFoodUseStart(Player player, ItemStack usingItem) {
        if (player.level().isClientSide()) {
            return;
        }
        if (!usingItem.isEdible()) {
            return;
        }
        var key = ForgeRegistries.ITEMS.getKey(usingItem.getItem());
        var data = player.getPersistentData();
        data.putString(PENDING_FOOD_ID, key.toString());
        data.putInt(PENDING_FOOD_LEVEL, player.getFoodData().getFoodLevel());
        data.putFloat(PENDING_SATURATION, player.getFoodData().getSaturationLevel());
    }

    public static void onFoodUseStop(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        var data = player.getPersistentData();
        data.remove(PENDING_FOOD_ID);
        data.remove(PENDING_FOOD_LEVEL);
        data.remove(PENDING_SATURATION);
    }

    public static void onFoodEaten(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        var data = player.getPersistentData();
        String id = data.getString(PENDING_FOOD_ID);
        int previousFoodLevel = data.getInt(PENDING_FOOD_LEVEL);
        float previousSaturation = data.getFloat(PENDING_SATURATION);
        data.remove(PENDING_FOOD_ID);
        data.remove(PENDING_FOOD_LEVEL);
        data.remove(PENDING_SATURATION);
        if (id.isEmpty()) {
            return;
        }

        var key = net.minecraft.resources.ResourceLocation.tryParse(id);
        if (key == null) {
            return;
        }

        NutrientFoodRecord record = NutrientFoodList.get(key);
        if (record == null) {
            return;
        }

        var foodData = player.getFoodData();
        NutrientCapability.getAndApply(player, nutrient -> {
            boolean dehydratedBeforeEat = isDehydrated(nutrient.hydration());
            boolean highProteinBeforeEat = nutrient.protein() >= HIGH_PROTEIN_MALABSORPTION_THRESHOLD;
            boolean highVitaminsBeforeEat = nutrient.vitamins() >= HIGH_VITAMINS_MALABSORPTION_THRESHOLD;
            boolean highDietaryFiberBeforeEat = nutrient.dietaryFiber() >= HIGH_DIETARY_FIBER_MALABSORPTION_THRESHOLD;
            boolean hasMalabsorption = highProteinBeforeEat || highVitaminsBeforeEat || highDietaryFiberBeforeEat;
            float absorptionFactor = hasMalabsorption ? HIGH_PROTEIN_ABSORPTION_FACTOR : 1.0f;
            nutrient.addCarbohydrate(record.CARBOHYDRATE() * absorptionFactor);
            nutrient.addFat(record.FAT() * absorptionFactor);
            nutrient.addProtein(record.PROTEIN() * absorptionFactor);
            nutrient.addVitamins(record.VITAMINS() * absorptionFactor);
            nutrient.addMinerals(record.MINERALS() * absorptionFactor);
            nutrient.addDietaryFiber(record.DIETARY_FIBER() * absorptionFactor);
            nutrient.addHydration(record.HYDRATION() * absorptionFactor);

            // 缺水状态下，进食不会恢复饥饿值。
            if (dehydratedBeforeEat) {
                foodData.setFoodLevel(previousFoodLevel);
                foodData.setSaturation(previousSaturation);
                return;
            }

            if (hasMalabsorption) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, HIGH_PROTEIN_NAUSEA_DURATION, 0));
            }
        });
    }

    public static void onPlayerWakeUp(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        NutrientCapability.getAndApply(player, nutrient -> {
            nutrient.addCarbohydrate(moveDelta(nutrient.carbohydrate(), SLEEP_REBALANCE_STEP));
            nutrient.addFat(moveDelta(nutrient.fat(), SLEEP_REBALANCE_STEP));
            nutrient.addProtein(moveDelta(nutrient.protein(), SLEEP_REBALANCE_STEP));
            nutrient.addVitamins(moveDelta(nutrient.vitamins(), SLEEP_REBALANCE_STEP));
            nutrient.addMinerals(moveDelta(nutrient.minerals(), SLEEP_REBALANCE_STEP));
            nutrient.addDietaryFiber(moveDelta(nutrient.dietaryFiber(), SLEEP_REBALANCE_STEP));
            nutrient.addHydration(-SLEEP_HYDRATION_DECAY);
        });
    }

    public static void onPlayerTick(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }

        applyHighFatEffects(player);
        applyHighMineralsEffects(player);
        handleHydrationAndSurvival(player);

        var foodData = player.getFoodData();
        if (foodData.getFoodLevel() > HUNGER_TRIGGER_LEVEL) {
            return;
        }

        NutrientCapability.getAndApply(player, nutrient -> {
            if (isDehydrated(nutrient.hydration())) {
                return;
            }

            float remaining = HUNGER_CONSUME_STEP;

            float usedCarb = Math.min(remaining, nutrient.carbohydrate());
            if (usedCarb > 0) {
                nutrient.addCarbohydrate(-usedCarb);
                remaining -= usedCarb;
            }

            float usedFat = Math.min(remaining, nutrient.fat());
            if (usedFat > 0) {
                nutrient.addFat(-usedFat);
                remaining -= usedFat;
            }

            float usedProtein = Math.min(remaining, nutrient.protein());
            if (usedProtein > 0) {
                nutrient.addProtein(-usedProtein);
                remaining -= usedProtein;
            }

            float consumed = HUNGER_CONSUME_STEP - remaining;
            if (consumed > 0) {
                float saturation = foodData.getSaturationLevel() + HUNGER_RESTORE_STEP * consumed / HUNGER_CONSUME_STEP;
                foodData.setSaturation(Math.min(foodData.getFoodLevel(), saturation));
            }
        });
    }

    public static float modifyIncomingDamage(Player player, float originalDamage) {
        if (originalDamage <= 0 || player.level().isClientSide()) {
            return originalDamage;
        }

        return NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.fat() <= LOW_FAT_DAMAGE_THRESHOLD) {
                return originalDamage * LOW_FAT_DAMAGE_MULTIPLIER;
            }
            return originalDamage;
        }, originalDamage);
    }

    public static void onPlayerDamaged(Player player, float damageAmount) {
        if (damageAmount <= 0 || player.level().isClientSide()) {
            return;
        }

        NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.protein() > LOW_PROTEIN_LACERATION_THRESHOLD) {
                return;
            }

            float lacerationValue = Math.min(0.12f, LOW_PROTEIN_LACERATION_BASE + damageAmount * 0.01f);
            var targets = BodyComponents.VISIBLE_BODIES;
            BodyComponents target = targets.get(player.getRandom().nextInt(targets.size()));
            HealthCapability.getAndApply(player, health -> {
                if (health.getComponent(target) instanceof AbstractVisibleBody body) {
                    body.injury(BodyCondition.OPEN_WOUND, lacerationValue);
                    health.addDirectInjury(body.getComponent(), Component.literal("撕裂伤"), lacerationValue, 1);
                }
            });
        });
    }

    public static boolean shouldBlockNaturalHealing(Player player, float healingAmount) {
        if (player.level().isClientSide() || healingAmount <= 0) {
            return false;
        }

        if (!player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            return false;
        }

        if (player.getFoodData().getFoodLevel() < 18 || player.hasEffect(MobEffects.REGENERATION)) {
            return false;
        }

        return NutrientCapability.getAndApply(player,
                nutrient -> nutrient.protein() <= LOW_PROTEIN_NATURAL_HEAL_BLOCK_THRESHOLD,
                false);
    }

    public static boolean hasAccurateBloodData(Player player) {
        return NutrientCapability.getAndApply(player,
                nutrient -> nutrient.minerals() > LOW_MINERALS_BLOOD_SCAN_BLOCK_THRESHOLD,
                true);
    }

    private static void handleHydrationAndSurvival(Player player) {
        if (!PlatformService.CONFIG.ENABLE_SURVIVAL_STATUS()) {
            return;
        }
        if (!PlatformService.CONFIG.ENABLE_HYDRATION_SYSTEM()) {
            return;
        }

        float hydrationDelta = -PlatformService.CONFIG.HYDRATION_DECAY_BASE();
        if (player.isSprinting()) {
            hydrationDelta -= PlatformService.CONFIG.HYDRATION_DECAY_SPRINT();
        }
        if (player.isInWaterRainOrBubble()) {
            hydrationDelta += PlatformService.CONFIG.HYDRATION_RECOVER_IN_WATER();
        }
        final float finalHydrationDelta = hydrationDelta;

        var foodData = player.getFoodData();
        NutrientCapability.getAndApply(player, nutrient -> {
            nutrient.addHydration(finalHydrationDelta);

            float hydration = nutrient.hydration();
            float criticalThreshold = normalizedCriticalThreshold();
            float lowThreshold = normalizedLowThreshold();

            if (nutrient.carbohydrate() <= PlatformService.CONFIG.LOW_CARBOHYDRATE_BLINDNESS_THRESHOLD()
                    && player.tickCount % PlatformService.CONFIG.LOW_CARBOHYDRATE_BLINDNESS_INTERVAL() == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, LOW_CARBOHYDRATE_BLINDNESS_DURATION, 0));
            }

            if (nutrient.carbohydrate() >= PlatformService.CONFIG.HIGH_CARBOHYDRATE_THRESHOLD() && isJumping(player)) {
                nutrient.addHydration(-PlatformService.CONFIG.HIGH_CARBOHYDRATE_JUMP_HYDRATION_PENALTY());
                hydration = nutrient.hydration();
            }

            if (hydration >= OVERHYDRATION_POISON_THRESHOLD) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
            }

            if (hydration <= criticalThreshold) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                foodData.addExhaustion(CRITICAL_HYDRATION_EXHAUSTION);
                return;
            }

            if (hydration <= lowThreshold) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
                foodData.addExhaustion(LOW_HYDRATION_EXHAUSTION);
            }
        });
    }

    private static void applyHighFatEffects(Player player) {
        NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.fat() >= HIGH_FAT_EFFECT_THRESHOLD) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, HIGH_FAT_EFFECT_DURATION, 0));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, HIGH_FAT_EFFECT_DURATION, 0));
            }
        });
    }

    private static void applyHighMineralsEffects(Player player) {
        NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.minerals() >= HIGH_MINERALS_HUNGER_THRESHOLD) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, HIGH_MINERALS_HUNGER_DURATION, 1));
            }
        });
    }

    private static boolean isDehydrated(float hydration) {
        return hydration <= normalizedLowThreshold();
    }

    private static float normalizedLowThreshold() {
        float low = PlatformService.CONFIG.HYDRATION_LOW_THRESHOLD();
        float critical = PlatformService.CONFIG.HYDRATION_CRITICAL_THRESHOLD();
        return Math.max(low, critical);
    }

    private static float normalizedCriticalThreshold() {
        float low = PlatformService.CONFIG.HYDRATION_LOW_THRESHOLD();
        float critical = PlatformService.CONFIG.HYDRATION_CRITICAL_THRESHOLD();
        return Math.min(low, critical);
    }

    private static boolean isJumping(Player player) {
        return !player.onGround()
                && player.getDeltaMovement().y > 0.05D
                && !player.isInWaterRainOrBubble();
    }

    private static float moveDelta(float current, float amount) {
        float delta = TARGET_BALANCE - current;
        if (Math.abs(delta) <= amount) {
            return delta;
        }
        return Math.signum(delta) * amount;
    }
}
