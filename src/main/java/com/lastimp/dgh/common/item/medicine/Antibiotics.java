package com.lastimp.dgh.common.item.medicine;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractDirectHealItems;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.system.disease.DiseaseManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.ANTIBIOTICS;

public class Antibiotics extends AbstractDirectHealItems {
    private static final DiseaseManager DISEASE_MANAGER = new DiseaseManager();

    public Antibiotics(Properties properties) {
        super(properties);
    }

    @Override
    public boolean heal(@NotNull LivingEntity source, @NotNull LivingEntity entity) {
        boolean bloodApplied = HealthCapability.getAndApply(entity, h -> {
            var blood = h.getComponent(BodyComponents.BLOOD);
            blood.healing(ANTIBIOTICS, 0.5f);
            return true;
        }, false);

        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            DiseaseCapability.getAndApply(player, disease -> {
                if (disease.sepsisStage() > 0) {
                    DISEASE_MANAGER.cureDisease(player, "sepsis");
                }
            });
        }

        return bloodApplied;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("治疗"));
        tooltipComponents.add(Component.literal("·败血症").withStyle(ChatFormatting.BLUE));
    }
}
