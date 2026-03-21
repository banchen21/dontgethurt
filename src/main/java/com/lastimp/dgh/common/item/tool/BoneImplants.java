package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class BoneImplants extends AbstractPartlyHealItem {
    private final ResourceLocation boneType;

    public BoneImplants(Properties properties) {
        super(properties);
        this.boneType = null;
    }

    public BoneImplants(Properties properties, ResourceLocation boneType) {
        super(properties);
        this.boneType = boneType;
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, (h) -> {
            AbstractVisibleBody body = (AbstractVisibleBody) h.getComponent(component);
            if (body instanceof AbstractExtremities extremities) {
                if (extremities.abnormal(TRAUMATIC_AMPUTATION) || extremities.abnormal(SURGICAL_AMPUTATION)) return false;
            }

            if (body.abnormal(DRILLED_BONES)) {
                if (body.boneCrafted() != boneType) return false;
                body.healing(FRACTURE, -ConditionAccessor.get(FRACTURE).maxValue());
                body.healing(BONE_DAMAGE, -ConditionAccessor.get(BONE_DAMAGE).maxValue());
                body.healing(BONE_DEATH, -ConditionAccessor.get(BONE_DEATH).maxValue());
                return true;
            }
            return false;
        }, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·骨折").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·骨损伤").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("·骨坏死").withStyle(ChatFormatting.BLUE));
    }
}
