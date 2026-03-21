package com.lastimp.dgh.forge.data;

import com.lastimp.dgh.common.tags.ModDamageType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var finalHealthTag = this.tag(ModDamageType.FINAL_HEALTH_DAMAGE);
        var bypassArmorTag = this.tag(DamageTypeTags.BYPASSES_ARMOR);
        var bypassShieldTag = this.tag(DamageTypeTags.BYPASSES_SHIELD);
        var bypassInvulnerabilityTag = this.tag(DamageTypeTags.BYPASSES_INVULNERABILITY);
        var bypassCooldownTag = this.tag(DamageTypeTags.BYPASSES_COOLDOWN);
        var bypassEffectsTag = this.tag(DamageTypeTags.BYPASSES_EFFECTS);
        var bypassResistanceTag = this.tag(DamageTypeTags.BYPASSES_RESISTANCE);
        var bypassEnchantmentsTag = this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS);

        for (ResourceKey<DamageType> damage : ModDamageType.DGH_FINAL_DAMAGE) {
            finalHealthTag.add(damage);
            bypassArmorTag.add(damage);
            bypassShieldTag.add(damage);
            bypassInvulnerabilityTag.add(damage);
            bypassCooldownTag.add(damage);
            bypassEffectsTag.add(damage);
            bypassResistanceTag.add(damage);
            bypassEnchantmentsTag.add(damage);
        }
    }
}
