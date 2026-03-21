package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Blood;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Head;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

import static com.lastimp.dgh.common.enums.BodyComponents.BLOOD;
import static com.lastimp.dgh.common.enums.BodyComponents.HEAD;

public class Hardener extends AbstractDirectHealItems {
    public Hardener(Properties properties) {
        super(properties);
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        return HealthCapability.getAndApply(entity, h -> {
            Head head = (Head) h.getComponent(HEAD);
            Blood blood = (Blood) h.getComponent(BLOOD);

            head.healing(WITHDRAW, -0.9f);
            blood.injury(OPIATE_ADDICTED, 0.18f);
            blood.setConditionValue(HARDENER, 1.0f);
            return true;
        }, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·戒断").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·钢化").withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.literal("·阿片成瘾").withStyle(ChatFormatting.RED));
    }
}
