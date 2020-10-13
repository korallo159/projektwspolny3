package jbwm.jbwm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.List;

public class JbwmMinezChests extends JbwmCommand implements Listener {

    public JbwmMinezChests() {
        super("tchest");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equals("tchest")) {
                Player player = (Player) sender;
                player.getInventory().addItem(getTchest(1));
            }
        }
        return true;
    }

    public ItemStack getTchest(int amount) {
        ItemStack tchest = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = tchest.getItemMeta();

        itemMeta.setDisplayName(ChatColor.RED + "Treasure chest");

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_RED + "Treasure chest admin");
        itemMeta.setLore(lore);

        tchest.setItemMeta(itemMeta);

        return tchest;
    }
/**
* Po wsadzeniu itemow i zamknieciu skrzyni zapisuje je w itemstacku
* TODO: tak jak nizej, trzeba sprawdzic, ze to treasure chest ten ktory sobie dodalismy z komendy.
 ** mysle, ze mozna zapisywac skrzynie poprzez nazwe. Czyli admin tworzy /tchest Skrzynia5 i wtedy dodaje sie juz do configu. idk wersja bardzo robocza
 */
    @EventHandler
    public void onTChestCloseSaveItems(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getHolder() instanceof Chest) {
            ArrayList<ItemStack> items = new ArrayList<>();
            for (ItemStack is : e.getInventory()) {
                if (is != null)
                    items.add(is);

            }
            /**
             * TODO: dodac do configu, tak zeby mozna bylo sie odwolac do tych itemow po restarcie
             */
        }
        }

    /** usuwa skrzynke po jej otworzeniu i zabraniu wszystkich przedmiotow
     * * TODO: Check that its treasure chest, add animation
     * *
     **/
/*    @EventHandler
    public void onChestCloseRemove(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getInventory().getHolder() instanceof Chest) {

            Chest chest = (Chest) e.getInventory().getHolder();
            for (ItemStack is : e.getInventory()) {
                if (is != null)
                    break;
                if (is == null) {
                    Block b = chest.getBlock();
                    b.setType(Material.AIR);
                    Collection<ItemStack> items = b.getDrops();
                    for (ItemStack is2 : items) {
                        p.getWorld().dropItemNaturally(b.getLocation(), is2);
                    }
                }
            }
        }
    }
*/





    }

