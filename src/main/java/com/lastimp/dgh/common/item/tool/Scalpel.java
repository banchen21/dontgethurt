package com.lastimp.dgh.common.item.tool;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class Scalpel extends AbstractPartlyHealItem {
    private static final Set<ResourceLocation> discover = new HashSet<>();

    public Scalpel(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, (h) -> {
            AbstractVisibleBody body = (AbstractVisibleBody) h.getComponent(component);
            if (body.abnormal(SURGERY_INCISION)) return false;

            body.setConditionValue(SURGERY_INCISION, ConditionAccessor.get(SURGERY_INCISION).maxValue());
            if (body.abnormal(HERB_BANDAGED))
                body.injury(INFECTION, 0.25f);

            for (var key : discover) {
                body.setConditionValue(key, body.getConditionHidden(key) + body.getConditionValue(key));
                body.setConditionHidden(key, ConditionAccessor.get(key).defaultValue());
            }
            return true;
        }, false);
    }

    public static void addDiscoverOnHeal(@NotNull ResourceLocation key) {
        discover.add(key);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("手术第一步"));
        tooltipComponents.add(Component.literal("·手术切口").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("造成"));
        tooltipComponents.add(Component.literal("·创伤性休克").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("·感染（草药）").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("·移除覆盖物").withStyle(ChatFormatting.RED));
    }

}
