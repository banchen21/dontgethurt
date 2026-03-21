package com.lastimp.dgh.common.client.gui.screen;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.network.message.MyHealingItemUseData;
import com.lastimp.dgh.common.client.ClientAccessor;
import com.lastimp.dgh.common.menu.BagMenu;
import com.lastimp.dgh.common.utils.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.lastimp.dgh.common.enums.BodyComponents.TORSO;

public class MedicineBagScreen extends AbstractContainerScreen<BagMenu> {
    private static final ResourceLocation HUD_BACKGROUND = ResourceHelper.ModResource("textures/gui/medicine_bag_hud.png");

    private static final int PANEL_WIDTH = 176;   // 面板宽度
    private static final int PANEL_HEIGHT = 130;  // 面板高度

    public MedicineBagScreen(BagMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
        super.init();
        this.addButtons();
    }

    private void addButtons() {
        for (int i = 0; i <this.menu.slots.size(); i++) {
            this.addDirectUseButton(i, 7 + i * 18, 28, 18, 18);
        }
    }

    private void addDirectUseButton(int index, int x, int y, int width, int height) {
        var button = Button.builder(Component.empty(), (b) -> {
            PlatformService.NETWORK.sendToServer(MyHealingItemUseData.getInstance(
                    ClientAccessor.getPlayerOrThrow().getUUID(), index + 36, TORSO
            ));
        }).bounds(this.leftPos + x, this.topPos + y, width, height).build();
        this.addWidget(button);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int panelX = (guiGraphics.guiWidth() - PANEL_WIDTH) / 2;
        int panelY = (guiGraphics.guiHeight() - PANEL_HEIGHT) / 2;

        guiGraphics.blit(HUD_BACKGROUND, panelX, panelY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!check()) return;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private boolean check() {
        Minecraft mc = ClientAccessor.mc();
        // 跳过：菜单界面、无玩家、隐藏GUI（按F1)
        return !(mc.level == null || mc.player == null || mc.options.hideGui);
    }
}
