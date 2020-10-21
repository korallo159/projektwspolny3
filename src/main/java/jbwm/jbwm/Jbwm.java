package jbwm.jbwm;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Jbwm extends JavaPlugin {
    public static Jbwm plugin;



    /**
     * tablica wszystkich klas z projektu, które muszą być stworzone tylko raz
     *
     */
    Class<?>[] classes = new Class<?>[] {TestKomenda.class, JbwmMinezChests.class, JbwmMinezChat.class};


    @Override
    public void onLoad() {
        plugin = this;
    }
    @Override
    public void onEnable() {
        final Config test = new Config("test"); // TODO usunąć
        // Tworzenie głównych klas modułów
        for (Class<?> clazz : this.classes)
            this.createInstance(clazz);
    }
    @Override
    public void onDisable() {
    }

    /**
     * Tworzy obkiet z klasy i go rejestruje
     *
     * @param clazz klasa obiektu
     */
    private void createInstance(Class<?> clazz) {
        try {
            this.register(clazz.newInstance());
        } catch (Throwable e) {
            Jbwm.error("Failed to create " + clazz.getSimpleName());
        }
    }
    /**
     * Rejestruje objekt tam gdzie to możliwe
     *
     * @param obj rejestrowany obiekt
     */
    private void register(Object obj) {
        if (obj instanceof Listener)
            this.getServer().getPluginManager().registerEvents((Listener) obj, this);
    }

    public static void dodajPermisje(String... permisje) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (String permisja : permisje)
            pluginManager.addPermission(new org.bukkit.permissions.Permission(permisja));
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
