package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import com.lastimp.dgh.common.entry.register.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

public class FoodConsumer extends AbstractDirectHealItems {
    public FoodConsumer(Properties properties) {
        super(properties);
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(ModEffects.FOOD_CONSUMER_EFFECT.get(), 20 * 120));
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·伤口抗性").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·饥饿").withStyle(ChatFormatting.RED));
    }
}
