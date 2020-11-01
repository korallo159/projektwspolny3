package jbwm.jbwm;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static jbwm.jbwm.Jbwm.plugin;

public class JbwmMinezChests extends JbwmCommand implements Listener {
    // id: location
    final Config configLocations = new Config("TreasurechestsLocations");

    public JbwmMinezChests() {
        super("tchest");
        placeAllTChests();
    }

    void placeAllTChests() {
        for (Map.Entry<String, Object> entry : configLocations.conf.getValues(false).entrySet())
            placeTChest(getConfig(entry.getKey()), (Location) entry.getValue());
    }

    boolean isEditing(Player p) {
        return p.hasMetadata("edytor");
    }

    public ItemStack getTchest(int amount) {
        ItemStack tchest = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = tchest.getItemMeta();

        itemMeta.setDisplayName(ChatColor.RED + "Treasure chest");

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_RED + "treasure chest");
        itemMeta.setLore(lore);

        tchest.setItemMeta(itemMeta);

        tchest.setAmount(amount);

        return tchest;
    }
    Chest getTchest(InventoryEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        Chest chest;
        if (holder instanceof DoubleChest)
            chest = (Chest) ((DoubleChest) holder).getLocation().getBlock().getState();
        else if (holder instanceof Chest)
            chest = (Chest) e.getInventory().getHolder();
        else
            return null;

        String chestName = chest.getCustomName();
        if (chestName == null || !chestName.equals(ChatColor.RED + "Treasure chest")) return null;

        return chest;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Chest chest = getTchest(e);
        if (chest == null) return;

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () ->
            removeChest(chest.getInventory().getLocation())
        , 5 * 60 * 20);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isEditing(e.getPlayer())) return;

        BlockState state = e.getBlock().getState();
        if (!(state instanceof Chest)) return;
        Chest chest = (Chest) state;

        if (chest.getCustomName() == null) return;
        if (!chest.getCustomName().equals(ChatColor.RED + "Treasure chest")) return;

        Location loc = chest.getInventory().getLocation();

        String id = findChest(loc);
        if (id == null) return;

        getConfig(id).f.delete();
        configLocations.conf.set(id, null);
        configLocations.save();

        if (!chest.getBlockData().getAsString().contains("type=single"))
            getSecondChest(chest).getBlock().setType(Material.AIR);
        loc.getBlock().setType(Material.AIR);

        e.getPlayer().sendMessage("Usunięto Tchesta");
    }
    @EventHandler
    public void onTChestClose(InventoryCloseEvent e) {
        Chest chest = getTchest(e);
        if (chest == null) return;

        Player p = (Player) e.getPlayer();

        if (isEditing(p))
            onTChestCloseSaveItems(p, chest.getInventory().getLocation());
    }

    void onTChestCloseSaveItems(Player p, Location loc) {
        Block b = loc.getBlock();;
        Chest chest = (Chest) b.getState();
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack is : chest.getInventory())
            if (is != null)
                items.add(is);

        Consumer<Object> save = i -> {
            String sc = "" + i;
            Config config = getConfig(sc);
            config.set("Items", items);
            config.set("Data", chest.getBlockData().getAsString());
            config.setDefault("Respawn", 5 * 60 * 60 * 20);
            config.save();

            configLocations.set(sc, loc);
            configLocations.save();
        };


        String id = findChest(loc);
        if (id != null) {
            save.accept(id);
            p.sendMessage("Zedytowano zawartość treasure chesta.");
        } else {
            int counter = -1;
            while (configLocations.conf.contains("" + ++counter));

            save.accept(counter);
            p.sendMessage("Utowrzono nowy treasure chest.");
        }
    }

    /**
     *  szuka id szkrzyni w configu przez lokacje
     *
     * @param loc szukana lokacja
     * @return id skrzyni w configu
     */
    String findChest(Location loc) {
        for (Map.Entry<String, Object> entry : configLocations.conf.getValues(false).entrySet())
            if (loc.equals(entry.getValue()))
                return entry.getKey();
        return null;
    }
    Location getSecondChest(Chest chest) {
        return getSecondChest(chest.getInventory().getLocation());
    }
    Location getSecondChest(Location loc) {
        if (("" + loc.getX()).endsWith(".5"))
            return loc.clone().add(1, 0, 0);
        else
            return loc.clone().add(0, 0, 1);
    }
    Config getConfig(String tChestId) {
        return new Config("Treasure Chests/" + tChestId);
    }

    void removeChest(Location loc) {
        if (!loc.getBlock().getType().equals(Material.CHEST))
            return;

        Chest oldChest = (Chest) loc.getBlock().getState();

        String id = findChest(loc);
        if (id == null) return;

        Block block = oldChest.getBlock();

        Location _loc = getSecondChest(oldChest);
        Supplier<Location> secondChest = () -> _loc;

        boolean doubleChest = !oldChest.getBlockData().getAsString().contains("type=single");
        if (doubleChest)
            secondChest.get().getBlock().setType(Material.AIR);

        block.setType(Material.AIR);

        Config config = getConfig(id);


        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            placeTChest(config, loc);
        }, config.conf.getLong("Respawn"));
    }

    boolean isDoubleChest(Location loc) {
        return ("" + loc.getX()).endsWith(".5") || ("" + loc.getZ()).endsWith(".5");
    }

    void placeTChest(Config config, Location loc) {
        if (loc.getBlock().getType().equals(Material.CHEST))
            return;
        String data = config.conf.getString("Data");
        List<ItemStack> items = (List<ItemStack>) config.conf.getList("Items");

        Block block = loc.getBlock();

        block.setType(Material.CHEST);
        block.setBlockData(Bukkit.createBlockData(data));

        Chest newChest = (Chest) block.getState();
        newChest.setCustomName(ChatColor.RED + "Treasure chest");
        if (isDoubleChest(loc)) {
            Block block2 = getSecondChest(loc).getBlock();
            block2.setType(Material.CHEST);
            data = data.contains("type=left") ? data.replace("type=left", "type=right") : data.replace("type=right", "type=left");
            block2.setBlockData(Bukkit.createBlockData(data));
            ((Chest) block2.getState()).setCustomName(ChatColor.RED + "Treasure chest");
        }
        newChest.setCustomName(ChatColor.RED + "Treasure chest");
        newChest.update();

        insertItems(newChest.getInventory(), items);
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


    // Command

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 1)
            return utab(args, "addnew"/*, "remove"*/, "editor", "tp", "setrespawntime", "reload");
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
            /*case "remove":
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
            */
            case "reload":
                configLocations.reload();
                break;
            case "tp":
                if (StringUtils.isNumeric(args[0])) {
                    String id = "" + Integer.valueOf(args[0]);
                    if (this.configLocations.conf.get(id) != null) {
                        player.teleport(this.configLocations.conf.getLocation(id));
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
                break;
            case "setrespawntime":
                if (args.length < 2){
                    player.sendMessage("/tchest setrespawntime <minutes>");
                    break;
                }
                int minutes = 0;
                try {
                    minutes = Integer.parseInt(args[1]);
                } catch (Throwable e) {
                    player.sendMessage("incorrect minutes");
                    break;
                }
                Block block = player.getTargetBlock(5);
                if (block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();

                    String chestName = chest.getCustomName();
                    if (chestName == null || !chestName.equals(ChatColor.RED + "Treasure chest")) break;

                    String id = findChest(chest.getInventory().getLocation());
                    if (id == null) {
                        player.sendMessage("To nie treasure chest");
                        break;
                    }

                    Config config = getConfig(id);
                    config.conf.set("Respawn", minutes * 60 * 20);
                    config.save();

                    player.sendMessage("Ustawiłeś respawn tej skrzyni na " + minutes + " minut");
                }

                break;

        }
        return true;
    }
}


