package com.lastimp.dgh.common.utils;

import com.lastimp.dgh.common.capability.HealthCapability;
import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;


public abstract class Utils {
    public static final String MODID = "dgh";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final float DELTA = 0.05f;
    public static final float EPS = 0.0001f;

    public static final RandomSource randomSource = RandomSource.create(987654321);

    public static void drop(Item item, LivingEntity entity, int amount) {
        var stack=  new ItemStack(item, amount);
        drop(stack, entity);
    }

    public static void drop(ItemStack stack, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (!player.addItem(stack)) {
                player.drop(stack, true, true);
            }
        } else {
            entity.level().addFreshEntity(new ItemEntity(entity.level(),entity.getX(), entity.getY(), entity.getZ(), stack.copy()));
        }
    }

    public static List<LivingEntity> getLivingWithHealth(ServerLevel level, Vec3 center, int range) {
        return level.getEntitiesOfClass(
                LivingEntity.class, AABB.ofSize(center, range, range, range),
                HealthCapability::has
        );
    }

    public static LivingEntity getLivingWithHealth(ServerLevel level, UUID uuid) {
        if (level.getEntity(uuid) instanceof LivingEntity livingEntity)
            if (HealthCapability.has(livingEntity))
                return livingEntity;
        return null;
    }

    public static boolean checkPlayerInvincible(ServerPlayer player) {
        return player.level().getDifficulty() == Difficulty.PEACEFUL || player.gameMode.isCreative() || player.isSpectator();
    }

    public static void broadcastMessageToTeam(ServerPlayer player, Component component) {
        Team team = player.getTeam();
        if (team != null && team.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                player.server.getPlayerList().broadcastSystemToTeam(player, component);
            } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                player.server.getPlayerList().broadcastSystemToAllExceptTeam(player, component);
            }
        } else {
            player.server.getPlayerList().broadcastSystemMessage(component, false);
        }
    }

    public static void broadcastMessageToTeam(LivingEntity livingEntity, Component component) {
        var server = livingEntity.getServer();
        if (server != null) {
            server.getPlayerList().broadcastSystemMessage(component, false);
        }
    }

    public static void addParticlesAroundSelf(ParticleOptions particleOption, LivingEntity target) {
        for(int i = 0; i < 5; ++i) {
            double d0 = target.getRandom().nextGaussian() * 0.02;
            double d1 = target.getRandom().nextGaussian() * 0.02;
            double d2 = target.getRandom().nextGaussian() * 0.02;
            target.level().addParticle(particleOption, target.getRandomX(1.0F), target.getRandomY() + (double)1.0F, target.getRandomZ(1.0F), d0, d1, d2);
        }
    }

    public static float sqrtFactor(float x, float q) {
        return (float) ((1 + q) * x / (q + Math.sqrt(x)));
    }

    public static float randomBetween(float min, float max) {
        return Mth.randomBetween(randomSource, min, max);
    }

    public static int getRandomIndex(float... weight) {
        for (int i = 0; i < weight.length; i++) {
            weight[i] *= Mth.randomBetween(randomSource, 0.0f, 1.0f);
        }
        int max = 0;
        float max_value = weight[0];
        for (int i = 1; i < weight.length; i++) {
            if (weight[i] > max_value) {
                max = i;
                max_value = weight[i];
            }
        }
        return max;
    }

    public static float[] getRandom(float ... weight) {
        float sum = 0;
        for (int i = 0; i < weight.length; i++) {
            weight[i] *= Mth.randomBetween(randomSource, 0.0f, 1.0f);
            sum += weight[i];
        }
        for (int i = 0; i < weight.length; i++) {
            weight[i] /= sum;
        }
        return weight;
    }

    public static boolean randomCheck(float value, float threshold, float factor, float p_min, float p_max) {
        if (value < threshold) return false;
        var check = Mth.randomBetween(randomSource, 0.0f, 1.0f);
        var prob = (value - threshold) / factor;
        return check < Mth.clamp(prob, p_min, p_max);
    }

    public static boolean randomCheck(float value, float threshold, float factor, float p_min, float p_max, int checkTimes) {
        if (checkTimes < 0) {
            do {
                if (randomCheck(value, threshold, factor, p_min, p_max)) return true;
            } while (++checkTimes <= 0);
            return false;
        } else {
            do {
                if (!randomCheck(value, threshold, factor, p_min, p_max)) return false;
            } while (--checkTimes >= 0);
            return true;
        }
    }
}
