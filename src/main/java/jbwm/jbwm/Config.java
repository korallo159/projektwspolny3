package jbwm.jbwm;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Plik konfiguracyjny
 *
 * conf - config
 * f - plik File configu
 *
 */
public class Config {
    public YamlConfiguration conf;
    public File f;

    /**
     * Tworzy plik Konfiguracyjny
     *
     * @param sc scieżka do pliku z folderu plugins/Jbwm
     */
    public Config(String sc) {
        this.f = new File(Jbwm.plugin.getDataFolder().getAbsolutePath() + "\\" + sc + ".yml");
        this.reload();
    }


    /**
     * Zapisuje plik i go przeładowywuje
     *
     */
    public void save() {
        try {
            this.conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.reload();
    }


    /**
     * Przeładowywuje plik
     *
     */
    public void reload() {
        if (!this.f.exists())
            try {
                String sc = f.getAbsolutePath();
                File dir = new File(sc.substring(0, sc.lastIndexOf("\\")));
                if (!dir.exists())
                    dir.mkdirs();

                this.f.createNewFile();
                Jbwm.log("Created new empty config " + f.getAbsolutePath());
            } catch (IOException e) {
                Jbwm.error("Failed to create " + f.getAbsolutePath());
            }

        this.conf = YamlConfiguration.loadConfiguration(f);
    }
}
