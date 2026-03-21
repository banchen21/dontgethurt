package com.lastimp.dgh.common.capability.healthCore.diseaseSystem;

import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.entry.register.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.GANGRENE;
import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.INFECTION;
import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.SEPSIS;
import static com.lastimp.dgh.common.enums.BodyComponents.BLOOD;
import static com.lastimp.dgh.common.enums.BodyComponents.VISIBLE_BODIES;

public class DiseaseState {
    public static final String MED_LAMIVUDINE = "lamivudine_capsule";
    public static final String MED_DEXTROMETHORPHAN = "dextromethorphan";
    public static final String MED_IBUPROFEN = "ibuprofen";
    public static final String MED_ORAL_LIQUID = "oral_liquid";
    public static final String MED_TARGETING_AGENT = "targeting_agent";
    public static final String MED_SEDATIVE = "sedative";
    public static final String MED_BLOCKER = "blocker";
    public static final String MED_RIBAVIRIN = "ribavirin";

    private static final String CONTAMINATED_NEEDLE_KEY = "dgh_contaminated_needle";

    private static final int TICKS_PER_SECOND = 20;
    private static final int DAY_TICKS = 24000;
    private static final int CAPSULE_COOLDOWN_TICKS = 300 * TICKS_PER_SECOND;
    private static final int CAPSULE_DELAY_TICKS = 120 * TICKS_PER_SECOND;
    private static final int URI_STAGE_TICKS = 12 * 60 * TICKS_PER_SECOND;
    private static final int CORPSE_STAGE_TICKS = 16 * 60 * TICKS_PER_SECOND;

    private int upperRespiratoryStage = 0;
    private int upperRespiratoryProgress = 0;
    private int upperRespiratorySuppressedTicks = 0;

    private boolean hiv = false;

    private int corpseToxinStage = 0;
    private int corpseToxinProgress = 0;
    private long corpseToxinBlockUntil = 0;
    private long lastCorpseDecayTick = 0;

    private String ptsdFearType = "";
    private int ptsdFearTicks = 0;

    private long nextCapsuleTick = 0;
    private final List<PendingDose> pendingDoses = new LinkedList<>();

    public void tick(HealthCapability health, LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        long now = entity.level().getGameTime();

        if (ptsdFearTicks > 0) {
            ptsdFearTicks--;
        }
        if (upperRespiratorySuppressedTicks > 0) {
            upperRespiratorySuppressedTicks--;
        }

        processPendingDoses(health, entity, now);
        updateUpperRespiratory(entity);
        updateCorpseToxin(health, entity, now);
        updateSepsisPenalty(health, entity);
    }

    public void onRainAction(HealthCapability health, Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        if (!player.level().isRainingAt(player.blockPosition())) {
            return;
        }

        float chance = 0.02f;
        if (hiv) {
            chance *= 1.8f;
        }
        float bloodSepsis = health.getComponent(BLOOD).getConditionValue(SEPSIS);
        chance *= (1.0f + bloodSepsis * 0.6f);

        if (player.getRandom().nextFloat() < chance && upperRespiratoryStage == 0) {
            upperRespiratoryStage = 1;
            upperRespiratoryProgress = 0;
        }
    }

    public void onInjury(HealthCapability health, LivingEntity target, Entity source) {
        if (target.level().isClientSide()) {
            return;
        }
        long now = target.level().getGameTime();

        if (source instanceof LivingEntity attacker) {
            ptsdFearType = attacker.getType().toString();
            ptsdFearTicks = DAY_TICKS * 2;

            if (target.getMaxHealth() > 0 && target.getHealth() / target.getMaxHealth() < 0.4f) {
                applyPtsdDebuff(target);
            }

            if (attacker.getMobType() == MobType.UNDEAD && now >= corpseToxinBlockUntil) {
                float chance = hiv ? 0.20f : 0.12f;
                if (target.getRandom().nextFloat() < chance && corpseToxinStage == 0) {
                    corpseToxinStage = 1;
                    corpseToxinProgress = 0;
                    lastCorpseDecayTick = now;
                }
            }
        }
    }

    public void onWakeUp() {
        ptsdFearTicks = Math.max(0, ptsdFearTicks - 6000);
    }

    public void onMedicineUsed(HealthCapability health, LivingEntity target, ItemStack stack) {
        if (!isNeedleMedicine(stack)) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTagElement("dgh");
        boolean contaminated = tag.getBoolean(CONTAMINATED_NEEDLE_KEY);
        if (contaminated) {
            var blood = health.getComponent(BLOOD);
            if (target.getRandom().nextFloat() < 0.18f) {
                blood.injury(SEPSIS, 0.15f);
            }
            if (!hiv && target.getRandom().nextFloat() < 0.05f) {
                hiv = true;
            }
        }
        tag.putBoolean(CONTAMINATED_NEEDLE_KEY, true);
    }

