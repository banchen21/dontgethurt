package com.lastimp.dgh.common.tags;

import com.lastimp.dgh.common.utils.ResourceHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.HashSet;
import java.util.Set;

public class ModDamageType {
    public static final TagKey<DamageType> FINAL_HEALTH_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, ResourceHelper.ModResource("final_health_damage"));
    public static final TagKey<DamageType> BULLETS = TagKey.create(Registries.DAMAGE_TYPE, ResourceHelper.ResourceLocation("tacz", "bullets"));

    public static final Set<ResourceKey<DamageType>> DGH_FINAL_DAMAGE = new HashSet<>();

    public static final ResourceKey<DamageType> OPEN_WOUND_DAMAGE = create("open_wound");
    public static final ResourceKey<DamageType> INTERNAL_INJURY_DAMAGE = create("internal_injury");
    public static final ResourceKey<DamageType> BURN_DAMAGE = create("burn");
    public static final ResourceKey<DamageType> BRAIN_DAMAGE = create("brain_damage");
    public static final ResourceKey<DamageType> BLEED_DAMAGE = create("bleed");
    public static final ResourceKey<DamageType> SURGERY_DAMAGE = create("surgery");
    public static final ResourceKey<DamageType> CANT_BREATH_DAMAGE = create("cant_breath");

    public static ResourceKey<DamageType> create(String name) {
        return create(ResourceHelper.ModResource(name));
    }

    public static ResourceKey<DamageType> create(ResourceLocation path) {
        var damageType = ResourceKey.create(Registries.DAMAGE_TYPE, path);
        DGH_FINAL_DAMAGE.add(damageType);
        return damageType;
    }
}
