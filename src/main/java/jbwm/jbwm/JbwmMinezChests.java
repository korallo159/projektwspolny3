package jbwm.jbwm;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static jbwm.jbwm.Jbwm.plugin;

/**
 *
 * 0.1 jak zniszczysz skrzynie recznie, to usuwa rowniez w configu z id
 * 1.jak ktos grzebie w skrzyni to usuwa po 5 min
 * 3.zrobic tak, aby komenda mozna bylo ustawic ile dana skrzynia ma respawnu
 *
 *
 */

public class JbwmMinezChests extends JbwmCommand implements Listener {
    final Config config = new Config("Treasure chests");

    public JbwmMinezChests() {
        super("tchest");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 1)
            return utab(args, "addnew", "remove", "editor", "tp");
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length < 1) return false;
        Player player = ((Player) sender).getPlayer();
        switch (args[0]) {
            case "addnew":
                player.getInventory().addItem(getTchest(1));
                player.sendMessage(ChatColor.RED + "Stworzyles treasure chesta. Wloz do niego przedmioty. Po zamknieciu przedmioty sie zapisza.");
                break;
            case "remove":
                if (StringUtils.isNumeric(args[0])) {
                    Integer id = Integer.valueOf(args[0]);
                    if (this.config.conf.get("Chest." + id) != null) {
                        Block b = player.getWorld().getBlockAt(this.config.conf.getLocation("Chest." + id + ".Location"));
                        b.setType(Material.AIR);
                        this.config.conf.set("Chest." + id, null);
                        this.config.save();
                        player.sendMessage("Usunales skrzynie z ID: " + (args[0]));
                    } else
                        player.sendMessage("Nie ma skrzyni z takim ID");
                }
                break;
            case "tp":
                if (StringUtils.isNumeric(args[0])) {
                    Integer id = Integer.valueOf(args[0]);
                    if (this.config.conf.get("Chest." + id + ".Location") != null) {
                        player.teleport(this.config.conf.getLocation("Chest." + id + ".Location"));
                        player.sendMessage("Przeteleportowano do skrzyni z id:" + id);
                    } else player.sendMessage("Nie ma skrzyni z takim ID");
                }
                break;
            case "editor":
                if (!isEditing(player)) {
                    player.setMetadata("edytor", new FixedMetadataValue(plugin, true));
                    player.sendMessage("Wlaczyles edytowanie tchestow");
                } else {
                    player.removeMetadata("edytor", plugin);
                    player.sendMessage("Wylaczyles edytowanie tchestow.");
                }
        }
        return true;
    }

    boolean isEditing(Player p) {
        return p.hasMetadata("edytor");
    }

    public ItemStack getTchest(int amount) {
        ItemStack tchest = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = tchest.getItemMeta();

        itemMeta.setDisplayName(ChatColor.RED + "Treasure chest");

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_RED + "treasure chest");
        itemMeta.setLore(lore);

        tchest.setItemMeta(itemMeta);

        tchest.setAmount(amount);

        return tchest;
    }


    /**
     * *podczas zamykania inv sprawdza czy ma permisje tchest.create jesli tak, to sprawdza czy ma editora.
     * *reszta zapisuje do configu items bez serialize
     * *kazda nowa skrzynka to nowe ID, zeby mozna bylo sie fajnie do nich odnosic
     **/
    @EventHandler
    public void onTChestClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        InventoryHolder holder = e.getInventory().getHolder();

        Chest chest;
        if (holder instanceof DoubleChest)
            chest = (Chest) ((DoubleChest) holder).getLocation().getBlock().getState();
        else if (holder instanceof Chest)
            chest = (Chest) e.getInventory().getHolder();
        else
            return;

        String chestName = chest.getCustomName();
        if (chestName == null || !chestName.equals(ChatColor.RED + "Treasure chest")) return;

        if (isEditing(p))
            onTChestCloseSaveItems(p, holder.getInventory().getLocation());
        else
            onChestCloseRemove(holder.getInventory().getLocation());
    }

    void onTChestCloseSaveItems(Player p, Location loc) {
        Block b = loc.getBlock();;
        Chest chest = (Chest) b.getState();
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack is : chest.getInventory())
            if (is != null)
                items.add(is);

        Consumer<Object> save = i -> {
            config.conf.set("Chest." + i + ".Items", items);
            config.conf.set("Chest." + i + ".Location", loc);
            config.save();
        };


        String id = findChest(loc);
        if (id != null) {
            save.accept(id);
            p.sendMessage("Zedytowano zawartość treasure chesta.");
        } else {
            int counter = -1;
            while (config.conf.contains("Chest." + ++counter));

            save.accept(counter);
            p.sendMessage("Utowrzono nowy treasure chest.");
        }
    }

    /**
     *  szuka id szkini w configu przez lokacje
     *
     * @param loc szukana lokacja
     * @return id skrzyni w configu
     */
    String findChest(Location loc) {
        ConfigurationSection section = config.conf.getConfigurationSection("Chest");
        if (section != null)
            for (String id : section.getKeys(false))
                if (loc.equals(config.conf.get("Chest." + id + ".Location")))
                    return id;
        return null;
    }

    void onChestCloseRemove(Location loc) {

        Chest oldChest = (Chest) loc.getBlock().getState();

        String id = findChest(loc);
        if (id == null) return;
        List<ItemStack> items = (List<ItemStack>) config.conf.getList("Chest." + id + ".Items");

        Block block = oldChest.getBlock();

        Supplier<Location> secondChest = () -> {
          if (("" + loc.getX()).endsWith(".5")) {
              Jbwm.log(1);
              return loc.clone().add(1, 0, 0);
          }
          else {
              Jbwm.log(2);
              return loc.clone().add(0, 0, 1);
          }
        };

        boolean doubleChest = !oldChest.getBlockData().getAsString().contains("type=single");
        if (doubleChest)
            secondChest.get().getBlock().setType(Material.AIR);

        block.setType(Material.AIR);

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            block.setType(oldChest.getType());
            block.setBlockData(oldChest.getBlockData());

            Chest newChest = (Chest) block.getState();
            if (doubleChest) {
                Block block2 = secondChest.get().getBlock();
                block2.setType(oldChest.getType());
                BlockData data2 = Bukkit.createBlockData(oldChest.getBlockData().getAsString().replace("type=left", "type=right"));
                block2.setBlockData(data2);
                block2.getState().update();
            }
            newChest.setCustomName(oldChest.getCustomName());
            newChest.update();

            insertItems(newChest.getInventory(), items);
        }, 80L);
    }

    /**
     * wkłada losowe itemy z listy items do inventory inv
     *
     * @param inv Inventory
     * @param items lista itemów
     */
    void insertItems(Inventory inv, List<ItemStack> items) {
        int change = 40;
        Random rand = new Random();
        for (ItemStack item : items) {
            if (rand.nextInt(100) < change) {
                int slot = rand.nextInt(inv.getSize());
                inv.setItem(slot, item);
            }
        }
    }

}


