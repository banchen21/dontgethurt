package com.lastimp.dgh.compact;

import com.lastimp.dgh.common.utils.Utils;
import net.minecraftforge.fml.ModList;

/**
 * M4 联动兼容注册中心：集中管理可选模组检测，提供功能开关标志位。
 * {@link #init()} 在 FMLCommonSetupEvent 最早期调用，供其他系统条件判断。
 */
public final class CompatRegistry {

    /** TaCZ（枪械模组）是否已加载 */
    public static boolean TAZC_LOADED = false;
    /** 东方小女仆是否已加载 */
    public static boolean TLM_LOADED = false;
    /** Patchouli（图书指南）是否已加载 */
    public static boolean PATCHOULI_LOADED = false;
    /** 东方小女仆-法术扩展是否已加载 */
    public static boolean TLM_SPELL_LOADED = false;
    /** 通用机械是否已加载（辐射来源之一） */
    public static boolean MEKANISM_LOADED = false;
    /** Alex's Caves 是否已加载（辐射来源之一） */
    public static boolean ALEXS_CAVES_LOADED = false;
    /** 核科学是否已加载（辐射来源之一） */
    public static boolean NUCLEAR_SCIENCE_LOADED = false;

    private CompatRegistry() {}

    /**
     * 探测联动模组并记录启动日志。
     * 在 {@code DontGetHurt.commonSetup()} 中调用。
     */
    public static void init() {
        TAZC_LOADED      = ModList.get().isLoaded("tazc");
        TLM_LOADED       = ModList.get().isLoaded("touhou_little_maid");
        PATCHOULI_LOADED = ModList.get().isLoaded("patchouli");
        TLM_SPELL_LOADED = ModList.get().isLoaded("touhou_little_maid_spell");
        MEKANISM_LOADED = ModList.get().isLoaded("mekanism");
        ALEXS_CAVES_LOADED = ModList.get().isLoaded("alexscaves");
        NUCLEAR_SCIENCE_LOADED = ModList.get().isLoaded("nuclearscience");

        Utils.LOGGER.info("[DGH Compat] tazc={} touhou_little_maid={} patchouli={} tlm_spell={} mekanism={} alexscaves={} nuclearscience={}",
            TAZC_LOADED, TLM_LOADED, PATCHOULI_LOADED, TLM_SPELL_LOADED,
            MEKANISM_LOADED, ALEXS_CAVES_LOADED, NUCLEAR_SCIENCE_LOADED);

        if (!TAZC_LOADED) {
            Utils.LOGGER.info("[DGH Compat] TaCZ 未加载，枪伤疾病触发已禁用");
        }
        if (!TLM_LOADED) {
            Utils.LOGGER.info("[DGH Compat] 东方小女仆 未加载，女仆联动功能已禁用");
        }
        if (!hasAnyRadiationMod()) {
            Utils.LOGGER.info("[DGH Compat] 未检测到辐射联动模组，绯红症辐射触发仅保留通用识别");
        }
    }

    public static boolean hasAnyRadiationMod() {
        return MEKANISM_LOADED || ALEXS_CAVES_LOADED || NUCLEAR_SCIENCE_LOADED;
    }
}
