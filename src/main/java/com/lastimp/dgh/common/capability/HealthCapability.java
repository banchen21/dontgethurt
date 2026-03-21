package com.lastimp.dgh.common.capability;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.capability.bodyPart.base.ArmorData;
import com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition;
import com.lastimp.dgh.common.capability.bodyPart.bodies.Torso;
import com.lastimp.dgh.common.capability.bodyPart.WholeBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractBody;
import com.lastimp.dgh.common.capability.bodyPart.base.AbstractExtremities;
import com.lastimp.dgh.common.capability.bodyPart.ConditionAccessor;
import com.lastimp.dgh.common.item.bases.AbstractHealingEquipment;
import com.lastimp.dgh.common.tags.ModTags;
import com.lastimp.dgh.common.utils.BookHelper;
import com.lastimp.dgh.common.utils.Serializable;
import com.lastimp.dgh.common.network.message.MyReadAllConditionData;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.container.DynamicItemHandler;
import com.lastimp.dgh.common.enums.BodyComponents;
import com.lastimp.dgh.common.entry.register.ModEffects;
import com.lastimp.dgh.common.entry.register.ModItems;
import com.lastimp.dgh.common.capability.healthCore.diseaseSystem.DiseaseState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.lastimp.dgh.common.capability.bodyPart.base.BodyCondition.*;
import static com.lastimp.dgh.common.enums.BodyComponents.*;
import static com.lastimp.dgh.common.enums.OperationType.SYN;

public class HealthCapability implements Serializable {
    public static final String HEALTH_RECORD = "health_record";
    //持久属性
    private final WholeBody body = new WholeBody();
    private final DynamicItemHandler oxygenMask = new DynamicItemHandler();
    private final DynamicItemHandler autoPulse = new DynamicItemHandler();
    private float vitality = 1.0f;
    private int slowDown = 0;
    private long livingTick = 0;
    private float almostDead = 1.0f;
    private int nearBedTick = 0;
    private float outerHealing = 0;
    private float outerHealingDelta = 0;
    private UUID lastHealer = UUID.randomUUID();
    private final ArmorData armorData = new ArmorData();
    private final DiseaseState disease = new DiseaseState();
    //同步属性
    private int armBreak = 0;
    private int availableEye = 2;
    private int maxAirSupply = 300;
    private boolean leftArmVisible = true, rightArmVisible = true, leftLegVisible = true, rightLegVisible = true;
    private boolean isDown = false;
    private boolean abnormal = true;
    //临时属性
    private boolean isDirty = true;
    private boolean haveKidney = true;
    private boolean isInfected = false;
    private int oxygenMaskCoolDown = 0;
    private int autoPulseCoolDown = 0;
    public LivingEntity currentHealer = null;
    private DamageSource lastEntityDamage;

    private final List<InjuryRecord> directInjury = new LinkedList<>();
    private final List<InjuryRecord> lastDeathDirectInjury = new LinkedList<>();

    public HealthCapability() {
        oxygenMask.addAllowed(ModTags.OXYGEN_SUPPLIERS);
        autoPulse.addAllowed(ModTags.AUTOPULSE);
    }

    //*********************全局方法*********************//
    public static boolean has(LivingEntity entity) {
        return PlatformService.CAPABILITY_HELPER.hasHealth(entity);
    }

    private static Optional<HealthCapability> get(LivingEntity entity) {
        return PlatformService.CAPABILITY_HELPER.getHealth(entity);
    }

    public static <T> T getAndApply(LivingEntity entity, Function<HealthCapability, T> function, T orElse) {
        return HealthCapability.get(entity).map(function::apply).orElse(orElse);
    }

    public static void getAndApply(LivingEntity entity, Consumer<HealthCapability> function) {
        HealthCapability.get(entity).ifPresent(function::accept);
    }

