package com.lastimp.dgh.common.config.record;

import net.minecraft.nbt.CompoundTag;

public record NutrientFoodRecord(
        float CARBOHYDRATE,
        float FAT,
        float PROTEIN,
        float VITAMINS,
        float MINERALS,
        float DIETARY_FIBER,
        float HYDRATION
) {
    public static CompoundTag serializeNBT(NutrientFoodRecord record) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("carbohydrate", record.CARBOHYDRATE);
        tag.putFloat("fat", record.FAT);
        tag.putFloat("protein", record.PROTEIN);
        tag.putFloat("vitamins", record.VITAMINS);
        tag.putFloat("minerals", record.MINERALS);
        tag.putFloat("dietary_fiber", record.DIETARY_FIBER);
        tag.putFloat("hydration", record.HYDRATION);
        return tag;
    }

    public static NutrientFoodRecord deserializeNBT(CompoundTag nbt) {
        return new NutrientFoodRecord(
                nbt.getFloat("carbohydrate"),
                nbt.getFloat("fat"),
                nbt.getFloat("protein"),
                nbt.getFloat("vitamins"),
                nbt.getFloat("minerals"),
                nbt.getFloat("dietary_fiber"),
                nbt.getFloat("hydration")
        );
    }
}
