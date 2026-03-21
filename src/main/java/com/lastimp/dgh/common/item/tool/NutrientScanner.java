package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.item.bases.AbstractHealingItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NutrientScanner extends AbstractHealingItem {
    public NutrientScanner(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand == InteractionHand.OFF_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        if (!level.isClientSide) {
            scanNutrient(player, player, player.getScoreboardName());
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            if (!(target instanceof Player targetPlayer)) {
                player.sendSystemMessage(Component.literal(target.getName().getString()).append(" 没有营养数据"));
            } else {
                scanNutrient(player, targetPlayer, targetPlayer.getScoreboardName());
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static void scanNutrient(Player scanner, Player target, String name) {
        NutrientCapability.getAndApply(target, nutrient -> {
            scanner.sendSystemMessage(Component.literal("=== ").append(name).append(" 的营养状态 ==="));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.hydration",      nutrient.hydration()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.carbohydrate",   nutrient.carbohydrate()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.fat",            nutrient.fat()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.protein",        nutrient.protein()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.minerals",       nutrient.minerals()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.vitamins",       nutrient.vitamins()));
            scanner.sendSystemMessage(fmt("gui.dgh.health_gui.nutrient.dietary_fiber",  nutrient.dietaryFiber()));
        });
    }

    private static Component fmt(String labelKey, float value) {
        String pct = String.format("%d%%", Math.round(value * 100));
        return Component.translatable(labelKey).append(Component.literal(": " + pct));
    }
}
