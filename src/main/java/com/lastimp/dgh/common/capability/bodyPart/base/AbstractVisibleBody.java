package com.lastimp.dgh.common.capability.bodyPart.base;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.item.tool.HealthScanner;
import com.lastimp.dgh.common.tags.ModTags;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Head;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Torso;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.item.tool.SurgeryBones;
import com.lastimp.dgh.common.entry.register.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;
import static com.lastimp.dgh.common.enums.BodyComponents.*;

public abstract class AbstractVisibleBody extends AbstractBody {
    private static final Collection<ResourceLocation> uniqueConditions = new LinkedHashSet<>();
    private static List<ResourceLocation> ANY_BODY_CONDITIONS;
    private float nextTickBleed;

    private AttributeInstance armor;
    private AttributeInstance armor_toughness;
    private AttributeInstance knock_back_resist;

    private UUID uuid_bone_stone;
    private UUID uuid_bone_copper;
    private UUID uuid_bone_iron;
    private UUID uuid_bone_gold;
    private UUID uuid_bone_dimond;
    private UUID uuid_bone_netherite;

    private int nextFractureTick = 1200;
    private float conditionDisplayValue;
    private float red;
    private float green;
    private float blue;

    public static void addCondition(Collection<ResourceLocation> key) {
        uniqueConditions.addAll(key);
    }

    @Override
    public List<ResourceLocation> getBodyConditions() {
        if (ANY_BODY_CONDITIONS == null) {
            ANY_BODY_CONDITIONS = new ArrayList<>(uniqueConditions);
        }
        return ANY_BODY_CONDITIONS;
    }

    @Override
    public AbstractBody update(HealthCapability health, LivingEntity entity) {
        super.update(health, entity);
        handleBandaged();
        handleHerb(entity);
        handleBurning(entity);
        handleInternalInjury(entity);
        handleOpenWound(entity);
        handlePassThrough(entity);
        handleFracture(health);
        handleSurgery(health);
        handleBleeding();
        updateBoneEffect(entity);
        handleBoneDamage(health);
        handleBoneDeath();
        return this;
    }

    @Override
    public AbstractBody updatePre(HealthCapability health, LivingEntity entity) {
        super.updatePre(health, entity);
        this.nextTickBleed = 0;
        return this;
    }

    @Override
    public AbstractBody updatePost(HealthCapability health, LivingEntity entity) {
        super.updatePost(health, entity);
        this.updateBodyColor(health);
        return this;
    }

    @Override
    public float updateVitalityLost(HealthCapability health, LivingEntity entity) {
        float lost = 0;
        lost += this.getCondition(BURN).getTotalValue();
        lost += this.getCondition(OPEN_WOUND).getTotalValue();
        lost += this.getCondition(PASS_THROUGH).getTotalValue();
        lost += this.getCondition(INTERNAL_INJURY).getTotalValue();
        return lost;
    }

    @Override
    public void healing(ResourceLocation key, float value) {
        float heal = Mth.clamp(Math.min(-value, this.getConditionValue(key)), 0.0f, 2.0f);
        handleResist(key, heal);
        super.healing(key, value);
    }

    @Override
    public void healingHidden(ResourceLocation key, float value) {
        float heal = Mth.clamp(Math.min(-value, this.getConditionHidden(key)), 0.0f, 2.0f);
        handleResist(key, heal);
        super.healingHidden(key, value);
    }

    private void handleResist(ResourceLocation key, float heal) {
        float shield = heal / 2;
        heal *= PlatformService.CONFIG.RESISTANCE_CONVERT_RATIO();
        if (key == BURN) {
            this.addConditionValue(BURN_RES, shield);
            this.addConditionHidden(BURN_RES, heal);
        } else if (key == OPEN_WOUND || key == PASS_THROUGH) {
            this.addConditionValue(OPEN_WOUND_RES, shield);
            this.addConditionHidden(OPEN_WOUND_RES, heal);
        } else if (key == INTERNAL_INJURY) {
            this.addConditionValue(INTERNAL_RES, shield);
            this.addConditionHidden(INTERNAL_RES, heal);
        }
    }

    @Override
    public int slowDownLevel(HealthCapability health) {
        int slowDown = (this.isBandaged() || isBadBandaged()) ? 1 : 0;
        slowDown += this.abnormal(PLASTER_CAST)? 2 : 0;
        if (this.abnormal(SURGERY_INCISION)) slowDown += 20;
        return slowDown;
    }

