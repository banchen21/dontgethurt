package com.lastimp.dgh.common.client.eventHandler;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.client.ClientAccessor;
import com.lastimp.dgh.common.client.hotkey.KeyBinding;
import com.lastimp.dgh.common.enums.BodyComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public abstract class ModOverlay {
    private static final int SMALL_CONDITION_X = PlatformService.CONFIG.SMALL_CONDITION_X();
    private static final int SMALL_CONDITION_Y = PlatformService.CONFIG.SMALL_CONDITION_Y();


    public static void renderEyeOverlay(LocalPlayer player, GuiGraphics graphics) {
        if (player.isDeadOrDying()) return;
        HealthCapability.getAndApply(player, h -> {
            int avaEye = h.availableEye();
            if (avaEye >= 2) return;
            int color = avaEye == 1 ? 0x80000000 : 0xEF000000;
            graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
        });
    }

    public static void renderConditionOverlay(LocalPlayer player, GuiGraphics graphics) {
        if (player.isDeadOrDying()) return;
        HealthCapability.getAndApply(player, h -> {
            if (PlatformService.CONFIG.SMALL_CONDITION_DISAPPEAR_DELAY() == -2) return;
            if (ClientTickEventHandler.ABNORMAL_DELAY <= 0 && PlatformService.CONFIG.SMALL_CONDITION_DISAPPEAR_DELAY() != -1) return;

            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.HEAD), graphics, 5,0,6,6);
            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.TORSO), graphics, 4,8,8,12);
            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.LEFT_ARM), graphics, 0, 8,2,9);
            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.RIGHT_ARM), graphics, 14,8,2, 9);
            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.LEFT_LEG), graphics, 4, 22,2, 9);
            renderBodyCondition((AbstractVisibleBody) h.getComponent(BodyComponents.RIGHT_LEG), graphics, 10, 22,2, 9);
        });
    }

    private static void renderBodyCondition(AbstractVisibleBody body, GuiGraphics graphics, int x_min, int y_min, int width, int height) {
        var visibility = body.conditionDisplayValue();
        int color = ((int)(visibility * 255) << 24) + ((int)(body.getColor().getLeft() * 255) << 16) + ((int)(body.getColor().getMiddle() * 255) << 8) + ((int)(body.getColor().getRight() * 255));
        graphics.fill(SMALL_CONDITION_X + x_min, SMALL_CONDITION_Y + y_min, SMALL_CONDITION_X + x_min + width, SMALL_CONDITION_Y + y_min + height, 0xFF909090);
        graphics.fill(SMALL_CONDITION_X + x_min, SMALL_CONDITION_Y + y_min, SMALL_CONDITION_X + x_min + width, SMALL_CONDITION_Y + y_min + height, color);
    }

    public static void renderDyingOverlay(LocalPlayer player, GuiGraphics graphics) {
        if (!HealthCapability.isDown(player)) return;
        graphics.drawCenteredString(ClientAccessor.mc().font,
            Component.literal("按下鼠标求救"),
            graphics.guiWidth() / 2, graphics.guiHeight() / 2 - 50, 0xFFFFFFFF
        );
        if (PlatformService.CONFIG.ENABLE_SELF_SUICIDE()) {
            int timeout = PlatformService.CONFIG.GIVE_UP_TIMEOUT_SECONDS();
            graphics.drawCenteredString(ClientAccessor.mc().font,
                Component.literal("按住").append(KeyBinding.GIVE_UP.getTranslatedKeyMessage()).append("键" + timeout + "秒放弃治疗"),
                graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 15 - 50, 0xFFFFFFFF
            );
        }
    }
}
