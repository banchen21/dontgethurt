package com.lastimp.dgh.common.event.eventHandler;

import com.lastimp.dgh.common.system.drug.DrugRuleEngine;
import net.minecraft.world.entity.player.Player;

public class DrugEventHandler {
    public static void onPlayerTick(Player player) {
        DrugRuleEngine.processPendingDoses(player);
    }
}
