package jbwm.jbwm;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jbwm.jbwm.Jbwm.plugin;

public class JbwmMinezChests extends JbwmCommand implements Listener {
    final Config config = new Config("Treasure chests");
    static HashMap<String, Boolean> editor = new HashMap<>();

    public JbwmMinezChests() {
        super("tchest");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();
            if(command.getName().equals("tchestaddnew")){
                player.getInventory().addItem(getTchest(1));
                player.sendMessage(ChatColor.RED + "Stworzyles treasure chesta. Wloz do niego przedmioty. Po zamknieciu przedmioty sie zapisza.");
            }
            if(command.getName().equals("tchestremove") && StringUtils.isNumeric(args[0])){
                Integer id = Integer.valueOf(args[0]);
                if(this.config.conf.get("Chest." +id) != null) {
                    Block b = player.getWorld().getBlockAt(this.config.conf.getLocation("Chest." + id + ".Location"));
                    b.setType(Material.AIR);
                    this.config.conf.set("Chest." + id, null);
                    this.config.save();
                    player.sendMessage("Usunales skrzynie z ID: " + (args[0]));
                }
                else
                    player.sendMessage("Nie ma skrzyni z takim ID");
            }
            if(command.getName().equals("tchesttp") && StringUtils.isNumeric(args[0])){
                Integer id = Integer.valueOf(args[0]);
                if(this.config.conf.get("Chest." +id + ".Location") != null) {
                    player.teleport(this.config.conf.getLocation("Chest." + id + ".Location"));
                    player.sendMessage("Przeteleportowano do skrzyni z id:" + id);
                }
                else player.sendMessage("Nie ma skrzyni z takim ID");

            }

            if(command.getName().equals("tchesteditor")){
                if(editor.get(player.getUniqueId().toString()) == null || editor.get(player.getUniqueId().toString()) == false) {
                    editor.put(player.getUniqueId().toString(), true);
                    player.sendMessage("Wlaczyles edytowanie treasure chestow");
                }
                else if(editor.get(player.getUniqueId().toString()) == true){
                    editor.put(player.getUniqueId().toString(), false);
                    player.sendMessage("Wylaczyles edytowanie tchestow.");
                }
            }


        }
        else return true;


        return true;
    }

    public ItemStack getTchest(int amount) {
        ItemStack tchest = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = tchest.getItemMeta();

        itemMeta.setDisplayName(ChatColor.RED + "Treasure chest");

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_RED + "treasure chest");
        itemMeta.setLore(lore);

        tchest.setItemMeta(itemMeta);

        return tchest;
    }
    /**
     **podczas zamykania inv sprawdza czy ma permisje tchest.create jesli tak, to sprawdza czy ma editora.
     **reszta zapisuje do configu items bez serialize
     **kazda nowa skrzynka to nowe ID, zeby mozna bylo sie fajnie do nich odnosic
     **/
    @EventHandler
    public void onTChestCloseSaveItems(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getHolder() instanceof Chest && ((Chest) e.getInventory().getHolder()).getCustomName() !=null
                && ((Chest) e.getInventory().getHolder()).getCustomName().equals(ChatColor.RED + "Treasure chest")
                && p.hasPermission("tchest.create"))
        {
            if (editor.containsKey(p.getUniqueId().toString()) && editor.get(p.getUniqueId().toString()) == true ) {
                Chest chest = (Chest) e.getInventory().getHolder();
                Block b = chest.getBlock();
                ArrayList<ItemStack> items = new ArrayList<>();
                for (ItemStack is : e.getInventory()) {
                    if (is != null)
                        items.add(is);
                }
                int counter = 0;
                if (config.conf.getConfigurationSection("Chest." + counter) == null) {
                    config.conf.set("Chest." + counter + ".Items",items);
                    config.conf.set("Chest." + counter + ".Location", b.getLocation());
                    this.config.save();
                }
                for (final String ids : config.conf.getConfigurationSection("Chest.").getKeys(false)) {
                    if (b.getLocation().equals(config.conf.get("Chest." + counter + ".Location"))) {
                        p.sendMessage("Zedytowano zawartość treasure chesta.");
                        break;
                    }
                    counter++;
                }
                config.conf.set("Chest." + counter + ".Items",items);
                config.conf.set("Chest." + counter + ".Location", b.getLocation());
                this.config.save();
            }
        }
        else
            return;
    }

    /**
     Tu na szybkosci zrobilem, ze jak gracz zamyka to usuwa skrzynie, wypaduja itemy i pojawia sie skrzynia po prostu zarys,
     */

    @EventHandler
    public void onSingleChestCloseRemove(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getHolder() instanceof Chest
                && ((Chest) e.getInventory().getHolder()).getCustomName() !=null
                &&  ((Chest) e.getInventory().getHolder()).getCustomName().equals(ChatColor.RED + "Treasure chest")) {
            if(!editor.containsKey(p.getUniqueId().toString()) || editor.get(p.getUniqueId().toString()) == false ) {
                Chest chest = (Chest) e.getInventory().getHolder();
                Block block = chest.getBlock();
                Material poprzedni = block.getType();
                block.setType(Material.AIR);
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> block.setType(poprzedni), 80L);
                block.getState();
            }
            else
                return;
        }
    }
}

