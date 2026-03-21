package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import com.lastimp.dgh.common.system.drug.DrugRuleEngine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

/** 胶囊类疾病药物：服药冷却 300s，延迟 120s 生效 */
public class DiseaseCapsule extends AbstractDirectHealItems {

    private final String doseType;

    public DiseaseCapsule(Properties properties, String doseType) {
        super(properties);
        this.doseType = doseType;
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) return false;
        return DiseaseCapability.getAndApply(player,
                disease -> DrugRuleEngine.tryCapsule(player, disease, doseType), false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("drug.dgh.capsule_tooltip_delay").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("drug.dgh.capsule_tooltip_cooldown").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("drug.dgh.dose." + doseType).withStyle(ChatFormatting.AQUA));
    }
}
