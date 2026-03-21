package com.lastimp.dgh.common.system.drug;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.system.disease.DiseaseManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * M3 药物规则引擎：统一处理胶囊（延迟/冷却）、药针（污染）、口服液（直效）三类药物逻辑。
 */
public class DrugRuleEngine {
    private static final DiseaseManager DISEASE_MANAGER = new DiseaseManager();

    private static long capsuleCooldownTicks() {
        return Math.max(20, PlatformService.CONFIG.DRUG_CAPSULE_COOLDOWN_TICKS());
    }

    private static long capsuleDelayTicks() {
        return Math.max(20, PlatformService.CONFIG.DRUG_CAPSULE_DELAY_TICKS());
    }

    private static long blockerDurationTicks() {
        return Math.max(20, PlatformService.CONFIG.DRUG_BLOCKER_DURATION_TICKS());
    }

    private static float needleContaminationChance() {
        return Math.max(0f, Math.min(1f, PlatformService.CONFIG.DRUG_NEEDLE_CONTAMINATION_CHANCE()));
    }

    // -------------------------------------------------------------------------
    // 公开 API
    // -------------------------------------------------------------------------

    /**
     * 尝试服用胶囊：检查冷却，排入延迟队列。
     *
     * @return 是否成功入队
     */
    public static boolean tryCapsule(Player player, DiseaseCapability disease, String doseType) {
        long now = player.level().getGameTime();
        long remaining = disease.getNextCapsuleTick() - now;
        if (remaining > 0) {
            long seconds = remaining / 20;
            player.displayClientMessage(
                    Component.translatable("drug.dgh.capsule_cooldown", seconds), true);
            return false;
        }
        disease.setNextCapsuleTick(now + capsuleCooldownTicks());
        long delaySec = Math.max(1, capsuleDelayTicks() / 20);
        disease.addPendingCapsule(doseType, now + capsuleDelayTicks());
        player.displayClientMessage(
            Component.translatable("drug.dgh.capsule_taken", delaySec), true);
        return true;
    }

    /**
     * 使用药针：立即生效，并根据是否已被污染触发感染风险。
     * 使用后药针标记为已污染。
     */
    public static boolean tryNeedle(Player player, DiseaseCapability disease, String doseType, ItemStack stack) {
        boolean contaminated = stack.hasTag() && stack.getTag().getBoolean("dgh_contaminated");
        if (contaminated && player.getRandom().nextFloat() < needleContaminationChance()) {
            DISEASE_MANAGER.triggerDisease(player, "sepsis");
            player.displayClientMessage(
                    Component.translatable("drug.dgh.needle_contaminated"), false);
        }
        // 已污染針具額外触发 AIDS：概率为 contamination_chance / 3
        if (contaminated && player.getRandom().nextFloat() < needleContaminationChance() / 3f) {
            DISEASE_MANAGER.triggerDisease(player, "aids");
        }
        applyDoseEffect(player, disease, doseType);
        stack.getOrCreateTag().putBoolean("dgh_contaminated", true);
        return true;
    }

    /**
     * 使用口服液：立即生效，无冷却/延迟。
     */
    public static boolean tryOralLiquid(Player player, DiseaseCapability disease, String doseType) {
        applyDoseEffect(player, disease, doseType);
        return true;
    }

    /**
     * 处理待生效的胶囊队列（在玩家每 20 tick 调用一次）。
     */
    public static void processPendingDoses(Player player) {
        if (player.level().isClientSide()) return;
        DiseaseCapability.getAndApply(player, disease -> {
            long now = player.level().getGameTime();
            disease.getPendingCapsules().removeIf(dose -> {
                if (now >= dose.readyTick()) {
                    applyDoseEffect(player, disease, dose.doseType());
                    player.displayClientMessage(
                            Component.translatable("drug.dgh.capsule_effect_applied",
                                    Component.translatable("drug.dgh.dose." + dose.doseType())), false);
                    return true;
                }
                return false;
            });
        });
    }

    // -------------------------------------------------------------------------
    // 内部实现
    // -------------------------------------------------------------------------

    private static void applyDoseEffect(Player player, DiseaseCapability disease, String doseType) {
        switch (doseType) {
            // --- 胶囊 ---
            case "dextromethorphan" -> {
                // 右美沙芬：轻症治愈，中症50%降级
                int s = disease.upperRespiratoryInfectionStage();
                if (s == 1) {
                    DISEASE_MANAGER.cureDisease(player, "upper_respiratory_infection");
                } else if (s >= 2 && player.getRandom().nextFloat() < 0.50f) {
                    setUpperRespiratoryToLight(disease);
                }
            }
            case "ibuprofen" -> {
                // 布洛芬：暂时将重型/中型压到轻型，轻型则仅缓解进度。
                if (disease.upperRespiratoryInfectionStage() >= 2) {
                    setUpperRespiratoryToLight(disease);
                } else if (disease.upperRespiratoryInfectionStage() == 1) {
                    disease.setUpperRespiratoryInfectionProgress(
                            Math.max(0, disease.upperRespiratoryInfectionProgress() - 60));
                }
            }
            // --- 药针 ---
            case "ribavirin" -> {
                // 利巴韦林：重/中 → 立即降为轻
                if (disease.upperRespiratoryInfectionStage() >= 2) {
                    setUpperRespiratoryToLight(disease);
                }
            }
            case "blocker" -> {
                // 阻断剂：短时间内不会感染/恶化尸毒
                long now = player.level().getGameTime();
                disease.setUndeadBlockerUntilTick(now + blockerDurationTicks());
            }
            // --- 口服液 ---
            case "sedative" -> {
                // 镇静剂：削减 PTSD 进度；轻症有概率治愈
                int s = disease.ptsdStage();
                if (s > 0) {
                    disease.setPtsdProgress(Math.max(0, disease.ptsdProgress() - 50));
                    if (s == 1 && player.getRandom().nextFloat() < 0.40f) {
                        DISEASE_MANAGER.cureDisease(player, "ptsd");
                    }
                }
            }
            case "lamivudine" -> {
                if (disease.aidsStage() > 0 && player.getRandom().nextFloat() < 0.10f) {
                    DISEASE_MANAGER.cureDisease(player, "aids");
                    player.displayClientMessage(
                            Component.translatable("drug.dgh.lamivudine_cured"), false);
                }
            }
            default -> {
                // 未知剂量类型，忽略
            }
        }
    }

    private static void setUpperRespiratoryToLight(DiseaseCapability disease) {
        disease.setUpperRespiratoryInfectionStage(1);
        disease.setUpperRespiratoryInfectionProgress(0);
    }
}
