package com.lastimp.dgh.common.capability;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.utils.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class NutrientCapability implements Serializable {
    private static final float MIN_VALUE = 0.0f;
    private static final float MAX_VALUE = 1.0f;

    private float carbohydrate = 0.5f;
    private float fat = 0.5f;
    private float protein = 0.5f;
    private float vitamins = 0.5f;
    private float minerals = 0.5f;
    private float dietaryFiber = 0.5f;
    private float hydration = 0.5f;

    public static boolean has(Entity entity) {
        return PlatformService.CAPABILITY_HELPER.hasNutrient(entity);
    }

    private static Optional<NutrientCapability> get(Player player) {
        return PlatformService.CAPABILITY_HELPER.getNutrient(player);
    }

    public static <T> T getAndApply(Player player, Function<NutrientCapability, T> function, T orElse) {
        return get(player).map(function::apply).orElse(orElse);
    }

    public static void getAndApply(Player player, Consumer<NutrientCapability> function) {
        get(player).ifPresent(function::accept);
    }

    private static float clamp(float value) {
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
    }

    public void addCarbohydrate(float delta) {
        carbohydrate = clamp(carbohydrate + delta);
    }

    public void addFat(float delta) {
        fat = clamp(fat + delta);
    }

    public void addProtein(float delta) {
        protein = clamp(protein + delta);
    }

    public void addVitamins(float delta) {
        vitamins = clamp(vitamins + delta);
    }

    public void addMinerals(float delta) {
        minerals = clamp(minerals + delta);
    }

    public void addDietaryFiber(float delta) {
        dietaryFiber = clamp(dietaryFiber + delta);
    }

    public void addHydration(float delta) {
        hydration = clamp(hydration + delta);
    }

    public float carbohydrate() {
        return carbohydrate;
    }

    public float fat() {
        return fat;
    }

    public float protein() {
        return protein;
    }

    public float vitamins() {
        return vitamins;
    }

    public float minerals() {
        return minerals;
    }

    public float dietaryFiber() {
        return dietaryFiber;
    }

    public float hydration() {
        return hydration;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("carbohydrate", carbohydrate);
        tag.putFloat("fat", fat);
        tag.putFloat("protein", protein);
        tag.putFloat("vitamins", vitamins);
        tag.putFloat("minerals", minerals);
        tag.putFloat("dietary_fiber", dietaryFiber);
        tag.putFloat("hydration", hydration);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        carbohydrate = clamp(nbt.getFloat("carbohydrate"));
        fat = clamp(nbt.getFloat("fat"));
        protein = clamp(nbt.getFloat("protein"));
        vitamins = clamp(nbt.getFloat("vitamins"));
        minerals = clamp(nbt.getFloat("minerals"));
        dietaryFiber = clamp(nbt.getFloat("dietary_fiber"));
        hydration = clamp(nbt.getFloat("hydration"));
    }
}
