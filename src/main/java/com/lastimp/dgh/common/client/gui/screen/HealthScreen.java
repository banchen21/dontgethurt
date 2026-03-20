
package com.lastimp.dgh.common.client.gui.screen;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.client.gui.component.DynamicBarHealthWidget;
import com.lastimp.dgh.common.client.gui.component.MaskableHealthWidget;
import com.lastimp.dgh.common.enums.KeyPressedType;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.lastimp.dgh.common.network.message.MyHealingItemUseData;
import com.lastimp.dgh.common.network.message.MyKeyPressedData;
import com.lastimp.dgh.common.client.ClientAccessor;
import com.lastimp.dgh.common.client.gui.GuiOpenWrapper;
import com.lastimp.dgh.common.client.gui.component.HealthComponentWidget;
import com.lastimp.dgh.common.client.gui.component.HealthConditionWidget;
import com.lastimp.dgh.common.menu.HealthMenu;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Torso;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.item.tool.HealthScanner;
import com.lastimp.dgh.common.network.message.MyReadAllConditionData;
import com.lastimp.dgh.common.entry.register.ModEffects;
import com.lastimp.dgh.common.entry.register.ModSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;
import static com.lastimp.dgh.common.enums.BodyComponents.*;
import static com.lastimp.dgh.common.enums.OperationType.HEALTH_SCANN;
import static com.lastimp.dgh.common.client.gui.component.HealthComponentWidget.*;

public class HealthScreen<T extends HealthMenu> extends AbstractContainerScreen<T> {
    protected static final ResourceLocation HUD_BACKGROUND = ResourceHelper.ModResource("textures/gui/health_hud.png");
    protected static final ResourceLocation HUD_ORGAN_BACKGROUND = ResourceHelper.ModResource("textures/gui/health_hud_cyber.png");
    protected static final ResourceLocation HUD_HEART_BEAT = ResourceHelper.ModResource("textures/gui/heart_beat_hud.png");
    protected static final ResourceLocation HUD_HEART_BEAT_ACC = ResourceHelper.ModResource("textures/gui/heart_beat_hud_acc.png");
    protected static final ResourceLocation HUD_HEART_BEAT_ACC2 = ResourceHelper.ModResource("textures/gui/heart_beat_hud_acc2.png");
    protected static final ResourceLocation HUD_HEART_BEAT_STOP = ResourceHelper.ModResource("textures/gui/heart_beat_hud_stop.png");
    protected static final ResourceLocation SLOT_DISABLE_MASK = ResourceHelper.ModResource("textures/gui/slot_disable_mask.png");
        // 记录是否至少点击过一次部位（用于初始保持空白）
        protected boolean hasUserSelectedOnce = false;


    protected static final int PANEL_WIDTH = 256;   // 面板宽度
    protected static final int PANEL_HEIGHT = 215;  // 面板高度
    protected static final int HEART_BEAT_X = 210;
    protected static final int HEART_BEAT_Y = 188;
    protected static final int HEART_BEAT_WIDTH = 40;
    protected static final int HEART_BEAT_HEIGHT = 16;

    protected final HashMap<BodyComponents, HealthComponentWidget> componentWidgets = new HashMap<>();
    protected final HashMap<ResourceLocation, HealthConditionWidget> conditionWidgets = new HashMap<>();
    protected BodyComponents selectedComponent = null;
    protected static HealthCapability healthData = null;
    protected static DiseaseCapability diseaseData = null;
    protected boolean onOrgan = false;