    public boolean consumeCapsuleDose(String doseType, LivingEntity entity) {
        long now = entity.level().getGameTime();
        if (now < nextCapsuleTick) {
            return false;
        }
        nextCapsuleTick = now + CAPSULE_COOLDOWN_TICKS;
        pendingDoses.add(new PendingDose(doseType, now + CAPSULE_DELAY_TICKS));
        return true;
    }

    public boolean applyDirectMedicine(String medicineType, LivingEntity entity) {
        boolean changed = switch (medicineType) {
            case MED_ORAL_LIQUID -> applyOralLiquid();
            case MED_TARGETING_AGENT -> applyTargetingAgent();
            case MED_SEDATIVE -> applySedative();
            case MED_BLOCKER -> applyBlocker(entity.level().getGameTime());
            case MED_RIBAVIRIN -> applyRibavirin();
            default -> false;
        };
        return changed;
    }

    private void processPendingDoses(HealthCapability health, LivingEntity entity, long now) {
        Iterator<PendingDose> it = pendingDoses.iterator();
        while (it.hasNext()) {
            PendingDose pending = it.next();
            if (pending.readyTick > now) {
                continue;
            }

            switch (pending.doseType) {
                case MED_LAMIVUDINE -> {
                    if (hiv && entity.getRandom().nextFloat() < 0.10f) {
                        hiv = false;
                    }
                }
                case MED_DEXTROMETHORPHAN -> {
                    if (upperRespiratoryStage == 1) {
                        upperRespiratoryStage = 0;
                        upperRespiratoryProgress = 0;
                    } else if (upperRespiratoryStage == 2 && entity.getRandom().nextFloat() < 0.60f) {
                        upperRespiratoryStage = 1;
                        upperRespiratoryProgress = 0;
                    }
                }
                case MED_IBUPROFEN -> {
                    if (upperRespiratoryStage >= 2) {
                        upperRespiratorySuppressedTicks = Math.max(upperRespiratorySuppressedTicks, 120 * TICKS_PER_SECOND);
                    }
                }
                default -> {
                }
            }
            it.remove();
        }
    }

