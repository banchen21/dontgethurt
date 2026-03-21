package com.lastimp.dgh.common.menu.menuProvider;

import com.lastimp.dgh.common.menu.HealthMenu;
import com.lastimp.dgh.common.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HealthMenuProvider implements MenuProvider {
    public final UUID target;
    public final boolean isDevice;

    public HealthMenuProvider(UUID target, boolean isDevice) {
        this.target = target;
        this.isDevice = isDevice;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui." + Utils.MODID + ".health_menu");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new HealthMenu(i, inventory, target, isDevice);
    }
}
