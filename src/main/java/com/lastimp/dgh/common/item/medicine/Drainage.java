package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
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

public class Drainage extends AbstractPartlyHealItem {

    public Drainage(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, health -> {
            AbstractBody body = health.getComponent(component);
            if (!body.abnormal(RETRACTED_SKIN) || !body.abnormal(PNEUMOTHORAX)) return false;
            body.setConditionValue(PNEUMOTHORAX, ConditionAccessor.get(PNEUMOTHORAX).minValue());
            body.setConditionValue(PNEUMOTHORAX_NEEDLE, ConditionAccessor.get(PNEUMOTHORAX_NEEDLE).minValue());
            return true;
        }, false);
    }

    @Override
    protected void initComponents() {
        applicableComponents.add(BodyComponents.TORSO);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·气胸").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("需要"));
        tooltipComponents.add(Component.literal("·皮肤牵开").withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·移除气胸针").withStyle(ChatFormatting.RED));
    }
}
