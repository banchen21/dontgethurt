package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class SurgicalDrill extends AbstractPartlyHealItem {
    public SurgicalDrill(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, (h) -> {
            AbstractVisibleBody body = (AbstractVisibleBody) h.getComponent(component);
            if (!body.abnormal(RETRACTED_SKIN)) return false;
            if (body.abnormal(DRILLED_BONES)) return false;
            if (body.abnormal(SAWED_BONES)) return false;
            if (body instanceof AbstractExtremities extremities)
                if (extremities.abnormal(TRAUMATIC_AMPUTATION) || extremities.abnormal(SURGICAL_AMPUTATION)) return false;

            body.setConditionValue(DRILLED_BONES, ConditionAccessor.get(DRILLED_BONES).maxValue());
            return true;
        }, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·骨折").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·骨损伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·骨坏死").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("需要"));
        tooltipComponents.add(Component.literal("·皮肤牵开").withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·骨骼钻孔").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("·创伤性休克").withStyle(ChatFormatting.RED));
    }
}
