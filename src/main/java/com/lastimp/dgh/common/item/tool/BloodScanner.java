
package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractHealingItem;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Blood;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.event.eventHandler.NutrientEventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BloodScanner extends AbstractHealingItem {
    public BloodScanner(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            boolean accurate = NutrientEventHandler.hasAccurateBloodData(player);
            HealthCapability.getAndApply(player, h -> BloodScanner.scanHealth(player, h, player.getScoreboardName(), accurate));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            if (!HealthCapability.has(target)) {
                player.sendSystemMessage(Component.translatable(target.getName().getString()).append("的血液很正常"));
            } else {
                var name = target instanceof Player ? target.getScoreboardName() : target.getName().getString();
                boolean accurate = !(target instanceof Player targetPlayer) || NutrientEventHandler.hasAccurateBloodData(targetPlayer);
                HealthCapability.getAndApply(target, h -> BloodScanner.scanHealth(player, h, name, accurate));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static void scanHealth(Player player, HealthCapability health, String name, boolean accurate) {
        Blood blood = (Blood) health.getComponent(BodyComponents.BLOOD);
        boolean hasAbnormal = false;
        for (var condition : ConditionAccessor.bloodConditions) {
            float value = blood.getConditionValue(condition);
            if (blood.abnormal(condition)) {
                if (!hasAbnormal)
                    player.sendSystemMessage(Component.translatable(name).append(accurate ? "的血液状态为：" : "的血液检测结果存在偏差："));
                hasAbnormal = true;
                float displayValue = accurate ? value : noisyValue(player, value);
                player.sendSystemMessage(
                        Component.translatable(condition.toString()).append(Component.literal(": " + String.format("%.2f", displayValue)))
                );
            }
        }
        if (!hasAbnormal) {
            if (accurate) {
                player.sendSystemMessage(Component.translatable(name).append("的血液很正常"));
            } else {
                player.sendSystemMessage(Component.translatable(name).append("的血液检测结果不稳定，未能获得准确数据"));
            }
        }
    }

    private static float noisyValue(Player player, float value) {
        float offset = (player.getRandom().nextFloat() - 0.5f) * 0.6f;
        return Mth.clamp(value + offset, 0.0f, 1.0f);
    }
}
