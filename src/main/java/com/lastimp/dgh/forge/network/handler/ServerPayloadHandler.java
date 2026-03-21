package com.lastimp.dgh.forge.network.handler;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractVisibleBody;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.enums.OperationType;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.network.message.MyHealingItemUseData;
import com.lastimp.dgh.common.network.message.MyKeyPressedData;
import com.lastimp.dgh.common.network.message.MyReadAllConditionData;
import com.lastimp.dgh.common.capability.healthCore.dyingSystem.DyingHandler;
import com.lastimp.dgh.common.menu.BagMenu;
import com.lastimp.dgh.common.menu.HealthMenu;
import com.lastimp.dgh.common.menu.MenuOpenWrapper;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.healthCore.healingSystem.HealingHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerPayloadHandler {

    public static void handleReadAllConditionData(final MyReadAllConditionData data, final Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> {
            UUID uuid = new UUID(data.id_most(), data.id_least());
            ServerPlayer sender = context.getSender();
            var target = Utils.getLivingWithHealth(sender.serverLevel(), uuid);
            if (target == null) return;

                HealthCapability.getAndApply(target, health -> {
                var operation = OperationType.valueOf(data.oper());
                if (operation == OperationType.HEALTH_SCANN) {
                    var diseaseTag = target instanceof net.minecraft.world.entity.player.Player player
                        ? DiseaseCapability.getAndApply(player, DiseaseCapability::serialize, null)
                        : null;
                    PlatformService.NETWORK.sendToPlayer(
                        context.getSender(),
                        MyReadAllConditionData.getInstance(uuid, target.getId(), health.serialize(), diseaseTag, operation)
                    );
                    return;
                }

                PlatformService.NETWORK.sendToPlayer(
                    context.getSender(),
                    MyReadAllConditionData.getInstance(uuid, target.getId(), health.serialize(), operation)
                );
                });
        });
        context.setPacketHandled(true);
    }

    public static void handleClientPress(final MyKeyPressedData data, final Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            switch (data.key()) {
                case KEY_HEALTH_MENU:
                    MenuOpenWrapper.openHealthMenu(player, player.getUUID(), false);
                    break;
                case KEY_SLOT_USE:
                    MenuOpenWrapper.openMenu(player.getInventory().getItem(data.index()), player);
                    break;
                case GIVE_UP:
                    DyingHandler.setLivingDead(player);
                    break;
                case CALL_FOR_HELP:
                    Utils.broadcastMessageToTeam(player, Component.literal(
                            player.getScoreboardName() + "在("
                                    + String.format("%.1f", player.position().x) + ", "
                                    + String.format("%.1f", player.position().y) + ", "
                                    + String.format("%.1f", player.position().z) + ")需要救助"
                    ));
                    break;
                case HEALTH_SCREEN_COMPONENT_SELECTION:
                    if (player.containerMenu instanceof HealthMenu healthMenu) {
                        HealthCapability.getAndApply(player, h -> {
                            var component = BodyComponents.HEAD;
                            if (data.index() != 0) {
                                component = BodyComponents.values()[Math.abs(data.index()) - 1];
                            }
                            healthMenu.setOrganActive(data.index() > 0, (AbstractVisibleBody) h.getComponent(component));
                        });
                    }
                    break;
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleHealingItemUsageData(final MyHealingItemUseData data, Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sourcePlayer = ctx.get().getSender();
            if (sourcePlayer == null) return;
            var target = Utils.getLivingWithHealth(ctx.get().getSender().serverLevel(), new UUID(data.id_most(), data.id_least()));
            if (target == null) return;

            if (data.slotNum() == MyHealingItemUseData.HAND_PULSE) {
                HealthCapability.handPulse(target);
                return;
            }

            if (sourcePlayer.containerMenu instanceof HealthMenu healthMenu) {
                HealingHandler.handleHealthMenuItemUse(healthMenu, data, sourcePlayer, target);
            } else if (sourcePlayer.containerMenu instanceof BagMenu.MedicineSmallBag medicineSmallBagMenu) {
                HealingHandler.handleMedicineBagMenuItemUse(medicineSmallBagMenu, data, sourcePlayer, target);
            }
        });
        context.setPacketHandled(true);
    }
}
