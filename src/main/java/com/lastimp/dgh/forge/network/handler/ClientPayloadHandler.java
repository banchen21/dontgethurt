
package com.lastimp.dgh.forge.network.handler;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.config.ModConfigs;
import com.lastimp.dgh.common.enums.OperationType;
import com.lastimp.dgh.common.network.message.MyReadAllConditionData;
import com.lastimp.dgh.common.network.message.MyServerConfigSynData;
import com.lastimp.dgh.common.client.ClientAccessor;
import com.lastimp.dgh.common.client.gui.GuiOpenWrapper;
import com.lastimp.dgh.common.capability.HealthCapability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPayloadHandler {

    public static void handleReadAllConditionData(final MyReadAllConditionData data, final Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            OperationType operation = OperationType.valueOf(data.oper());
            if (operation == OperationType.HEALTH_SCANN && GuiOpenWrapper.healthScreen() != null) {
                HealthCapability health = MyReadAllConditionData.getHealthFromInstance(data.tag());
                GuiOpenWrapper.healthScreen().setHealthData(health);
                DiseaseCapability disease = MyReadAllConditionData.getDiseaseFromInstance(data.extraTag());
                GuiOpenWrapper.healthScreen().setDiseaseData(disease);
            } else if (operation == OperationType.SYN) {
                var entity = ClientAccessor.getLiving(data.entityID());
                if (entity != null && HealthCapability.has(entity)) {
                    HealthCapability.getAndApply(entity, health -> health.lightDeserializeNBT(data.tag()));
                }
            }
        }));
        context.setPacketHandled(true);
    }

    public static void handleServerConfigSYNData(final MyServerConfigSynData data, final Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> ModConfigs.loadServerData(data.configType(), data.tag()));
        context.setPacketHandled(true);
    }
}
