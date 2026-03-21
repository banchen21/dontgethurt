package com.lastimp.dgh.forge.entry;

import com.lastimp.dgh.common.entry.IEntry;
import com.lastimp.dgh.common.entry.IRegistryHandler;
import com.lastimp.dgh.common.entry.register.*;
import com.lastimp.dgh.common.menu.IMenuFactory;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.forge.menu.MenuFactoryNF;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RegistryHandlerNF implements IRegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Utils.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Utils.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Utils.MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Utils.MODID);
    public static final DeferredRegister<Potion> MOD_POTIONS = DeferredRegister.create(Registries.POTION, Utils.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Utils.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Utils.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Utils.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Utils.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSION = DeferredRegister.create(Registries.VILLAGER_PROFESSION, Utils.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Utils.MODID);

    @Override
    public void register() {
        ModBlocks.register();
        ModItems.register();
        ModEffects.register();
        ModPotions.register();
        ModSounds.register();
        ModEntities.register();
        ModMenus.register();
        ModCreativeModTabs.register();
        ModVillagers.register();
    }

    @Override
    public ResourceLocation itemID(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    @Override
    public IEntry<Block> registerBlock(String name, Supplier<Block> factory) {
        return cast(BLOCKS.register(name, factory));
    }

    @Override
    public <T extends BlockEntity> IEntry<BlockEntityType<T>> registerBlockEntity(String name, Supplier<BlockEntityType<?>> factory) {
        return cast(BLOCK_ENTITIES.register(name, factory));
    }

    @Override
    public <T extends Item> IEntry<T> registerItem(String name, Supplier<? extends Item> factory) {
        return cast(ITEMS.register(name, factory));
    }

    @Override
    public IEntry<MobEffect> registerEffect(String name, Supplier<MobEffect> factory) {
        return cast(MOB_EFFECTS.register(name, factory));
    }

    @Override
    public IEntry<Potion> registerPotion(String name, Supplier<Potion> factory) {
        return cast(MOD_POTIONS.register(name, factory));
    }

    @Override
    public IEntry<SoundEvent> registerSoundEvent(String name, Supplier<SoundEvent> factory) {
        return cast(SOUNDS.register(name, factory));
    }

    @Override
    public <T extends Entity> IEntry<EntityType<T>> registerEntityType(String name, Supplier<EntityType<?>> factory) {
        return cast(ENTITY_TYPES.register(name, factory));
    }

    @Override
    public <T extends MenuType<?>> IEntry<T> registerMenus(String name, IMenuFactory<? extends AbstractContainerMenu> factory) {
        return cast(MENU_TYPES.register(name, () -> new MenuType<>(new MenuFactoryNF<>(factory), FeatureFlags.DEFAULT_FLAGS)));
    }

    @Override
    public IEntry<CreativeModeTab> registerCreativeTabs(String name, Supplier<CreativeModeTab> factory) {
        return cast(CREATIVE_MODE_TABS.register(name, factory));
    }

    @Override
    public IEntry<VillagerProfession> registerVillagerProfession(String name, Supplier<VillagerProfession> factory) {
        return cast(VILLAGER_PROFESSION.register(name, factory));
    }

    @Override
    public IEntry<PoiType> registerPoiType(String name, Supplier<PoiType> factory) {
        return cast(POI_TYPES.register(name, factory));
    }

    @SuppressWarnings("unchecked")
    private static <T> IEntry<T> cast(RegistryObject<?> holder) {
        return new EntryNF<>((RegistryObject<T>) holder);
    }
}
