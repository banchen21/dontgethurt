package com.lastimp.dgh.common.entry.register;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.entry.IEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModPotions {
    public static final Map<IEntry<Potion>, String> POTIONS = new HashMap<>();
    public static final IEntry<Potion> COMBAT_STIMULANT_POTION = register(
            "combat_stimulant", () -> new Potion(new MobEffectInstance(ModEffects.COMBAT_STIMULANT_EFFECT.get(), 20 * 75))
    );

    public static final IEntry<Potion> ANALGESIA_POISON_POTION = register(
            "analgesia_poison", () -> new Potion(new MobEffectInstance(ModEffects.ANALGESIA_POISON_EFFECT.get(), 20 * 60))
    );

    private static IEntry<Potion> register(String name, Supplier<Potion> sup) {
        var potion = PlatformService.REGISTRY_HANDLER.registerPotion(name, sup);
        POTIONS.put(potion, name);
        return potion;
    }

    public static void register() {
    }


}
