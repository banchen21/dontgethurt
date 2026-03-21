
package com.lastimp.dgh.common.capability.bodyPart.bodies;

import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.entry.register.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;
import static com.lastimp.dgh.common.enums.BodyComponents.*;

public class Blood extends AbstractBody {
    private static final Collection<ResourceLocation> uniqueConditions = new LinkedHashSet<>();
    private static List<ResourceLocation> BLOOD_CONDITIONS;

    private float bloodLost;
    private boolean oxygenLost;
    private float sepsis;

    public static void addCondition(Collection<ResourceLocation> key) {
        uniqueConditions.addAll(key);
    }

    @Override
    public List<ResourceLocation> getBodyConditions() {
        if (BLOOD_CONDITIONS == null) {
            BLOOD_CONDITIONS = new ArrayList<>(uniqueConditions);
        }
        return BLOOD_CONDITIONS;
    }

    @Override
    public float getVitalityWeight() {
        return 1;
    }

    @Override
    public String getShortID() {
        return "C264AB58-CC16-425E-B12D";
    }

    @Override
    public BodyComponents getBodyType() {
        return BLOOD;
    }

    @Override
    public Component getComponent() {
        return Component.literal("血液");
    }

    @Override
    public void addOriginOrgan(LivingEntity livingEntity, boolean newEntity) {

    }

    @Override
    public AbstractBody update(HealthCapability health, LivingEntity entity) {
        super.update(health, entity);
        this.handleBloodVolume(health);
        this.handleOpiateAddicted(health, entity);
        this.handleWithdraw(health);
        this.handleOxygen(health, entity);
        this.handlePressure(health);
        this.handleSepsis(health);
        this.handleCombatStimulant(health, entity);
        return this;
    }

    @Override
    public float updateVitalityLost(HealthCapability health, LivingEntity entity) {
        float lost = 0;
        if (this.abnormal(OPIATE_OVERDOSE))
            lost += Mth.clamp(this.getConditionValue(OPIATE_OVERDOSE) - 0.5f, 0.0f, 0.5f);
        return lost;
    }

    @Override
    public int slowDownLevel(HealthCapability health) {
        return this.getConditionValue(OPIATE_OVERDOSE) < 0.5f? 0 : 8;
    }

    @Override
    public void healingAll(boolean healPain) {
        this.getBodyConditions().forEach(key -> {
            var condition = ConditionAccessor.get(key);
            this.setConditionValue(key, condition.defaultValue());
            this.setConditionHidden(key, condition.defaultValue());
        });
    }

    private void handleBloodVolume(HealthCapability health) {
        this.bloodLost = 0;
        for (var component : VISIBLE_BODIES) {
            AbstractVisibleBody body = (AbstractVisibleBody) health.getComponent(component);
            if (body.abnormal(SURGERY_INCISION) && !body.abnormal(CLAMPED_BLEEDING))
                this.bloodLost += 0.007f;
            if (body.abnormal(ARTERIAL_BLEEDING) && !body.abnormal(CLAMPED_ARTERIES))
                this.bloodLost += PlatformService.CONFIG.FRACTURE_BLOOD_RATIO();
            if (body instanceof Torso torso && torso.abnormal(AORTIC_RUPTURE))
                this.bloodLost += PlatformService.CONFIG.FRACTURE_BLOOD_RATIO() * 3;
            if (body.abnormal(BLEED))
                this.bloodLost += body.getConditionValue(BLEED) * PlatformService.CONFIG.BLEED_VOLUME_RATIO();
        }
        this.injury(BLOOD_LOSS, this.bloodLost * Utils.DELTA);

        float blood_loss_healing = ConditionAccessor.get(BLOOD_LOSS).healingSpeed();
        blood_loss_healing = Math.max(0, blood_loss_healing - bloodLost * 2);
        this.healing(BLOOD_LOSS, -blood_loss_healing * Utils.DELTA);
    }

