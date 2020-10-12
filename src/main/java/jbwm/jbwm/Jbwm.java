package jbwm.jbwm;

import com.google.common.collect.Lists;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public final class Jbwm extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static String listToString(Object[] lista, int start) {
        return listToString(Lists.newArrayList(lista), start, " ");
    }
    public static String listToString(Object[] lista, int start, String wstawka) {
        return listToString(Lists.newArrayList(lista), start, wstawka);
    }
    public static String listToString(List<?> lista, int start) {
        return listToString(lista, start, " ");
    }
    public static String listToString(List<?> lista, int start, String wstawka) {
        StringBuilder s = new StringBuilder(lista.size() > start ? ""+lista.get(start) : "");
        int i=0;
        for (Object obj : lista)
            if (i++ > start)
                s.append(wstawka).append(obj == null ? null : obj.toString());
        return s.toString();
    }

    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final String logprefix = "[Jbwm] ";
    public static void log(Object... msg) {
        logger.info(logprefix + listToString(msg, 0));
    }
    public static void warn(Object... msg) {
        logger.warning(logprefix + listToString(msg, 0));
    }
    public static void error(Object...msg) {
        logger.severe(logprefix + listToString(msg, 0));
    }
}
