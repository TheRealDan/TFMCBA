package me.therealdan.tfmcba.listeners;

import me.therealdan.tfmcba.TFMCBA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TrashCanListener implements Listener {

    private Inventory trashcan;

    public TrashCanListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TFMCBA.getInstance(), () -> tick(), 100, 20);
    }

    private void tick() {
        if (trashcan == null) return;

        for (ItemStack itemStack : trashcan.getContents()) {
            if (itemStack != null) {
                trashcan.remove(itemStack);
                return;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SURVIVAL) && event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) {
            player.openInventory(getTrashcan());
        }
    }

    private Inventory getTrashcan() {
        if (trashcan == null) trashcan = Bukkit.createInventory(null, 18, ChatColor.translateAlternateColorCodes('&', "&1Trash Can"));
        return trashcan;
    }
}