    private void updateBodyColor(HealthCapability health) {
        float injury = 0.0f;
        float pain = 0.0f;
        float comfort = 0.0f;
        float amputation = 0.0f;
        for (var key : this.getBodyConditions()) {
            var condition = ConditionAccessor.get(key);
            if (!this.visibilityCheck(this, key)) continue;
            float value = this.getCondition(key).getDisplayValue();
            if (!condition.abnormal(value)) continue;
            float difference = Mth.abs(value - condition.defaultValue());
            if (condition.isInjury()) injury += difference;
            else if (key == SURGICAL_AMPUTATION || key == TRAUMATIC_AMPUTATION) amputation += difference;
            else if (condition.isPain()) pain += difference;
            else if (condition.isComfort()) comfort += difference;
        }
        float condition = (injury >= 0.01) ? injury : (pain > 0.01)? pain : (comfort > 0.01)? comfort : (amputation > 0.01)? amputation : 0.0f;
        this.conditionDisplayValue = health.updateIfDirty(Mth.clamp(condition, 0.0f, 1.0f) * 0.7f, this.conditionDisplayValue);
        if (injury >= 0.01)     this.setColor(health, 1.0f, 0.0f, 0.2f);
        else if (pain > 0.01)   this.setColor(health, 1.0f, 1.0f, 0.2f);
        else if (comfort > 0.01) this.setColor(health, 0.0f, 1.0f, 0.2f);
        else if (amputation > 0.01) this.setColor(health, 0.1f, 0.1f, 0.1f);
    }

    protected boolean visibilityCheck(AbstractBody body, ResourceLocation key) {
        if (!HealthScanner.healthScannerConditions().contains(key)) return false;
//        if (!this.menu.isDevice && !HealthScanner.eyesightConditions().contains(key)) return false;
        if (ConditionAccessor.resistConditions.contains(key) && body.abnormalWithHidden(key)) return true;
        if (!ConditionAccessor.get(key).abnormal(body.getCondition(key).getDisplayValue())) return false;
        return true;
    }

    private void handleBandaged() {
        if (isBandaged()) {
            var bandage = ConditionAccessor.get(BANDAGED);
            var factor = 0.25f;
            factor = this.abnormalWithHidden(OPEN_WOUND) || this.abnormalWithHidden(PASS_THROUGH) ? 1 : factor;
            factor = this.abnormalWithHidden(BURN) ? 2 : factor;
            this.addConditionValue(BANDAGED, - bandage.healingSpeed() * Utils.DELTA * factor);
            if (!isBandaged()) {
                this.injury(BANDAGED_DIRTY, ConditionAccessor.get(BANDAGED_DIRTY).maxValue());
            }
        }
    }

    private void handleHerb(LivingEntity entity) {
        if (!this.abnormal(HERB_BANDAGED)) return;
        if (entity.isInWaterRainOrBubble()) {
            this.injury(HERB_BANDAGED, -0.1f * Utils.DELTA);
        }
        if (this.abnormal(SURGERY_INCISION)) {
            this.injury(INFECTION, ConditionAccessor.get(INFECTION).healingSpeed() * 4 * Utils.DELTA);
        }
    }

    private void handleBurning(LivingEntity entity) {
        if (!this.abnormalWithHidden(BURN)) return;
        this.handleBandageAcc(BURN, PlatformService.CONFIG.BANDAGE_ACC());
        this.handleCover(BURN);
        this.handleInjuryInfection(BURN);
        this.handleCombatStimulant(entity, BURN);

        if (isBandaged() || isHerbed()) return;
        this.nextTickBleed += this.getCondition(BURN).getValue() * PlatformService.CONFIG.BURN_BLEED_RATIO();
    }

    private void handleInternalInjury(LivingEntity entity) {
        if (!this.abnormalWithHidden(INTERNAL_INJURY)) return;
        this.handleCombatStimulant(entity, INTERNAL_INJURY);

        this.nextTickBleed += this.getCondition(INTERNAL_INJURY).getValue() * PlatformService.CONFIG.INTERNAL_BLEED_RATIO();
    }

