package com.lastimp.dgh.compact.TaZC;

import com.lastimp.dgh.common.system.disease.DiseaseManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * M4 TaCZ 联动：子弹伤害 → 疾病触发。
 * <ul>
 *   <li>子弹创伤 → 脓毒症（概率随伤害量线性上涨，上限 30%）</li>
 *   <li>子弹创伤 → 上呼吸道感染进度加速（8% 概率）</li>
 * </ul>
 * 通过类名前缀 {@code com.tacz.} 识别 TaCZ 子弹，避免硬编码类引用。
 */
public class TaCZDiseaseHandler {

    private static final DiseaseManager DISEASE_MANAGER = new DiseaseManager();

    private TaCZDiseaseHandler() {}

    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var source = event.getSource();
        var direct = source.getDirectEntity();
        if (direct == null) return;

        // 通过类名前缀判断是否来自 TaCZ 子弹/射击实体，避免硬依赖类引用
        if (!direct.getClass().getName().startsWith("com.tacz.")) return;

        float damage = event.getAmount();
        if (damage < 1.0f) return;

        // 高速子弹伤口 → 脓毒症（概率随伤害量线性增加，上限 30%）
        float sepsisChance = Math.min(0.30f, damage * 0.02f);
        if (player.getRandom().nextFloat() < sepsisChance) {
            DISEASE_MANAGER.triggerDisease(player, "sepsis");
        }

        // 子弹创伤 → 上呼吸道感染进度加速（炎症应激反应）
        if (player.getRandom().nextFloat() < 0.08f) {
            DISEASE_MANAGER.triggerDisease(player, "upper_respiratory_infection");
        }
    }
}
