package jbwm.jbwm;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.processing.Messager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static jbwm.jbwm.Jbwm.plugin;

public class JbwmMinezChat extends JbwmCommand implements Listener {

    public JbwmMinezChat() {
        super("localchat");
        Jbwm.dodajPermisje("localchat.bypass");
        ustawKomende("krzyk", "Wpisz aby krzyczeć", Lists.newArrayList());
    }
    private boolean localchat = true;
    private HashMap<UUID, Integer> screamCooldown = new HashMap<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 1)
        return utab(args, "bypass", "toggle");
        return null;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e ){
        Player p = e.getPlayer();
        if(p.hasPermission("localchat.bypass")){
            p.setMetadata("bypass", new FixedMetadataValue(plugin, true));

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length < 1) return false;
        Player player = ((Player) sender).getPlayer();
        switch (args[0]) {
            case "reload":
                config.reload();
                player.sendMessage("Przeladowano localchat");
                break;
            case "bypass":
                if (!isChatBypassing(player)) {
                    player.setMetadata("bypass", new FixedMetadataValue(plugin, true));
                    player.sendMessage(ChatColor.GREEN +"Możesz pisać globalnie");
                } else {
                    player.removeMetadata("bypass", plugin);
                    player.sendMessage(ChatColor.RED + "Twoje wiadomosci sa lokalne");
                }
                break;
            case "toggle":
                if(localchat)
                    localchat = false;
                else
                    localchat = true;
                break;

        }
        return true;
    }
    boolean isChatBypassing(Player p) {
        return p.hasMetadata("bypass");
    }
    Config config = new Config("LocalChatConfig");
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
       String message = e.getMessage();
       if(message.contains("/krzycz")){
        p.sendMessage(message);
        return;
       }
        if(localchat)
        if (!isChatBypassing(p)) {
            int distance = config.conf.getInt("messagedistance");
            e.getRecipients().removeIf(player-> player.getLocation().distance(e.getPlayer().getLocation()) > distance && !player.hasMetadata("bypass"));

        }
        else return;
    }

}