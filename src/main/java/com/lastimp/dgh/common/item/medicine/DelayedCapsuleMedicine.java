package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DelayedCapsuleMedicine extends AbstractDirectHealItems {
    private final String medicineType;
    private final List<String> targets;

    public DelayedCapsuleMedicine(Properties properties, String medicineType, String... targets) {
        super(properties);
        this.medicineType = medicineType;
        this.targets = Arrays.asList(targets);
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        return HealthCapability.getAndApply(entity, health -> health.disease().consumeCapsuleDose(medicineType, entity), false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("胶囊药物").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·服药后120秒生效").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("·两次服用间隔300秒").withStyle(ChatFormatting.GRAY));
        for (String target : targets) {
            tooltipComponents.add(Component.literal(target).withStyle(ChatFormatting.AQUA));
        }
    }
}