    public static void handPulse(LivingEntity entity) {
        if (has(entity)) {
            HealthCapability.getAndApply(entity, HealthCapability::handPulse);
        }
    }

    public static boolean isDown(LivingEntity entity) {
        return HealthCapability.getAndApply(entity, h -> {
            if (entity.level().isClientSide()){
                return h.isDown;
            } else {
                if (entity instanceof ServerPlayer player && Utils.checkPlayerInvincible(player)) return false;
                if (isDying(entity)) return true;
                if (entity.hasEffect(ModEffects.ANALGESIA_POISON_EFFECT.get())) return true;
                if (h.getComponent(HEAD).getConditionValue(BodyCondition.COMA) > 0.5f) return true;
                if (h.isFrozen()) return true;
                return false;
            }
        }, entity.hasEffect(ModEffects.ANALGESIA_POISON_EFFECT.get()));
    }

    public static boolean isFootLostDown(LivingEntity entity) {
        return HealthCapability.getAndApply(entity, h -> !h.rightLegVisible && !h.leftLegVisible, false);
    }

    public static boolean isDying(LivingEntity entity) {
        return entity.getHealth() < 0.05 && !entity.isDeadOrDying();
    }

    public static void recordEntityDamage(LivingEntity livingEntity, DamageSource source) {
        if (source == null || source.getEntity() == null) return;
        HealthCapability.getAndApply(livingEntity, h -> h.setLastEntityDamage(source));
    }

    //*********************查询方法*********************//
    public AbstractBody getComponent(BodyComponents component) {
        return this.body.getComponent(component);
    }

    public BodyComponents findFor(ResourceLocation condition) {
        for (var component : VISIBLE_BODIES) {
            if (this.getComponent(component).abnormal(condition))
                return component;
        }
        if (this.getComponent(BLOOD).abnormal(condition))
            return BLOOD;
        return null;
    }

    public BodyComponents findForHidden(ResourceLocation condition) {
        for (var component : VISIBLE_BODIES) {
            if (this.getComponent(component).abnormalWithHidden(condition))
                return component;
        }
        if (this.getComponent(BLOOD).abnormalWithHidden(condition))
            return BLOOD;
        return null;
    }

    public boolean intensePain() {
        return  this.getComponent(LEFT_ARM).abnormal(INTENSE_PAIN) ||
                this.getComponent(RIGHT_ARM).abnormal(INTENSE_PAIN) ||
                this.getComponent(LEFT_LEG).abnormal(INTENSE_PAIN) ||
                this.getComponent(RIGHT_LEG).abnormal(INTENSE_PAIN) ||
                this.getComponent(HEAD).abnormal(INTENSE_PAIN) ||
                this.getComponent(TORSO).abnormal(INTENSE_PAIN);
    }

    public float bloodOxygenFactor() {
        return 300.f / this.maxAirSupply;
    }

    public boolean canEat() {
        return this.getComponent(TORSO).countOrganMatch(ModTags.STOMACH) > 0;
    }

    public boolean safeSurgery() {
        return this.nearBedTick > 0 || ((Torso)this.getComponent(TORSO)).safeSurgery();
    }

    public boolean isFrozen() {
        return this.autoPulse.getStackInSlot(0).is(ModItems.STASIS_BAG.get());
    }

    //*********************状态更新*********************//
    public HealthCapability update(LivingEntity entity) {
        this.updateALLEquipments(entity);
        if (!this.isFrozen())
            this.body.update(this, entity);
        this.disease.tick(this, entity);
        this.updateLabels(entity);
        return this;
    }

