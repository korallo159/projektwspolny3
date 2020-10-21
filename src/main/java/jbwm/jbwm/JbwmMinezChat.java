package jbwm.jbwm;

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

import java.util.List;
import static jbwm.jbwm.Jbwm.plugin;

public class JbwmMinezChat extends JbwmCommand implements Listener {

    public JbwmMinezChat() {
        super("localchat");
        Jbwm.dodajPermisje("localchat.bypass");
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 1)
        return utab(args, "bypass");
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length < 1) return false;
        Player player = ((Player) sender).getPlayer();
        switch (args[0]) {
            case "bypass":
                if (!isChatBypassing(player)) {
                    player.setMetadata("bypass", new FixedMetadataValue(plugin, true));
                    player.sendMessage(ChatColor.GREEN +"Możesz pisać globalnie");
                } else {
                    player.removeMetadata("bypass", plugin);
                    player.sendMessage(ChatColor.RED + "Twoje wiadomosci sa lokalne");
                }
        }
        return true;
    }
    boolean isChatBypassing(Player p) {
        return p.hasMetadata("bypass");
    }
    Config config = new Config("LocalChatConfig");
    @EventHandler
    public void onPlayerNearbyChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!isChatBypassing(p)) {
            int distance = config.conf.getInt("messagedistance");
            e.getRecipients().removeIf(player-> player.getLocation().distance(e.getPlayer().getLocation()) > distance);

        }
        else return;
    }

}