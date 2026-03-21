package com.lastimp.dgh.common.system.disease;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition;
import com.lastimp.dgh.compact.CompatRegistry;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DiseaseManager {
    private static final float AIDS_INFECTION_BOOST_PER_STAGE = 0.20f;
    private static final float RAIN_URI_BASE_CHANCE = 0.08f;
    private static final float RAIN_URI_LOW_PROTEIN_BONUS = 0.10f;
    private static final float RAIN_URI_MID_PROTEIN_BONUS = 0.05f;
    private static final float RAIN_URI_LOW_VITAMIN_BONUS = 0.08f;
    private static final float RAIN_URI_MID_VITAMIN_BONUS = 0.04f;
    private static final float RAIN_URI_LOW_FIBER_BONUS = 0.06f;
    private static final float RAIN_URI_MID_FIBER_BONUS = 0.03f;
    private static final float URI_SLEEP_HEAL_STAGE1_CHANCE = 0.65f;
    private static final float URI_SLEEP_HEAL_STAGE2_CHANCE = 0.35f;
    private static final float URI_SLEEP_PROTEIN_COST = 0.12f;
    private static final float URI_SLEEP_VITAMIN_COST = 0.06f;
    private static final float SEPSIS_ACTION_CARBOHYDRATE_COST = 0.010f;
    private static final float SEPSIS_ACTION_FAT_COST = 0.008f;
    private static final float SEPSIS_POISON_STAGE1_CHANCE = 0.25f;
    private static final float SEPSIS_POISON_STAGE2_CHANCE = 0.50f;
    private static final float SEPSIS_POISON_STAGE3_CHANCE = 0.85f;
    private static final String PTSD_DEATH_CHAIN = "dgh_ptsd_death_chain";
    private static final String PTSD_LAST_DEATH_TIME = "dgh_ptsd_last_death_time";
    private static final String PTSD_LAST_SOURCE = "dgh_ptsd_last_source";
    private static final String PTSD_LAST_SOURCE_TIME = "dgh_ptsd_last_source_time";
    private static final int PROGRESS_MAX = 100;
    private static final long PTSD_DEATH_WINDOW = 24000L;

    public void updateDisease(Player player, DiseaseCapability capability) {
        if (player.level().isClientSide()) {
            return;
        }

        syncWithHealth(player, capability);
        if (player.tickCount % 20 != 0) {
            return;
        }

        updateUpperRespiratoryInfection(player, capability);
        updateSepsis(player, capability);
        updateUndeadInfection(player, capability);
        updateDietaryComplication(player, capability);
        updatePtsd(player, capability);
        updateFractureDislocation(player, capability);
        updateAids(player, capability);
        updateTetanus(player, capability);
        updateCrimsonDisease(player, capability);
        updateHippocraticSyndrome(player, capability);
        updateEnderErosion(player, capability);
        applySymptoms(player, capability);
    }

    public void triggerDisease(Player player, String diseaseType) {
        DiseaseCapability.getAndApply(player, capability -> {
            switch (diseaseType) {
                case "upper_respiratory_infection" -> infectImmediately(player, "upper_respiratory_infection", capability.upperRespiratoryInfectionStage(), capability::setUpperRespiratoryInfectionStage, capability::setUpperRespiratoryInfectionProgress);
                case "sepsis" -> infectImmediately(player, "sepsis", capability.sepsisStage(), capability::setSepsisStage, capability::setSepsisProgress);
                case "undead_infection" -> infectImmediately(player, "undead_infection", capability.undeadInfectionStage(), capability::setUndeadInfectionStage, capability::setUndeadInfectionProgress);
                case "dietary_complication" -> infectImmediately(player, "dietary_complication", capability.dietaryComplicationStage(), capability::setDietaryComplicationStage, capability::setDietaryComplicationProgress);
                case "ptsd" -> infectImmediately(player, "ptsd", capability.ptsdStage(), capability::setPtsdStage, capability::setPtsdProgress);
                case "fracture_dislocation" -> infectImmediately(player, "fracture_dislocation", capability.fractureDislocationStage(), capability::setFractureDislocationStage, capability::setFractureDislocationProgress);
                case "aids" -> infectImmediately(player, "aids", capability.aidsStage(), capability::setAidsStage, capability::setAidsProgress);
                case "tetanus" -> infectImmediately(player, "tetanus", capability.tetanusStage(), capability::setTetanusStage, capability::setTetanusProgress);
                case "crimson_disease" -> infectImmediately(player, "crimson_disease", capability.crimsonDiseaseStage(), capability::setCrimsonDiseaseStage, capability::setCrimsonDiseaseProgress);
                case "hippocratic_syndrome" -> infectImmediately(player, "hippocratic_syndrome", capability.hippocraticSyndromeStage(), capability::setHippocraticSyndromeStage, capability::setHippocraticSyndromeProgress);
                case "ender_erosion" -> infectImmediately(player, "ender_erosion", capability.enderErosionStage(), capability::setEnderErosionStage, capability::setEnderErosionProgress);
                default -> {
                }
            }
        });
    }

    public void cureDisease(Player player, String diseaseType) {
        DiseaseCapability.getAndApply(player, capability -> {
            switch (diseaseType) {
                case "upper_respiratory_infection" -> reduceStage(player, "upper_respiratory_infection", capability.upperRespiratoryInfectionStage(), capability::setUpperRespiratoryInfectionStage, capability::setUpperRespiratoryInfectionProgress);
                case "sepsis" -> reduceStage(player, "sepsis", capability.sepsisStage(), capability::setSepsisStage, capability::setSepsisProgress);
                case "undead_infection" -> reduceStage(player, "undead_infection", capability.undeadInfectionStage(), capability::setUndeadInfectionStage, capability::setUndeadInfectionProgress);
                case "dietary_complication" -> reduceStage(player, "dietary_complication", capability.dietaryComplicationStage(), capability::setDietaryComplicationStage, capability::setDietaryComplicationProgress);
                case "ptsd" -> reduceStage(player, "ptsd", capability.ptsdStage(), capability::setPtsdStage, capability::setPtsdProgress);
                case "fracture_dislocation" -> reduceStage(player, "fracture_dislocation", capability.fractureDislocationStage(), capability::setFractureDislocationStage, capability::setFractureDislocationProgress);
                case "aids" -> reduceStage(player, "aids", capability.aidsStage(), capability::setAidsStage, capability::setAidsProgress);
                case "tetanus" -> reduceStage(player, "tetanus", capability.tetanusStage(), capability::setTetanusStage, capability::setTetanusProgress);
                case "crimson_disease" -> reduceStage(player, "crimson_disease", capability.crimsonDiseaseStage(), capability::setCrimsonDiseaseStage, capability::setCrimsonDiseaseProgress);
                case "hippocratic_syndrome" -> reduceStage(player, "hippocratic_syndrome", capability.hippocraticSyndromeStage(), capability::setHippocraticSyndromeStage, capability::setHippocraticSyndromeProgress);
                case "ender_erosion" -> reduceStage(player, "ender_erosion", capability.enderErosionStage(), capability::setEnderErosionStage, capability::setEnderErosionProgress);
                default -> {
                }
            }
        });
    }

    public void onPlayerDamage(Player player, DiseaseCapability capability, DamageSource source, float amount) {
        if (player.level().isClientSide() || amount <= 0) {
            return;
        }

        if (capability.ptsdStage() > 0
                && player.getHealth() <= player.getMaxHealth() * 0.5f
                && isFearedSourceAttack(player, source)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, Math.max(0, capability.ptsdStage() - 1)));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, Math.max(0, capability.ptsdStage() - 1)));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, Math.max(0, capability.ptsdStage() - 1)));
        }

        rememberThreat(player, source);
        triggerInjuryInfection(player, capability, amount);

        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) {
            triggerFallTrauma(player, capability, amount);
        }

        if (source.getEntity() instanceof LivingEntity attacker
            && attacker.getMobType() == MobType.UNDEAD
            && player.level().getGameTime() >= capability.getUndeadBlockerUntilTick()) {
            infectImmediately(player, "undead_infection", capability.undeadInfectionStage(), capability::setUndeadInfectionStage, capability::setUndeadInfectionProgress);
        }

        // 辐射联动基础桥接：检测到辐射伤害时推进绯红症
        if (CompatRegistry.hasAnyRadiationMod() && isRadiationThreat(source)) {
            int crimsonDelta = amplifyByAids(capability, "crimson_disease", 30);
            addProgress(player, "crimson_disease",
                    capability.crimsonDiseaseStage(), capability.crimsonDiseaseProgress(),
                    crimsonDelta, capability::setCrimsonDiseaseStage, capability::setCrimsonDiseaseProgress);
        }

        // 末影侵蚀：末地来源伤害或末地维度战斗时触发
        if (isEndThreat(source, player, amount)) {
            addProgress(player, "ender_erosion",
                    capability.enderErosionStage(), capability.enderErosionProgress(),
                    35, capability::setEnderErosionStage, capability::setEnderErosionProgress);
        }

    }

    public void onPlayerWakeUp(Player player, DiseaseCapability capability) {
        if (player.level().isClientSide()) {
            return;
        }

        NutrientCapability.getAndApply(player, nutrient -> {
            int uriStage = capability.upperRespiratoryInfectionStage();
            if (uriStage > 0 && uriStage < 3
                    && nutrient.protein() >= URI_SLEEP_PROTEIN_COST
                    && nutrient.vitamins() >= URI_SLEEP_VITAMIN_COST) {
                float healChance = uriStage == 1 ? URI_SLEEP_HEAL_STAGE1_CHANCE : URI_SLEEP_HEAL_STAGE2_CHANCE;
                if (player.getRandom().nextFloat() < healChance) {
                    nutrient.addProtein(-URI_SLEEP_PROTEIN_COST);
                    nutrient.addVitamins(-URI_SLEEP_VITAMIN_COST);
                    reduceStage(player, "upper_respiratory_infection", uriStage,
                            capability::setUpperRespiratoryInfectionStage,
                            capability::setUpperRespiratoryInfectionProgress);
                }
            }

            if (capability.dietaryComplicationStage() > 0 && isBalanced(nutrient)) {
                reduceStage(player, "dietary_complication", capability.dietaryComplicationStage(), capability::setDietaryComplicationStage, capability::setDietaryComplicationProgress);
            }
        });

        if (capability.ptsdStage() > 0 && player.getRandom().nextFloat() < 0.5f) {
            reduceStage(player, "ptsd", capability.ptsdStage(), capability::setPtsdStage, capability::setPtsdProgress);
        }

        // 末影侵蚀：离开末地后可通过睡眠获得轻度缓解
        if (capability.enderErosionStage() > 0
                && player.level().dimension() != Level.END
                && player.getRandom().nextFloat() < 0.35f) {
            reduceStage(player, "ender_erosion", capability.enderErosionStage(),
                    capability::setEnderErosionStage, capability::setEnderErosionProgress);
        }
    }

    public void triggerRainInfection(Player player, DiseaseCapability capability) {
        if (!isRainExposure(player)) {
            return;
        }

        float[] chance = new float[]{RAIN_URI_BASE_CHANCE * aidsInfectionFactor(capability)};
        NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.protein() < 0.25f) {
                chance[0] += RAIN_URI_LOW_PROTEIN_BONUS;
            } else if (nutrient.protein() < 0.40f) {
                chance[0] += RAIN_URI_MID_PROTEIN_BONUS;
            }

            if (nutrient.vitamins() < 0.25f) {
                chance[0] += RAIN_URI_LOW_VITAMIN_BONUS;
            } else if (nutrient.vitamins() < 0.40f) {
                chance[0] += RAIN_URI_MID_VITAMIN_BONUS;
            }

            if (nutrient.dietaryFiber() < 0.25f) {
                chance[0] += RAIN_URI_LOW_FIBER_BONUS;
            } else if (nutrient.dietaryFiber() < 0.40f) {
                chance[0] += RAIN_URI_MID_FIBER_BONUS;
            }
        });

        if (player.getRandom().nextFloat() < Math.min(0.9f, chance[0])) {
            infectImmediately(player, "upper_respiratory_infection", capability.upperRespiratoryInfectionStage(), capability::setUpperRespiratoryInfectionStage, capability::setUpperRespiratoryInfectionProgress);
        }
    }

    public void triggerInjuryInfection(Player player, DiseaseCapability capability, float amount) {
        if (amount < 2.0f) {
            return;
        }

        int[] sepsisDelta = new int[]{35};
        float[] tetanusFactor = new float[]{1.0f};
        NutrientCapability.getAndApply(player, nutrient -> {
            if (nutrient.dietaryFiber() < 0.25f) {
                sepsisDelta[0] += 15;
                tetanusFactor[0] += 0.35f;
            } else if (nutrient.dietaryFiber() < 0.40f) {
                sepsisDelta[0] += 8;
                tetanusFactor[0] += 0.15f;
            }
        });

        int boostedSepsisDelta = amplifyByAids(capability, "sepsis", sepsisDelta[0]);
        addProgress(player, "sepsis", capability.sepsisStage(), capability.sepsisProgress(), boostedSepsisDelta, capability::setSepsisStage, capability::setSepsisProgress);

        // M5: 伤口感染破伤风概率：伤害越大概率越高，上限 20%
        float tetanusChance = Math.min(0.20f, amount * 0.03f) * aidsInfectionFactor(capability) * tetanusFactor[0];
        if (player.getRandom().nextFloat() < tetanusChance) {
            infectImmediately(player, "tetanus", capability.tetanusStage(), capability::setTetanusStage, capability::setTetanusProgress);
        }
    }

    public void triggerFallTrauma(Player player, DiseaseCapability capability, float distance) {
        if (distance < 6.0f) {
            return;
        }

        infectImmediately(player, "fracture_dislocation", capability.fractureDislocationStage(), capability::setFractureDislocationStage, capability::setFractureDislocationProgress);
    }

    public void recordDeath(Player player, DiseaseCapability capability) {
        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        long gameTime = player.level().getGameTime();
        long lastDeathTime = persistedTag.getLong(PTSD_LAST_DEATH_TIME);
        int deathChain = persistedTag.getInt(PTSD_DEATH_CHAIN);

        if (gameTime - lastDeathTime > PTSD_DEATH_WINDOW) {
            deathChain = 0;
        }

        deathChain++;
        persistedTag.putLong(PTSD_LAST_DEATH_TIME, gameTime);
        persistedTag.putInt(PTSD_DEATH_CHAIN, deathChain);
        data.put(Player.PERSISTED_NBT_TAG, persistedTag);

        if (deathChain >= 2) {
            infectImmediately(player, "ptsd", capability.ptsdStage(), capability::setPtsdStage, capability::setPtsdProgress);
        }
    }

    private void syncWithHealth(Player player, DiseaseCapability capability) {
        HealthCapability.getAndApply(player, health -> {
            var blood = health.getComponent(BodyComponents.BLOOD);
            float sepsisValue = blood.getConditionValue(BodyCondition.SEPSIS);
            int mappedSepsisStage = 0;
            if (sepsisValue > 0.65f) {
                mappedSepsisStage = 3;
            } else if (sepsisValue > 0.35f) {
                mappedSepsisStage = 2;
            } else if (sepsisValue > 0.10f) {
                mappedSepsisStage = 1;
            }
            if (mappedSepsisStage > capability.sepsisStage()) {
                changeStage(player, "sepsis", capability.sepsisStage(), mappedSepsisStage, capability::setSepsisStage, capability::setSepsisProgress, 0);
            }

            int fractureCount = 0;
            int dislocationCount = 0;
            for (var component : BodyComponents.VISIBLE_BODIES) {
                if (!(health.getComponent(component) instanceof AbstractVisibleBody body)) {
                    continue;
                }
                if (body.abnormalWithHidden(BodyCondition.FRACTURE)) {
                    fractureCount++;
                }
                if (body instanceof AbstractExtremities extremities && extremities.abnormal(BodyCondition.DISLOCATION)) {
                    dislocationCount++;
                }
            }

            int mappedFractureStage = 0;
            if (fractureCount >= 2) {
                mappedFractureStage = 3;
            } else if (fractureCount == 1) {
                mappedFractureStage = 2;
            } else if (dislocationCount > 0) {
                mappedFractureStage = 1;
            }
            if (mappedFractureStage > capability.fractureDislocationStage()) {
                changeStage(player, "fracture_dislocation", capability.fractureDislocationStage(), mappedFractureStage, capability::setFractureDislocationStage, capability::setFractureDislocationProgress, 0);
            }
        });
    }

    private void updateUpperRespiratoryInfection(Player player, DiseaseCapability capability) {
        if (capability.upperRespiratoryInfectionStage() <= 0) {
            return;
        }

        final int[] delta = new int[]{-4};
        NutrientCapability.getAndApply(player, nutrient -> {
            if (player.isInWaterRainOrBubble()) {
                delta[0] = 12;
            } else if (nutrient.protein() < 0.25f) {
                delta[0] = 8;
            } else if (nutrient.protein() < 0.40f) {
                delta[0] = 4;
            }
        });
        int boostedDelta = amplifyByAids(capability, "upper_respiratory_infection", delta[0]);
        updateProgress(player, "upper_respiratory_infection", capability.upperRespiratoryInfectionStage(), capability.upperRespiratoryInfectionProgress(), boostedDelta, capability::setUpperRespiratoryInfectionStage, capability::setUpperRespiratoryInfectionProgress);
    }

    private void updateSepsis(Player player, DiseaseCapability capability) {
        if (capability.sepsisStage() <= 0) {
            return;
        }

        final int[] delta = new int[]{-6};
        HealthCapability.getAndApply(player, health -> {
            float value = health.getComponent(BodyComponents.BLOOD).getConditionValue(BodyCondition.SEPSIS);
            if (value > 0.50f) {
                delta[0] = 12;
            } else if (value > 0.20f) {
                delta[0] = 6;
            }
        });
        int boostedDelta = amplifyByAids(capability, "sepsis", delta[0]);
        updateProgress(player, "sepsis", capability.sepsisStage(), capability.sepsisProgress(), boostedDelta, capability::setSepsisStage, capability::setSepsisProgress);

        if (isPlayerActive(player)) {
            NutrientCapability.getAndApply(player, nutrient -> {
                nutrient.addCarbohydrate(-SEPSIS_ACTION_CARBOHYDRATE_COST * capability.sepsisStage());
                nutrient.addFat(-SEPSIS_ACTION_FAT_COST * capability.sepsisStage());
            });
        }
    }

    private void updateUndeadInfection(Player player, DiseaseCapability capability) {
        if (capability.undeadInfectionStage() <= 0) {
            return;
        }

        if (player.level().getGameTime() < capability.getUndeadBlockerUntilTick()) {
            updateProgress(player, "undead_infection",
                    capability.undeadInfectionStage(), capability.undeadInfectionProgress(),
                    -10, capability::setUndeadInfectionStage, capability::setUndeadInfectionProgress);
            return;
        }

        int delta = player.level().getMaxLocalRawBrightness(player.blockPosition()) <= 7 ? 8 : 4;
        int boostedDelta = amplifyByAids(capability, "undead_infection", delta);
        updateProgress(player, "undead_infection", capability.undeadInfectionStage(), capability.undeadInfectionProgress(), boostedDelta, capability::setUndeadInfectionStage, capability::setUndeadInfectionProgress);
    }

    private void updateDietaryComplication(Player player, DiseaseCapability capability) {
        final int[] delta = new int[]{-8};
        final boolean[] imbalanced = new boolean[]{false};
        NutrientCapability.getAndApply(player, nutrient -> {
            float maxDiff = maxNutritionDiff(nutrient);
            if (maxDiff >= 0.35f) {
                imbalanced[0] = true;
                delta[0] = 10;
            } else if (maxDiff >= 0.22f) {
                imbalanced[0] = true;
                delta[0] = 5;
            }
        });

        if (imbalanced[0] && capability.dietaryComplicationStage() == 0) {
            infectImmediately(player, "dietary_complication", capability.dietaryComplicationStage(), capability::setDietaryComplicationStage, capability::setDietaryComplicationProgress);
            return;
        }

        if (capability.dietaryComplicationStage() <= 0) {
            return;
        }

        int boostedDelta = amplifyByAids(capability, "dietary_complication", delta[0]);
        updateProgress(player, "dietary_complication", capability.dietaryComplicationStage(), capability.dietaryComplicationProgress(), boostedDelta, capability::setDietaryComplicationStage, capability::setDietaryComplicationProgress);
    }

    private void updatePtsd(Player player, DiseaseCapability capability) {
        if (capability.ptsdStage() <= 0) {
            return;
        }

        var data = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        long lastThreatTime = data.getLong(PTSD_LAST_SOURCE_TIME);
        int delta = player.level().getGameTime() - lastThreatTime < 200 && player.getHealth() <= player.getMaxHealth() * 0.5f ? 8 : -3;
        updateProgress(player, "ptsd", capability.ptsdStage(), capability.ptsdProgress(), delta, capability::setPtsdStage, capability::setPtsdProgress);
    }

    private void updateFractureDislocation(Player player, DiseaseCapability capability) {
        if (capability.fractureDislocationStage() <= 0) {
            return;
        }

        final int[] delta = new int[]{-6};
        HealthCapability.getAndApply(player, health -> {
            for (var component : BodyComponents.VISIBLE_BODIES) {
                if (!(health.getComponent(component) instanceof AbstractVisibleBody body)) {
                    continue;
                }
                if (body.abnormalWithHidden(BodyCondition.FRACTURE)) {
                    delta[0] = 8;
                    return;
                }
                if (body instanceof AbstractExtremities extremities && extremities.abnormal(BodyCondition.DISLOCATION)) {
                    delta[0] = 4;
                }
            }
        });
        int boostedDelta = amplifyByAids(capability, "fracture_dislocation", delta[0]);
        updateProgress(player, "fracture_dislocation", capability.fractureDislocationStage(), capability.fractureDislocationProgress(), boostedDelta, capability::setFractureDislocationStage, capability::setFractureDislocationProgress);
    }

    private void applySymptoms(Player player, DiseaseCapability capability) {
        int uriStage = capability.upperRespiratoryInfectionStage();
        if (uriStage > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, Math.max(0, uriStage - 1), false, false, true));
            if (uriStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, uriStage - 2, false, false, true));
            }
        }

        int sepsisStage = capability.sepsisStage();
        if (sepsisStage > 0) {
            if (player.getRandom().nextFloat() < sepsisPoisonChance(sepsisStage)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, Math.max(0, sepsisStage - 1), false, false, true));
            }
            player.causeFoodExhaustion(0.05f * sepsisStage);
        }

        int undeadStage = capability.undeadInfectionStage();
        if (undeadStage > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, Math.max(0, undeadStage - 1), false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, Math.max(0, undeadStage - 1), false, false, true));
            if (undeadStage >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false, true));
            }
        }

        int dietaryStage = capability.dietaryComplicationStage();
        if (dietaryStage > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
            if (dietaryStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, dietaryStage - 2, false, false, true));
            }
        }

        int fractureStage = capability.fractureDislocationStage();
        if (fractureStage > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, Math.max(0, fractureStage - 1), false, false, true));
        }

        // M5 新疾病症状
        int aidsStage = capability.aidsStage();
        if (aidsStage > 0) {
            // 艾滋病：免疫抑制，吸收值减少，耐久削弱
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, Math.max(0, aidsStage - 1), false, false, true));
            if (aidsStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false, true));
            }
        }

        int tetanusStage = capability.tetanusStage();
        if (tetanusStage > 0) {
            // 破伤风：僵直/颤抖，严重时恶心+力竭
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, Math.max(0, tetanusStage - 1), false, false, true));
            if (tetanusStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
                player.causeFoodExhaustion(0.04f * tetanusStage);
            }
            if (tetanusStage >= 3) {
                // 阶段3：每20tick格外扣血（致死轨迹）
                player.hurt(player.level().damageSources().generic(), 0.5f);
            }
        }

        int crimsonStage = capability.crimsonDiseaseStage();
        if (crimsonStage > 0) {
            // 绯红症：自我调节受阻，消化障碍，营养吸收减损
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, Math.max(0, crimsonStage - 1), false, false, true));
            if (crimsonStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false, true));
            }
            if (crimsonStage >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, false, false, true));
            }
        }

        int hippocraticStage = capability.hippocraticSyndromeStage();
        if (hippocraticStage > 0) {
            // 希波克症：慢性致死，后期明显衰竭
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, Math.max(0, hippocraticStage - 1), false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, Math.max(0, hippocraticStage - 1), false, false, true));
            if (hippocraticStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, false, false, true));
            }
            if (hippocraticStage >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false, true));
            }
        }

        int enderErosionStage = capability.enderErosionStage();
        if (enderErosionStage > 0) {
            // 末影侵蚀：行为异常与感知紊乱
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, Math.max(0, enderErosionStage - 1), false, false, true));
            if (enderErosionStage >= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, true));
            }
            if (enderErosionStage >= 3) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false, true));
                player.hurt(player.level().damageSources().magic(), 0.3f);
            }
        }
    }

    private void rememberThreat(Player player, DamageSource source) {
        String threatSource = sourceKey(source);
        if (threatSource.isEmpty()) {
            return;
        }

        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        persistedTag.putString(PTSD_LAST_SOURCE, threatSource);
        persistedTag.putLong(PTSD_LAST_SOURCE_TIME, player.level().getGameTime());
        data.put(Player.PERSISTED_NBT_TAG, persistedTag);
    }

    private boolean isFearedSourceAttack(Player player, DamageSource source) {
        String currentSource = sourceKey(source);
        if (currentSource.isEmpty()) {
            return false;
        }

        var data = player.getPersistentData();
        var persistedTag = data.getCompound(Player.PERSISTED_NBT_TAG);
        String fearedSource = persistedTag.getString(PTSD_LAST_SOURCE);
        return !fearedSource.isEmpty() && fearedSource.equals(currentSource);
    }

    private String sourceKey(DamageSource source) {
        if (source.getEntity() != null) {
            return source.getEntity().getType().toShortString();
        }

        String msgId = source.getMsgId();
        return msgId == null ? "" : msgId;
    }

    private float maxNutritionDiff(NutrientCapability nutrient) {
        float target = 0.5f;
        float maxDiff = Math.abs(nutrient.carbohydrate() - target);
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.fat() - target));
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.protein() - target));
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.vitamins() - target));
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.minerals() - target));
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.dietaryFiber() - target));
        maxDiff = Math.max(maxDiff, Math.abs(nutrient.hydration() - target));
        return maxDiff;
    }

    private boolean isBalanced(NutrientCapability nutrient) {
        return maxNutritionDiff(nutrient) < 0.15f;
    }

    // -------- M5 新疾病进展逻辑 --------

    private void updateAids(Player player, DiseaseCapability capability) {
        if (capability.aidsStage() <= 0) return;
        // 艾滋病：固定缓慢恶化，无自愈。拉米夫定胶囊 10% 治愈。
        updateProgress(player, "aids", capability.aidsStage(), capability.aidsProgress(),
                3, capability::setAidsStage, capability::setAidsProgress);
    }

    private void updateTetanus(Player player, DiseaseCapability capability) {
        if (capability.tetanusStage() <= 0) return;
        // 破伤风：快速恶化（delta=+8），深度感染（>=2阶段）加倍（delta=+16），无治疗。
        int stage = capability.tetanusStage();
        int delta = stage >= 2 ? 16 : 8;
        int boostedDelta = amplifyByAids(capability, "tetanus", delta);
        updateProgress(player, "tetanus", stage, capability.tetanusProgress(),
            boostedDelta, capability::setTetanusStage, capability::setTetanusProgress);
    }

    private void updateCrimsonDisease(Player player, DiseaseCapability capability) {
        if (capability.crimsonDiseaseStage() <= 0) return;
        // 绯红症：中等速度恶化（delta=+5），无自愈，联动模组辐射来源触发。
        int boostedDelta = amplifyByAids(capability, "crimson_disease", 5);
        updateProgress(player, "crimson_disease", capability.crimsonDiseaseStage(), capability.crimsonDiseaseProgress(),
            boostedDelta, capability::setCrimsonDiseaseStage, capability::setCrimsonDiseaseProgress);

        // 绯红症 III 阶段会激活希波克症（慢性致死）
        if (capability.crimsonDiseaseStage() >= 3 && capability.hippocraticSyndromeStage() <= 0) {
            infectImmediately(player, "hippocratic_syndrome", capability.hippocraticSyndromeStage(),
                    capability::setHippocraticSyndromeStage, capability::setHippocraticSyndromeProgress);
        }
    }

    private void updateHippocraticSyndrome(Player player, DiseaseCapability capability) {
        if (capability.hippocraticSyndromeStage() <= 0) return;
        // 希波克症：缓慢进展，但后期具备持续致死压力。
        int stage = capability.hippocraticSyndromeStage();
        int delta = stage >= 3 ? 4 : 2;
        updateProgress(player, "hippocratic_syndrome", stage, capability.hippocraticSyndromeProgress(),
                delta, capability::setHippocraticSyndromeStage, capability::setHippocraticSyndromeProgress);

        if (stage >= 3) {
            player.hurt(player.level().damageSources().wither(), 0.5f);
        }
    }

    private void updateEnderErosion(Player player, DiseaseCapability capability) {
        if (capability.enderErosionStage() <= 0) return;

        // 在末地时恶化更快，离开末地可缓慢恢复
        int delta = player.level().dimension() == Level.END ? 8 : -2;
        int boostedDelta = amplifyByAids(capability, "ender_erosion", delta);
        updateProgress(player, "ender_erosion", capability.enderErosionStage(), capability.enderErosionProgress(),
            boostedDelta, capability::setEnderErosionStage, capability::setEnderErosionProgress);
    }

    private boolean isEndThreat(DamageSource source, Player player, float amount) {
        if (amount < 2.0f) {
            return false;
        }

        if (player.level().dimension() == Level.END && source.getEntity() != null) {
            return true;
        }

        var direct = source.getDirectEntity();
        var attacker = source.getEntity();
        String directName = direct == null ? "" : direct.getType().toShortString();
        String attackerName = attacker == null ? "" : attacker.getType().toShortString();

        return directName.contains("ender") || attackerName.contains("ender") || attackerName.contains("shulker");
    }

    private boolean isRadiationThreat(DamageSource source) {
        String msgId = source.getMsgId();
        if (msgId == null || msgId.isEmpty()) {
            return false;
        }
        String id = msgId.toLowerCase();
        return id.contains("radiation") || id.contains("nuclear") || id.contains("irradiat");
    }

    private boolean isRainExposure(Player player) {
        return player.level().isRainingAt(player.blockPosition().above());
    }

    private float aidsInfectionFactor(DiseaseCapability capability) {
        if (capability.aidsStage() <= 0) {
            return 1.0f;
        }
        return 1.0f + capability.aidsStage() * AIDS_INFECTION_BOOST_PER_STAGE;
    }

    private float sepsisPoisonChance(int stage) {
        return switch (stage) {
            case 1 -> SEPSIS_POISON_STAGE1_CHANCE;
            case 2 -> SEPSIS_POISON_STAGE2_CHANCE;
            default -> SEPSIS_POISON_STAGE3_CHANCE;
        };
    }

    private boolean isPlayerActive(Player player) {
        var motion = player.getDeltaMovement();
        double horizontalSpeed = motion.x * motion.x + motion.z * motion.z;
        return horizontalSpeed > 0.0009D
                || player.isSprinting()
                || player.swinging
                || player.isUsingItem();
    }

    private int amplifyByAids(DiseaseCapability capability, String diseaseName, int delta) {
        if (delta <= 0 || "aids".equals(diseaseName)) {
            return delta;
        }
        return Math.max(1, Math.round(delta * aidsInfectionFactor(capability)));
    }

    private void infectImmediately(Player player, String diseaseName, int currentStage, IntSetter stageSetter, IntSetter progressSetter) {
        if (currentStage > 0) {
            progressSetter.set(PROGRESS_MAX);
            return;
        }
        changeStage(player, diseaseName, currentStage, 1, stageSetter, progressSetter, 0);
    }

    private void addProgress(Player player, String diseaseName, int currentStage, int currentProgress, int delta, IntSetter stageSetter, IntSetter progressSetter) {
        if (currentStage <= 0) {
            infectImmediately(player, diseaseName, currentStage, stageSetter, progressSetter);
            return;
        }
        updateProgress(player, diseaseName, currentStage, currentProgress, delta, stageSetter, progressSetter);
    }

    private void updateProgress(Player player, String diseaseName, int currentStage, int currentProgress, int delta, IntSetter stageSetter, IntSetter progressSetter) {
        if (currentStage <= 0) {
            return;
        }

        int nextProgress = Math.max(0, Math.min(PROGRESS_MAX, currentProgress + delta));
        if (delta > 0 && nextProgress >= PROGRESS_MAX && currentStage < 3) {
            changeStage(player, diseaseName, currentStage, currentStage + 1, stageSetter, progressSetter, 0);
            return;
        }

        if (delta < 0 && nextProgress == 0 && currentStage > 0) {
            if (currentStage == 1) {
                changeStage(player, diseaseName, currentStage, 0, stageSetter, progressSetter, 0);
            } else {
                changeStage(player, diseaseName, currentStage, currentStage - 1, stageSetter, progressSetter, 50);
            }
            return;
        }

        progressSetter.set(nextProgress);
    }

    private void reduceStage(Player player, String diseaseName, int currentStage, IntSetter stageSetter, IntSetter progressSetter) {
        if (currentStage <= 0) {
            return;
        }
        int nextStage = Math.max(0, currentStage - 1);
        changeStage(player, diseaseName, currentStage, nextStage, stageSetter, progressSetter, 0);
    }

    private void changeStage(Player player, String diseaseName, int fromStage, int toStage, IntSetter stageSetter, IntSetter progressSetter, int nextProgress) {
        stageSetter.set(toStage);
        progressSetter.set(nextProgress);
        if (fromStage == toStage) {
            return;
        }

        Utils.LOGGER.info("Disease {} for {} changed from stage {} to {}", diseaseName, player.getGameProfile().getName(), fromStage, toStage);
        player.displayClientMessage(Component.translatable("disease.dgh.stage_changed",
                Component.translatable("disease.dgh." + diseaseName),
                Component.translatable("disease.dgh.stage." + toStage)), true);
    }

    @FunctionalInterface
    private interface IntSetter {
        void set(int value);
    }
}