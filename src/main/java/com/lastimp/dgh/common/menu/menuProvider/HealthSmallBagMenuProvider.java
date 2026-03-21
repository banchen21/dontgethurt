package com.lastimp.dgh.common.menu.menuProvider;

import com.lastimp.dgh.common.menu.BagMenu;
import com.lastimp.dgh.common.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HealthSmallBagMenuProvider implements MenuProvider {
    protected final ItemStack bagStack;

    public HealthSmallBagMenuProvider(ItemStack bagStack) {
        this.bagStack = bagStack;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui." + Utils.MODID + ".health_small_bag");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BagMenu.HealthSmallBag(i, inventory, bagStack);
    }

    public static class MedicineSmallBagMenuProvider extends HealthSmallBagMenuProvider {
        public MedicineSmallBagMenuProvider(ItemStack bagStack) {
            super(bagStack);
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new BagMenu.MedicineSmallBag(i, inventory, this.bagStack);
        }
    }
}