    public void updateALLEquipments(LivingEntity entity) {
        if (updateEquipment(entity, this.oxygenMask, this.oxygenMaskCoolDown)) {
            this.oxygenMaskCoolDown = this.getCoolDown(this.oxygenMask);
            var oxygenMaskStack = this.oxygenMask.getStackInSlot(0);
            oxygenMaskStack.setDamageValue(oxygenMaskStack.getDamageValue() + 1);
            if (oxygenMaskStack.getDamageValue() >= oxygenMaskStack.getMaxDamage()) {
                this.oxygenMask.setStackInSlot(0, ItemStack.EMPTY);
            }
        } else if (this.oxygenMaskCoolDown > 0) {
            this.oxygenMaskCoolDown--;
        }
        if (updateEquipment(entity, this.autoPulse, this.autoPulseCoolDown)) {
            this.autoPulseCoolDown = this.getCoolDown(this.autoPulse);
            var autoPulseStack = this.autoPulse.getStackInSlot(0);
            autoPulseStack.setDamageValue(autoPulseStack.getDamageValue() + 1);
            if (autoPulseStack.getDamageValue() >= autoPulseStack.getMaxDamage()) {
                this.autoPulse.setStackInSlot(0, ItemStack.EMPTY);
            }
        } else if (this.autoPulseCoolDown > 0) {
            this.autoPulseCoolDown--;
        }
    }

    private boolean updateEquipment(LivingEntity entity, DynamicItemHandler handler, int cooldown) {
        var equip = handler.getStackInSlot(0);
        if (equip.isEmpty() || cooldown > 0) return false;
        if (equip.getDamageValue() >= equip.getMaxDamage()) return false;
        return ((AbstractHealingEquipment)equip.getItem()).heal(entity);
    }

    private int getCoolDown(DynamicItemHandler handler) {
        return ((AbstractHealingEquipment)handler.getStackInSlot(0).getItem()).getMaxCooldown();
    }

    private void updateLabels(LivingEntity entity) {
        //持久属性
        this.vitality = 1.0f - this.body.updateVitalityLost(this, entity);
        this.vitality = (this.vitality > 0.999f) ? 1.0f : this.vitality;
        this.slowDown = this.body.slowDownLevel(this);
        if (entity instanceof Player player) {
            if (player.getMainHandItem().is(ModItems.WALKING_STICK.get()))
                this.slowDown = Math.max(0, this.slowDown - 3);
            if (player.getOffhandItem().is(ModItems.WALKING_STICK.get()))
                this.slowDown = Math.max(0, this.slowDown - 3);
        }
        this.livingTick = Math.min(Integer.MAX_VALUE, this.livingTick + 1);
        this.almostDead = Math.min(this.almostDead, this.vitality);
        this.nearBedTick = Math.max(0 ,this.nearBedTick);
        this.outerHealing = Math.max(0, this.outerHealing - this.outerHealingDelta);
        this.outerHealingDelta = this.outerHealing <= 0 ? 0 : this.outerHealingDelta + 1.0f / PlatformService.CONFIG.HEALTH_SHIELD_REDU() / 20;
        this.armorData.update(entity);
        //同步属性
        var newArmBreak = (AbstractExtremities.available(this, LEFT_ARM) ? 0 : 1) + (AbstractExtremities.available(this, RIGHT_ARM) ? 0 : 1);
        this.armBreak = updateIfDirty(newArmBreak, this.armBreak);
        this.leftArmVisible = updateIfDirty(AbstractExtremities.visible(this, LEFT_ARM), this.leftArmVisible);
        this.rightArmVisible = updateIfDirty(AbstractExtremities.visible(this, RIGHT_ARM), this.rightArmVisible);
        this.leftLegVisible = updateIfDirty(AbstractExtremities.visible(this, LEFT_LEG), this.leftLegVisible);
        this.rightLegVisible = updateIfDirty(AbstractExtremities.visible(this, RIGHT_LEG), this.rightLegVisible);
        this.availableEye = updateIfDirty(this.getComponent(HEAD).countOrganMatch(ModTags.EYE), this.availableEye);
        this.maxAirSupply = updateIfDirty(((Torso)this.getComponent(TORSO)).additionAir() + 150, this.maxAirSupply);
        this.abnormal = updateIfDirty(this.body.abnormal(), this.abnormal);
        //临时属性
        this.handleAdrenaline(entity);
        this.isInfected = this.body.isInfected();
        this.haveKidney = this.getComponent(TORSO).countOrganMatch(ModTags.KIDNEY) > 0;
        this.isDown = updateIfDirty(isDown(entity), this.isDown);
        if (!this.abnormal) {
            this.directInjury.clear();
            this.currentHealer = null;
            this.lastEntityDamage = null;
        }
    }

