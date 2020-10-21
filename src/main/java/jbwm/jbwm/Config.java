package jbwm.jbwm;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

                if (!wyjmijPlik(sc.substring(sc.lastIndexOf("\\") + 1), sc)) {
                    f.createNewFile();
                    String path = f.getAbsolutePath();
                    Jbwm.log("Created new empty config " + path);
                }
            } catch (IOException e) {
                Jbwm.error("Failed to create " + f.getAbsolutePath());
            }
        this.conf = YamlConfiguration.loadConfiguration(f);
    }


    @SuppressWarnings("resource")
    public static boolean wyjmijPlik(String co, String gdzie) {
        Jbwm.warn(co, gdzie);
        File f2 = new File(gdzie);
        try {
            JarFile jar = new JarFile("plugins/JBWM.jar");
            JarEntry plik = jar.getJarEntry(co);
            if (plik == null)
                return false;
            InputStream inputStream = jar.getInputStream(plik);

            int read;
            byte[] bytes = new byte[1024];
            FileOutputStream outputStream = new FileOutputStream(f2);

            while ((read = inputStream.read(bytes)) != -1)
                outputStream.write(bytes, 0, read);

            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