    private void handleOxygen(HealthCapability health, LivingEntity entity) {
        this.oxygenLost = false;
        var bloodLoss = this.getConditionValue(BLOOD_LOSS);
        if (bloodLoss > 0.4) {
            if (this.getConditionValue(OXYGEN) < (bloodLoss - 0.4f)) {
                this.setConditionValue(OXYGEN, (bloodLoss - 0.4f) / 0.6f);
                oxygenLost = true;
            }
        }
        var bloodPressure = this.getConditionValue(BLOOD_PRESSURE);
        if (bloodPressure < 0.7) {
            if (this.getConditionValue(OXYGEN) < (0.7 - bloodPressure)) {
                this.injury(OXYGEN, 0.5f * ConditionAccessor.get(OXYGEN).healingSpeed() * Utils.DELTA);
                oxygenLost = true;
            }
        }
        var torso = health.getComponent(TORSO);
        if (torso.abnormal(HEARTRATE_STOP)) {
            this.injury(OXYGEN, ConditionAccessor.get(OXYGEN).healingSpeed() * Utils.DELTA);
            oxygenLost = true;
        }
    }

    private void handleOpiateAddicted(HealthCapability health, LivingEntity entity) {
        if (entity.hasEffect(ModEffects.COMBAT_STIMULANT_EFFECT.get())) {
            this.injury(OPIATE_ADDICTED, Utils.DELTA * 0.0026f);
        }
    }

    private void handleWithdraw(HealthCapability health) {
        if (this.abnormal(OPIATE_ADDICTED)) {
            Head head = (Head) health.getComponent(HEAD);
            if (head.getConditionValue(WITHDRAW) < this.getConditionValue(OPIATE_ADDICTED) && !health.getComponent(TORSO).abnormal(ANALGESIA))
                head.injury(WITHDRAW, this.getConditionValue(OPIATE_ADDICTED) * Utils.DELTA * PlatformService.CONFIG.WITHDRAW_RATIO());
        }
    }

    private void handlePressure(HealthCapability health) {
        var torso = health.getComponent(TORSO);
        if (torso.abnormal(HEARTRATE_IRREGULAR) && this.getConditionValue(BLOOD_PRESSURE) > 1.0 - torso.getConditionValue(HEARTRATE_IRREGULAR)) {
            this.injury(BLOOD_PRESSURE, -0.05f * Utils.DELTA);
        } else if (torso.abnormal(HEARTRATE_STOP)) {
            this.setConditionValue(BLOOD_PRESSURE, ConditionAccessor.get(BLOOD_PRESSURE).minValue());
        } else if (this.abnormal(BLOOD_PRESSURE)) {
            int factor = (this.getConditionValue(BLOOD_PRESSURE) > ConditionAccessor.get(BLOOD_PRESSURE).defaultValue()) ? -1 : 1;
            this.healing(BLOOD_PRESSURE, factor * ConditionAccessor.get(BLOOD_PRESSURE).healingSpeed() * Utils.DELTA);
        }
    }

    private void handleSepsis(HealthCapability health) {
        this.sepsis = 0;
        float infection = 0;
        float gangrene = 0;
        float foreign_object = 0;
        for (var component : BodyComponents.VISIBLE_BODIES) {
            AbstractVisibleBody body = (AbstractVisibleBody) health.getComponent(component);
            infection += body.getConditionValue(INFECTION);
            if (body instanceof AbstractExtremities extremities)
                gangrene += extremities.getConditionValue(GANGRENE);
            foreign_object += body.getConditionValue(FOREIGN_OBJECT);
        }
        this.sepsis += infection * ConditionAccessor.get(SEPSIS).healingSpeed() / 0.5f;
        this.sepsis += gangrene * ConditionAccessor.get(SEPSIS).healingSpeed() / 0.15f;
        this.sepsis += foreign_object * ConditionAccessor.get(SEPSIS).healingSpeed() / 0.15f;
        health.getComponent(BLOOD).injury(SEPSIS, this.sepsis * Utils.DELTA);

        if (this.abnormal(ANTIBIOTICS) && this.getConditionValue(OXYGEN) < 0.3f && this.getConditionValue(BLOOD_PRESSURE) > 0.7f) {
            this.healing(SEPSIS, -ConditionAccessor.get(SEPSIS).healingSpeed() * Utils.DELTA * 2);
        }
    }

    private void handleCombatStimulant(HealthCapability health, LivingEntity entity) {
        if (!entity.hasEffect(ModEffects.COMBAT_STIMULANT_EFFECT.get())) return;
        this.healing(BLOOD_LOSS, -0.02f * Utils.DELTA);
        var head = health.getComponent(HEAD);
        head.healing(WITHDRAW, -0.02f * Utils.DELTA);
    }

    public float bloodLost() {
        return this.bloodLost;
    }

    public boolean oxygenLost() {
        return oxygenLost;
    }

    public float sepsis() {
        return sepsis;
    }
}