    public <T> T updateIfDirty(T value, T oldValue) {
        if (value != oldValue) {
            this.isDirty = true;
        }
        return value;
    }

    public void SYNIfDirty(LivingEntity livingEntity) {
        if (!this.isDirty) return;
        PlatformService.NETWORK.sendToPlayersNear(
                (ServerLevel) livingEntity.level(), null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 64,
                MyReadAllConditionData.getInstance(livingEntity.getUUID(), livingEntity.getId(), this.lightSerializeNBT(), SYN)
        );
        this.isDirty = false;
    }

    public void handPulse() {
        if (this.autoPulseCoolDown() > 0) return;
        if (this.isFrozen()) return;

        this.autoPulseCoolDown = 20;
        var blood = this.getComponent(BodyComponents.BLOOD);
        Torso torso = (Torso) this.getComponent(BodyComponents.TORSO);
        blood.healing(OXYGEN, -ConditionAccessor.get(OXYGEN).healingSpeed());
        blood.healing(BLOOD_PRESSURE, Utils.randomBetween(0.01f, 0.1f));
        torso.addHeartRate(-Utils.randomBetween(0.01f, 0.2f));
    }

    private void handleAdrenaline(LivingEntity entity) {
        if (this.vitality < 0.8 && this.almostDead > 0.8) {
            entity.addEffect(new MobEffectInstance(ModEffects.ADRENALINE_EFFECT.get(), 40 * 20, 0));
        } else if (this.vitality < 0.3 && this.almostDead > 0.3) {
            entity.addEffect(new MobEffectInstance(ModEffects.ADRENALINE_EFFECT.get(), 20 * 20, 1));
        }
    }

    public void addOriginOrganOnDeath(LivingEntity livingEntity) {
        this.body.addOriginOrgan(livingEntity, false);
    }

    public void addOriginOrganFully(LivingEntity livingEntity) {
        this.body.addOriginOrgan(livingEntity, true);
    }

    public boolean write(ItemStack stack, Component name, Component author) {
        if (!stack.is(Items.WRITTEN_BOOK)) return false;
        if (this.lastDeathDirectInjury.isEmpty() && this.directInjury.isEmpty()) return false;

        var recordList = this.lastDeathDirectInjury.isEmpty() ? this.directInjury : this.lastDeathDirectInjury;
        Component title = Component.literal(name.getString() + (this.lastDeathDirectInjury.isEmpty() ? "的病例" : "的尸检报告"));
        var bookTag = BookHelper.getBookTag(title, author, recordList);
        stack.getOrCreateTagElement("tag").merge(bookTag);
        return true;
    }

    public void healingAll(boolean healPain) {
        this.body.healingAll(healPain);
        this.resetAlmostDead();
    }

    public float hurtAfterArmor(BodyComponents component, ResourceLocation resistType, float amount) {
        return this.armorData.hurt(component, resistType, amount);
    }

    public float armorResist(BodyComponents component, ResourceLocation resistType) {
        return this.armorData.getResist(component, resistType);
    }

    public void refreshArmor() {
        this.armorData.setDirty();
    }

    public void addDirectInjury(Entity source, Component body, Component condition, float value) {
        this.addDirectInjury(source != null ? source.getName() : Component.literal("环境"), body, condition, value);
    }

    public void addDirectInjury(Component body, Component condition, float value, int level) {
        this.directInjury.add(new InjuryRecord("", body.getString(), condition.getString(), value, level));
    }