    private void handleOpenWound(LivingEntity entity) {
        if (!this.abnormalWithHidden(OPEN_WOUND)) return;
        this.handleBandageAcc(OPEN_WOUND, PlatformService.CONFIG.BANDAGE_ACC());
        this.handleCover(OPEN_WOUND);
        this.handleInjuryInfection(OPEN_WOUND);
        this.handleCombatStimulant(entity, OPEN_WOUND);

        if (isBandaged() || isHerbed()) return;
        this.nextTickBleed += this.getCondition(OPEN_WOUND).getValue() * PlatformService.CONFIG.OPEN_WOUND_BLEED_RATIO();
    }

    private void handlePassThrough(LivingEntity entity) {
        if (!this.abnormalWithHidden(PASS_THROUGH)) return;
        this.handleBandageAcc(PASS_THROUGH, PlatformService.CONFIG.BANDAGE_ACC());
        this.handleCover(PASS_THROUGH);
        this.handleInjuryInfection(PASS_THROUGH);
        this.handleCombatStimulant(entity, PASS_THROUGH);

        if (isBandaged() || isHerbed()) return;
        this.nextTickBleed += this.getCondition(PASS_THROUGH).getValue() * PlatformService.CONFIG.OPEN_WOUND_BLEED_RATIO() * 1.5f;
    }

    private void handleBandageAcc(ResourceLocation condition, float acc) {
        if (isBandaged()) {
            this.healingHidden(condition, - ConditionAccessor.get(condition).healingSpeed() * Utils.DELTA * (isBadBandaged() ? 1.0f : acc));
        }
        if (isHerbed()) {
            this.healingHidden(condition, - ConditionAccessor.get(condition).healingSpeed() * Utils.DELTA * (isBadBandaged() ? 1.0f : acc));
        }
    }

    protected void handleCover(ResourceLocation condition) {
        ConditionState state = this.getCondition(condition);
        if (!isBandaged() && !isBadBandaged()) {
            this.setConditionValue(condition, state.getValue() + state.getHiddenValue());
            state.setHiddenValue(ConditionAccessor.get(condition).defaultValue());
        }
    }

