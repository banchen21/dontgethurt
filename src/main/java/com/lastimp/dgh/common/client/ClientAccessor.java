package com.lastimp.dgh.common.client;

import com.lastimp.dgh.common.utils.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class ClientAccessor {
    private static final Lazy<Minecraft> MINECRAFT = Lazy.of(Minecraft::getInstance);

    public static Minecraft mc() {
        return MINECRAFT.get();
    }

    public static boolean canRenderGui() {
        Minecraft mc = Minecraft.getInstance();
        return !(mc.level == null || mc.player == null || mc.options.hideGui);
    }

    public static ClientLevel getLevel() {
        return Minecraft.getInstance().level;
    }

    public static long getGameTime() {
        return mc().level.getGameTime();
    }

    public static Optional<LocalPlayer> getPlayer() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

    public static @NotNull LocalPlayer getPlayerOrThrow() {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    public static LivingEntity getLiving(ClientLevel level, UUID uuid, Vec3 center, int range) {
        var result = level.getEntitiesOfClass(
                LivingEntity.class, AABB.ofSize(center, range, range, range),
                (entity) -> entity.getUUID().equals(uuid)
        );
        if (!result.isEmpty())
            return result.get(0);
        return null;
    }

    public static LivingEntity getLiving(int id) {
        var result = getLevel().getEntity(id);
        if (result instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }

    public static void setHandsBusy(LocalPlayer player, boolean busy) {
        if (player == null) return;
        try {
            // try method first
            try {
                var m = player.getClass().getMethod("setHandsBusy", boolean.class);
                m.invoke(player, busy);
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // try field fallback (common accessor name guesses)
            for (String name : new String[]{"handsBusy", "isHandsBusy", "hands_busy"}) {
                try {
                    var f = player.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                        f.setBoolean(player, busy);
                        return;
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void setMissTime(int t) {
        try {
            Minecraft mc = Minecraft.getInstance();
            try {
                var m = mc.getClass().getMethod("setMissTime", int.class);
                m.invoke(mc, t);
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // try field fallback
            for (String name : new String[]{"missTime", "tickMissTime", "miss_time"}) {
                try {
                    var f = mc.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    if (f.getType() == int.class || f.getType() == Integer.class) {
                        f.setInt(mc, t);
                        return;
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
