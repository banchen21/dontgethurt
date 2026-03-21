package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.enums.BodyComponents;
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

public class PlasticSkin extends Bandages {
    public PlasticSkin(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        if (super.healOn(source, entity, component)) {
            return HealthCapability.getAndApply(entity, h -> {
                AbstractBody body = h.getComponent(component);
                body.healing(BANDAGED, 0.5f);
                return true;
            }, false);
        } else {
            return HealthCapability.getAndApply(entity, h -> {
                AbstractBody body = h.getComponent(component);
                if (!body.abnormal(SURGERY_INCISION)) return false;
                body.healing(BURN, -0.25f);
                return true;
            }, false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·外伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·烧伤（手术中）").withStyle(ChatFormatting.BLUE));
    }
}
