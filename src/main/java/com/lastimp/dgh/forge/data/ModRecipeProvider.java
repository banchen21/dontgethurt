
package com.lastimp.dgh.forge.data;

import com.lastimp.dgh.common.entry.register.ModItems;
import com.lastimp.dgh.common.entry.register.ModPotions;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.lastimp.dgh.common.utils.Utils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Utils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BANDAGE.get(), 8)
                .pattern("aaa")
                .define('a', ItemTags.WOOL)
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOD_PACK_EMPTY.get())
                .pattern(" a ")
                .pattern("b b")
                .pattern(" b ")
                .define('a', ItemTags.WOOL)
                .define('b', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeOutput, ResourceHelper.ModResource("blood_pack_empty"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOOD_PACK_EMPTY.get())
                .requires(ModItems.BLOOD_PACK.get(), 1)
                .unlockedBy("has_blood_pack", has(ModItems.BLOOD_PACK.get()))
                .save(recipeOutput, ResourceHelper.ModResource("blood_pack_empty_unfill"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SUTURE.get(), 8)
                .pattern("c a")
                .pattern("ca ")
                .pattern("bcc")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_INGOT)
                .define('c', Tags.Items.STRING)
                .unlockedBy("has_string", has(Tags.Items.STRING))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOD_SCANNER.get(), 1)
                .pattern(" d ")
                .pattern("bcb")
                .pattern("aaa")
                .define('a', Items.IRON_INGOT)
                .define('b', Items.REDSTONE)
                .define('c', ModItems.BLOOD_PACK_EMPTY.get())
                .define('d', Items.GLASS)
                .unlockedBy("has_blood_pack_empty", has(ModItems.BLOOD_PACK_EMPTY.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NUTRIENT_SCANNER.get(), 1)
                .pattern(" d ")
                .pattern("bcb")
                .pattern("aaa")
                .define('a', Items.IRON_INGOT)
                .define('b', Items.REDSTONE)
                .define('c', Items.WHEAT_SEEDS)
                .define('d', Items.GLASS)
                .unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEALTH_SCANNER.get(), 1)
                .pattern(" d ")
                .pattern("bcb")
                .pattern("aaa")
                .define('a', Items.IRON_INGOT)
                .define('b', Items.REDSTONE)
                .define('c', ModItems.BANDAGE.get())
                .define('d', Items.GLASS)
                .unlockedBy("has_bandage", has(ModItems.BANDAGE.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GYPSUM.get(), 1)
                .pattern(" a ")
                .pattern("aba")
                .pattern(" a ")
                .define('a', Items.CLAY_BALL)
                .define('b', Items.BONE_MEAL)
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MORPHINE.get(), 2)
                .requires(Items.POPPY, 1)
                .requires(Items.GLASS_BOTTLE, 2)
                .requires(Items.FERMENTED_SPIDER_EYE, 1)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEALTH_CARE_BAG.get(), 1)
                .pattern("bab")
                .pattern("cde")
                .pattern("bfb")
                .define('a', ItemTags.WOOL)
                .define('b', Items.LEATHER)
                .define('c', ModItems.BANDAGE.get())
                .define('d', ModItems.SUTURE.get())
                .define('e', ModItems.MORPHINE.get())
                .define('f', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_WRENCH.get(), 1)
                .pattern("a a")
                .pattern(" a ")
                .pattern(" a ")
                .define('a', ItemTags.PLANKS)
                .unlockedBy("has_plank", has(ItemTags.PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.BONE_MEAL)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_WOOD.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', ItemTags.LOGS)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_STONE.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.STONE)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_COPPER.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.COPPER_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_IRON.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_GOLD.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.GOLD_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_DIMOND.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.DIAMOND)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IMPLANTS_NETHERITE.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE_BLOCK)
                .define('b', Items.NETHERITE_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SCALPEL.get(), 1)
                .pattern(" a ")
                .pattern(" b ")
                .pattern(" b ")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEMOSTAT.get(), 1)
                .pattern("a a")
                .pattern(" b ")
                .pattern(" b ")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RETRACTOR.get(), 1)
                .pattern("a a")
                .pattern("bab")
                .pattern("b b")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SURGICAL_DRILL.get(), 1)
                .pattern(" c ")
                .pattern("abc")
                .pattern("  c")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_BLOCK)
                .define('c', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TWEEZER.get(), 1)
                .pattern("a a")
                .pattern("a a")
                .pattern(" a ")
                .define('a', Items.IRON_NUGGET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SURGERY_TOOL_BAG.get(), 1)
                .pattern("bab")
                .pattern("cde")
                .pattern("bfb")
                .define('a', ItemTags.WOOL)
                .define('b', Items.LEATHER)
                .define('c', ModItems.SCALPEL.get())
                .define('d', ModItems.HEMOSTAT.get())
                .define('e', ModItems.RETRACTOR.get())
                .define('f', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OPERATING_BED_BLOCK_ITEM.get(), 1)
                .pattern(" c ")
                .pattern("bab")
                .pattern("   ")
                .define('a', ItemTags.BEDS)
                .define('b', Items.IRON_BARS)
                .define('c', ModItems.MORPHINE.get())
                .unlockedBy("has_bad", has(ItemTags.BEDS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BONE_NATURAL.get(), 2)
                .requires(Items.BONE, 2)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_WOOD.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', ItemTags.LOGS)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_STONE.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.STONE)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_COPPER.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.COPPER_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_IRON.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_GOLD.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.GOLD_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_DIMOND.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.DIAMOND)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BONE_NETHERITE.get(), 1)
                .pattern(" b ")
                .pattern("bab")
                .pattern(" b ")
                .define('a', Items.BONE)
                .define('b', Items.NETHERITE_INGOT)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SURGERY_SAW.get(), 1)
                .pattern("  a")
                .pattern(" ab")
                .pattern("ab ")
                .define('a', Items.IRON_INGOT)
                .define('b', Items.IRON_NUGGET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.NALOXONE.get(), 1)
                .requires(ModItems.MORPHINE.get(), 1)
                .requires(Items.MILK_BUCKET, 1)
                .requires(Items.GLOWSTONE_DUST, 2)
                .unlockedBy("has_mophine", has(ModItems.MORPHINE.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TOURNIQUET.get(), 2)
                .pattern(" a ")
                .pattern("a a")
                .pattern("ba ")
                .define('a', Items.LEATHER)
                .define('b', Items.STRING)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEDICAL_STENT.get(), 1)
                .pattern(" ba")
                .pattern("bab")
                .pattern("ab ")
                .define('a', Items.STRING)
                .define('b', Items.IRON_NUGGET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NEEDLE.get(), 1)
                .pattern("  b")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DRAINAGE.get(), 2)
                .pattern("cbb")
                .pattern("  b")
                .pattern("abb")
                .define('a', Items.IRON_NUGGET)
                .define('b', Items.STRING)
                .define('c', Items.LEATHER)
                .unlockedBy("has_string", has(Items.STRING))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADRENALINE.get(), 1)
                .pattern(" c ")
                .pattern("sbs")
                .pattern("rmr")
                .define('b', Items.GLASS_BOTTLE)
                .define('c', Items.GOLDEN_CARROT)
                .define('s', Items.SUGAR)
                .define('r', Items.REDSTONE)
                .define('m', Items.GLISTERING_MELON_SLICE)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OXYGEN_MASK.get(), 1)
                .pattern(" g ")
                .pattern("wbw")
                .pattern(" i ")
                .define('g', Items.GLASS)
                .define('w', ItemTags.WOOL)
                .define('i', Items.IRON_INGOT)
                .define('b', Items.BAMBOO_BLOCK)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(recipeOutput, ResourceHelper.ModResource("oxygen_mask"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.OXYGEN_MASK.get(), 1)
                .requires(ModItems.OXYGEN_MASK.get(), 1)
                .requires(Items.BAMBOO_BLOCK, 1)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(recipeOutput, ResourceHelper.ModResource("oxygen_mask_repair"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTOPULSE.get(), 1)
                .pattern("igi")
                .pattern("rar")
                .pattern("idi")
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GOLD_INGOT)
                .define('r', Items.REDSTONE)
                .define('a', ModItems.ADRENALINE.get())
                .define('d', Items.DIAMOND)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput, ResourceHelper.ModResource("autopulse"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AUTOPULSE.get(), 1)
                .requires(ModItems.AUTOPULSE.get(), 1)
                .requires(ModItems.ADRENALINE.get(), 1)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput, ResourceHelper.ModResource("autopulse_repair"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANTISEPTIC.get(), 2)
                .requires(Items.GLASS_BOTTLE, 2)
                .requires(Items.WATER_BUCKET, 1)
                .requires(Items.CHARCOAL, 1)
                .unlockedBy("has_coal", has(Items.CHARCOAL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANTISEPTIC_SPRAYER.get(), 1)
                .pattern(" r ")
                .pattern("iai")
                .pattern(" bi")
                .define('i', Items.IRON_INGOT)
                .define('a', ModItems.ANTISEPTIC.get())
                .define('r', Items.REDSTONE)
                .define('b', ItemTags.BUTTONS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput, ResourceHelper.ModResource("antiseptic_sprayer"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANTISEPTIC_SPRAYER.get(), 1)
                .requires(ModItems.ANTISEPTIC_SPRAYER.get(), 1)
                .requires(ModItems.ANTISEPTIC.get(), 1)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput, ResourceHelper.ModResource("antiseptic_sprayer_repair"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANTIBIOTICS.get(), 1)
                .requires(ModItems.ANTISEPTIC.get(), 1)
                .requires(Items.SUGAR, 1)
                .requires(Items.BROWN_MUSHROOM, 1)
                .requires(Items.RED_MUSHROOM, 1)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANTIBIOTIC_OINTMENT.get(), 1)
                .requires(ModItems.ANTIBIOTICS.get(), 1)
                .requires(Items.SLIME_BALL, 1)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LIMB_REF_BEG.get(), 1)
                .pattern("igi")
                .pattern("gbg")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GLASS)
                .define('b', Items.BLUE_ICE)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLASTIC_SKIN.get(), 6)
                .pattern("bbb")
                .pattern("fif")
                .pattern("bbb")
                .define('b', ModItems.BANDAGE.get())
                .define('i', ModItems.ANTIBIOTIC_OINTMENT.get())
                .define('f', Items.BLAZE_POWDER)
                .unlockedBy("has_bandage", has(ModItems.BANDAGE.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANTIBIOTIC_GLUE.get(), 1)
                .requires(ModItems.ANTIBIOTICS.get(), 1)
                .requires(Items.MAGMA_CREAM, 1)
                .unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STASIS_BAG.get())
                .pattern("IGI")
                .pattern("LSL")
                .pattern("IRI")
                .define('I', Items.BLUE_ICE)
                .define('G', Items.GHAST_TEAR)
                .define('L', Items.LEATHER)
                .define('S', Items.SOUL_LANTERN)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_ice", has(Items.BLUE_ICE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WALKING_STICK.get())
                .pattern("iii")
                .pattern("i i")
                .pattern("i i")
                .define('i', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STRETCHER.get())
                .pattern("   ")
                .pattern(" ii")
                .pattern("www")
                .define('i', Items.STICK)
                .define('w', ItemTags.PLANKS)
                .unlockedBy("has_stick", has(Items.STICK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_USE_BAG.get())
                .pattern("iii")
                .pattern("bgb")
                .pattern("rir")
                .define('i', Items.IRON_INGOT)
                .define('b', ItemTags.STONE_BUTTONS)
                .define('r', Items.REDSTONE)
                .define('g', Items.TRAPPED_CHEST)
                .unlockedBy("has_trap", has(Items.TRAPPED_CHEST))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEDICINE_BAG.get())
                .pattern("w w")
                .pattern("wgw")
                .pattern(" i ")
                .define('i', Items.IRON_NUGGET)
                .define('w', ItemTags.WOOL)
                .define('g', Items.GLASS_BOTTLE)
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GRASS_STRING.get(), 3)
                .requires(Items.GRASS, 3)
                .unlockedBy("has_grass", has(Items.GRASS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLAMP.get())
                .pattern("wgw")
                .pattern("w w")
                .pattern("wgw")
                .define('w', ItemTags.PLANKS)
                .define('g', Tags.Items.STRING)
                .unlockedBy("has_string", has(Tags.Items.STRING))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HERB_BANDAGE.get(), 4)
                .requires(Items.GRASS, 2)
                .requires(ItemTags.SAPLINGS)
                .requires(ItemTags.SMALL_FLOWERS)
                .unlockedBy("has_grass", has(Items.GRASS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AED.get())
                .pattern("grg")
                .pattern("lal")
                .pattern("ccc")
                .define('c', Items.WAXED_CUT_COPPER_SLAB)
                .define('l', Items.LIGHTNING_ROD)
                .define('g', Items.GOLD_INGOT)
                .define('r', Items.REDSTONE)
                .define('a', Items.AMETHYST_BLOCK)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AED.get())
                .requires(ModItems.AED.get())
                .requires(Items.AMETHYST_BLOCK)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput, ResourceHelper.ModResource("aed_repair"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FOOD_CONSUMER.get())
                .requires(Tags.Items.CROPS)
                .requires(Tags.Items.CROPS)
                .requires(Items.SUGAR, 2)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FENTANYL.get())
                .requires(ModItems.MORPHINE.get())
                .requires(Items.POPPY, 2)
                .unlockedBy("has_poppy", has(Items.POPPY))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HYPERZINE.get())
                .requires(ModItems.FENTANYL.get())
                .requires(Items.SUGAR)
                .requires(Items.GUNPOWDER)
                .requires(Items.BONE_MEAL)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HARDENER.get())
                .requires(ModItems.FENTANYL.get())
                .requires(Items.TURTLE_HELMET)
                .requires(Items.GUNPOWDER)
                .requires(Items.BONE_MEAL)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(recipeOutput);

        // M3 疾病药物
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DEXTROMETHORPHAN.get())
                .requires(Items.SUGAR)
                .requires(ModItems.MORPHINE.get())
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_morphine", has(ModItems.MORPHINE.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.IBUPROFEN.get())
                .requires(ModItems.FOOD_CONSUMER.get())
                .requires(Items.SUGAR)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_food_consumer", has(ModItems.FOOD_CONSUMER.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RIBAVIRIN.get())
                .requires(ModItems.ANTIBIOTICS.get())
                .requires(ModItems.NEEDLE.get())
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_antibiotics", has(ModItems.ANTIBIOTICS.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOCKER.get())
                .requires(ModItems.HYPERZINE.get())
                .requires(ModItems.HARDENER.get())
                .requires(ModItems.NEEDLE.get())
                .unlockedBy("has_hyperzine", has(ModItems.HYPERZINE.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SEDATIVE.get())
                .requires(ModItems.MORPHINE.get())
                .requires(ModItems.NALOXONE.get())
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_naloxone", has(ModItems.NALOXONE.get()))
                .save(recipeOutput);

        // M3 针具清洁（已污染的注射针 + 消毒剂 → 新的干净针，产出物无污染 NBT）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RIBAVIRIN.get())
                .requires(ModItems.RIBAVIRIN.get())
                .requires(ModItems.ANTISEPTIC.get())
                .unlockedBy("has_ribavirin", has(ModItems.RIBAVIRIN.get()))
                .save(recipeOutput, ResourceHelper.ModResource("ribavirin_cleaned"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOCKER.get())
                .requires(ModItems.BLOCKER.get())
                .requires(ModItems.ANTISEPTIC.get())
                .unlockedBy("has_blocker", has(ModItems.BLOCKER.get()))
                .save(recipeOutput, ResourceHelper.ModResource("blocker_cleaned"));

        // M5 拉米夫定胶囊：安印席山 + 吸水石 + 玻璃瓶
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LAMIVUDINE_CAPSULE.get())
                .requires(ModItems.ANTIBIOTICS.get())
                .requires(ModItems.MORPHINE.get())
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_antibiotics", has(ModItems.ANTIBIOTICS.get()))
                .save(recipeOutput);


        var book = PatchouliAPI.get().getBookStack(ResourceHelper.ModResource("medical_guide"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, book.getItem())
                .requires(Items.BOOK, 1)
                .requires(ModItems.BANDAGE.get(), 1)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(recipeOutput, ResourceHelper.ModResource("medical_guide"));
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerBasicBrewingRecipe(Potions.STRONG_REGENERATION, new ItemStack(ModItems.MORPHINE.get()), ModPotions.COMBAT_STIMULANT_POTION.get());
            registerBasicBrewingRecipe(Potions.STRONG_HARMING, new ItemStack(ModItems.MORPHINE.get()), ModPotions.ANALGESIA_POISON_POTION.get());
        });
    }

    private static void registerBasicBrewingRecipe(Potion inputPotion, ItemStack ingredient, Potion outputPotion) {
        ItemStack inputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), inputPotion);
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(inputStack),
                Ingredient.of(ingredient),
                outputStack
        );
    }
}
