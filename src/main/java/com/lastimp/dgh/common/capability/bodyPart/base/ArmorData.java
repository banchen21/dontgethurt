package com.lastimp.dgh.common.capability.bodyPart.base;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.capability.healthCore.damageSystem.InjuryDataSet;
import com.lastimp.dgh.common.config.record.ArmorListRecord;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.enums.InjuryPart;
import com.lastimp.dgh.common.utils.Serializable;
import com.lastimp.dgh.common.utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ArmorData implements Serializable {
    private static final ArmorListRecord DEFAULT_RECORD = new ArmorListRecord(0,0,0,0,0,0);
    //临时数据
    private boolean dirty = true;
    private final EnumMap<InjuryPart, ArmorListRecord> bodyData = new EnumMap<>(InjuryPart.class);
    //持久数据
    private final EnumMap<InjuryPart, Float> coolDown = new EnumMap<>(InjuryPart.class);
    private final EnumMap<InjuryPart, Map<ResourceLocation, Float>> resists = new EnumMap<>(InjuryPart.class);

    public ArmorData() {
        this.buildFor(InjuryPart.HEAD);
        this.buildFor(InjuryPart.BODY);
        this.buildFor(InjuryPart.FEET);
    }

    private void buildFor(InjuryPart part) {
        Map<ResourceLocation, Float> map = new HashMap<>();
        ConditionAccessor.resistConditions.forEach(res -> map.put(res, 0f));
        this.resists.put(part, map);
        this.coolDown.put(part, 0f);
    }

    public void update(LivingEntity entity) {
        if (this.dirty) {
            bodyData.put(InjuryPart.HEAD, ArmorListRecord.combine(entity, EquipmentSlot.HEAD));
            bodyData.put(InjuryPart.BODY, ArmorListRecord.combine(entity, EquipmentSlot.CHEST));
            bodyData.put(InjuryPart.FEET, ArmorListRecord.combine(entity, EquipmentSlot.LEGS, EquipmentSlot.FEET));
            bodyData.put(InjuryPart.DEFAULT, DEFAULT_RECORD);
            this.dirty = false;
        }

        this.coolDown.forEach((key, vlue) -> this.coolDown.put(key, Math.max(0, vlue - Utils.DELTA)));
        this.resists.forEach((key, resist) -> {
            if (this.coolDown.get(key) > 0) return;
            var partRes = this.resists.get(key);
            var data = this.bodyData.get(key);
            partRes.forEach((res, value) -> {
                value += (PlatformService.CONFIG.BLOCK_RECOVER_SPEED() + (data.locToTough(res) / 100)) * data.locToRes(res) / 100 * Utils.DELTA;
                partRes.put(res, Math.min(value, data.locToRes(res) / 100));
            });
        });
    }

    public float hurt(BodyComponents component, ResourceLocation resistType, float amount) {
        InjuryPart injuryPart = InjuryDataSet.componentToPart(component);
        this.coolDown.put(injuryPart, PlatformService.CONFIG.BLOCK_RECOVER_DELAY());
        var resist = this.getResist(component, resistType);
        var block = Math.min(resist, amount);
        this.resists.get(injuryPart).put(resistType, resist - block);
        return amount - block;
    }

    public float getResist(BodyComponents component, ResourceLocation resistType) {
        InjuryPart injuryPart = InjuryDataSet.componentToPart(component);
        return this.resists.get(injuryPart).get(resistType);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        this.resists.forEach((key, resistValues) -> this.serialize(tag, key, resistValues));
        return tag;
    }

    private void serialize(CompoundTag tag, InjuryPart key, Map<ResourceLocation, Float> resistValues) {
        CompoundTag partTag = new CompoundTag();
        partTag.putFloat("coolDown", this.coolDown.get(key));
        resistValues.forEach((resistName, value) -> partTag.putFloat(resistName.toString(), value));
        tag.put(key.name(), partTag);
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.resists.keySet().forEach(key -> this.deserialize(nbt, key, this.resists.get(key)));
    }

    private void deserialize(CompoundTag nbt, InjuryPart key,Map<ResourceLocation, Float> resistValues) {
        CompoundTag partTag = nbt.getCompound(key.name());
        this.coolDown.put(key, partTag.getFloat("coolDown"));
        resistValues.keySet().forEach(resistName -> resistValues.put(resistName, partTag.getFloat(resistName.toString())));
    }

    public void setDirty() {
        this.dirty = true;
    }
}