    public void handleFoodAcc(LivingEntity entity, ResourceLocation condition, float acc, float consume) {
        if (!this.abnormalWithHidden(condition)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        var food = player.getFoodData();
        if (food.getFoodLevel() < 16) return;

        if (this.abnormalOnlyHidden(condition)) {
            this.healingHidden(condition, - ConditionAccessor.get(condition).healingSpeed() * Utils.DELTA * acc);
        } else {
            this.healing(condition, - ConditionAccessor.get(condition).healingSpeed() * Utils.DELTA * acc);
        }
        food.addExhaustion(consume);
    }

    private void handleInjuryInfection(ResourceLocation condition) {
        ConditionState state = this.getCondition(condition);
        float factor = this.countOrganMatch(ModTags.SKIN) > 0 ? 1 : 3;
        if (isBadBandaged()) {
            this.injury(INFECTION, ConditionAccessor.get(INFECTION).healingSpeed() * Utils.DELTA * 3 * state.getTotalValue() * factor);
        } else if (!isBandaged() && !isHerbed()) {
            this.injury(INFECTION, ConditionAccessor.get(INFECTION).healingSpeed() * Utils.DELTA * state.getTotalValue() * factor);
        }
    }

    public void cureInfection(float factor) {
        if (this.abnormal(OINTMENT))
            this.healing(INFECTION, -ConditionAccessor.get(INFECTION).healingSpeed() * 3 * Utils.DELTA * factor);
        else if (this.isBandaged() || isHerbed())
            this.healing(INFECTION, -ConditionAccessor.get(INFECTION).healingSpeed() * 2 * Utils.DELTA * factor);
        else if (!this.isBadBandaged())
            this.healing(INFECTION, -ConditionAccessor.get(INFECTION).healingSpeed() * Utils.DELTA * factor);
    }

    private void handleCombatStimulant(LivingEntity entity, ResourceLocation condition) {
        if (!entity.hasEffect(ModEffects.COMBAT_STIMULANT_EFFECT.get())) return;
        if (this.abnormalOnlyHidden(condition)) {
            this.healingHidden(condition, -0.02f * Utils.DELTA);
        } else {
            this.healing(condition, -0.02f * Utils.DELTA);
        }
    }

    private void handleFracture(HealthCapability health) {
        if (!this.abnormalWithHidden(FRACTURE)) return;
        this.handleCover(FRACTURE);

        Torso torso = (Torso) health.getComponent(TORSO);
        if (!this.abnormal(CLAMP_PLATE) && !torso.abnormal(ANALGESIA) && !this.isBandaged() && !this.isBadBandaged()) {
            this.setConditionValue(INTENSE_PAIN, ConditionAccessor.get(INTENSE_PAIN).maxValue());
        }

        if (this.abnormal(PLASTER_CAST) && this.boneCrafted() == null)
            this.healingHidden(FRACTURE, -ConditionAccessor.get(FRACTURE).healingSpeed() * Utils.DELTA);
    }

    private void handleSurgery(HealthCapability health) {
        Head head = (Head) health.getComponent(HEAD);
        float factor = health.safeSurgery() ? 0.1f : 1;
        if (this.abnormal(SURGERY_INCISION)) {
            head.injury(TRAUMATIC_SHOCK, 0.02f * Utils.DELTA * factor);
        }
        if (this.abnormal(RETRACTED_SKIN))
            head.injury(TRAUMATIC_SHOCK, 0.02f * Utils.DELTA * factor);
        if (this.abnormal(DRILLED_BONES))
            head.injury(TRAUMATIC_SHOCK, 0.015f * Utils.DELTA * factor);
        if (this.abnormal(SAWED_BONES))
            head.injury(TRAUMATIC_SHOCK, 0.015f * Utils.DELTA * factor);
    }

    private void handleBleeding() {
        this.getCondition(BLEED).setValue(this.nextTickBleed);
    }

    public boolean canHurtBone() {
        return !this.abnormalWithHidden(SAWED_BONES);
    }

    private void handleBoneDamage(HealthCapability health) {
        if (this.abnormal(SAWED_BONES)) return;
        var blood = health.getComponent(BLOOD);

        if (!this.canHurtBone()) return;
        if (blood.abnormal(OXYGEN)) {
            this.injury(BONE_DAMAGE, blood.getConditionValue(OXYGEN) * 1.1f * ConditionAccessor.get(BONE_DAMAGE).healingSpeed() * Utils.DELTA);
        }
        if (blood.abnormal(SEPSIS)) {
            this.injury(BONE_DAMAGE, blood.getConditionValue(SEPSIS) * 2 * ConditionAccessor.get(BONE_DAMAGE).healingSpeed() * Utils.DELTA);
        }
        if (!health.haveKidney()) {
            this.injury(BONE_DAMAGE, blood.getConditionValue(SEPSIS) * 4 * ConditionAccessor.get(BONE_DAMAGE).healingSpeed() * Utils.DELTA);
        }
    }

    private void handleBoneDeath() {
        if (!this.canHurtBone()) return;
        if (this.getConditionValue(BONE_DAMAGE) > 0.9f) {
            this.injury(BONE_DEATH, ConditionAccessor.get(BONE_DEATH).maxValue());
        }

        if (!this.abnormal(BONE_DEATH) || this.abnormalWithHidden(FRACTURE)) {
            this.nextFractureTick = (int) (1200 + Utils.randomBetween(-1, 1) * 600);
        } else {
            this.nextFractureTick--;
            if (this.nextFractureTick <= 0) {
                this.injury(FRACTURE, ConditionAccessor.get(FRACTURE).maxValue());
            }
        }
    }

    protected void updateBoneEffect(LivingEntity entity) {
        if (armor == null) armor = entity.getAttribute(Attributes.ARMOR);
        if (armor_toughness == null) armor_toughness = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (knock_back_resist == null) knock_back_resist = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        updateStoneBoneEffect();
        updateCopperBoneEffect();
        updateIronBoneEffect();
        updateGoldBoneEffect();
        updateDimondBoneEffect();
        updateNetheriteBoneEffect();
    }

    private void updateStoneBoneEffect() {
        if (uuid_bone_stone == null)
            uuid_bone_stone = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_STONE);

        if (this.getConditionHidden(BONE_STONE) > ConditionAccessor.get(BONE_STONE).maxValue() - Utils.EPS) {
            if (knock_back_resist != null && knock_back_resist.getModifier(uuid_bone_stone) == null)
                knock_back_resist.addPermanentModifier(new AttributeModifier(
                        uuid_bone_stone,
                        "bone_stone",
                        0.15,
                        AttributeModifier.Operation.ADDITION
                ));
            if (armor != null && armor.getModifier(uuid_bone_stone) == null)
                armor.addPermanentModifier(new AttributeModifier(
                        uuid_bone_stone,
                        "bone_stone",
                        0.5,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (knock_back_resist != null && knock_back_resist.getModifier(uuid_bone_stone) != null)
                knock_back_resist.removeModifier(uuid_bone_stone);
            if (armor != null && armor.getModifier(uuid_bone_stone) != null)
                armor.removeModifier(uuid_bone_stone);
        }
    }

    private void updateCopperBoneEffect() {
        if (uuid_bone_copper == null)
            uuid_bone_copper = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_COPPER);

        if (this.getConditionHidden(BONE_COPPER) > ConditionAccessor.get(BONE_COPPER).maxValue() - Utils.EPS) {
            if (armor != null && armor.getModifier(uuid_bone_copper) == null)
                armor.addPermanentModifier(new AttributeModifier(
                        uuid_bone_copper,
                        "bone_copper",
                        0.5,
                        AttributeModifier.Operation.ADDITION
                ));
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_copper) == null)
                armor_toughness.addPermanentModifier(new AttributeModifier(
                        uuid_bone_copper,
                        "bone_copper",
                        0.5,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (armor != null && armor.getModifier(uuid_bone_copper) != null)
                armor.removeModifier(uuid_bone_copper);
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_copper) != null)
                armor_toughness.removeModifier(uuid_bone_copper);
        }
    }

    private void updateIronBoneEffect() {
        if (uuid_bone_iron == null)
            uuid_bone_iron = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_IRON);

        if (this.getConditionHidden(BONE_IRON) > ConditionAccessor.get(BONE_IRON).maxValue() - Utils.EPS) {
            if (armor != null && armor.getModifier(uuid_bone_iron) == null)
                armor.addPermanentModifier(new AttributeModifier(
                        uuid_bone_iron,
                        "bone_iron",
                        1,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (armor != null && armor.getModifier(uuid_bone_iron) != null)
                armor.removeModifier(uuid_bone_iron);
        }
    }

    private void updateGoldBoneEffect() {
        if (uuid_bone_gold == null)
            uuid_bone_gold = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_GOLD);

        if (this.getConditionHidden(BONE_GOLD) > ConditionAccessor.get(BONE_GOLD).maxValue() - Utils.EPS) {
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_gold) == null)
                armor_toughness.addPermanentModifier(new AttributeModifier(
                        uuid_bone_gold,
                        "bone_gold",
                        2,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_gold) != null)
                armor_toughness.removeModifier(uuid_bone_gold);
        }
    }

    private void updateDimondBoneEffect() {
        if (uuid_bone_dimond == null)
            uuid_bone_dimond = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_DIMOND);

        if (this.getConditionHidden(BONE_DIMOND) > ConditionAccessor.get(BONE_DIMOND).maxValue() - Utils.EPS) {
            if (armor != null && armor.getModifier(uuid_bone_dimond) == null)
                armor.addPermanentModifier(new AttributeModifier(
                        uuid_bone_dimond,
                        "bone_dimond",
                        2,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (armor != null && armor.getModifier(uuid_bone_dimond) != null)
                armor.removeModifier(uuid_bone_dimond);
        }
    }

    private void updateNetheriteBoneEffect() {
        if (uuid_bone_netherite == null)
            uuid_bone_netherite = UUID.fromString(this.getShortID() + "-" + SurgeryBones.ID_NETHERITE);

        if (this.getConditionHidden(BONE_NETHERITE) > ConditionAccessor.get(BONE_NETHERITE).maxValue() - Utils.EPS) {
            if (armor != null && armor.getModifier(uuid_bone_netherite) == null)
                armor.addPermanentModifier(new AttributeModifier(
                        uuid_bone_netherite,
                        "arm_bone_netherite",
                        2,
                        AttributeModifier.Operation.ADDITION
                ));
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_netherite) == null)
                armor_toughness.addPermanentModifier(new AttributeModifier(
                        uuid_bone_netherite,
                        "arm_bone_netherite",
                        1,
                        AttributeModifier.Operation.ADDITION
                ));
        } else {
            if (armor != null && armor.getModifier(uuid_bone_netherite) != null)
                armor.removeModifier(uuid_bone_netherite);
            if (armor_toughness != null && armor_toughness.getModifier(uuid_bone_netherite) != null)
                armor_toughness.removeModifier(uuid_bone_netherite);
        }
    }

    public boolean isBandaged() {
        return this.abnormal(BANDAGED);
    }

    public boolean isHerbed() {
        return this.abnormal(HERB_BANDAGED);
    }

    public boolean isBadBandaged() {
        return this.abnormal(BANDAGED_DIRTY);
    }

    public float fractThreshold () {
        float value = PlatformService.CONFIG.BASE_FRACTURE_THRESHOLD();
        var bone = this.boneCrafted();
        if (bone == BONE_COPPER) {
            value += 0.1f;
        } else if (bone == BONE_GOLD) {
            value += 0.2f;
        } else if (bone == BONE_IRON) {
            value += 0.05f;
        } else if (bone == BONE_NETHERITE) {
            value += 0.1f;
        }
        return value;
    }

    public int fractCheckTimes (HealthCapability health) {
        var bone = this.boneCrafted();
        var base = health.getComponent(BLOOD).abnormal(HARDENER) ? 1 : 0;
        if (bone == BONE_WOOD) return base-1;
        if (bone == BONE_DIMOND) return base+1;
        if (bone == BONE_NETHERITE) return base+1;
        return base;
    }

    public ResourceLocation boneCrafted() {
        for (var key : ConditionAccessor.bones.keySet()) {
            if (this.abnormalWithHidden(key))
                return key;
        }
        return null;
    }

    public boolean isInfected() {
        return this.getConditionValue(INFECTION) > 0.1;
    }

    public UUID boneUUID() {
        ResourceLocation bone = this.boneCrafted();
        if (BONE_STONE.equals(bone)) return uuid_bone_stone;
        if (BONE_COPPER.equals(bone)) return uuid_bone_copper;
        if (BONE_IRON.equals(bone)) return uuid_bone_iron;
        if (BONE_GOLD.equals(bone)) return uuid_bone_gold;
        if (BONE_DIMOND.equals(bone)) return uuid_bone_dimond;
        if (BONE_NETHERITE.equals(bone)) return uuid_bone_netherite;
        return null;
    }

    public float getArmor() {
        var bone = this.boneUUID();
        return bone != null ? (float) this.armor.getModifier(bone).getAmount() : 0;
    }

    public float getRoughness() {
        var bone = this.boneUUID();
        return bone != null ? (float) this.armor_toughness.getModifier(bone).getAmount() : 0;
    }

    public CompoundTag lightSerializeNBT() {
        var nbt = super.lightSerializeNBT();
        nbt.putFloat("conditionDisplayValue", this.conditionDisplayValue);
        nbt.putFloat("red", this.red);
        nbt.putFloat("green", this.green);
        nbt.putFloat("blue", this.blue);
        return nbt;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        var nbt = super.serializeNBT();
        nbt.putInt("nextFractureTick", this.nextFractureTick);
        nbt.putFloat("conditionDisplayValue", this.conditionDisplayValue);
        nbt.putFloat("red", this.red);
        nbt.putFloat("green", this.green);
        nbt.putFloat("blue", this.blue);
        return nbt;
    }

    public void lightDeserializeNBT(CompoundTag nbt) {
        this.conditionDisplayValue = nbt.getFloat("conditionDisplayValue");
        this.red = nbt.getFloat("red");
        this.green = nbt.getFloat("green");
        this.blue = nbt.getFloat("blue");
        super.lightDeserializeNBT(nbt);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.nextFractureTick = nbt.getInt("nextFractureTick");
        this.conditionDisplayValue = nbt.getFloat("conditionDisplayValue");
        this.red = nbt.getFloat("red");
        this.green = nbt.getFloat("green");
        this.blue = nbt.getFloat("blue");
        super.deserializeNBT(nbt);
    }

    public float conditionDisplayValue() {
        return conditionDisplayValue;
    }

    public void setColor(HealthCapability health, float red, float green, float blue) {
        this.red = health.updateIfDirty(red, this.red);
        this.green = health.updateIfDirty(green, this.green);
        this.blue = health.updateIfDirty(blue, this.blue);
    }

    public Triple<Float, Float, Float> getColor() {
        return Triple.of(this.red, this.green, this.blue);
    }
}
