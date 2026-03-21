
package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Head;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class Sutures extends AbstractPartlyHealItem {
    private static final Set<ResourceLocation> cover = new HashSet<>();

    public Sutures(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, health -> {
            AbstractBody body = health.getComponent(component);

            boolean success = false;
            if (body.abnormal(OPEN_WOUND)) {
                body.healing(OPEN_WOUND, -0.2f);
                success = true;
            }
            if (body.abnormal(PASS_THROUGH)) {
                body.healing(PASS_THROUGH, -0.2f);
                success = true;
            }
            if (body.abnormal(SURGERY_INCISION)) {
                body.setConditionValue(SURGERY_INCISION, ConditionAccessor.get(SURGERY_INCISION).defaultValue());
                body.setConditionValue(CLAMPED_BLEEDING, ConditionAccessor.get(CLAMPED_BLEEDING).defaultValue());
                success = true;
            }
            if (body.abnormal(RETRACTED_SKIN)) {
                body.setConditionValue(RETRACTED_SKIN, ConditionAccessor.get(RETRACTED_SKIN).defaultValue());
                body.setConditionValue(DRILLED_BONES, ConditionAccessor.get(DRILLED_BONES).defaultValue());
                body.setConditionValue(CLAMPED_ARTERIES, ConditionAccessor.get(CLAMPED_ARTERIES).defaultValue());
                if (body instanceof AbstractExtremities || body instanceof Head)
                    body.setConditionValue(ARTERIAL_BLEEDING, ConditionAccessor.get(ARTERIAL_BLEEDING).defaultValue());
                success = true;
            }

            if (success) {
                if (body.abnormal(HERB_BANDAGED)) {
                    body.injury(INFECTION, 0.25f);
                }
                for (var key : cover) {
                    body.setConditionHidden(key, body.getConditionValue(key) + body.getConditionHidden(key));
                    body.setConditionValue(key, ConditionAccessor.get(key).defaultValue());
                }
            }
            return success;
        }, false);
    }

    public static void addCoverOnHeal(@NotNull ResourceLocation key) {
        cover.add(key);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·外伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·手术切口").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·感染（草药）").withStyle(ChatFormatting.RED));
    }
}
