package jbwm.jbwm;

import com.google.common.collect.Lists;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public final class Jbwm extends JavaPlugin {
    public static Jbwm plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }
    @Override
    public void onEnable() {
        new TestKomenda();
        new JbwmMinezChests();
    }
    @Override
    public void onDisable() {
    }


    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final String logprefix = "[Jbwm] ";
    public static void log(Object... msg) {
        logger.info(logprefix + Func.listToString(msg, 0));
    }
    public static void warn(Object... msg) {
        logger.warning(logprefix + Func.listToString(msg, 0));
    }
    public static void error(Object...msg) {
        logger.severe(logprefix + Func.listToString(msg, 0));
    }
}
