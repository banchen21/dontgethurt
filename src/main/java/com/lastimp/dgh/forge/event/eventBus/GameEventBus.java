package com.lastimp.dgh.forge.event.eventBus;

import com.lastimp.dgh.common.buffs.buff.KeepLivingEffect;
import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.capability.NutrientCapability;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractArm;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractLeg;
import com.lastimp.dgh.common.utils.command.Command;
import com.lastimp.dgh.common.entry.register.ModBlocks;
import com.lastimp.dgh.common.entry.register.ModVillagers;
import com.lastimp.dgh.common.event.eventHandler.DiseaseEventHandler;
import com.lastimp.dgh.common.event.eventHandler.LivingEntityEventHandler;
import com.lastimp.dgh.common.event.eventHandler.NutrientEventHandler;
import com.lastimp.dgh.common.event.eventHandler.PlayerEventHandler;
import com.lastimp.dgh.common.event.eventHandler.VillagerEventHandler;
import com.lastimp.dgh.common.item.bases.AbstractSmallBag;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.forge.capability.provider.BagItemInventoryProvider;
import com.lastimp.dgh.forge.capability.provider.DiseaseProvider;
import com.lastimp.dgh.forge.capability.provider.HealthProvider;
import com.lastimp.dgh.forge.capability.provider.NutrientProvider;
import com.lastimp.dgh.forge.container.BackpackInventoryNF;
import com.lastimp.dgh.forge.entry.register.ModCapabilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = Utils.MODID)
public class GameEventBus {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Command.onRegisterCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onHealthAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity livingEntity && HealthCapability.has(livingEntity)) {
            event.addCapability(ModCapabilities.HEALTH_RL, new HealthProvider());
        }
    }

    @SubscribeEvent
    public static void onNutrientAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && NutrientCapability.has(player)) {
            event.addCapability(ModCapabilities.NUTRIENT_RL, new NutrientProvider());
        }
    }

    @SubscribeEvent
    public static void onDiseaseAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && DiseaseCapability.has(player)) {
            event.addCapability(ModCapabilities.DISEASE_RL, new DiseaseProvider());
        }
    }

    @SubscribeEvent
    public static void onBagAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        var stack = event.getObject();
        if (stack.getItem() instanceof AbstractSmallBag bag) {
            var inv = new BackpackInventoryNF(9);
            var provider = new BagItemInventoryProvider(inv, stack);
            bag.initBag(inv);
            event.addCapability(ModCapabilities.BAG_INV_RL, provider);
        }
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(
                Registries.POINT_OF_INTEREST_TYPE,
                helper -> {
                    for (var state : ModBlocks.OPERATING_BED_BLOCK.get().getStateDefinition().getPossibleStates()) {
                        GameData.getBlockStatePointOfInterestTypeMap().put(state, ModVillagers.DOCTOR_POI.get());
                    }
                });
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        ModVillagers.addCustomTrades(event.getType(), event.getTrades());
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity))
            return;
        LivingEntityEventHandler.addOrgan(livingEntity);

        if (!(livingEntity instanceof Villager villager))
            return;
        VillagerEventHandler.addBrain(villager);
    }

    @SubscribeEvent
    public static void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEventHandler.logIn(event.getEntity());
    }

    @SubscribeEvent
    public static void logOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerEventHandler.logOut(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        DiseaseEventHandler.onRainAction(event.getEntity());
        var result = PlayerEventHandler.onPlayerInteractEntity(event.getEntity(), event.getTarget(), event.getHand());
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        var player = event.getEntity();
        if (player == null) return;
        try {
            var stack = event.getItemStack();
            if (stack.isEdible()) {
                boolean canEat = com.lastimp.dgh.common.capability.HealthCapability.getAndApply(player, h -> h.canEat(), true);
                if (!canEat) {
                    event.setCanceled(true);
                    event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                }
            }
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEventHandler.onPlayerRespawn(event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntityEventHandler.tickPre(event.getEntity());
        if (event.getEntity() instanceof Player player) {
            NutrientEventHandler.onPlayerTick(player);
            DiseaseEventHandler.onPlayerTick(player);
        }
    }

    @SubscribeEvent
    public static void onFoodUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player) {
            NutrientEventHandler.onFoodUseStart(player, event.getItem());
        }
    }

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            NutrientEventHandler.onFoodEaten(player);
        }
    }

    @SubscribeEvent
    public static void onFoodUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            NutrientEventHandler.onFoodUseStop(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        NutrientEventHandler.onPlayerWakeUp(event.getEntity());
        DiseaseEventHandler.onPlayerWakeUp(event.getEntity());
    }

    @SubscribeEvent
    public static void onBreath(LivingBreatheEvent event) {
        if (event.canBreathe())
            event.setCanBreathe(LivingEntityEventHandler.onBreath(event.getEntity()));
    }

    @SubscribeEvent
    public static void onInjury(LivingDamageEvent event) {
        var damage = LivingEntityEventHandler.onInjury(event.getEntity(), event.getSource(), event.getAmount());
        event.setAmount(damage);
        if (event.getEntity() instanceof Player player) {
            NutrientEventHandler.onPlayerDamaged(player, damage);
            DiseaseEventHandler.onPlayerDamage(player, event.getSource(), damage);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        var ent = event.getEntity();
        if (!(ent instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) ent;
        if (!HealthCapability.has(livingEntity)) return;
        HealthCapability.getAndApply(livingEntity, HealthCapability::refreshArmor);
    }

    @SubscribeEvent
    public static void onHealing(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player
                && NutrientEventHandler.shouldBlockNaturalHealing(player, event.getAmount())) {
            event.setCanceled(true);
            return;
        }
        LivingEntityEventHandler.onHealing(event.getEntity(), event.getAmount());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            DiseaseEventHandler.onPlayerDeath(player);
        }
        LivingEntityEventHandler.onLivingDeath(event.getEntity());
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        float modifier = KeepLivingEffect.onBreakSpeed(event.getEntity());
        modifier *= AbstractArm.onBreakSpeed(event.getEntity());
        event.setNewSpeed(event.getOriginalSpeed() * modifier);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        int safeDistance = AbstractLeg.onFall(event.getEntity());
        event.setDistance(Math.max(0, event.getDistance() - safeDistance));
        if (event.getEntity() instanceof Player player) {
            DiseaseEventHandler.onPlayerFall(player, event.getDistance());
        }
    }
}
