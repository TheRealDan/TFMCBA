package me.therealdan.tfmcba.statistics;

import me.therealdan.battlearena.mechanics.statistics.Statistics;
import me.therealdan.battlearena.util.Icon;
import me.therealdan.tfmcba.TFMCBA;
import net.theforcemc.TheForceMC;
import net.theforcemc.mechanics.equipment.shootable.gun.Gun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class StatisticsViewer implements Listener {

    private static StatisticsViewer statisticsHandler;

    private HashSet<UUID> mainUIOpen = new HashSet<>();
    private HashMap<UUID, UUID> playerUIOpen = new HashMap<>();
    private HashMap<UUID, String> gunUIOpen = new HashMap<>();
    private Inventory mainUI = null;
    private HashMap<UUID, ItemStack> playerIcons = new HashMap<>();
    private HashMap<UUID, HashMap<String, ItemStack>> playerGunIcons = new HashMap<>();

    private StatisticsViewer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TFMCBA.getInstance(), () -> tick(), 100, 20);
    }

    private void tick() {
        if (mainUIOpen.size() > 0) getMainUI(true);

        for (UUID uuid : new ArrayList<>(playerUIOpen.keySet()))
            open(Bukkit.getPlayer(uuid), Bukkit.getPlayer(playerUIOpen.get(uuid)));

        for (UUID uuid : new ArrayList<>(gunUIOpen.keySet()))
            open(Bukkit.getPlayer(uuid), Gun.byID(gunUIOpen.get(uuid)));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!(isViewingMainUI(player) || isViewingPlayerUI(player) || isViewingGunUI(player))) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().equals(Material.AIR)) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        Player target = Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
        if (target != null) {
            open(player, target);
            return;
        }

        Gun gun = Gun.byItemStack(event.getCurrentItem());
        if (gun != null) {
            open(player, gun);
            return;
        }

        open(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        mainUIOpen.remove(player.getUniqueId());
        playerUIOpen.remove(player.getUniqueId());
        gunUIOpen.remove(player.getUniqueId());
    }

    public void open(Player player) {
        player.openInventory(getMainUI(false));
        mainUIOpen.add(player.getUniqueId());
    }

    public void open(Player player, Player target) {
        int size = 9;
        while (size < Gun.values().size()) size += 9;
        if (size > 54) size = 54;

        Inventory ui = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', "&1" + target.getName() + "'s Statistics"));

        List<Gun> guns = new ArrayList<>(Gun.values());
        while (guns.size() > 0) {
            Gun first = null;
            for (Gun each : guns) {
                if (first == null) {
                    first = each;
                } else if (first.getDisplayName().compareTo(each.getDisplayName()) > 0) {
                    first = each;
                }
            }

            ui.addItem(getIcon(target, first));
            guns.remove(first);
        }

        player.openInventory(ui);
        playerUIOpen.put(player.getUniqueId(), target.getUniqueId());
    }

    public void open(Player player, Gun gun) {
        int size = 9;
        while (size < Bukkit.getOnlinePlayers().size() + 1) size += 9;
        if (size > 54) size = 54;

        Inventory ui = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', gun.getDisplayName() + "&1's Statistics"));

        ui.addItem(getIcon(gun));

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        while (players.size() > 0) {
            Player first = null;
            for (Player each : players) {
                if (first == null) {
                    first = each;
                } else if (first.getName().compareTo(each.getDisplayName()) > 0) {
                    first = each;
                }
            }

            ui.addItem(getPlayerGunIcon(first, gun));
            players.remove(first);
        }

        player.openInventory(ui);
        gunUIOpen.put(player.getUniqueId(), gun.getID());
    }

    public boolean isViewingMainUI(Player player) {
        return mainUIOpen.contains(player.getUniqueId());
    }

    public boolean isViewingPlayerUI(Player player) {
        return playerUIOpen.containsKey(player.getUniqueId());
    }

    public boolean isViewingGunUI(Player player) {
        return gunUIOpen.containsKey(player.getUniqueId());
    }

    private ItemStack getIcon(OfflinePlayer player) {
        ItemStack icon;
        if (playerIcons.containsKey(player.getUniqueId())) {
            icon = playerIcons.get(player.getUniqueId());
        } else {
            icon = Icon.build(Material.SKULL_ITEM, 3, false, TheForceMC.MAIN + player.getName());
            SkullMeta skullMeta = (SkullMeta) icon.getItemMeta();
            skullMeta.setOwner(player.getName());
            icon.setItemMeta(skullMeta);
        }

        Statistics statistics = Statistics.byUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add(TheForceMC.MAIN + "KDR: " + TheForceMC.SECOND + statistics.getKDRString());
        lore.add(TheForceMC.MAIN + "Kills: " + TheForceMC.SECOND + statistics.getKills());
        lore.add(TheForceMC.MAIN + "Deaths: " + TheForceMC.SECOND + statistics.getDeaths());
        lore.add(TheForceMC.MAIN + "Games Played: " + TheForceMC.SECOND + statistics.getGamesPlayed());
        lore.add(TheForceMC.MAIN + "Games Won: " + TheForceMC.SECOND + statistics.getGamesWon());
        ItemMeta itemMeta = icon.getItemMeta();
        itemMeta.setLore(lore);
        icon.setItemMeta(itemMeta);

        playerIcons.put(player.getUniqueId(), icon);
        return icon;
    }

    private ItemStack getIcon(Player player, Gun gun) {
        me.therealdan.tfmcba.statistics.Statistics statistics = me.therealdan.tfmcba.statistics.Statistics.byPlayer(player);

        List<String> lore = new ArrayList<>();
        lore.add(TheForceMC.MAIN + "Time held: " + TheForceMC.SECOND + getTime(statistics.getTimeHeld(gun)));
        lore.add(TheForceMC.MAIN + "Bullets fired: " + TheForceMC.SECOND + statistics.getBulletsFired(gun));
        lore.add(TheForceMC.MAIN + "Damage dealt: " + TheForceMC.SECOND + statistics.getDamageDealt(gun));
        lore.add(TheForceMC.MAIN + "Kills: " + TheForceMC.SECOND + statistics.getKills(gun));

        return gun.getItemStack(lore);
    }

    private ItemStack getPlayerGunIcon(OfflinePlayer player, Gun gun) {
        ItemStack icon;
        if (!playerGunIcons.containsKey(player.getUniqueId())) playerGunIcons.put(player.getUniqueId(), new HashMap<>());
        if (playerGunIcons.get(player.getUniqueId()).containsKey(gun.getID())) {
            icon = playerGunIcons.get(player.getUniqueId()).get(gun.getID());
        } else {
            icon = Icon.build(Material.SKULL_ITEM, 3, false, TheForceMC.MAIN + player.getName() + "'s " + gun.getDisplayName() + TheForceMC.MAIN + " Statistics");
            SkullMeta skullMeta = (SkullMeta) icon.getItemMeta();
            skullMeta.setOwner(player.getName());
            icon.setItemMeta(skullMeta);
        }

        me.therealdan.tfmcba.statistics.Statistics statistics = me.therealdan.tfmcba.statistics.Statistics.byUUID(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        lore.add(TheForceMC.MAIN + "Time held: " + TheForceMC.SECOND + statistics.getTimeHeld(gun));
        lore.add(TheForceMC.MAIN + "Bullets fired: " + TheForceMC.SECOND + statistics.getBulletsFired(gun));
        lore.add(TheForceMC.MAIN + "Damage dealt: " + TheForceMC.SECOND + statistics.getDamageDealt(gun));
        lore.add(TheForceMC.MAIN + "Kills: " + TheForceMC.SECOND + statistics.getKills(gun));
        ItemMeta itemMeta = icon.getItemMeta();
        itemMeta.setLore(lore);
        icon.setItemMeta(itemMeta);

        playerGunIcons.get(player.getUniqueId()).put(gun.getID(), icon);
        return icon;
    }

    private ItemStack getIcon(Gun gun) {
        long timeHeld = 0;
        for (me.therealdan.tfmcba.statistics.Statistics statistics : me.therealdan.tfmcba.statistics.Statistics.values())
            timeHeld += statistics.getTimeHeld(gun);

        long bulletsFired = 0;
        for (me.therealdan.tfmcba.statistics.Statistics statistics : me.therealdan.tfmcba.statistics.Statistics.values())
            bulletsFired += statistics.getBulletsFired(gun);

        double damageDealt = 0;
        for (me.therealdan.tfmcba.statistics.Statistics statistics : me.therealdan.tfmcba.statistics.Statistics.values())
            damageDealt += statistics.getDamageDealt(gun);

        long kills = 0;
        for (me.therealdan.tfmcba.statistics.Statistics statistics : me.therealdan.tfmcba.statistics.Statistics.values())
            kills += statistics.getKills(gun);

        List<String> lore = new ArrayList<>();
        lore.add(TheForceMC.MAIN + "Total Time held: " + TheForceMC.SECOND + getTime(timeHeld));
        lore.add(TheForceMC.MAIN + "Total Bullets fired: " + TheForceMC.SECOND + bulletsFired);
        lore.add(TheForceMC.MAIN + "Total Damage dealt: " + TheForceMC.SECOND + damageDealt);
        lore.add(TheForceMC.MAIN + "Total Kills: " + TheForceMC.SECOND + kills);

        return gun.getItemStack(lore);
    }

    private Inventory getMainUI(boolean update) {
        int size = 9;
        while (size < Bukkit.getOnlinePlayers().size()) size += 9;
        if (size > 54) size = 54;

        if (mainUI == null || update) {
            if (mainUI == null) mainUI = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', "&1Statistics"));

            mainUI.clear();
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            while (players.size() > 0) {
                Player first = null;
                for (Player each : players) {
                    if (first == null) {
                        first = each;
                    } else if (first.getName().compareTo(each.getDisplayName()) > 0) {
                        first = each;
                    }
                }

                mainUI.addItem(getIcon(first));
                players.remove(first);
            }
        }

        return mainUI;
    }

    private String getTime(long milliseconds) {
        long time = milliseconds / 1000;

        int days = 0;
        while (time >= 86400) {
            days++;
            time -= 86400;
        }

        String seconds = Long.toString(time % 60);
        String minutes = Long.toString((time / 60) % 60);
        String hours = Long.toString((time / 60) / 60);

        seconds = (seconds.length() > 1) ? seconds : "0" + seconds;
        minutes = (minutes.length() > 1) ? minutes : "0" + minutes;
        hours = (hours.length() > 1) ? hours : "0" + hours;

        if (days > 1) {
            return days + " days, " + hours + ":" + minutes + ":" + seconds;
        } else if (days > 0) {
            return days + " day, " + hours + ":" + minutes + ":" + seconds;
        } else {
            return hours + ":" + minutes + ":" + seconds;
        }
    }

    public static StatisticsViewer getInstance() {
        if (statisticsHandler == null) statisticsHandler = new StatisticsViewer();
        return statisticsHandler;
    }
}