    public HealthScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        GuiOpenWrapper.setHealthScreen(this);
    }

    @Override
    protected void init () {
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
        super.init();

        // 清理旧的
        this.clearWidgets();
        componentWidgets.clear();
        this.addHealthWidget(31, 6, 34, 32, HEAD, SPRITES_HEAD, SPRITES_HEAD_LIGHT);
        this.addHealthWidget(32,40, 32, 39, TORSO, SPRITES_TORSO, SPRITES_TORSO_LIGHT);
        this.addHealthWidget(20, 41, 12, 46, LEFT_ARM, SPRITES_LEFT_ARM, SPRITES_LEFT_ARM_LIGHT);
        this.addHealthWidget(65, 41, 12, 46, RIGHT_ARM, SPRITES_RIGHT_ARM, SPRITES_RIGHT_ARM_LIGHT);
        this.addHealthWidget(29, 79, 18, 45, LEFT_LEG, SPRITES_LEFT_LEG, SPRITES_LEFT_LEG_LIGHT);
        this.addHealthWidget(49, 79, 18, 45, RIGHT_LEG, SPRITES_RIGHT_LEG, SPRITES_RIGHT_LEG_LIGHT);

        conditionWidgets.clear();
        for (var key : HealthScanner.healthScannerConditions()) {
            addConditionWidget(ConditionAccessor.get(key));
        }
        this.addHandPulseWidget(210, 166, 17, 16);
    }

    protected void addHealthWidget(int x, int y, int width, int height, BodyComponents idx, ResourceLocation resource, ResourceLocation resourceLighted) {
        HealthComponentWidget w = new HealthComponentWidget(
                this.leftPos + x, this.topPos + y, width, height,
                Component.literal(idx.toString()),
                (button) -> {
                    // 始终立即设置选中，保证用户点击后有反馈
                    this.selectedComponent = idx;
                    this.hasUserSelectedOnce = true;
                    if (healthData != null) {
                        boolean onOrgan = this.selectedComponent == idx && healthData.getComponent(idx).abnormal(RETRACTED_SKIN) && !this.onOrgan;
                        onOrgan &= this.getMenu().targetEntity.equals(ClientAccessor.getPlayerOrThrow().getUUID());
                        this.setOnOrgan(onOrgan);
                    }
                },
                idx, resource, resourceLighted
        );
        componentWidgets.put(idx, w);
        this.addRenderableWidget(w);
    }

    protected void addConditionWidget(BodyCondition condition) {
        HealthConditionWidget w;
        if (condition.isInjury()) {
            w = new MaskableHealthWidget(condition);
        } else if (condition.isResist()) {
            w = new DynamicBarHealthWidget(condition, 0xFFF4FFA7, 0xFF00F0FF);
        } else {
            w = new HealthConditionWidget(condition);
        }
        conditionWidgets.put(ResourceHelper.ResourceBySeperator(condition.name(), ':'), w);
        this.addRenderableWidget(w);
    }

    protected void addHandPulseWidget(int x, int y, int width, int height) {
        var button = Button.builder(Component.empty(), (b) -> {
            PlatformService.NETWORK.sendToServer(MyHealingItemUseData.getInstance(
                    this.menu.targetEntity, MyHealingItemUseData.HAND_PULSE, TORSO
            ));
        }).bounds(this.leftPos + x, this.topPos + y, width, height).build();
        this.addWidget(button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!ClientAccessor.canRenderGui()) return;
        this.refreshComponent();
        this.refreshCondition();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void refreshComponent() {
        if (healthData == null) return;
        if (this.componentWidgets.isEmpty()) return;

        for (BodyComponents component : BodyComponents.VISIBLE_BODIES) {
            AbstractVisibleBody body = (AbstractVisibleBody) healthData.getComponent(component);
            HealthComponentWidget widget = this.componentWidgets.get(component);
            widget.setConditionValue(body.conditionDisplayValue());
            widget.setRedAndGreen(body.getColor());
        }
    }

    protected void refreshCondition() {
        if (selectedComponent == null) return;
        if (healthData == null) return;

        for (HealthConditionWidget widget : this.conditionWidgets.values()){
            widget.visible = false;
        }

        int widgetCount = 0;
        AbstractBody bodyPart = healthData.getComponent(this.selectedComponent);
        for (var condition : bodyPart.getBodyConditions()) {
            HealthConditionWidget widget = this.conditionWidgets.get(condition);
            if (!this.visibilityCheck(bodyPart, condition)) continue;
            if (widgetCount > 12) break;

            widget.setSeverity(bodyPart.getCondition(condition).getDisplayValue());
            if (condition == FRACTURE && (bodyPart instanceof AbstractVisibleBody visibleBody)) {
                var bone = visibleBody.boneCrafted();
                int color = bone == null ? ConditionAccessor.get(FRACTURE).color() : ConditionAccessor.get(bone).color();
                widget.setPortionColor(color);
            } else if (ConditionAccessor.resistConditions.contains(condition)) {
                float addition = Math.max(0, bodyPart.getCondition(condition).getHiddenValue());
                float armorRes = healthData.armorResist(this.selectedComponent, condition);
                ((DynamicBarHealthWidget) widget).setBarSeverity(addition, armorRes);
            }

            widget.setPosition(
                    this.leftPos + 85 + (widgetCount % 2) * 72,
                    this.topPos + 11 + (widgetCount / 2) * 18
            );
            widget.visible = true;
            widgetCount += 1;
        }
    }

    protected boolean visibilityCheck(AbstractBody body, ResourceLocation key) {
        if (this.onOrgan) return false;
        if (!HealthScanner.healthScannerConditions().contains(key)) return false;
        if (!this.menu.isDevice && !HealthScanner.eyesightConditions().contains(key)) return false;
        if (ConditionAccessor.resistConditions.contains(key) && (body.abnormalWithHidden(key) || healthData.armorResist(this.selectedComponent, key) > 0)) return true;
        if (!ConditionAccessor.get(key).abnormal(body.getCondition(key).getDisplayValue())) return false;
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick,  int mouseX, int mouseY) {
        int panelX = (guiGraphics.guiWidth() - PANEL_WIDTH) / 2;
        int panelY = (guiGraphics.guiHeight() - PANEL_HEIGHT) / 2;

        guiGraphics.blit(this.getHudBackground(), panelX, panelY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        this.renderHeartBeat(guiGraphics);
        this.renderDisableSlots(guiGraphics);
    }

    @Override
    public void onClose() {
        setHealthData(null);
        GuiOpenWrapper.setHealthScreen(null);
        super.onClose();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 初始未点击过时保持空白
        if (this.selectedComponent == null) return;

        // 如果已选中但数据尚未到达，显示占位加载文字
        if (healthData == null) {
            String loading = "正在读取部位数据...";
            int x = this.leftPos + 85;
            int y = this.topPos + 11;
            guiGraphics.drawString(this.font, loading, x, y, 0xFF000000, false);
        }
        // 否则交由条件 widget 自行渲染（它们是 renderable widgets）
    }

    protected void renderHeartBeat(GuiGraphics guiGraphics) {
        int panelX = (guiGraphics.guiWidth() - PANEL_WIDTH) / 2 + HEART_BEAT_X;
        int panelY = (guiGraphics.guiHeight() - PANEL_HEIGHT) / 2 + HEART_BEAT_Y;
        int max_width = (int) (HEART_BEAT_WIDTH * 0.75);

        long tick = ClientAccessor.getGameTime();
        ResourceLocation heartBeat = HUD_HEART_BEAT;
        if (healthData != null) {
            Torso torso = (Torso) healthData.getComponent(TORSO);
            if (torso.abnormal(HEARTRATE_STOP)) heartBeat = HUD_HEART_BEAT_STOP;
            else if (torso.abnormal(HEARTRATE_IRREGULAR)) heartBeat = HUD_HEART_BEAT_ACC2;
            else if (torso.abnormal(HEARTRATE_INCREASE)) heartBeat = HUD_HEART_BEAT_ACC;
        }
        RenderSystem.enableBlend();
        for (int i = 0; i < max_width; i++) {
            int location = Math.toIntExact((i + tick) % HEART_BEAT_WIDTH);
            guiGraphics.setColor(0.0F, (float) i / max_width, 0.0F, 1.0F);
            guiGraphics.blit(heartBeat, panelX + location, panelY, 0, location, 0, 1, HEART_BEAT_HEIGHT, HEART_BEAT_WIDTH, HEART_BEAT_HEIGHT);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    protected void renderDisableSlots(GuiGraphics guiGraphics) {
        if (this.menu.getBag() == null) {
            for (int row = 0; row < 9; row++) {
                guiGraphics.blit(SLOT_DISABLE_MASK, this.leftPos + 233, this.topPos + 21 + row * 18, 0, 0, 18, 18, 18, 18);
            }
        }
        if (this.onOrgan) {
            for (int row = 0; row < 2; ++row) {
                for (int col = 0; col < 6; ++col) {
                    int index = row * 6 + col + 1;
                    if (index > healthData.getComponent(this.selectedComponent).organ1Level())
                        renderDisableSlot(guiGraphics, this.leftPos + 103 + col * 18, this.topPos + 11 + row * 18);
                    if (index > healthData.getComponent(this.selectedComponent).organ2Level())
                        renderDisableSlot(guiGraphics, this.leftPos + 103 + col * 18, this.topPos + 48 + row * 18);
                    if (index > healthData.getComponent(this.selectedComponent).organ3Level())
                        renderDisableSlot(guiGraphics, this.leftPos + 103 + col * 18, this.topPos + 85 + row * 18);
                }
            }
        }
    }

    private void renderDisableSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(SLOT_DISABLE_MASK, x, y,
                0,      // blitOffset
                0f, 0f, // uOffset, vOffset
                18, 18, // regionWidth, regionHeight = 整个纹理
                18, 18  // textureWidth, textureHeight
        );
    }

    @Override
    protected void containerTick() {
        var mc = ClientAccessor.mc();
        if (mc.level == null || mc.player == null) {
            GuiOpenWrapper.closeScreen();
            return;
        }
        var target = ClientAccessor.getLiving(mc.level, this.menu.targetEntity, mc.player.getEyePosition(), 40);
        if (target == null || target.isDeadOrDying() || (HealthCapability.isDown(ClientAccessor.getPlayerOrThrow()) && !ClientAccessor.getPlayerOrThrow().hasEffect(ModEffects.ADRENALINE_EFFECT.get()))) {
            GuiOpenWrapper.closeScreen();
            return;
        }
        PlatformService.NETWORK.sendToServer(MyReadAllConditionData.getInstance(
                this.menu.targetEntity, 0, null, HEALTH_SCANN
        ));

        if (healthData != null && (this.selectedComponent == null || !healthData.getComponent(this.selectedComponent).abnormal(RETRACTED_SKIN)) && this.onOrgan) {
            this.setOnOrgan(false);
        }

        this.playSound();
        this.updateEquipVisibleCoolDown();
    }

    protected void playSound() {
        long tick = ClientAccessor.getGameTime();
        SoundEvent sound = ModSounds.HEARTBEAT_NORMAL.get();
        if (healthData != null) {
            Torso torso = (Torso) healthData.getComponent(TORSO);
            if (torso.abnormal(HEARTRATE_STOP)) sound = ModSounds.HEARTBEAT_STOP.get();
            else if (torso.abnormal(HEARTRATE_IRREGULAR)) sound = ModSounds.HEARTBEAT_ACC2.get();
            else if (torso.abnormal(HEARTRATE_INCREASE)) sound = ModSounds.HEARTBEAT_ACC.get();
        }
        if (tick % HEART_BEAT_WIDTH == 1) {
            ClientAccessor.mc().getSoundManager().play(
                    SimpleSoundInstance.forUI( sound, 1.0f)
            );
        }
    }

    protected void updateEquipVisibleCoolDown() {
        var player = ClientAccessor.mc().player;
        if (player == null || HealthScreen.healthData == null) return;
        var cooldowns = player.getCooldowns();
        var oxygenMask = this.menu.getSlot(45).getItem().getItem();
        if (!cooldowns.isOnCooldown(oxygenMask))
            cooldowns.addCooldown(oxygenMask, HealthScreen.healthData.oxygenMaskCoolDown());
        var autopulse = this.menu.getSlot(46).getItem().getItem();
        if (!cooldowns.isOnCooldown(autopulse))
            cooldowns.addCooldown(autopulse, HealthScreen.healthData.autoPulseCoolDown());
    }

    public void setHealthData(HealthCapability healthData) {
        HealthScreen.healthData = healthData;
        if (healthData != null) this.menu.setEquipments(healthData);
    }

    public void setDiseaseData(DiseaseCapability diseaseData) {
        HealthScreen.diseaseData = diseaseData;
    }

    private void setOnOrgan(boolean onOrgan) {
        this.onOrgan = onOrgan;
        this.menu.setOrganActive(onOrgan, (AbstractVisibleBody) healthData.getComponent(this.selectedComponent));

        int componentIndex = this.selectedComponent != null ? onOrgan ? (this.selectedComponent.ordinal() + 1) : -(this.selectedComponent.ordinal() + 1) : 0;
        PlatformService.NETWORK.sendToServer(MyKeyPressedData.getInstance(
                KeyPressedType.HEALTH_SCREEN_COMPONENT_SELECTION, componentIndex
        ));
    }

    public BodyComponents getSelectedComponent() {
        return selectedComponent;
    }

    @Override
    public T getMenu() {
        return this.menu;
    }

    protected ResourceLocation getHudBackground() {
        return this.onOrgan ? HUD_ORGAN_BACKGROUND : HUD_BACKGROUND;
    }
}
