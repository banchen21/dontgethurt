package com.lastimp.dgh.common.config;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.config.impl.ArmorList;
import com.lastimp.dgh.common.config.impl.HealthLivingEntityList;
import com.lastimp.dgh.common.config.impl.NutrientFoodList;
import com.lastimp.dgh.common.config.impl.PlayerBlackList;
import com.lastimp.dgh.common.network.message.MyServerConfigSynData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class ModConfigs {

    private static final Map<Type, IConfigLoader<?>> configs = new HashMap<>();

    static {
        configs.put(Type.HEALTH, new HealthLivingEntityList());
        configs.put(Type.BLACKLIST, new PlayerBlackList());
        configs.put(Type.ARMOR, new ArmorList());
        configs.put(Type.NUTRIENT_FOOD, new NutrientFoodList());
    }

    public static CompoundTag getCompound(Type type) {
        return configs.get(type).getConfig();
    }

    public static void loadExternalList() {
        configs.values().forEach(IConfigLoader::loadExternalList);
    }

    public static void loadServerData(Type type, CompoundTag data) {
        configs.get(type).loadServerData(data);
    }

    public static void synToPlayer(ServerPlayer player) {
        configs.keySet().forEach(type -> PlatformService.NETWORK.sendToPlayer(player, MyServerConfigSynData.getInstance(type)));
    }

    public enum Type {
        HEALTH, BLACKLIST, ARMOR, NUTRIENT_FOOD
    }
}
