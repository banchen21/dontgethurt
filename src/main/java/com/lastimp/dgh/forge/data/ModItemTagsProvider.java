package com.lastimp.dgh.forge.data;

import com.lastimp.dgh.common.tags.ModTags;
import com.lastimp.dgh.common.entry.register.ModItems;
import com.lastimp.dgh.common.utils.ResourceHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends TagsProvider<Item> {
    public static final ResourceKey<Item> SHEARS = ResourceKey.create(Registries.ITEM, ResourceHelper.ResourceLocation("minecraft", "shears"));
    public static final ResourceKey<Item> WRITABLE_BOOK = ResourceKey.create(Registries.ITEM, ResourceHelper.ResourceLocation("minecraft", "writable_book"));
    public static final ResourceKey<Item> POTIONS = ResourceKey.create(Registries.ITEM, ResourceHelper.ResourceLocation("minecraft", "potion"));

    protected ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ITEM, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.MEDICINE)
                .add(ModItems.BANDAGE.getKey())
                .add(ModItems.BLOOD_PACK.getKey())
                .add(ModItems.BLOOD_PACK_EMPTY.getKey())
                .add(ModItems.GYPSUM.getKey())
                .add(ModItems.MORPHINE.getKey())
                .add(ModItems.SUTURE.getKey())
                .add(ModItems.NALOXONE.getKey())
                .add(ModItems.TOURNIQUET.getKey())
                .add(ModItems.NEEDLE.getKey())
                .add(ModItems.ADRENALINE.getKey())
                .add(ModItems.ANTISEPTIC.getKey())
                .add(ModItems.ANTIBIOTICS.getKey())
                .add(ModItems.ANTIBIOTIC_OINTMENT.getKey())
                .add(ModItems.PLASTIC_SKIN.getKey())
                .add(ModItems.ANTIBIOTIC_GLUE.getKey())
                .add(ModItems.MANNITOL.getKey())
                .add(ModItems.HERB_BANDAGE.getKey())
                .add(ModItems.CLAMP.getKey())
                .add(ModItems.FOOD_CONSUMER.getKey())
                .add(ModItems.FENTANYL.getKey())
                .add(ModItems.HYPERZINE.getKey())
                .add(ModItems.HARDENER.getKey())
                .add(ModItems.LAMIVUDINE_CAPSULE.getKey())
                .add(ModItems.DEXTROMETHORPHAN.getKey())
                .add(ModItems.IBUPROFEN.getKey())
                .add(ModItems.ORAL_LIQUID.getKey())
                .add(ModItems.TARGETING_AGENT.getKey())
                .add(ModItems.SEDATIVE.getKey())
                .add(ModItems.BLOCKER.getKey())
                .add(ModItems.RIBAVIRIN.getKey());

        this.tag(ModTags.MEDICINE_DIRECT)
                .add(POTIONS)
                .add(ModItems.BLOOD_PACK.getKey())
                .add(ModItems.BLOOD_PACK_EMPTY.getKey())
                .add(ModItems.MORPHINE.getKey())
                .add(ModItems.NALOXONE.getKey())
                .add(ModItems.ADRENALINE.getKey())
                .add(ModItems.ANTIBIOTICS.getKey())
                .add(ModItems.MANNITOL.getKey())
                .add(ModItems.FOOD_CONSUMER.getKey())
                .add(ModItems.FENTANYL.getKey())
                .add(ModItems.HYPERZINE.getKey())
                .add(ModItems.HARDENER.getKey())
                .add(ModItems.LAMIVUDINE_CAPSULE.getKey())
                .add(ModItems.DEXTROMETHORPHAN.getKey())
                .add(ModItems.IBUPROFEN.getKey())
                .add(ModItems.ORAL_LIQUID.getKey())
                .add(ModItems.TARGETING_AGENT.getKey())
                .add(ModItems.SEDATIVE.getKey())
                .add(ModItems.BLOCKER.getKey())
                .add(ModItems.RIBAVIRIN.getKey());

        this.tag(ModTags.MEDICAL_TOOLS)
                .add(SHEARS)
                .add(WRITABLE_BOOK)
                .add(ModItems.HEALTH_SCANNER.getKey())
                .add(ModItems.BLOOD_SCANNER.getKey())
                .add(ModItems.WOOD_WRENCH.getKey())
                .add(ModItems.HEALTH_CARE_BAG.getKey())
                .add(ModItems.SURGERY_TOOL_BAG.getKey())
                .add(ModItems.LIMB_REF_BEG.getKey())
                .add(ModItems.SCALPEL.getKey())
                .add(ModItems.HEMOSTAT.getKey())
                .add(ModItems.RETRACTOR.getKey())
                .add(ModItems.SURGICAL_DRILL.getKey())
                .add(ModItems.TWEEZER.getKey())
                .add(ModItems.BONE_IMPLANTS.getKey())
                .add(ModItems.BONE_IMPLANTS_WOOD.getKey())
                .add(ModItems.BONE_IMPLANTS_STONE.getKey())
                .add(ModItems.BONE_IMPLANTS_COPPER.getKey())
                .add(ModItems.BONE_IMPLANTS_IRON.getKey())
                .add(ModItems.BONE_IMPLANTS_GOLD.getKey())
                .add(ModItems.BONE_IMPLANTS_DIMOND.getKey())
                .add(ModItems.BONE_IMPLANTS_NETHERITE.getKey())
                .add(ModItems.SURGERY_SAW.getKey())
                .add(ModItems.BONE_NATURAL.getKey())
                .add(ModItems.BONE_WOOD.getKey())
                .add(ModItems.BONE_STONE.getKey())
                .add(ModItems.BONE_COPPER.getKey())
                .add(ModItems.BONE_IRON.getKey())
                .add(ModItems.BONE_GOLD.getKey())
                .add(ModItems.BONE_DIMOND.getKey())
                .add(ModItems.BONE_NETHERITE.getKey())
                .add(ModItems.MEDICAL_STENT.getKey())
                .add(ModItems.DRAINAGE.getKey())
                .add(ModItems.OXYGEN_MASK.getKey())
                .add(ModItems.ANTISEPTIC_SPRAYER.getKey())
                .add(ModItems.AUTOPULSE.getKey())
                .add(ModItems.HUMAN_HAND.getKey())
                .add(ModItems.HUMAN_LEG.getKey())
                .add(ModItems.STASIS_BAG.getKey())
                .add(ModItems.AUTO_USE_BAG.getKey())
                .add(ModItems.MEDICINE_BAG.getKey())
                .add(ModItems.AED.getKey());

        this.tag(ModTags.MEDICAL_TOOLS_BASIC)
                .add(SHEARS)
                .add(ModItems.HEALTH_SCANNER.getKey())
                .add(ModItems.BLOOD_SCANNER.getKey())
                .add(ModItems.WOOD_WRENCH.getKey())
                .add(ModItems.OXYGEN_MASK.getKey())
                .add(ModItems.ANTISEPTIC_SPRAYER.getKey())
                .add(ModItems.AUTOPULSE.getKey())
                .add(ModItems.STASIS_BAG.getKey())
                .add(ModItems.AUTO_USE_BAG.getKey())
                .add(ModItems.AED.getKey());

        this.tag(ModTags.MEDICAL_TOOLS_SURGERY)
                .add(ModItems.SCALPEL.getKey())
                .add(ModItems.HEMOSTAT.getKey())
                .add(ModItems.RETRACTOR.getKey())
                .add(ModItems.SURGICAL_DRILL.getKey())
                .add(ModItems.TWEEZER.getKey())
                .add(ModItems.BONE_IMPLANTS.getKey())
                .add(ModItems.BONE_IMPLANTS_WOOD.getKey())
                .add(ModItems.BONE_IMPLANTS_STONE.getKey())
                .add(ModItems.BONE_IMPLANTS_COPPER.getKey())
                .add(ModItems.BONE_IMPLANTS_IRON.getKey())
                .add(ModItems.BONE_IMPLANTS_GOLD.getKey())
                .add(ModItems.BONE_IMPLANTS_DIMOND.getKey())
                .add(ModItems.BONE_IMPLANTS_NETHERITE.getKey())
                .add(ModItems.SURGERY_SAW.getKey())
                .add(ModItems.BONE_NATURAL.getKey())
                .add(ModItems.BONE_WOOD.getKey())
                .add(ModItems.BONE_STONE.getKey())
                .add(ModItems.BONE_COPPER.getKey())
                .add(ModItems.BONE_IRON.getKey())
                .add(ModItems.BONE_GOLD.getKey())
                .add(ModItems.BONE_DIMOND.getKey())
                .add(ModItems.BONE_NETHERITE.getKey())
                .add(ModItems.MEDICAL_STENT.getKey())
                .add(ModItems.DRAINAGE.getKey())
                .add(ModItems.OXYGEN_MASK.getKey())
                .add(ModItems.SUTURE.getKey())
                .add(ModItems.AUTO_USE_BAG.getKey())
                .add(ModItems.AED.getKey());

        this.tag(ModTags.MEDICAL_TOOLS_SHEARS)
                .add(SHEARS)
                .add(ModItems.SCALPEL.getKey());

        this.tag(ModTags.MEDICAL_LIMBS)
                .add(ModItems.HUMAN_HAND.getKey())
                .add(ModItems.HUMAN_LEG.getKey())
                .add(ModItems.BRAIN.getKey())
                .add(ModItems.EYE.getKey())
                .add(ModItems.SPINAL_CORD.getKey())
                .add(ModItems.HEART.getKey())
                .add(ModItems.KIDNEY.getKey())
                .add(ModItems.LIVER.getKey())
                .add(ModItems.LUNGS.getKey())
                .add(ModItems.STOMACH.getKey())
                .add(ModItems.MUSCLE.getKey())
                .add(ModItems.NEURO.getKey())
                .add(ModItems.SKIN.getKey());

        this.tag(ModTags.ORGAN)
                .add(ModItems.BRAIN.getKey())
                .add(ModItems.EYE.getKey())
                .add(ModItems.SPINAL_CORD.getKey())
                .add(ModItems.HEART.getKey())
                .add(ModItems.KIDNEY.getKey())
                .add(ModItems.LIVER.getKey())
                .add(ModItems.LUNGS.getKey())
                .add(ModItems.STOMACH.getKey())
                .add(ModItems.MUSCLE.getKey())
                .add(ModItems.NEURO.getKey())
                .add(ModItems.SKIN.getKey());

        this.tag(ModTags.ORGAN_HEAD)
                .add(ModItems.BRAIN.getKey())
                .add(ModItems.EYE.getKey())
                .add(ModItems.SPINAL_CORD.getKey());

        this.tag(ModTags.ORGAN_TORSO)
                .add(ModItems.HEART.getKey())
                .add(ModItems.KIDNEY.getKey())
                .add(ModItems.LIVER.getKey())
                .add(ModItems.LUNGS.getKey())
                .add(ModItems.STOMACH.getKey());

        this.tag(ModTags.ORGAN_LEG)
                .add(ModItems.MUSCLE.getKey())
                .add(ModItems.NEURO.getKey())
                .add(ModItems.SKIN.getKey());

        this.tag(ModTags.ORGAN_ARM)
                .add(ModItems.MUSCLE.getKey())
                .add(ModItems.NEURO.getKey())
                .add(ModItems.SKIN.getKey());

        this.tag(ModTags.BRAIN)
                .add(ModItems.BRAIN.getKey());
        this.tag(ModTags.EYE)
                .add(ModItems.EYE.getKey());
        this.tag(ModTags.HEART)
                .add(ModItems.HEART.getKey());
        this.tag(ModTags.KIDNEY)
                .add(ModItems.KIDNEY.getKey());
        this.tag(ModTags.LIVER)
                .add(ModItems.LIVER.getKey());
        this.tag(ModTags.LUNGS)
                .add(ModItems.LUNGS.getKey());
        this.tag(ModTags.MUSCLE)
                .add(ModItems.MUSCLE.getKey());
        this.tag(ModTags.NEURO)
                .add(ModItems.NEURO.getKey());
        this.tag(ModTags.SKIN)
                .add(ModItems.SKIN.getKey());
        this.tag(ModTags.STOMACH)
                .add(ModItems.STOMACH.getKey());
        this.tag(ModTags.SPINAL_CORD)
                .add(ModItems.SPINAL_CORD.getKey());

        this.tag(ModTags.MEDICAL_TOOLS_SMALL_BAGS)
                .add(ModItems.HEALTH_CARE_BAG.getKey())
                .add(ModItems.SURGERY_TOOL_BAG.getKey())
                .add(ModItems.LIMB_REF_BEG.getKey())
                .add(ModItems.AUTO_USE_BAG.getKey())
                .add(ModItems.MEDICINE_BAG.getKey());

        this.tag(ModTags.MEDICAL_USAGE_BAGS)
                .add(ModItems.AUTO_USE_BAG.getKey());

        this.tag(ItemTags.BEDS)
                .add(ModItems.OPERATING_BED_BLOCK_ITEM.getKey());

        this.tag(Tags.Items.STRING)
                .add(ModItems.GRASS_STRING.getKey());

        this.tag(ModTags.OXYGEN_SUPPLIERS)
                .add(ModItems.OXYGEN_MASK.getKey());
        this.tag(ModTags.AUTOPULSE)
                .add(ModItems.AUTOPULSE.getKey())
                .add(ModItems.STASIS_BAG.getKey());

        //tfc
        this.tag(ModTags.MEDICAL_TOOLS)
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bismuth_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/copper"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/wrought_iron"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/blue_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/red_steel"));

        this.tag(ModTags.MEDICAL_TOOLS_BASIC)
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bismuth_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/copper"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/wrought_iron"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/blue_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/red_steel"));

        this.tag(ModTags.MEDICAL_TOOLS_SHEARS)
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bismuth_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/bronze"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/copper"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/wrought_iron"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/black_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/blue_steel"))
                .addOptional(ResourceHelper.ResourceLocation("tfc", "metal/shears/red_steel"));
    }
}