    private void updateUpperRespiratory(LivingEntity entity) {
        if (upperRespiratoryStage <= 0) {
            return;
        }

        upperRespiratoryProgress++;
        int stageTicks = URI_STAGE_TICKS;
        if (hiv) {
            stageTicks = (int) (stageTicks * 0.75f);
        }

        if (upperRespiratoryProgress >= stageTicks && upperRespiratoryStage < 3) {
            upperRespiratoryStage++;
            upperRespiratoryProgress = 0;
        }

        int effectStage = upperRespiratoryStage;
        if (upperRespiratorySuppressedTicks > 0 && upperRespiratoryStage >= 2) {
            effectStage = 1;
        }

        if (effectStage > 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 59, effectStage - 1));
        }
    }

    private void updateCorpseToxin(HealthCapability health, LivingEntity entity, long now) {
        if (corpseToxinStage <= 0) {
            return;
        }

        corpseToxinProgress++;
        if (corpseToxinProgress >= CORPSE_STAGE_TICKS && corpseToxinStage < 3) {
            corpseToxinStage++;
            corpseToxinProgress = 0;
        }

        if (corpseToxinStage < 2) {
            return;
        }

        if (lastCorpseDecayTick == 0) {
            lastCorpseDecayTick = now;
        }
        if (now - lastCorpseDecayTick < DAY_TICKS) {
            return;
        }
        lastCorpseDecayTick = now;

        for (var component : VISIBLE_BODIES) {
            AbstractBody body = health.getComponent(component);
            body.injury(INFECTION, 0.06f * corpseToxinStage);
            if (body instanceof AbstractExtremities extremities) {
                extremities.injury(GANGRENE, 0.05f * corpseToxinStage);
            }
        }
        health.getComponent(BLOOD).injury(SEPSIS, 0.03f * corpseToxinStage);
        if (corpseToxinStage >= 3) {
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
        }
    }

    private void updateSepsisPenalty(HealthCapability health, LivingEntity entity) {
        float sepsisValue = health.getComponent(BLOOD).getConditionValue(SEPSIS);
        if (sepsisValue <= 0.2f) {
            return;
        }
        if (entity.getDeltaMovement().horizontalDistanceSqr() < 0.0025) {
            return;
        }

        if (entity instanceof Player player) {
            player.causeFoodExhaustion(0.01f * sepsisValue);
        }
        if (entity.getRandom().nextFloat() < 0.0025f * sepsisValue) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
        }
    }

    private void applyPtsdDebuff(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0));
    }

    private boolean applyOralLiquid() {
        if (upperRespiratoryStage <= 0 || upperRespiratoryStage >= 3) {
            return false;
        }
        upperRespiratoryProgress = Math.max(0, upperRespiratoryProgress - 3000);
        return true;
    }

    private boolean applyTargetingAgent() {
        if (corpseToxinStage <= 0) {
            return false;
        }
        corpseToxinProgress = Math.max(0, corpseToxinProgress - 6000);
        return true;
    }

    private boolean applySedative() {
        if (ptsdFearTicks <= 0) {
            return false;
        }
        ptsdFearTicks = Math.max(0, ptsdFearTicks - 12000);
        return true;
    }

    private boolean applyBlocker(long now) {
        long newExpire = now + 90L * TICKS_PER_SECOND;
        boolean changed = newExpire > corpseToxinBlockUntil;
        corpseToxinBlockUntil = Math.max(corpseToxinBlockUntil, newExpire);
        return changed;
    }

    private boolean applyRibavirin() {
        if (upperRespiratoryStage < 2) {
            return false;
        }
        upperRespiratoryStage = 1;
        upperRespiratoryProgress = 0;
        return true;
    }

    private static boolean isNeedleMedicine(ItemStack stack) {
        return stack.is(ModItems.MORPHINE.get())
                || stack.is(ModItems.FENTANYL.get())
                || stack.is(ModItems.NALOXONE.get())
                || stack.is(ModItems.ADRENALINE.get())
                || stack.is(ModItems.MANNITOL.get())
                || stack.is(ModItems.HYPERZINE.get())
                || stack.is(ModItems.HARDENER.get())
                || stack.is(ModItems.ANTIBIOTICS.get());
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("upperRespiratoryStage", upperRespiratoryStage);
        tag.putInt("upperRespiratoryProgress", upperRespiratoryProgress);
        tag.putInt("upperRespiratorySuppressedTicks", upperRespiratorySuppressedTicks);

        tag.putBoolean("hiv", hiv);

        tag.putInt("corpseToxinStage", corpseToxinStage);
        tag.putInt("corpseToxinProgress", corpseToxinProgress);
        tag.putLong("corpseToxinBlockUntil", corpseToxinBlockUntil);
        tag.putLong("lastCorpseDecayTick", lastCorpseDecayTick);

        tag.putString("ptsdFearType", ptsdFearType);
        tag.putInt("ptsdFearTicks", ptsdFearTicks);

        tag.putLong("nextCapsuleTick", nextCapsuleTick);

        ListTag pendingTag = new ListTag();
        for (PendingDose pendingDose : pendingDoses) {
            CompoundTag item = new CompoundTag();
            item.putString("doseType", pendingDose.doseType);
            item.putLong("readyTick", pendingDose.readyTick);
            pendingTag.add(item);
        }
        tag.put("pendingDoses", pendingTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        upperRespiratoryStage = tag.getInt("upperRespiratoryStage");
        upperRespiratoryStage = Mth.clamp(upperRespiratoryStage, 0, 3);
        upperRespiratoryProgress = Math.max(0, tag.getInt("upperRespiratoryProgress"));
        upperRespiratorySuppressedTicks = Math.max(0, tag.getInt("upperRespiratorySuppressedTicks"));

        hiv = tag.getBoolean("hiv");

        corpseToxinStage = Mth.clamp(tag.getInt("corpseToxinStage"), 0, 3);
        corpseToxinProgress = Math.max(0, tag.getInt("corpseToxinProgress"));
        corpseToxinBlockUntil = tag.getLong("corpseToxinBlockUntil");
        lastCorpseDecayTick = tag.getLong("lastCorpseDecayTick");

        ptsdFearType = tag.getString("ptsdFearType");
        ptsdFearTicks = Math.max(0, tag.getInt("ptsdFearTicks"));

        nextCapsuleTick = tag.getLong("nextCapsuleTick");

        pendingDoses.clear();
        ListTag pendingTag = tag.getList("pendingDoses", ListTag.TAG_COMPOUND);
        for (int i = 0; i < pendingTag.size(); i++) {
            CompoundTag item = pendingTag.getCompound(i);
            pendingDoses.add(new PendingDose(item.getString("doseType"), item.getLong("readyTick")));
        }
    }

    public int upperRespiratoryStage() {
        return upperRespiratoryStage;
    }

    public boolean hiv() {
        return hiv;
    }

    public int corpseToxinStage() {
        return corpseToxinStage;
    }

    public int ptsdFearTicks() {
        return ptsdFearTicks;
    }

    private record PendingDose(String doseType, long readyTick) {
    }
}
