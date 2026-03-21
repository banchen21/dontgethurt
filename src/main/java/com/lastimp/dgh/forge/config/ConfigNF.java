
package com.lastimp.dgh.forge.config;

import com.lastimp.dgh.common.config.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ConfigNF implements IConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder()
            .comment("General settings")
            .push("general_1.3.3");

    private static final ForgeConfigSpec.DoubleValue BODY_LIFE_FACTOR = BUILDER
            .comment("肢体血量系数")
            .defineInRange("BODY_LIFE_FACTOR", 1.0f, 0, 1000);

    private static final ForgeConfigSpec.BooleanValue TRADITION_HEALING = BUILDER
            .comment("启用伤口治疗，关闭生命盾")
            .define("TRADITION_HEALING", false);

    private static final ForgeConfigSpec.DoubleValue HEALTH_SHIELD_REDU = BUILDER
            .comment("生命盾衰减率(越大越慢)")
            .defineInRange("HEALTH_SHIELD_REDU", 60f, 1, Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue HEALING_FACTOR = BUILDER
            .comment("回血系数")
            .defineInRange("HEALING_FACTOR", 0.5f, 0, 1000);

    private static final ForgeConfigSpec.DoubleValue BANDAGE_ACC = BUILDER
            .comment("绷带回复增益系数")
            .defineInRange("BANDAGE_ACC", 2.0, 0, 10);

    private static final ForgeConfigSpec.DoubleValue BURN_BLEED_RATIO = BUILDER
            .comment("烧伤出血系数")
            .defineInRange("BURN_BLEED_RATIO", 0.5, 0, 10);

    private static final ForgeConfigSpec.DoubleValue INTERNAL_BLEED_RATIO = BUILDER
            .comment("内伤出血系数")
            .defineInRange("INTERNAL_BLEED_RATIO", 0.2, 0, 10);

    private static final ForgeConfigSpec.DoubleValue OPEN_WOUND_BLEED_RATIO = BUILDER
            .comment("开放伤出血系数")
            .defineInRange("OPEN_WOUND_BLEED_RATIO", 0.8, 0, 10);

    private static final ForgeConfigSpec.DoubleValue INTERNAL_FOOD_HEALING = BUILDER
            .comment("饱食度恢复系数")
            .defineInRange("INTERNAL_FOOD_HEALING", 4.0, 1.0, Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BLEED_VOLUME_RATIO = BUILDER
            .comment("出血-失血转化系数")
            .defineInRange("BLEED_VOLUME_RATIO", 0.005, 0, Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue WITHDRAW_RATIO = BUILDER
            .comment("成瘾-戒断转化系数")
            .defineInRange("WITHDRAW_RATIO", 0.03, 0, Float.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BASE_SELF_HEALING_TIME = BUILDER
            .comment("伤口自愈系数")
            .defineInRange("BASE_SELF_HEALING_TIME", 500, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BASE_MED_AVAILABLE_TIME = BUILDER
            .comment("药品有效时间系数")
            .defineInRange("BASE_MED_AVAILABLE_TIME", 100, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue VOLUME_SELF_HEALING_TIME = BUILDER
            .comment("出血自愈系数")
            .defineInRange("VOLUME_SELF_HEALING_TIME", 200, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue RESISTANCE_CONVERT_RATIO = BUILDER
            .comment("治愈-抗性转化系数")
            .defineInRange("RESISTANCE_CONVERT_RATIO", 0.02, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue RESISTANCE_MAX = BUILDER
            .comment("抗性上限")
            .defineInRange("RESISTANCE_MAX", 0.4, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue BASE_DISLOCATION_THRESHOLD = BUILDER
            .comment("伤口脱臼阈值")
            .defineInRange("BASE_DISLOCATION_THRESHOLD", 0.1, 0, 2.0);

    private static final ForgeConfigSpec.DoubleValue BASE_FRACTURE_THRESHOLD = BUILDER
            .comment("伤口骨折阈值")
            .defineInRange("BASE_FRACTURE_THRESHOLD", 0.25, 0, 2.0);

    private static final ForgeConfigSpec.DoubleValue BASE_DISLOCATION_MAX_PROB = BUILDER
            .comment("脱臼概率上限")
            .defineInRange("BASE_DISLOCATION_MAX_PROB", 0.8, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue BASE_FRACTURE_MAX_PROB = BUILDER
            .comment("骨折概率上限")
            .defineInRange("BASE_FRACTURE_MAX_PROB", 0.8, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue FRACTURE_ARTERIAL_PROB = BUILDER
            .comment("骨折-动脉出血概率")
            .defineInRange("FRACTURE_ARTERIAL_PROB", 0.1, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue FRACTURE_BLOOD_RATIO = BUILDER
            .comment("动脉出血-失血速度")
            .defineInRange("FRACTURE_ARTERIAL_PROB", 0.015, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue BASE_PNEUMOTHORAX_PROB = BUILDER
            .comment("气胸概率")
            .defineInRange("BASE_PNEUMOTHORAX_PROB", 0.05, 0, 1);

    private static final ForgeConfigSpec.DoubleValue BASE_AMPUTATION_THRESHOLD = BUILDER
            .comment("截肢阈值")
            .defineInRange("BASE_AMPUTATION_THRESHOLD", 0.05, 0, 2.0);

    private static final ForgeConfigSpec.DoubleValue BASE_AMPUTATION_MAX_PROB = BUILDER
            .comment("截肢最大概率")
            .defineInRange("BASE_AMPUTATION_MAX_PROB", 0.3, 0, 1.0);

    private static final ForgeConfigSpec.BooleanValue ALLOW_DOWN = BUILDER
            .comment("允许濒死倒地")
            .define("ALLOW_DOWN", true);

    private static final ForgeConfigSpec.BooleanValue PLAYER_DOCTOR_HEALING = BUILDER
            .comment("允许医生村民治疗")
            .define("PLAYER_DOCTOR_HEALING", true);

    private static final ForgeConfigSpec.DoubleValue BYPASS_BRAIN_DAMAGE_PROB = BUILDER
            .comment("头部贯穿伤-脑损伤概率")
            .defineInRange("BYPASS_BRAIN_DAMAGE_PROB", 0.7, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue BYPASS_FOREIGN_PROB = BUILDER
            .comment("贯穿伤-体内异物概率")
            .defineInRange("BYPASS_FOREIGN_PROB", 0.8, 0, 1.0);

    private static final ForgeConfigSpec.BooleanValue PLAYER_GLOWING = BUILDER
            .comment("允许玩家倒地发光")
            .define("DOWN_GLOWING", true);

    private static final ForgeConfigSpec.BooleanValue PLAYER_DOWN_MOVING = BUILDER
            .comment("允许玩家倒地爬行")
            .define("PLAYER_DOWN_MOVING", true);

    private static final ForgeConfigSpec.IntValue DAMAGE_PART_STRICK_LEVEL = BUILDER
            .comment("伤害部位严格等级：0-随机部位，1-少量部位约束，2-大量部位约束，3-严格部位")
            .defineInRange("DAMAGE_PART_STRICK_LEVEL", 2, 0, 3);

    private static final ForgeConfigSpec.BooleanValue LIMITED_BODY_PART_VITALITY_LOST = BUILDER
            .comment("受限的部位生命损伤")
            .define("LIMITED_BODY_PART_VITALITY_LOST", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_LIVING_EFFECT = BUILDER
            .comment("启用长生久视buff")
            .define("ENABLE_LIVING_EFFECT", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_SELF_SUICIDE = BUILDER
            .comment("启用放弃治疗")
            .define("ENABLE_SELF_SUICIDE", true);

    private static final ForgeConfigSpec.IntValue SMALL_CONDITION_X = BUILDER
            .comment("健康小人ui的x位置")
            .defineInRange("SMALL_CONDITION_X", 308, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue SMALL_CONDITION_Y = BUILDER
            .comment("健康小人ui的y位置")
            .defineInRange("SMALL_CONDITION_Y", 214, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue SMALL_CONDITION_DISAPPEAR_DELAY = BUILDER
            .comment("健康小人ui的渐隐时间(-1为永不消失, -2为永不显示)")
            .defineInRange("SMALL_CONDITION_DISAPPEAR_DELAY", 3, -2, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ARMOR_RECALCULATE = BUILDER
            .comment("启用改型护甲计算")
            .define("ARMOR_RECALCULATE", true);

    private static final ForgeConfigSpec.DoubleValue BLOCK_RECOVER_DELAY = BUILDER
            .comment("护甲格挡恢复延迟")
            .defineInRange("BLOCK_RECOVER_DELAY", 5, 0, Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BLOCK_RECOVER_SPEED = BUILDER
            .comment("护甲格挡恢复速度")
            .defineInRange("BLOCK_RECOVER_SPEED", 0.1, 0, 1.0);

    private static final ForgeConfigSpec.BooleanValue ENABLE_SURVIVAL_STATUS = BUILDER
            .comment("启用生存状态扩展框架")
            .define("ENABLE_SURVIVAL_STATUS", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_HYDRATION_SYSTEM = BUILDER
            .comment("启用水分系统")
            .define("ENABLE_HYDRATION_SYSTEM", true);

    private static final ForgeConfigSpec.DoubleValue HYDRATION_DECAY_BASE = BUILDER
            .comment("基础水分衰减(每秒)")
            .defineInRange("HYDRATION_DECAY_BASE", 0.0012, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue HYDRATION_DECAY_SPRINT = BUILDER
            .comment("冲刺额外水分衰减(每秒)")
            .defineInRange("HYDRATION_DECAY_SPRINT", 0.0015, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue HYDRATION_RECOVER_IN_WATER = BUILDER
            .comment("雨水/涉水环境水分恢复(每秒)")
            .defineInRange("HYDRATION_RECOVER_IN_WATER", 0.001, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue HYDRATION_LOW_THRESHOLD = BUILDER
            .comment("水分偏低阈值")
            .defineInRange("HYDRATION_LOW_THRESHOLD", 0.25, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue HYDRATION_CRITICAL_THRESHOLD = BUILDER
            .comment("水分危险阈值")
            .defineInRange("HYDRATION_CRITICAL_THRESHOLD", 0.1, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue LOW_CARBOHYDRATE_BLINDNESS_THRESHOLD = BUILDER
            .comment("低糖触发失明阈值")
            .defineInRange("LOW_CARBOHYDRATE_BLINDNESS_THRESHOLD", 0.20, 0, 1.0);

    private static final ForgeConfigSpec.IntValue LOW_CARBOHYDRATE_BLINDNESS_INTERVAL = BUILDER
            .comment("低糖失明触发间隔（ticks）")
            .defineInRange("LOW_CARBOHYDRATE_BLINDNESS_INTERVAL", 120, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue HIGH_CARBOHYDRATE_THRESHOLD = BUILDER
            .comment("高糖阈值")
            .defineInRange("HIGH_CARBOHYDRATE_THRESHOLD", 0.80, 0, 1.0);

    private static final ForgeConfigSpec.DoubleValue HIGH_CARBOHYDRATE_JUMP_HYDRATION_PENALTY = BUILDER
            .comment("高糖跳跃额外失水")
            .defineInRange("HIGH_CARBOHYDRATE_JUMP_HYDRATION_PENALTY", 0.015, 0, 1.0);

    private static final ForgeConfigSpec.IntValue DRUG_CAPSULE_COOLDOWN_TICKS = BUILDER
            .comment("胶囊服药冷却（ticks）")
            .defineInRange("DRUG_CAPSULE_COOLDOWN_TICKS", 6000, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DRUG_CAPSULE_DELAY_TICKS = BUILDER
            .comment("胶囊延迟生效（ticks）")
            .defineInRange("DRUG_CAPSULE_DELAY_TICKS", 2400, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DRUG_BLOCKER_DURATION_TICKS = BUILDER
            .comment("阻断剂持续时长（ticks）")
            .defineInRange("DRUG_BLOCKER_DURATION_TICKS", 2400, 20, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue DRUG_NEEDLE_CONTAMINATION_CHANCE = BUILDER
            .comment("污染药针感染概率")
            .defineInRange("DRUG_NEEDLE_CONTAMINATION_CHANCE", 0.35, 0.0, 1.0);

        private static final ForgeConfigSpec.DoubleValue DRUG_LAMIVUDINE_CURE_CHANCE = BUILDER
            .comment("拉米夫定胶囊治愈 AIDS 的概率（0.0 - 1.0）")
            .defineInRange("DRUG_LAMIVUDINE_CURE_CHANCE", 0.10, 0.0, 1.0);

    private static final ForgeConfigSpec.IntValue GIVE_UP_TIMEOUT_SECONDS = BUILDER
            .comment("放弃治疗所需持续时间（秒）")
            .defineInRange("GIVE_UP_TIMEOUT_SECONDS", 5, 1, 60);

    // 构建配置
    public static final ForgeConfigSpec SPEC = BUILDER.pop().build();

    @Override
    public Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public float BODY_LIFE_FACTOR() {
        return (float) (double) BODY_LIFE_FACTOR.get();
    }

    @Override
    public boolean TRADITION_HEALING() {
        return TRADITION_HEALING.get();
    }

    @Override
    public float HEALTH_SHIELD_REDU() {
        return (float) (double) HEALTH_SHIELD_REDU.get();
    }

    @Override
    public float HEALING_FACTOR() {
        return (float) (double) HEALING_FACTOR.get();
    }

    @Override
    public float BANDAGE_ACC() {
        return (float) (double) BANDAGE_ACC.get();
    }

    @Override
    public float BURN_BLEED_RATIO() {
        return (float) (double) BURN_BLEED_RATIO.get();
    }

    @Override
    public float INTERNAL_BLEED_RATIO() {
        return (float) (double) INTERNAL_BLEED_RATIO.get();
    }

    @Override
    public float OPEN_WOUND_BLEED_RATIO() {
        return (float) (double) OPEN_WOUND_BLEED_RATIO.get();
    }

    @Override
    public float INTERNAL_FOOD_HEALING() {
        return (float) (double) INTERNAL_FOOD_HEALING.get();
    }

    @Override
    public float BLEED_VOLUME_RATIO() {
        return (float) (double) BLEED_VOLUME_RATIO.get();
    }

    @Override
    public float WITHDRAW_RATIO() {
        return (float) (double) WITHDRAW_RATIO.get();
    }

    @Override
    public float BASE_SELF_HEALING_TIME() {
        return BASE_SELF_HEALING_TIME.get();
    }

    @Override
    public float BASE_MED_AVAILABLE_TIME() {
        return BASE_MED_AVAILABLE_TIME.get();
    }

    @Override
    public float VOLUME_SELF_HEALING_TIME() {
        return VOLUME_SELF_HEALING_TIME.get();
    }

    @Override
    public float RESISTANCE_CONVERT_RATIO() {
        return (float) (double) RESISTANCE_CONVERT_RATIO.get();
    }

    @Override
    public float RESISTANCE_MAX() {
        return (float) (double) RESISTANCE_MAX.get();
    }

    @Override
    public float BASE_DISLOCATION_THRESHOLD() {
        return (float) (double) BASE_DISLOCATION_THRESHOLD.get();
    }

    @Override
    public float BASE_FRACTURE_THRESHOLD() {
        return (float) (double) BASE_FRACTURE_THRESHOLD.get();
    }

    @Override
    public float BASE_DISLOCATION_MAX_PROB() {
        return (float) (double) BASE_DISLOCATION_MAX_PROB.get();
    }

    @Override
    public float BASE_FRACTURE_MAX_PROB() {
        return (float) (double) BASE_FRACTURE_MAX_PROB.get();
    }

    @Override
    public float FRACTURE_ARTERIAL_PROB() {
        return (float) (double) FRACTURE_ARTERIAL_PROB.get();
    }

    @Override
    public float FRACTURE_BLOOD_RATIO() {
        return (float) (double) FRACTURE_BLOOD_RATIO.get();
    }

    @Override
    public float BASE_PNEUMOTHORAX_PROB() {
        return (float) (double) BASE_PNEUMOTHORAX_PROB.get();
    }

    @Override
    public float BASE_AMPUTATION_THRESHOLD() {
        return (float) (double) BASE_AMPUTATION_THRESHOLD.get();
    }

    @Override
    public float BASE_AMPUTATION_MAX_PROB() {
        return (float) (double) BASE_AMPUTATION_MAX_PROB.get();
    }

    @Override
    public boolean ALLOW_DOWN() {
        return ALLOW_DOWN.get();
    }

    @Override
    public boolean PLAYER_DOCTOR_HEALING() {
        return PLAYER_DOCTOR_HEALING.get();
    }

    @Override
    public float BYPASS_BRAIN_DAMAGE_PROB() {
        return (float) (double) BYPASS_BRAIN_DAMAGE_PROB.get();
    }

    @Override
    public float BYPASS_FOREIGN_PROB() {
        return (float) (double) BYPASS_FOREIGN_PROB.get();
    }

    @Override
    public boolean PLAYER_GLOWING() {
        return PLAYER_GLOWING.get();
    }

    @Override
    public boolean PLAYER_DOWN_MOVING() {
        return PLAYER_DOWN_MOVING.get();
    }

    @Override
    public int DAMAGE_PART_STRICK_LEVEL() {
        return DAMAGE_PART_STRICK_LEVEL.get();
    }

    @Override
    public boolean LIMITED_BODY_PART_VITALITY_LOST() {
        return LIMITED_BODY_PART_VITALITY_LOST.get();
    }

    @Override
    public boolean ENABLE_LIVING_EFFECT() {
        return ENABLE_LIVING_EFFECT.get();
    }

    @Override
    public boolean ENABLE_SELF_SUICIDE() {
        return ENABLE_SELF_SUICIDE.get();
    }

    @Override
    public int SMALL_CONDITION_X() {
        return SMALL_CONDITION_X.get();
    }

    @Override
    public int SMALL_CONDITION_Y() {
        return SMALL_CONDITION_Y.get();
    }

    @Override
    public int SMALL_CONDITION_DISAPPEAR_DELAY() {
        return SMALL_CONDITION_DISAPPEAR_DELAY.get();
    }

    @Override
    public boolean ARMOR_RECALCULATE() {
        return ARMOR_RECALCULATE.get();
    }

    @Override
    public float BLOCK_RECOVER_DELAY() {
        return (float) (double) BLOCK_RECOVER_DELAY.get();
    }

    @Override
    public float BLOCK_RECOVER_SPEED() {
        return (float) (double) BLOCK_RECOVER_SPEED.get();
    }

    @Override
    public boolean ENABLE_SURVIVAL_STATUS() {
        return ENABLE_SURVIVAL_STATUS.get();
    }

    @Override
    public boolean ENABLE_HYDRATION_SYSTEM() {
        return ENABLE_HYDRATION_SYSTEM.get();
    }

    @Override
    public float HYDRATION_DECAY_BASE() {
        return (float) (double) HYDRATION_DECAY_BASE.get();
    }

    @Override
    public float HYDRATION_DECAY_SPRINT() {
        return (float) (double) HYDRATION_DECAY_SPRINT.get();
    }

    @Override
    public float HYDRATION_RECOVER_IN_WATER() {
        return (float) (double) HYDRATION_RECOVER_IN_WATER.get();
    }

    @Override
    public float HYDRATION_LOW_THRESHOLD() {
        return (float) (double) HYDRATION_LOW_THRESHOLD.get();
    }

    @Override
    public float HYDRATION_CRITICAL_THRESHOLD() {
        return (float) (double) HYDRATION_CRITICAL_THRESHOLD.get();
    }

    @Override
    public float LOW_CARBOHYDRATE_BLINDNESS_THRESHOLD() {
        return (float) (double) LOW_CARBOHYDRATE_BLINDNESS_THRESHOLD.get();
    }

    @Override
    public int LOW_CARBOHYDRATE_BLINDNESS_INTERVAL() {
        return LOW_CARBOHYDRATE_BLINDNESS_INTERVAL.get();
    }

    @Override
    public float HIGH_CARBOHYDRATE_THRESHOLD() {
        return (float) (double) HIGH_CARBOHYDRATE_THRESHOLD.get();
    }

    @Override
    public float HIGH_CARBOHYDRATE_JUMP_HYDRATION_PENALTY() {
        return (float) (double) HIGH_CARBOHYDRATE_JUMP_HYDRATION_PENALTY.get();
    }

    @Override
    public int DRUG_CAPSULE_COOLDOWN_TICKS() {
        return DRUG_CAPSULE_COOLDOWN_TICKS.get();
    }

    @Override
    public int DRUG_CAPSULE_DELAY_TICKS() {
        return DRUG_CAPSULE_DELAY_TICKS.get();
    }

    @Override
    public int DRUG_BLOCKER_DURATION_TICKS() {
        return DRUG_BLOCKER_DURATION_TICKS.get();
    }

    @Override
    public float DRUG_NEEDLE_CONTAMINATION_CHANCE() {
        return (float) (double) DRUG_NEEDLE_CONTAMINATION_CHANCE.get();
    }

    @Override
    public float DRUG_LAMIVUDINE_CURE_CHANCE() {
        return (float) (double) DRUG_LAMIVUDINE_CURE_CHANCE.get();
    }

    @Override
    public int GIVE_UP_TIMEOUT_SECONDS() {
        return GIVE_UP_TIMEOUT_SECONDS.get();
    }

}