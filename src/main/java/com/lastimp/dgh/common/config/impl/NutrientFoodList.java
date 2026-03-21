package com.lastimp.dgh.common.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lastimp.dgh.common.config.IConfigLoader;
import com.lastimp.dgh.common.config.record.NutrientFoodRecord;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NutrientFoodList implements IConfigLoader<Map.Entry<ResourceLocation, NutrientFoodRecord>> {
    private static final Map<ResourceLocation, NutrientFoodRecord> NUTRIENT_IN_FOOD = new HashMap<>();

    public static NutrientFoodRecord get(ResourceLocation itemId) {
        return NUTRIENT_IN_FOOD.get(itemId);
    }

    @Override
    public String getID() {
        return "nutrient_food_list_1.0";
    }

    @Override
    public int listType() {
        return ListTag.TAG_COMPOUND;
    }

    @Override
    public Set<Map.Entry<ResourceLocation, NutrientFoodRecord>> get() {
        return NUTRIENT_IN_FOOD.entrySet();
    }

    @Override
    public Tag save(Map.Entry<ResourceLocation, NutrientFoodRecord> data) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", data.getKey().toString());
        tag.put("value", NutrientFoodRecord.serializeNBT(data.getValue()));
        return tag;
    }

    @Override
    public void read(Tag tag) {
        CompoundTag item = (CompoundTag) tag;
        ResourceLocation id = ResourceLocation.tryParse(item.getString("id"));
        if (id != null) {
            NUTRIENT_IN_FOOD.put(id, NutrientFoodRecord.deserializeNBT(item.getCompound("value")));
        }
    }

    @Override
    public void load(JsonElement element) {
        JsonObject json = (JsonObject) element;
        ResourceLocation id = ResourceLocation.tryParse(json.get("id").getAsString());
        if (id == null) {
            return;
        }

        float carbohydrate = IConfigLoader.readOrWriteDefault(json, "CARBOHYDRATE", 0.0f);
        float fat = IConfigLoader.readOrWriteDefault(json, "FAT", 0.0f);
        float protein = IConfigLoader.readOrWriteDefault(json, "PROTEIN", 0.0f);
        float vitamins = IConfigLoader.readOrWriteDefault(json, "VITAMINS", 0.0f);
        float minerals = IConfigLoader.readOrWriteDefault(json, "MINERALS", 0.0f);
        float dietaryFiber = IConfigLoader.readOrWriteDefault(json, "DIETARY_FIBER", 0.0f);
        float hydration = IConfigLoader.readOrWriteDefault(json, "HYDRATION", 0.0f);

        NUTRIENT_IN_FOOD.put(id, new NutrientFoodRecord(carbohydrate, fat, protein, vitamins, minerals, dietaryFiber, hydration));
    }

    @Override
    public String example() {
        return "[\n" +
                "  {\n" +
                "    \"id\": \"minecraft:apple\",\n" +
                "    \"CARBOHYDRATE\": 0.35,\n" +
                "    \"FAT\": 0.05,\n" +
                "    \"PROTEIN\": 0.05,\n" +
                "    \"VITAMINS\": 0.40,\n" +
                "    \"MINERALS\": 0.20,\n" +
                "    \"DIETARY_FIBER\": 0.35,\n" +
                "    \"HYDRATION\": 0.20\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"minecraft:cooked_beef\",\n" +
                "    \"CARBOHYDRATE\": 0.05,\n" +
                "    \"FAT\": 0.45,\n" +
                "    \"PROTEIN\": 0.55,\n" +
                "    \"VITAMINS\": 0.10,\n" +
                "    \"MINERALS\": 0.20,\n" +
                "    \"DIETARY_FIBER\": 0.0,\n" +
                "    \"HYDRATION\": 0.05\n" +
                "  }\n" +
                "]";
    }
}
