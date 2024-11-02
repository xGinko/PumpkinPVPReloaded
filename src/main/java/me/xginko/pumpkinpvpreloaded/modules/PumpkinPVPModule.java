package me.xginko.pumpkinpvpreloaded.modules;

import com.google.common.collect.ImmutableSet;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.utils.Disableable;
import me.xginko.pumpkinpvpreloaded.utils.Enableable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PumpkinPVPModule implements Enableable, Disableable {

    protected static final Set<Class<PumpkinPVPModule>> AVAILABLE_MODULES;
    protected static final Set<PumpkinPVPModule> ENABLED_MODULES;

    static {
        AVAILABLE_MODULES = new Reflections(PumpkinPVPModule.class.getPackage().getName())
                .get(Scanners.SubTypes.of(PumpkinPVPModule.class).asClass())
                .stream()
                .filter(clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                .map(clazz -> (Class<PumpkinPVPModule>) clazz)
                .sorted(Comparator.comparing(Class::getSimpleName))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
        ENABLED_MODULES = new HashSet<>();
    }

    protected final PumpkinPVPReloaded plugin;
    protected final PumpkinPVPConfig config;
    protected final GracefulScheduling scheduling;
    protected final String configPath, logFormat;
    protected final boolean enabled_in_config;

    public PumpkinPVPModule(String configPath, boolean defEnabled) {
        this(configPath, defEnabled, null);
    }

    public PumpkinPVPModule(String configPath, boolean defEnabled, String comment) {
        this.configPath = configPath;
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.config = PumpkinPVPReloaded.config();
        this.scheduling = PumpkinPVPReloaded.scheduling();

        if (comment == null || comment.isEmpty()) {
            this.enabled_in_config = config.getBoolean(configPath + ".enable", defEnabled);
        } else {
            this.enabled_in_config = config.getBoolean(configPath + ".enable", defEnabled, comment);
        }

        String[] paths = configPath.split("\\.");
        if (paths.length <= 2) {
            this.logFormat = "<" + configPath + "> {}";
        } else {
            this.logFormat = "<" + paths[paths.length - 2] + "." + paths[paths.length - 1] + "> {}";
        }
    }

    public boolean shouldEnable() {
        return enabled_in_config;
    }

    public static void disableAll() {
        ENABLED_MODULES.forEach(Disableable::disable);
        ENABLED_MODULES.clear();
    }

    public static void reloadModules() {
        disableAll();

        for (Class<PumpkinPVPModule> moduleClass : AVAILABLE_MODULES) {
            try {
                PumpkinPVPModule module = moduleClass.getDeclaredConstructor().newInstance();
                if (module.shouldEnable()) {
                    ENABLED_MODULES.add(module);
                }
            } catch (Throwable t) { // This is not laziness. We want to catch everything here if it fails to init
                PumpkinPVPReloaded.logger().warn("Failed initialising module class '{}'.", moduleClass.getSimpleName(), t);
            }
        }

        ENABLED_MODULES.forEach(Enableable::enable);
    }

    protected void error(String message, Throwable throwable) {
        PumpkinPVPReloaded.logger().error(logFormat, message, throwable);
    }

    protected void error(String message) {
        PumpkinPVPReloaded.logger().error(logFormat, message);
    }

    protected void warn(String message) {
        PumpkinPVPReloaded.logger().warn(logFormat, message);
    }

    protected void info(String message) {
        PumpkinPVPReloaded.logger().info(logFormat, message);
    }

    protected void notRecognized(Class<?> clazz, String unrecognized) {
        warn("Unable to parse " + clazz.getSimpleName() + " at '" + unrecognized + "'. Please check your configuration.");
    }
}
