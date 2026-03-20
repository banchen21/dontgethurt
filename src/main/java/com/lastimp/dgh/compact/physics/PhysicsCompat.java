package com.lastimp.dgh.compact.physics;

import com.lastimp.dgh.common.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Utils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PhysicsCompat {
    private static final Logger LOGGER = LogManager.getLogger("dgh-physics-compat");

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("physicsmod")) return;
        LOGGER.info("Physics Mod detected — applying non-mixin compat protections");

        // Existing logger fallbacks
        safeEnsureLogger("net.diebuddies.config.ConfigAnimations");
        safeEnsureLogger("net.diebuddies.minecraft.weather.WeatherEffects");

        // Best-effort neutralization for known problematic classes from physics mod.
        // We cannot add missing fields expected by a binary compiled against different
        // mappings, but we can try to replace static Supplier/Function-like fields
        // with no-op stubs to reduce likelihood of NoSuchMethodError/NullPointer.
        safeNeutralizeClass("net.diebuddies.bridge.KeyBindingsRegistry");
        safeNeutralizeClass("net.diebuddies.minecraft.weather.WeatherParticlesRegistry");
    }

    private static void safeEnsureLogger(String className) {
        try {
            Class<?> cls = Class.forName(className);
            try {
                Field f = cls.getDeclaredField("LOGGER");
                f.setAccessible(true);
                Object cur = f.get(null);
                if (cur == null) {
                    f.set(null, LogManager.getLogger("physicsmod-compat"));
                    LOGGER.info("Injected fallback LOGGER into {}", className);
                } else {
                    LOGGER.debug("{} already has LOGGER", className);
                }
            } catch (NoSuchFieldException nsf) {
                LOGGER.debug("{} has no LOGGER field", className);
            } catch (Throwable t) {
                LOGGER.warn("Failed to set LOGGER on {}: {}", className, t.toString());
            }
        } catch (ClassNotFoundException cnf) {
            LOGGER.debug("Physics class not present: {}", className);
        } catch (Throwable t) {
            LOGGER.warn("Unexpected error while handling {}: {}", className, t.toString());
        }
    }

    private static void safeNeutralizeClass(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                int mods = f.getModifiers();
                if (!Modifier.isStatic(mods)) continue;
                f.setAccessible(true);
                Class<?> t = f.getType();
                try {
                    if (Supplier.class.isAssignableFrom(t)) {
                        Supplier<?> noOp = () -> null;
                        f.set(null, noOp);
                        LOGGER.info("Replaced Supplier static field {} in {} with no-op", f.getName(), className);
                    } else if (Function.class.isAssignableFrom(t)) {
                        Function<?, ?> noOpF = (o) -> null;
                        f.set(null, noOpF);
                        LOGGER.info("Replaced Function static field {} in {} with no-op", f.getName(), className);
                    } else if (BiFunction.class.isAssignableFrom(t)) {
                        BiFunction<?, ?, ?> noOpB = (a, b) -> null;
                        f.set(null, noOpB);
                        LOGGER.info("Replaced BiFunction static field {} in {} with no-op", f.getName(), className);
                    } else {
                        // For other reference types, if currently null try to set a harmless default (null leave)
                        Object cur = null;
                        try { cur = f.get(null); } catch (Throwable ignored) {}
                        if (cur == null) {
                            // nothing safe to set generically
                            LOGGER.debug("Static field {} in {} is null (no replacement)", f.getName(), className);
                        }
                    }
                } catch (Throwable tt) {
                    LOGGER.debug("Failed to neutralize field {} in {}: {}", f.getName(), className, tt.toString());
                }
            }
        } catch (ClassNotFoundException cnf) {
            LOGGER.debug("Physics class not present: {}", className);
        } catch (Throwable t) {
            LOGGER.warn("Unexpected error while neutralizing {}: {}", className, t.toString());
        }
    }
}
