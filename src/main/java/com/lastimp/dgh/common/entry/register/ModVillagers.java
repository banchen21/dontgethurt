package com.lastimp.dgh.common.entry.register;

import com.google.common.collect.ImmutableSet;
import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.entry.IEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;
import java.util.Set;

public class ModVillagers {
    public static final IEntry<PoiType> DOCTOR_POI = PlatformService.REGISTRY_HANDLER.registerPoiType("doctor_poi",
            () -> new PoiType(
                    Set.copyOf(ModBlocks.OPERATING_BED_BLOCK.get().getStateDefinition().getPossibleStates()),
                    1, 1
            )
    );

    public static final IEntry<VillagerProfession> DOCTOR_MAKER = PlatformService.REGISTRY_HANDLER.registerVillagerProfession("doctor",
            () -> new VillagerProfession(
                    "doctor",
                    holder -> holder.is(DOCTOR_POI.getKey()),
                    holder -> holder.is(DOCTOR_POI.getKey()),
                    ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_BUTCHER
            )
    );

    public static void addCustomTrades(VillagerProfession type, Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
        if (type == DOCTOR_MAKER.get()) {
            List<VillagerTrades.ItemListing> level1Trades = trades.get(1);
            List<VillagerTrades.ItemListing> level2Trades = trades.get(2);
            List<VillagerTrades.ItemListing> level3Trades = trades.get(3);
            List<VillagerTrades.ItemListing> level4Trades = trades.get(4);
            List<VillagerTrades.ItemListing> level5Trades = trades.get(5);

            // 1级交易：绷带、血包、石膏
            level1Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.WHITE_WOOL, 18), // 玩家出售的物品
                    new ItemStack(Items.EMERALD, 1), // 玩家获得的物品
                    16, 2, 0.05F
            ));
            level1Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.POPPY, 8), // 玩家出售的物品
                    new ItemStack(Items.EMERALD, 1), // 玩家获得的物品
                    16, 2, 0.05F
            ));
            level1Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), // 玩家出售的物品
                    new ItemStack(ModItems.BANDAGE.get(), 3), // 玩家获得的物品
                    16, 1, 0.05F
            ));
            level1Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2), // 玩家出售的物品
                    new ItemStack(ModItems.BLOOD_PACK_EMPTY.get(), 1), // 玩家获得的物品
                    new ItemStack(ModItems.BLOOD_PACK.get(), 1), // 玩家获得的物品
                    16, 1, 0.2F
            ));
            level1Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3), // 玩家出售的物品
                    new ItemStack(ModItems.GYPSUM.get(), 1), // 玩家获得的物品
                    16, 1, 0.2F
            ));

            // 2级交易：消毒剂、吗啡、缝合线、右美沙芬胶囊、布洛芬胶囊
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(ModItems.DEXTROMETHORPHAN.get(), 2),
                    12, 5, 0.2F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3),
                    new ItemStack(ModItems.IBUPROFEN.get(), 2),
                    12, 5, 0.2F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.CHARCOAL, 26), // 玩家出售的物品
                    new ItemStack(Items.EMERALD, 1), // 玩家获得的物品
                    16, 10, 0.05F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.SPIDER_EYE, 4), // 玩家出售的物品
                    new ItemStack(Items.EMERALD, 1), // 玩家获得的物品
                    16, 10, 0.05F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3), // 玩家出售的物品
                    new ItemStack(ModItems.ANTISEPTIC.get(), 1), // 玩家获得的物品
                    16, 5, 0.2F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 4), // 玩家出售的物品
                    new ItemStack(ModItems.MORPHINE.get(), 1), // 玩家获得的物品
                    16, 5, 0.2F
            ));
            level2Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2), // 玩家出售的物品
                    new ItemStack(ModItems.SUTURE.get(), 6), // 玩家获得的物品
                    16, 5, 0.05F
            ));

            // 3级交易：气胸针、止血带、广谱抗生素、骨骼植入物、利巴韦林注射、镇静剂
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.RIBAVIRIN.get(), 1),
                    8, 10, 0.2F
            ));
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModItems.SEDATIVE.get(), 2),
                    10, 10, 0.2F
            ));
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ModItems.NEEDLE.get(), 2),
                    10, 10, 0.05F
            ));
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(ModItems.TOURNIQUET.get(), 1),
                    10, 10, 0.2F
            ));
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 7),
                    new ItemStack(ModItems.ANTIBIOTICS.get(), 1),
                    10, 10, 0.2F
            ));
            level3Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModItems.BONE_IMPLANTS.get(), 1),
                    10, 10, 0.2F
            ));

            // 4级交易：引流管、医用支架、肾上腺素、吗啡酮、阻断剂、拉米夫定
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(ModItems.BLOCKER.get(), 1),
                    6, 15, 0.2F
            ));
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(ModItems.LAMIVUDINE_CAPSULE.get(), 1),
                    6, 15, 0.2F
            ));
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ModItems.NEEDLE.get(), 2),
                    10, 15, 0.05F
            ));
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.MEDICAL_STENT.get(), 1),
                    10, 15, 0.2F
            ));
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(ModItems.ADRENALINE.get(), 1),
                    10, 15, 0.2F
            ));
            level4Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(ModItems.NALOXONE.get(), 1),
                    10, 15, 0.2F
            ));

            // 5级交易：停滞袋、甘露醇
            level5Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 32),
                    new ItemStack(ModItems.STASIS_BAG.get(), 1),
                    4, 30, 0.2F
            ));
            level5Trades.add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 20),
                    new ItemStack(ModItems.MANNITOL.get(), 1),
                    4, 30, 0.2F
            ));
        }
    }

    public static void register() {
    }
}