    public void addDirectInjury(Component body, Component condition, int level) {
        this.directInjury.add(new InjuryRecord("", body.getString(), condition.getString(), -1, level));
    }

    public void addToLastDeathDirectInjury(Collection<InjuryRecord> lastDeathDirectInjury) {
        this.lastDeathDirectInjury.addAll(lastDeathDirectInjury);
    }

    //*********************序列化*********************//
    public CompoundTag lightSerializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("armBreak", this.armBreak);
        tag.putBoolean("leftArmVisible", this.leftArmVisible);
        tag.putBoolean("rightArmVisible", this.rightArmVisible);
        tag.putBoolean("leftLegVisible", this.leftLegVisible);
        tag.putBoolean("rightLegVisible", this.rightLegVisible);
        tag.putInt("availableEye", this.availableEye);
        tag.putBoolean("isDown", this.isDown);
        tag.putInt("maxAirSupply", this.maxAirSupply);
        tag.putBoolean("abnormal", this.abnormal);
        tag.put("body", this.body.lightSerializeNBT());
        return tag;
    }

    public CompoundTag deathSerializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("body", this.body.deathSerializeNBT());
        serializeRecord("directInjury", this.directInjury, tag);
        return tag;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("body", this.body.serialize());
        tag.put("disease", this.disease.serializeNBT());
        tag.putFloat("playerVitality", this.vitality);
        tag.putLong("livingTick", this.livingTick);
        tag.putFloat("almostDead", this.almostDead);
        tag.putInt("nearBedTick", this.nearBedTick);
        tag.putFloat("outerHealing", this.outerHealing);
        tag.putFloat("outerHealingDelta", this.outerHealingDelta);
        tag.put("oxygenMask", this.oxygenMask.serialize());
        tag.put("autoPulse", this.autoPulse.serialize());
        tag.putInt("oxygenMaskCoolDown", this.oxygenMaskCoolDown);
        tag.putInt("autoPulseCoolDown", this.autoPulseCoolDown);
        tag.putUUID("lastHealer", this.lastHealer);
        tag.put("armor", this.armorData.serialize());

        serializeRecord("directInjury", this.directInjury, tag);
        serializeRecord("lastDeathDirectInjury", this.lastDeathDirectInjury, tag);
        return tag;
    }

    private static void serializeRecord(String key, List<InjuryRecord> recordList, CompoundTag tag) {
        ListTag listTag = new ListTag();
        recordList.forEach((record) -> listTag.add(listTag.size(), record.serialize()));
        tag.put(key, listTag);
    }

    public void lightDeserializeNBT(CompoundTag nbt) {
        if (nbt == null) return;
        this.armBreak = nbt.getInt("armBreak");
        this.leftArmVisible = nbt.getBoolean("leftArmVisible");
        this.rightArmVisible = nbt.getBoolean("rightArmVisible");
        this.leftLegVisible = nbt.getBoolean("leftLegVisible");
        this.rightLegVisible = nbt.getBoolean("rightLegVisible");
        this.availableEye = nbt.getInt("availableEye");
        this.isDown = nbt.getBoolean("isDown");
        this.maxAirSupply = nbt.getInt("maxAirSupply");
        this.abnormal = nbt.getBoolean("abnormal");
        this.body.lightDeserializeNBT(nbt.getCompound("body"));
    }

    public void respawnDeserializeNBT(CompoundTag nbt) {
        this.body.respawnDeserializeNBT(nbt.getCompound("body"));
        deserializeRecord("directInjury", this.lastDeathDirectInjury, nbt);
        this.isDirty = true;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.body.deserialize(nbt.getCompound("body"));
        this.disease.deserializeNBT(nbt.getCompound("disease"));
        this.vitality = nbt.getFloat("playerVitality");
        this.livingTick = nbt.getLong("livingTick");
        this.almostDead = nbt.getFloat("almostDead");
        this.nearBedTick = nbt.getInt("nearBedTick");
        this.outerHealing = nbt.getFloat("outerHealing");
        this.outerHealingDelta = nbt.getFloat("outerHealingDelta");
        this.oxygenMask.deserialize(nbt.getCompound("oxygenMask"));
        this.autoPulse.deserialize(nbt.getCompound("autoPulse"));
        this.oxygenMaskCoolDown = nbt.getInt("oxygenMaskCoolDown");
        this.autoPulseCoolDown = nbt.getInt("autoPulseCoolDown");
        if (nbt.get("lastHealer") != null)
            this.lastHealer = nbt.getUUID("lastHealer");
        this.armorData.deserialize(nbt.getCompound("armor"));

        deserializeRecord("directInjury", this.directInjury, nbt);
        deserializeRecord("lastDeathDirectInjury", this.lastDeathDirectInjury, nbt);
        this.isDirty = true;
    }

    private static void deserializeRecord(String key, List<InjuryRecord> recordList, CompoundTag nbt) {
        ListTag listTag = nbt.getList(key, ListTag.TAG_COMPOUND);
        recordList.clear();
        listTag.forEach((tag -> recordList.add(InjuryRecord.phrase((CompoundTag) tag))));
    }

    //*********************getter setter*********************//
    public float vitality() {
        return vitality;
    }

    public int slowDown() {
        return slowDown;
    }

    public float almostDead() {
        return almostDead;
    }

    public void resetAlmostDead() {
        this.almostDead = 1.0f;
    }

    public long livingTick() {
        return livingTick;
    }

    public int armBreak() {
        return armBreak;
    }

    public int availableEye() {return availableEye;}

    public void setNearBedTick(int nearBedTick) {
        this.nearBedTick = nearBedTick;
    }

    public float outerHealing() {
        return outerHealing;
    }

    public void setOuterHealing(float outerHealing) {
        this.outerHealing = outerHealing;
    }

    public boolean isInfected() {
        return this.isInfected;
    }

    public boolean haveKidney() {
        return haveKidney;
    }

    public DynamicItemHandler oxygenMask() {
        return oxygenMask;
    }

    public DynamicItemHandler autoPulse() {
        return autoPulse;
    }

    public int autoPulseCoolDown() {
        return autoPulseCoolDown;
    }

    public int oxygenMaskCoolDown() {
        return oxygenMaskCoolDown;
    }

    public boolean leftArmVisible() {
        return leftArmVisible;
    }

    public boolean leftLegVisible() {
        return leftLegVisible;
    }

    public boolean rightArmVisible() {
        return rightArmVisible;
    }

    public boolean rightLegVisible() {
        return rightLegVisible;
    }

    public void setLastHealer(UUID lastHealer) {
        this.lastHealer = lastHealer;
    }

    public UUID lastHealer() {
        return lastHealer;
    }

    public int maxAirSupply() {
        return this.maxAirSupply;
    }

    public DamageSource lastEntityDamage() {
        return lastEntityDamage;
    }

    public void setLastEntityDamage(DamageSource lastEntityDamage) {
        this.lastEntityDamage = lastEntityDamage;
    }

    public void addDirectInjury(Component source, Component body, Component condition, float value) {
        this.directInjury.add(new InjuryRecord(source.getString(), body.getString(), condition.getString(), value));
    }

    public List<InjuryRecord> directInjury() {
        return this.directInjury;
    }

    public void clearDirectInjury() {
        this.directInjury.clear();
    }

    public List<InjuryRecord> lastDeathDirectInjury() {
        return lastDeathDirectInjury;
    }

    public void clearLastDeathDirectInjury() {
        this.lastDeathDirectInjury.clear();
    }

    public boolean abnormal() {
        return abnormal;
    }

    public DiseaseState disease() {
        return disease;
    }
}
