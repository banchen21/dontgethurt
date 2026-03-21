package com.lastimp.dgh.forge.data;

import com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition;
import com.lastimp.dgh.common.client.hotkey.KeyBinding;
import com.lastimp.dgh.common.entry.IEntry;
import com.lastimp.dgh.common.entry.register.ModEffects;
import com.lastimp.dgh.common.entry.register.ModItems;
import com.lastimp.dgh.common.entry.register.ModPotions;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;

import static com.lastimp.dgh.common.tags.ModDamageType.*;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.dgh", "dgh");

        this.add(BodyCondition.BURN.toString(), "烧伤");
        this.add(BodyCondition.INTERNAL_INJURY.toString(), "内伤");
        this.add(BodyCondition.OPEN_WOUND.toString(), "撕裂伤");
        this.add(BodyCondition.PASS_THROUGH.toString(), "贯穿伤");
        this.add(BodyCondition.BLEED.toString(), "出血");
        this.add(BodyCondition.INFECTION.toString(), "感染");
        this.add(BodyCondition.FOREIGN_OBJECT.toString(), "体内异物");
        this.add(BodyCondition.FRACTURE.toString(), "骨折");
        this.add(BodyCondition.INTENSE_PAIN.toString(), "剧痛");

        this.add(BodyCondition.BANDAGED.toString(), "绷带包扎");
        this.add(BodyCondition.BANDAGED_DIRTY.toString(), "脏绷带");
        this.add(BodyCondition.OINTMENT.toString(), "药膏涂抹");
        this.add(BodyCondition.HERB_BANDAGED.toString(), "草药涂抹");

        this.add(BodyCondition.BURN_RES.toString(), "烧伤吸收");
        this.add(BodyCondition.INTERNAL_RES.toString(), "内伤吸收");
        this.add(BodyCondition.OPEN_WOUND_RES.toString(), "外伤吸收");

        this.add(BodyCondition.BONE_DAMAGE.toString(), "骨损伤");
        this.add(BodyCondition.BONE_DEATH.toString(), "骨坏死");

        this.add(BodyCondition.SURGERY_INCISION.toString(), "手术切口");
        this.add(BodyCondition.CLAMPED_BLEEDING.toString(), "夹闭止血");
        this.add(BodyCondition.RETRACTED_SKIN.toString(), "皮肤牵开");
        this.add(BodyCondition.DRILLED_BONES.toString(), "骨骼钻孔");
        this.add(BodyCondition.SAWED_BONES.toString(), "骨锯开");

        this.add(BodyCondition.DISLOCATION.toString(), "脱臼");
        this.add(BodyCondition.PLASTER_CAST.toString(), "石膏固定");
        this.add(BodyCondition.ARTERIAL_BLEEDING.toString(), "动脉出血");
        this.add(BodyCondition.CLAMPED_ARTERIES.toString(), "动脉夹闭");
        this.add(BodyCondition.GANGRENE.toString(), "坏疽");
        this.add(BodyCondition.SURGICAL_AMPUTATION.toString(), "手术性截肢");
        this.add(BodyCondition.TRAUMATIC_AMPUTATION.toString(), "创伤性截肢");
        this.add(BodyCondition.CLAMP_PLATE.toString(), "夹板固定");

        this.add(BodyCondition.ANALGESIA.toString(), "镇痛");
        this.add(BodyCondition.RESPIRATORY_ARREST.toString(), "呼吸停止");
        this.add(BodyCondition.AORTIC_RUPTURE.toString(), "主动脉破裂");
        this.add(BodyCondition.HEARTRATE_INCREASE.toString(), "心率加快");
        this.add(BodyCondition.HEARTRATE_IRREGULAR.toString(), "心律不齐");
        this.add(BodyCondition.HEARTRATE_STOP.toString(), "心跳停止");
        this.add(BodyCondition.PNEUMOTHORAX.toString(), "气胸");
        this.add(BodyCondition.PNEUMOTHORAX_NEEDLE.toString(), "气胸针");

        this.add(BodyCondition.WITHDRAW.toString(), "戒断");
        this.add(BodyCondition.TRAUMATIC_SHOCK.toString(), "手术休克");
        this.add(BodyCondition.BRAIN_DAMAGE.toString(), "脑损伤");
        this.add(BodyCondition.COMA.toString(), "昏迷");

        this.add(BodyCondition.SEPSIS.toString(), "败血症");
        this.add(BodyCondition.HEMOTRANSFUSION.toString(), "输血性休克");
        this.add(BodyCondition.BLOOD_LOSS.toString(), "失血");
        this.add(BodyCondition.BLOOD_PRESSURE.toString(), "血压");
        this.add(BodyCondition.PH_LEVEL.toString(), "酸碱性");
        this.add(BodyCondition.IMMUNITY.toString(), "免疫力");
        this.add(BodyCondition.OPIATE_OVERDOSE.toString(), "阿片中毒");
        this.add(BodyCondition.OPIATE_ADDICTED.toString(), "阿片成瘾");
        this.add(BodyCondition.OXYGEN.toString(), "低血氧");
        this.add(BodyCondition.ANTIBIOTICS.toString(), "广谱抗生素");
        this.add(BodyCondition.HARDENER.toString(), "钢化");

        this.add(BodyCondition.BONE_WOOD.toString(), "木骨植入");
        this.add(BodyCondition.BONE_STONE.toString(), "石骨植入");
        this.add(BodyCondition.BONE_COPPER.toString(), "铜骨植入");
        this.add(BodyCondition.BONE_IRON.toString(), "铁骨植入");
        this.add(BodyCondition.BONE_GOLD.toString(), "金骨植入");
        this.add(BodyCondition.BONE_DIMOND.toString(), "钻骨植入");
        this.add(BodyCondition.BONE_NETHERITE.toString(), "下界骨植入");

        this.add("gui.dgh.health_gui.title", "健康状态");
        this.add("gui.dgh.health_gui.nutrient_panel_title", "[ 营养状态 ]");
        this.add("gui.dgh.health_gui.disease_title", "疾病:");
        this.add("gui.dgh.health_gui.disease_panel_title", "[ 疾病状态 ]");
        this.add("gui.dgh.health_gui.disease_none", "状态良好 - 暂无已知疾病");
        this.add("gui.dgh.health_gui.disease_loading", "疾病: 读取中…");
        this.add("gui.dgh.health_gui.drug_quick_status", "药物 冷却:%1$s 待生效:%2$s 阻断:%3$s");
        this.add("gui.dgh.health_gui.nutrient.hydration", "水分");
        this.add("gui.dgh.health_gui.nutrient.carbohydrate", "糖类");
        this.add("gui.dgh.health_gui.nutrient.fat", "油脂");
        this.add("gui.dgh.health_gui.nutrient.protein", "蛋白质");
        this.add("gui.dgh.health_gui.nutrient.minerals", "无机盐");
        this.add("gui.dgh.health_gui.nutrient.vitamins", "维生素");
        this.add("gui.dgh.health_gui.nutrient.dietary_fiber", "膳食纤维");
        this.add("gui.dgh.health_gui.drug_quick_ready", "就绪");
        this.add("gui.dgh.health_gui.drug_quick_none", "无");
        this.add("gui.dgh.health_gui.no_visible_condition", "当前部位无可见伤情");
        this.add("gui.dgh.health_gui.symptom_title", "当前症状:");
        this.add("disease.dgh.summary", "%1$s%2$s");
        this.add("disease.dgh.stage_changed", "%1$s 已进入 %2$s");
        this.add("disease.dgh.upper_respiratory_infection", "上呼吸道感染");
        this.add("disease.dgh.sepsis", "脓毒症");
        this.add("disease.dgh.undead_infection", "尸毒感染");
        this.add("disease.dgh.dietary_complication", "饮食并发症");
        this.add("disease.dgh.ptsd", "PTSD");
        this.add("disease.dgh.fracture_dislocation", "骨错位");
        this.add("disease.dgh.stage.0", "无");
        this.add("disease.dgh.stage.1", "I");
        this.add("disease.dgh.stage.2", "II");
        this.add("disease.dgh.stage.3", "III");
        // 各疾病各阶段症状描述
        this.add("disease.dgh.symptom.upper_respiratory_infection.1", "挖掘迟缓");
        this.add("disease.dgh.symptom.upper_respiratory_infection.2", "挖掘迟缓 / 轻度虚弱");
        this.add("disease.dgh.symptom.upper_respiratory_infection.3", "挖掘迟缓 / 严重虚弱");
        this.add("disease.dgh.symptom.sepsis.1", "持续中毒");
        this.add("disease.dgh.symptom.sepsis.2", "持续中毒 / 食欲下降");
        this.add("disease.dgh.symptom.sepsis.3", "严重中毒 / 食欲大量下降");
        this.add("disease.dgh.symptom.undead_infection.1", "饥饿感 / 行动迟缓");
        this.add("disease.dgh.symptom.undead_infection.2", "强烈饥饿感 / 行动迟缓");
        this.add("disease.dgh.symptom.undead_infection.3", "极度饥饿 / 行动迟缓 / 虚弱");
        this.add("disease.dgh.symptom.dietary_complication.1", "迷乱");
        this.add("disease.dgh.symptom.dietary_complication.2", "迷乱 / 轻度虚弱");
        this.add("disease.dgh.symptom.dietary_complication.3", "迷乱 / 虚弱");
        this.add("disease.dgh.symptom.ptsd.1", "受伤时: 减速 / 虚弱");
        this.add("disease.dgh.symptom.ptsd.2", "受伤时: 减速II / 虚弱II");
        this.add("disease.dgh.symptom.ptsd.3", "受伤时: 严重减速 / 虚弱III");
        this.add("disease.dgh.symptom.fracture_dislocation.1", "行动迟缓");
        this.add("disease.dgh.symptom.fracture_dislocation.2", "行动迟缓II");
        this.add("disease.dgh.symptom.fracture_dislocation.3", "行动迟缓III");

        // M5 新病种：艾滋病、破伤风、绯红症
        this.add("disease.dgh.aids", "艾滋病");
        this.add("disease.dgh.symptom.aids.1", "免疫力下降 I");
        this.add("disease.dgh.symptom.aids.2", "免疫力下降 II（挺生弱化）");
        this.add("disease.dgh.symptom.aids.3", "免疫力下降 III（挑掘难度大幅提升）");
        this.add("disease.dgh.tetanus", "破伤风");
        this.add("disease.dgh.symptom.tetanus.1", "肌肉僵直、行动迟缓");
        this.add("disease.dgh.symptom.tetanus.2", "持续背弓张、恶心ぬ僵");
        this.add("disease.dgh.symptom.tetanus.3", "呼吸补舾辭（致命）");
        this.add("disease.dgh.crimson_disease", "绯红症");
        this.add("disease.dgh.symptom.crimson_disease.1", "自我调节受阻、持续飢饥");
        this.add("disease.dgh.symptom.crimson_disease.2", "煎蛮宵、虚弱、出汗");
        this.add("disease.dgh.symptom.crimson_disease.3", "希波克症唤醒（慢性致死）");
        this.add("disease.dgh.hippocratic_syndrome", "希波克症");
        this.add("disease.dgh.symptom.hippocratic_syndrome.1", "慢性衰竭、持续虚弱");
        this.add("disease.dgh.symptom.hippocratic_syndrome.2", "器官衰退、凋零加剧");
        this.add("disease.dgh.symptom.hippocratic_syndrome.3", "慢性致死（不可逆恶化）");
        this.add("disease.dgh.ender_erosion", "末影侵蚀");
        this.add("disease.dgh.symptom.ender_erosion.1", "感知紊乱、方向错乱");
        this.add("disease.dgh.symptom.ender_erosion.2", "视野侵蚀、持续眩晕");
        this.add("disease.dgh.symptom.ender_erosion.3", "末影侵蚀失控（行为异常）");

        // 药物系统提示
        this.add("drug.dgh.capsule_cooldown", "冷却中：%1$s 秒后可再次服用");
        this.add("drug.dgh.capsule_taken", "已服药，%1$s 秒后生效");
        this.add("drug.dgh.capsule_effect_applied", "药力生效：%1$s");
        this.add("drug.dgh.capsule_tooltip_delay", "服用后约120秒生效");
        this.add("drug.dgh.capsule_tooltip_cooldown", "服用后有300秒冷却");
        this.add("drug.dgh.needle_contaminated", "药针已污染，感染风险增加！");
        this.add("drug.dgh.needle_contaminated_tip", "该药针已污染，再次使用可能感染");
        this.add("drug.dgh.needle_clean_tip", "该药针干净，使用后会被污染");
        this.add("drug.dgh.needle_clean_recipe_tip", "[合成] 针具 + 消毒剂 可将注射针清洁还原");
        this.add("drug.dgh.dose.dextromethorphan", "右美沙芬胶囊");
        this.add("drug.dgh.dose.ibuprofen", "布洛芬胶囊");
        this.add("drug.dgh.dose.ribavirin", "利巴韦林注射");
        this.add("drug.dgh.dose.blocker", "阻断剂注射");
        this.add("drug.dgh.dose.sedative", "镇静剂口服液");
        this.add("drug.dgh.dose.lamivudine", "拉米夫定胶囊");
        this.add("drug.dgh.lamivudine_cured", "拉米夫定起效，艾滋病得治！");
        this.add("drug.dgh.ender_erosion_relief", "镇静剂生效，末影侵蚀暂时缓解");

        this.add(KeyBinding.KEY_CATEGORY_DGH, "DGH");
        this.add(KeyBinding.KEY_HEALTH_MENU, "健康面板");
        this.add(KeyBinding.KEY_GIVE_UP, "放弃生命");
        this.add(KeyBinding.KEY_CALL_FOR_HELP, "呼叫救援");
        this.add("dgh.book.medical_guide", "医疗指南");
        this.add("dgh.landing_text", "未经过审批，需要谨慎使用。");
        this.add(ModItems.HEALTH_SCANNER.get(), "健康扫描仪");
        this.add(ModItems.BLOOD_SCANNER.get(), "血液扫描仪");
        this.add(ModItems.NUTRIENT_SCANNER.get(), "营养扫描仪");
        this.add(ModItems.BLOOD_PACK.get(), "血袋");
        this.add(ModItems.BLOOD_PACK_EMPTY.get(), "空血袋");
        this.add(ModItems.BANDAGE.get(), "绷带");
        this.add(ModItems.MORPHINE.get(), "吗啡");
        this.add(ModItems.GYPSUM.get(), "石膏");
        this.add(ModItems.SUTURE.get(), "缝合线");
        this.add(ModItems.OPERATING_BED_BLOCK_ITEM.get(), "手术床");
        this.add(ModItems.HEALTH_CARE_BAG.get(), "医疗包");
        this.add(ModItems.SURGERY_TOOL_BAG.get(), "手术工具包");
        this.add(ModItems.WOOD_WRENCH.get(), "木扳手");
        this.add(ModItems.SCALPEL.get(), "手术刀");
        this.add(ModItems.HEMOSTAT.get(), "止血钳");
        this.add(ModItems.RETRACTOR.get(), "牵开器");
        this.add(ModItems.SURGICAL_DRILL.get(), "手术钻");
        this.add(ModItems.TWEEZER.get(), "镊子");
        this.add(ModItems.BONE_IMPLANTS.get(), "骨骼植入物");
        this.add(ModItems.BONE_IMPLANTS_WOOD.get(), "木骨植入物");
        this.add(ModItems.BONE_IMPLANTS_STONE.get(), "石骨植入物");
        this.add(ModItems.BONE_IMPLANTS_COPPER.get(), "铜骨植入物");
        this.add(ModItems.BONE_IMPLANTS_IRON.get(), "铁骨植入物");
        this.add(ModItems.BONE_IMPLANTS_GOLD.get(), "金骨植入物");
        this.add(ModItems.BONE_IMPLANTS_DIMOND.get(), "钻骨植入物");
        this.add(ModItems.BONE_IMPLANTS_NETHERITE.get(), "下界合金骨植入物");
        this.add(ModItems.SURGERY_SAW.get(), "手术锯");
        this.add(ModItems.BONE_NATURAL.get(), "自然骨");
        this.add(ModItems.BONE_WOOD.get(), "木骨");
        this.add(ModItems.BONE_STONE.get(), "石骨");
        this.add(ModItems.BONE_COPPER.get(), "铜骨");
        this.add(ModItems.BONE_IRON.get(), "铁骨");
        this.add(ModItems.BONE_GOLD.get(), "金骨");
        this.add(ModItems.BONE_DIMOND.get(), "钻骨");
        this.add(ModItems.BONE_NETHERITE.get(), "下界合金骨");
        this.add(ModItems.NALOXONE.get(), "烯丙羟吗啡酮");
        this.add(ModItems.MEDICAL_STENT.get(), "医用支架");
        this.add(ModItems.TOURNIQUET.get(), "动脉止血带");
        this.add(ModItems.NEEDLE.get(), "气胸针");
        this.add(ModItems.DRAINAGE.get(), "引流管");
        this.add(ModItems.ADRENALINE.get(), "肾上腺素");
        this.add(ModItems.OXYGEN_MASK.get(), "急救呼吸气囊");
        this.add(ModItems.ANTIBIOTIC_OINTMENT.get(), "抗生素软膏");
        this.add(ModItems.ANTISEPTIC_SPRAYER.get(), "消毒喷雾器");
        this.add(ModItems.ANTISEPTIC.get(), "消毒剂");
        this.add(ModItems.AUTOPULSE.get(), "自动心肺复苏器");
        this.add(ModItems.ANTIBIOTICS.get(), "广谱抗生素");
        this.add(ModItems.LIMB_REF_BEG.get(), "器官储存箱");
        this.add(ModItems.HUMAN_HAND.get(), "手臂");
        this.add(ModItems.HUMAN_LEG.get(), "腿");
        this.add(ModItems.PLASTIC_SKIN.get(), "可塑人造皮肤");
        this.add(ModItems.ANTIBIOTIC_GLUE.get(), "抗生素凝胶");
        this.add(ModItems.STASIS_BAG.get(), "停滞袋");
        this.add(ModItems.WALKING_STICK.get(), "木拐");
        this.add(ModItems.STRETCHER.get(), "木担架");
        this.add(ModItems.MANNITOL.get(), "甘露醇");
        this.add(ModItems.AUTO_USE_BAG.get(), "便携医疗包");
        this.add(ModItems.MEDICINE_BAG.get(), "药剂包");
        this.add(ModItems.CLAMP.get(), "夹板");
        this.add(ModItems.GRASS_STRING.get(), "草绳");
        this.add(ModItems.HERB_BANDAGE.get(), "草药");
        this.add(ModItems.AED.get(), "电击除颤器");
        this.add(ModItems.FOOD_CONSUMER.get(), "健胃消食片");
        this.add(ModItems.FENTANYL.get(), "芬太尼");
        this.add(ModItems.HYPERZINE.get(), "加速粉剂");
        this.add(ModItems.HARDENER.get(), "钢化粉剂");
        this.add(ModItems.LAMIVUDINE_CAPSULE.get(), "拉米夫定胶囊");
        this.add(ModItems.DEXTROMETHORPHAN.get(), "右美沙芬");
        this.add(ModItems.IBUPROFEN.get(), "布洛芬");
        this.add(ModItems.ORAL_LIQUID.get(), "口服液");
        this.add(ModItems.TARGETING_AGENT.get(), "靶向剂");
        this.add(ModItems.SEDATIVE.get(), "镇静剂");
        this.add(ModItems.BLOCKER.get(), "阻断剂");
        this.add(ModItems.RIBAVIRIN.get(), "利巴韦林");
        this.add(ModItems.BRAIN.get(), "人类大脑");
        this.add(ModItems.EYE.get(), "人类眼睛");
        this.add(ModItems.SPINAL_CORD.get(), "人类脊髓");
        this.add(ModItems.HEART.get(), "人类心脏");
        this.add(ModItems.KIDNEY.get(), "人类肾");
        this.add(ModItems.LIVER.get(), "人类肝脏");
        this.add(ModItems.LUNGS.get(), "人类肺");
        this.add(ModItems.STOMACH.get(), "人类胃");
        this.add(ModItems.MUSCLE.get(), "人类肌肉");
        this.add(ModItems.NEURO.get(), "人类神经");
        this.add(ModItems.SKIN.get(), "人类皮肤");

        this.add(ModEffects.STAGGER_EFFECT.get(), "缓行");
        this.add(ModEffects.INTENSE_PAIN_EFFECT.get(), "剧痛");
        this.add(ModEffects.SWEATING_EFFECT.get(), "出汗");
        this.add(ModEffects.CRAVING_EFFECT.get(), "渴望");
        this.add(ModEffects.KEEP_LIVING_EFFECT.get(), "长生久视");
        this.add(ModEffects.CURE_EFFECT.get(), "重振旗鼓");
        this.add(ModEffects.PALE_SKIN_EFFECT.get(), "皮肤苍白");
        this.add(ModEffects.HARD_BREATH_EFFECT.get(), "呼吸困难");
        this.add(ModEffects.INCREASED_HEARTRATE_EFFECT.get(), "心跳加快");
        this.add(ModEffects.INFLAMMATION_EFFECT.get(), "炎症");
        this.add(ModEffects.FEVER_EFFECT.get(), "发烧");
        this.add(ModEffects.ADRENALINE_EFFECT.get(), "肾上腺素");
        this.add(ModEffects.COMBAT_STIMULANT_EFFECT.get(), "战斗兴奋剂");
        this.add(ModEffects.ANALGESIA_POISON_EFFECT.get(), "眩晕毒剂中毒");
        this.add(ModEffects.FOOD_CONSUMER_EFFECT.get(), "消食");

        this.addPotion(ModPotions.COMBAT_STIMULANT_POTION, "战斗兴奋剂");
        this.addPotion(ModPotions.ANALGESIA_POISON_POTION, "眩晕毒剂");

        this.add("death.attack." + OPEN_WOUND_DAMAGE.location(), "%1$s 的身体被撕碎了");
        this.add("death.attack." + INTERNAL_INJURY_DAMAGE.location(), "%1$s 体内一塌糊涂");
        this.add("death.attack." + BURN_DAMAGE.location(), "%1$s 变成了黑碳");
        this.add("death.attack." + BRAIN_DAMAGE.location(), "%1$s 变成了植物人");
        this.add("death.attack." + BLEED_DAMAGE.location(), "%1$s 失血过多");
        this.add("death.attack." + SURGERY_DAMAGE.location(), "%1$s 死于手术事故");
        this.add("death.attack." + CANT_BREATH_DAMAGE.location(), "%1$s 无法呼吸");

        this.add("death.attack." + OPEN_WOUND_DAMAGE.location() + ".player", "%1$s 的身体被%2$s撕碎了");
        this.add("death.attack." + INTERNAL_INJURY_DAMAGE.location() + ".player", "%1$s 的体内被%2$s打烂了");
        this.add("death.attack." + BURN_DAMAGE.location() + ".player", "%1$s 被%2$s变成了黑碳");
        this.add("death.attack." + BRAIN_DAMAGE.location() + ".player", "%1$s 被%2$s变成了植物人");
        this.add("death.attack." + BLEED_DAMAGE.location() + ".player", "%1$s 由于%2$s失血过多");
        this.add("death.attack." + SURGERY_DAMAGE.location() + ".player", "%1$s 死于手术事故");
        this.add("death.attack." + CANT_BREATH_DAMAGE.location() + ".player", "%1$s 由于%2$s无法呼吸");

        this.add("entity.minecraft.villager.dgh.doctor", "医生");

        this.add("task.dgh.bring_to_bed", "猫车");
        this.add("task.dgh.bring_to_bed.desc", "把倒下的主人救回出生点");

        this.add("tooltip.dgh.open_resist", "%s 外伤格挡");
        this.add("tooltip.dgh.internal_resist", "%s 内伤格挡");
        this.add("tooltip.dgh.burn_resist", "%s 烧伤格挡");
        this.add("tooltip.dgh.open_tough", "%s 外伤格挡恢复");
        this.add("tooltip.dgh.internal_tough", "%s 内伤格挡恢复");
        this.add("tooltip.dgh.burn_tough", "%s 烧伤格挡恢复");
    }

    private void addPotion(IEntry<Potion> potion, String translation) {
        String potionName = ModPotions.POTIONS.get(potion);
        // 普通药水
        this.add("item.minecraft.potion.effect." + potionName, translation);
        // 喷溅药水
        this.add("item.minecraft.splash_potion.effect." + potionName, "喷溅型" + translation);
        // 滞留药水
        this.add("item.minecraft.lingering_potion.effect." + potionName, "滞留型" + translation);
        // 药箭（如果你允许合成）
        this.add("item.minecraft.tipped_arrow.effect." + potionName, translation + "之箭");
    }
}
