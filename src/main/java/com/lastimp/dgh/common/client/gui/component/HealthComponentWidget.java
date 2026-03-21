package com.lastimp.dgh.common.client.gui.component;

import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

public class HealthComponentWidget extends Button {
    public static final ResourceLocation SPRITES_HEAD = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_head.png");
    public static final ResourceLocation SPRITES_TORSO = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_torso.png");
    public static final ResourceLocation SPRITES_LEFT_ARM = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_left_arm.png");
    public static final ResourceLocation SPRITES_RIGHT_ARM = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_right_arm.png");
    public static final ResourceLocation SPRITES_LEFT_LEG = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_left_leg.png");
    public static final ResourceLocation SPRITES_RIGHT_LEG = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_right_leg.png");
    public static final ResourceLocation SPRITES_HEAD_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_head_lighted.png");
    public static final ResourceLocation SPRITES_TORSO_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_torso_lighted.png");
    public static final ResourceLocation SPRITES_LEFT_ARM_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_left_arm_lighted.png");
    public static final ResourceLocation SPRITES_RIGHT_ARM_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_right_arm_lighted.png");
    public static final ResourceLocation SPRITES_LEFT_LEG_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_left_leg_lighted.png");
    public static final ResourceLocation SPRITES_RIGHT_LEG_LIGHT = ResourceHelper.ModResource("textures/gui/sprites/widget/health_hud_right_leg_lighted.png");

    public final BodyComponents id;
    private final ResourceLocation resource;
    private final ResourceLocation resourceLighted;
    private float conditionValue;
    public float red;
    private float green;
    private float blue;

    public HealthComponentWidget(int x, int y, int width, int height, Component message, OnPress onPress, BodyComponents id, ResourceLocation resource, ResourceLocation resourceLighted) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.id = id;
        this.resource = resource;
        this.resourceLighted = resourceLighted;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.setColor(this.red, this.green, this.blue, this.conditionValue);

        RenderSystem.enableBlend();
        gui.blit(this.resource, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);

        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.isHoveredOrFocused())
            gui.blit(this.resourceLighted, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
    }

    public void setConditionValue(float conditionValue) {
        this.conditionValue = conditionValue;
    }

    public void setRedAndGreen(Triple<Float, Float, Float> color) {
        this.red = color.getLeft();
        this.green = color.getMiddle();
        this.blue = color.getRight();
    }
}
