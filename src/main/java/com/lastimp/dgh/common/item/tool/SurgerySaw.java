package com.lastimp.dgh.common.item.tool;

import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.item.bases.AbstractPartlyHealItem;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractArm;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Torso;
import com.lastimp.dgh.common.capability.healthCore.dyingSystem.DyingHandler;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.entry.register.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;

public class SurgerySaw extends AbstractPartlyHealItem {
    public SurgerySaw(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean healOn(@NotNull LivingEntity source, @NotNull LivingEntity entity, BodyComponents component) {
        return HealthCapability.getAndApply(entity, (h) -> {
            AbstractVisibleBody body = (AbstractVisibleBody) h.getComponent(component);
            //必须手术中，牵开皮肤
            if (!body.abnormal(RETRACTED_SKIN)) return false;
            //不能已经手术截肢
            if (body instanceof AbstractExtremities extremities && extremities.abnormal(SURGICAL_AMPUTATION)) return false;
            //如果是头，导致死亡
            if (component == BodyComponents.HEAD) return this.sawHead(source, entity);
            //已经牵开皮肤、没有骨锯开，则锯开骨头，取消骨钻开
            if (!body.abnormal(SAWED_BONES)) {
                if (!(body instanceof AbstractExtremities extremities) || !extremities.abnormal(TRAUMATIC_AMPUTATION)) {
                    return this.saw(source, body);
                } else {
                    return this.cut(source, extremities);
                }
            } else if (body instanceof AbstractExtremities extremities) {
                //已经牵开皮肤、有骨锯开
                return this.cut(source, extremities);
            }
            return false;
        }, false);
    }

    protected boolean saw(LivingEntity player, AbstractVisibleBody body) {
        int boneNumMax = (body instanceof Torso) ? 8 : 2;
        float returnFactor = body.getConditionValue(FRACTURE) + body.getConditionValue(BONE_DAMAGE) + body.getConditionValue(BONE_DEATH);
        if (body instanceof AbstractExtremities)
            returnFactor += body.getConditionValue(TRAUMATIC_AMPUTATION) + body.getConditionValue(SURGICAL_AMPUTATION);
        int boneReturn = (int) (boneNumMax * (1.0 - Math.min(1.0, returnFactor)));

        body.setConditionValue(SAWED_BONES, ConditionAccessor.get(SAWED_BONES).maxValue());
        body.setConditionValue(DRILLED_BONES, ConditionAccessor.get(DRILLED_BONES).defaultValue());
        body.setConditionValue(FRACTURE, ConditionAccessor.get(FRACTURE).defaultValue());
        body.setConditionValue(BONE_DAMAGE, ConditionAccessor.get(BONE_DAMAGE).defaultValue());
        body.setConditionValue(BONE_DEATH, ConditionAccessor.get(BONE_DEATH).defaultValue());

        ResourceLocation boneKey = body.boneCrafted();
        if (boneKey == null) {
            Utils.drop(ModItems.BONE_NATURAL.get(), player, boneReturn);
        } else {
            Utils.drop(ConditionAccessor.bones.get(boneKey).get(), player, boneReturn);
            body.setConditionValue(boneKey, ConditionAccessor.get(boneKey).defaultValue());
        }
        return true;
    }

    protected boolean cut(LivingEntity player, AbstractExtremities body) {
        if (body.abnormal(SURGICAL_AMPUTATION)) return false;

        if (!body.abnormal(TRAUMATIC_AMPUTATION)) {
            Item limb = body instanceof AbstractArm ? ModItems.HUMAN_HAND.get() : ModItems.HUMAN_LEG.get();
            Utils.drop(limb, player, 1);
        }

        body.setConditionValue(SURGICAL_AMPUTATION, ConditionAccessor.get(SURGICAL_AMPUTATION).maxValue());
        body.setConditionValue(TRAUMATIC_AMPUTATION, ConditionAccessor.get(TRAUMATIC_AMPUTATION).defaultValue());
        return true;
    }

    public static void sawExcept(LivingEntity source, AbstractBody body, ResourceLocation exception, int maxAmount) {
        float returnFactor = body.getConditionValue(FRACTURE) + body.getConditionValue(BONE_DAMAGE) + body.getConditionValue(BONE_DEATH);
        int boneReturn = (int) (maxAmount * (1.0 - Math.min(1.0, returnFactor)));
        for (var key : ConditionAccessor.bones.keySet()) {
            if (body.abnormal(key) && key != exception) {
                Utils.drop(ConditionAccessor.bones.get(key).get(), source, (int)(boneReturn * body.getConditionValue(key)));
                body.setConditionValue(key, ConditionAccessor.get(key).defaultValue());
            }
        }
        if (exception != null) {
            Utils.drop(ModItems.BONE_NATURAL.get(), source, (int)(boneReturn * (1.0f - body.getConditionValue(SAWED_BONES))));
            body.setConditionValue(SAWED_BONES, ConditionAccessor.get(SAWED_BONES).maxValue());
        }
    }

    private boolean sawHead(LivingEntity source, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), player.getGameProfile()));
            Utils.drop(head, source);
        }
        DyingHandler.setLivingDead(entity);
        return true;
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
        tooltipComponents.add(Component.literal("·骨锯开").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("·死亡（头）").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("·创伤性休克").withStyle(ChatFormatting.RED));
    }
}
