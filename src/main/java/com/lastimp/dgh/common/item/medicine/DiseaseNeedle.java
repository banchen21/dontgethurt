package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import com.lastimp.dgh.common.system.drug.DrugRuleEngine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

/** 
 * 药针类疾病药物：立即生效，使用后标记为污染，污染针重复使用有概率
 * 触发脓毒症（概率 35%）。
 */
public class DiseaseNeedle extends AbstractDirectHealItems {

    private final String doseType;

    public DiseaseNeedle(Properties properties, String doseType) {
        super(properties);
        this.doseType = doseType;
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) return false;
        ItemStack stack = findStack(player);
        if (stack == null || stack.isEmpty()) return false;
        return DiseaseCapability.getAndApply(player,
                disease -> DrugRuleEngine.tryNeedle(player, disease, doseType, stack), false);
    }

    /** 在玩家手中找到本物品的 ItemStack（用于读写 NBT 污染标记） */
    private ItemStack findStack(Player player) {
        if (player.getMainHandItem().is(this)) return player.getMainHandItem();
        if (player.getOffhandItem().is(this)) return player.getOffhandItem();
        return null;
    }

    /** 显示污染状态提示 */
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("drug.dgh.dose." + doseType).withStyle(ChatFormatting.AQUA));
        boolean contaminated = stack.hasTag() && stack.getTag().getBoolean("dgh_contaminated");
        if (contaminated) {
            tooltipComponents.add(Component.translatable("drug.dgh.needle_contaminated_tip").withStyle(ChatFormatting.RED));
            tooltipComponents.add(Component.translatable("drug.dgh.needle_clean_recipe_tip").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("drug.dgh.needle_clean_tip").withStyle(ChatFormatting.GREEN));
        }
    }
}
