package com.lastimp.dgh.common.capability.healthCore.damageSystem;

import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.enums.InjuryPart;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;

public record InjuryDataSet(
        InjuryPart targetPart, InjuryData<? extends AbstractBody>[] injuryData
) {
    private static final EquipmentSlot[] ALL = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final EquipmentSlot[] LEG = {EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final EquipmentSlot[] BODY = {EquipmentSlot.CHEST};
    private static final EquipmentSlot[] HEAD = {EquipmentSlot.HEAD};

    public void handle(LivingEntity entity, DamageSource source, HealthCapability health, float totalDamage) {
        Arrays.stream(this.injuryData).forEach(data -> data.handle(entity, source, health, totalDamage));
    }

    public static <T extends AbstractBody> InjuryData<T> createData(BodyComponents component, InjuryHandler<T> handler) {
        return createData(component, 1, handler);
    }

    public static <T extends AbstractBody> InjuryData<T> createData(BodyComponents component, float factor, InjuryHandler<T> handler) {
        return new InjuryData<>(component, factor, handler);
    }

    @SafeVarargs
    public static InjuryDataSet create(InjuryPart injuryPart, final InjuryData<? extends AbstractBody> ...data) {
        return new InjuryDataSet(injuryPart, data);
    }

    public static EquipmentSlot[] componentToSlot(BodyComponents component) {
        switch (component) {
            case LEFT_LEG, RIGHT_LEG -> {
                return LEG;
            }
            case TORSO, RIGHT_ARM, LEFT_ARM -> {
                return BODY;
            }
            case HEAD -> {
                return HEAD;
            }
            default -> {
                return ALL;
            }
        }
    }

    public static InjuryPart componentToPart(BodyComponents component) {
        switch (component) {
            case LEFT_LEG, RIGHT_LEG -> {
                return InjuryPart.FEET;
            }
            case TORSO, RIGHT_ARM, LEFT_ARM -> {
                return InjuryPart.BODY;
            }
            case HEAD -> {
                return InjuryPart.HEAD;
            }
            default -> {
                return InjuryPart.DEFAULT;
            }
        }
    }

    public static class InjuryData<T extends AbstractBody> {
        private final BodyComponents component;
        private float factor;
        private final InjuryDataSet.InjuryHandler<T> handler;

        public InjuryData(BodyComponents component, float factor, InjuryHandler<T> handler) {
            this.component = component;
            this.factor = factor;
            this.handler = handler;
        }

        @SuppressWarnings("unchecked")
        public void handle(LivingEntity entity, DamageSource source, HealthCapability health, float totalDamage) {
            this.handler.handle(source, entity, health, (T) health.getComponent(this.component), factor * totalDamage);
        }

        public BodyComponents component() {
            return component;
        }

        public float factor() {
            return factor;
        }

        public void setFactor(float factor) {
            this.factor = factor;
        }

        public InjuryHandler<T> handler() {
            return handler;
        }
    }

    public interface InjuryHandler<T extends AbstractBody> {
        void handle(DamageSource source, LivingEntity entity, HealthCapability health, T body, float damageAmount);
    }
}
