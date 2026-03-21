package com.lastimp.dgh.forge.event.eventBus;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.forge.container.BackpackInventoryNF;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Utils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HealthCapability.class);
        event.register(NutrientCapability.class);
        event.register(DiseaseCapability.class);
        event.register(BackpackInventoryNF.class);
    }
}