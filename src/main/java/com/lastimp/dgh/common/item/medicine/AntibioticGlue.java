package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class AntibioticGlue extends AbstractPartlyHealItem {
    public AntibioticGlue(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, h -> {
            var body = h.getComponent(component);
            if (body.getConditionValue(OINTMENT) > 0.95f) return false;
            body.healing(BURN, -0.15f);
            body.healing(INFECTION, -1f);
            return true;
        }, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·烧伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·感染").withStyle(ChatFormatting.BLUE));
    }
}
