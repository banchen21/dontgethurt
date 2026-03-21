package com.lastimp.dgh.common.capability;

import com.lastimp.dgh.common.PlatformService;
import com.lastimp.dgh.common.utils.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiseaseCapability implements Serializable {
    private static final int MIN_STAGE = 0;
    private static final int MAX_STAGE = 3;
    private static final int MIN_PROGRESS = 0;
    private static final int MAX_PROGRESS = 100;

    public static final String DISEASE_RECORD = "disease_record";

    private int upperRespiratoryInfectionStage = 0;
    private int upperRespiratoryInfectionProgress = 0;
    private int sepsisStage = 0;
    private int sepsisProgress = 0;
    private int undeadInfectionStage = 0;
    private int undeadInfectionProgress = 0;
    private int dietaryComplicationStage = 0;
    private int dietaryComplicationProgress = 0;
    private int ptsdStage = 0;
    private int ptsdProgress = 0;
    private int fractureDislocationStage = 0;
    private int fractureDislocationProgress = 0;
    // M5: 扩展病种
    private int aidsStage = 0;
    private int aidsProgress = 0;
    private int tetanusStage = 0;
    private int tetanusProgress = 0;
    private int crimsonDiseaseStage = 0;
    private int crimsonDiseaseProgress = 0;
    private int hippocraticSyndromeStage = 0;
    private int hippocraticSyndromeProgress = 0;
    private int enderErosionStage = 0;
    private int enderErosionProgress = 0;
    private int stage = 0;
    private int progress = 0;

    // M3: 药物系统状态
    /** 下次可服用胶囊的游戏刻（服药冷却 300s = 6000 ticks） */
    private long nextCapsuleTick = 0L;
    /** 胶囊待生效队列 */
    private final List<PendingCapsule> pendingCapsules = new ArrayList<>();
    /** 阻断剂免疫尸毒的截止游戏刻 */
    private long undeadBlockerUntilTick = 0L;

    /** 胶囊待生效条目 */
    public record PendingCapsule(String doseType, long readyTick) {}

    public long getNextCapsuleTick() { return nextCapsuleTick; }
    public void setNextCapsuleTick(long tick) { nextCapsuleTick = tick; }

    public List<PendingCapsule> getPendingCapsules() { return pendingCapsules; }
    public void addPendingCapsule(String doseType, long readyTick) {
        pendingCapsules.add(new PendingCapsule(doseType, readyTick));
    }

    public long getUndeadBlockerUntilTick() { return undeadBlockerUntilTick; }
    public void setUndeadBlockerUntilTick(long tick) { undeadBlockerUntilTick = tick; }

    public static boolean has(Entity entity) {
        return PlatformService.CAPABILITY_HELPER.hasDisease(entity);
    }

    private static Optional<DiseaseCapability> get(Player player) {
        return PlatformService.CAPABILITY_HELPER.getDisease(player);
    }

    public static <T> T getAndApply(Player player, Function<DiseaseCapability, T> function, T orElse) {
        return get(player).map(function::apply).orElse(orElse);
    }

    public static void getAndApply(Player player, Consumer<DiseaseCapability> function) {
        get(player).ifPresent(function::accept);
    }

    private static int clampStage(int stage) {
        return Math.max(MIN_STAGE, Math.min(MAX_STAGE, stage));
    }

    private static int clampProgress(int progress) {
        return Math.max(MIN_PROGRESS, Math.min(MAX_PROGRESS, progress));
    }

    public int upperRespiratoryInfectionStage() {
        return upperRespiratoryInfectionStage;
    }

    public void setUpperRespiratoryInfectionStage(int stage) {
        upperRespiratoryInfectionStage = clampStage(stage);
    }

    public int upperRespiratoryInfectionProgress() {
        return upperRespiratoryInfectionProgress;
    }

    public void setUpperRespiratoryInfectionProgress(int progress) {
        upperRespiratoryInfectionProgress = clampProgress(progress);
    }

    public int sepsisStage() {
        return sepsisStage;
    }

    public void setSepsisStage(int stage) {
        sepsisStage = clampStage(stage);
    }

    public int sepsisProgress() {
        return sepsisProgress;
    }

    public void setSepsisProgress(int progress) {
        sepsisProgress = clampProgress(progress);
    }

    public int undeadInfectionStage() {
        return undeadInfectionStage;
    }

    public void setUndeadInfectionStage(int stage) {
        undeadInfectionStage = clampStage(stage);
    }

    public int undeadInfectionProgress() {
        return undeadInfectionProgress;
    }

    public void setUndeadInfectionProgress(int progress) {
        undeadInfectionProgress = clampProgress(progress);
    }

    public int dietaryComplicationStage() {
        return dietaryComplicationStage;
    }

    public void setDietaryComplicationStage(int stage) {
        dietaryComplicationStage = clampStage(stage);
    }

    public int dietaryComplicationProgress() {
        return dietaryComplicationProgress;
    }

    public void setDietaryComplicationProgress(int progress) {
        dietaryComplicationProgress = clampProgress(progress);
    }

    public int ptsdStage() {
        return ptsdStage;
    }

    public void setPtsdStage(int stage) {
        ptsdStage = clampStage(stage);
    }

    public int ptsdProgress() {
        return ptsdProgress;
    }

    public void setPtsdProgress(int progress) {
        ptsdProgress = clampProgress(progress);
    }

    public int fractureDislocationStage() {
        return fractureDislocationStage;
    }

    public void setFractureDislocationStage(int stage) {
        fractureDislocationStage = clampStage(stage);
    }

    public int fractureDislocationProgress() {
        return fractureDislocationProgress;
    }

    public void setFractureDislocationProgress(int progress) {
        fractureDislocationProgress = clampProgress(progress);
    }

    public int aidsStage() { return aidsStage; }
    public void setAidsStage(int s) { aidsStage = clampStage(s); }
    public int aidsProgress() { return aidsProgress; }
    public void setAidsProgress(int p) { aidsProgress = clampProgress(p); }

    public int tetanusStage() { return tetanusStage; }
    public void setTetanusStage(int s) { tetanusStage = clampStage(s); }
    public int tetanusProgress() { return tetanusProgress; }
    public void setTetanusProgress(int p) { tetanusProgress = clampProgress(p); }

    public int crimsonDiseaseStage() { return crimsonDiseaseStage; }
    public void setCrimsonDiseaseStage(int s) { crimsonDiseaseStage = clampStage(s); }
    public int crimsonDiseaseProgress() { return crimsonDiseaseProgress; }
    public void setCrimsonDiseaseProgress(int p) { crimsonDiseaseProgress = clampProgress(p); }

    public int hippocraticSyndromeStage() { return hippocraticSyndromeStage; }
    public void setHippocraticSyndromeStage(int s) { hippocraticSyndromeStage = clampStage(s); }
    public int hippocraticSyndromeProgress() { return hippocraticSyndromeProgress; }
    public void setHippocraticSyndromeProgress(int p) { hippocraticSyndromeProgress = clampProgress(p); }

    public int enderErosionStage() { return enderErosionStage; }
    public void setEnderErosionStage(int s) { enderErosionStage = clampStage(s); }
    public int enderErosionProgress() { return enderErosionProgress; }
    public void setEnderErosionProgress(int p) { enderErosionProgress = clampProgress(p); }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = clampStage(stage);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = clampProgress(progress);
    }

    public boolean hasAnyDisease() {
        return upperRespiratoryInfectionStage > 0 ||
                sepsisStage > 0 ||
                undeadInfectionStage > 0 ||
                dietaryComplicationStage > 0 ||
                ptsdStage > 0 ||
                fractureDislocationStage > 0 ||
                aidsStage > 0 ||
                tetanusStage > 0 ||
                crimsonDiseaseStage > 0 ||
                hippocraticSyndromeStage > 0 ||
                enderErosionStage > 0;
    }

    public CompoundTag serializeRespawnPersistent() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ptsd_stage", ptsdStage);
        tag.putInt("ptsd_progress", ptsdProgress);
        return tag;
    }

    public void deserializeRespawnPersistent(CompoundTag nbt) {
        ptsdStage = clampStage(nbt.getInt("ptsd_stage"));
        ptsdProgress = clampProgress(nbt.getInt("ptsd_progress"));
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("upper_respiratory_infection_stage", upperRespiratoryInfectionStage);
        tag.putInt("upper_respiratory_infection_progress", upperRespiratoryInfectionProgress);
        tag.putInt("sepsis_stage", sepsisStage);
        tag.putInt("sepsis_progress", sepsisProgress);
        tag.putInt("undead_infection_stage", undeadInfectionStage);
        tag.putInt("undead_infection_progress", undeadInfectionProgress);
        tag.putInt("dietary_complication_stage", dietaryComplicationStage);
        tag.putInt("dietary_complication_progress", dietaryComplicationProgress);
        tag.putInt("ptsd_stage", ptsdStage);
        tag.putInt("ptsd_progress", ptsdProgress);
        tag.putInt("fracture_dislocation_stage", fractureDislocationStage);
        tag.putInt("fracture_dislocation_progress", fractureDislocationProgress);
        tag.putInt("aids_stage", aidsStage);
        tag.putInt("aids_progress", aidsProgress);
        tag.putInt("tetanus_stage", tetanusStage);
        tag.putInt("tetanus_progress", tetanusProgress);
        tag.putInt("crimson_disease_stage", crimsonDiseaseStage);
        tag.putInt("crimson_disease_progress", crimsonDiseaseProgress);
        tag.putInt("hippocratic_syndrome_stage", hippocraticSyndromeStage);
        tag.putInt("hippocratic_syndrome_progress", hippocraticSyndromeProgress);
        tag.putInt("ender_erosion_stage", enderErosionStage);
        tag.putInt("ender_erosion_progress", enderErosionProgress);
        tag.putInt("stage", stage);
        tag.putInt("progress", progress);
        tag.putLong("next_capsule_tick", nextCapsuleTick);
        tag.putLong("undead_blocker_until_tick", undeadBlockerUntilTick);
        ListTag pendingTag = new ListTag();
        for (PendingCapsule pc : pendingCapsules) {
            CompoundTag pcTag = new CompoundTag();
            pcTag.putString("dose_type", pc.doseType());
            pcTag.putLong("ready_tick", pc.readyTick());
            pendingTag.add(pcTag);
        }
        tag.put("pending_capsules", pendingTag);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        upperRespiratoryInfectionStage = clampStage(nbt.getInt("upper_respiratory_infection_stage"));
        upperRespiratoryInfectionProgress = clampProgress(nbt.getInt("upper_respiratory_infection_progress"));
        sepsisStage = clampStage(nbt.getInt("sepsis_stage"));
        sepsisProgress = clampProgress(nbt.getInt("sepsis_progress"));
        undeadInfectionStage = clampStage(nbt.getInt("undead_infection_stage"));
        undeadInfectionProgress = clampProgress(nbt.getInt("undead_infection_progress"));
        dietaryComplicationStage = clampStage(nbt.getInt("dietary_complication_stage"));
        dietaryComplicationProgress = clampProgress(nbt.getInt("dietary_complication_progress"));
        ptsdStage = clampStage(nbt.getInt("ptsd_stage"));
        ptsdProgress = clampProgress(nbt.getInt("ptsd_progress"));
        fractureDislocationStage = clampStage(nbt.getInt("fracture_dislocation_stage"));
        fractureDislocationProgress = clampProgress(nbt.getInt("fracture_dislocation_progress"));
        aidsStage = clampStage(nbt.getInt("aids_stage"));
        aidsProgress = clampProgress(nbt.getInt("aids_progress"));
        tetanusStage = clampStage(nbt.getInt("tetanus_stage"));
        tetanusProgress = clampProgress(nbt.getInt("tetanus_progress"));
        crimsonDiseaseStage = clampStage(nbt.getInt("crimson_disease_stage"));
        crimsonDiseaseProgress = clampProgress(nbt.getInt("crimson_disease_progress"));
        hippocraticSyndromeStage = clampStage(nbt.getInt("hippocratic_syndrome_stage"));
        hippocraticSyndromeProgress = clampProgress(nbt.getInt("hippocratic_syndrome_progress"));
        enderErosionStage = clampStage(nbt.getInt("ender_erosion_stage"));
        enderErosionProgress = clampProgress(nbt.getInt("ender_erosion_progress"));
        stage = clampStage(nbt.getInt("stage"));
        progress = clampProgress(nbt.getInt("progress"));
        nextCapsuleTick = nbt.getLong("next_capsule_tick");
        undeadBlockerUntilTick = nbt.getLong("undead_blocker_until_tick");
        pendingCapsules.clear();
        ListTag pendingTag = nbt.getList("pending_capsules", Tag.TAG_COMPOUND);
        for (int i = 0; i < pendingTag.size(); i++) {
            CompoundTag pcTag = pendingTag.getCompound(i);
            pendingCapsules.add(new PendingCapsule(pcTag.getString("dose_type"), pcTag.getLong("ready_tick")));
        }
    }
}
