package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class Tweezer extends AbstractPartlyHealItem {
    public Tweezer(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, (h) -> {
            AbstractVisibleBody body = (AbstractVisibleBody) h.getComponent(component);
            if (body.abnormal(RETRACTED_SKIN)) {
                body.injury(OPEN_WOUND, 0.03f);
                body.healing(INTERNAL_INJURY, -Mth.randomBetween(Utils.randomSource, 0.05f, 0.2f));
                body.healing(FOREIGN_OBJECT, -Mth.randomBetween(Utils.randomSource, 0.05f, 0.2f));
            } else if (body.abnormal(PASS_THROUGH)) {
                body.injury(OPEN_WOUND, 0.05f);
                body.healing(FOREIGN_OBJECT, -Mth.randomBetween(Utils.randomSource, 0.05f, 0.2f));
            } else {
                body.injury(OPEN_WOUND, 0.05f);
                body.setConditionValue(INTENSE_PAIN, ConditionAccessor.get(INTENSE_PAIN).maxValue());
            }
            return true;
        }, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·内伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·体内异物").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("需要"));
        tooltipComponents.add(Component.literal("·皮肤牵开").withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·撕裂伤").withStyle(ChatFormatting.RED));
    }
}
