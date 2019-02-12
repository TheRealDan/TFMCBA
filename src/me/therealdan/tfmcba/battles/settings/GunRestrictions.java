package me.therealdan.tfmcba.battles.settings;

import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.battlearena.util.Icon;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GunRestrictions extends Setting {

    private HashSet<String> disabled = new HashSet<>();

    public GunRestrictions() {
        super("Gun Settings", 1);
    }

    @Override
    public boolean click(Player player, boolean shift, boolean left) {
        open(player);
        return true;
    }

    @Override
    public boolean click(Player player, ItemStack icon, boolean shift, boolean left) {
        if (icon.getType().equals(Material.BARRIER)) return false;

        for (Gun gun : Gun.values()) {
            if (gun.is(icon)) {
                if (isAllowed(gun)) {
                    deny(gun);
                } else {
                    allow(gun);
                }
                open(player);
                return true;
            }
        }

        if (getAllIcon().isSimilar(icon)) {
            if (somethingDisabled()) {
                allowAll();
            } else {
                for (Gun gun : Gun.values()) {
                    if (gun.getID().equals("admin")) continue;
                    deny(gun);
                }
            }
        }

        open(player);
        return true;
    }

    private void open(Player player) {
        Inventory ui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&1Gun Settings"));

        for (Gun gun : Gun.values(true)) {
            if (gun.getID().equals("admin")) continue;
            ui.addItem(getIcon(gun));
        }

        ui.setItem(ui.getSize() - 2, getAllIcon());
        ui.setItem(ui.getSize() - 1, Icon.build(Material.BARRIER, 0, false, "&6Back"));

        player.openInventory(ui);
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_AXE;
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Click to edit Gun settings");
        description.add("");

        int maxLength = 40;
        description.add("&7Enabled Guns:");
        StringBuilder stringBuilder = new StringBuilder();
        for (Gun gun : Gun.values(true)) {
            if (gun.getID().equals("admin")) continue;
            if (!isAllowed(gun)) continue;
            if (stringBuilder.length() > maxLength) {
                description.add(stringBuilder.toString().replaceFirst(", ", ""));
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append("&f, ").append(gun.getDisplayName());
        }
        description.add(stringBuilder.toString().replaceFirst(", ", ""));

        description.add("");
        description.add("&7Disabled Guns:");
        stringBuilder = new StringBuilder();
        for (Gun gun : Gun.values(true)) {
            if (gun.getID().equals("admin")) continue;
            if (isAllowed(gun)) continue;
            if (stringBuilder.length() > maxLength) {
                description.add(stringBuilder.toString().replaceFirst(", ", ""));
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append("&f, ").append(gun.getDisplayName());
        }
        description.add(stringBuilder.toString().replaceFirst(", ", ""));

        return description;
    }

    public void allow(Gun gun) {
        disabled.remove(gun.getID());
    }

    public void deny(Gun gun) {
        disabled.add(gun.getID());
    }

    public void allowAll() {
        disabled.clear();
    }

    public boolean somethingDisabled() {
        return disabled.size() > 0;
    }

    public boolean isAllowed(Gun gun) {
        return !disabled.contains(gun.getID());
    }

    private ItemStack getAllIcon() {
        return Icon.build(Material.GOLD_AXE, 0, false, somethingDisabled() ? "&aEnable All" : "&cDisable All");
    }

    private ItemStack getIcon(Gun gun) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', (isAllowed(gun) ? "&aEnabled" : "&cDisabled")));
        return gun.getItemStack(lore);
    }

    @Override
    public Object clone() {
        GunRestrictions gunRestrictions = new GunRestrictions();
        gunRestrictions.disabled.addAll(disabled);
        return gunRestrictions;
    }
}