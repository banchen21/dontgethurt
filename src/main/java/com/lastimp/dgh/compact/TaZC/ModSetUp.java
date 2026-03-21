package com.lastimp.dgh.compact.TaZC;

import com.lastimp.dgh.common.utils.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Utils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetUp {
    @SubscribeEvent
    public static void onAttachCapabilities(final FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("tazc")) return;
        MinecraftForge.EVENT_BUS.addListener(TaCZDiseaseHandler::onLivingHurt);
    }
}