package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.tfmcba.settings.GunRestrictions;
import net.theforcemc.TheForceMC;
import net.theforcemc.equipment.Equipment;
import net.theforcemc.equipment.armor.Armor;
import net.theforcemc.equipment.melee.Melee;
import net.theforcemc.equipment.shootable.gun.Gun;
import net.theforcemc.util.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EquipmentSelector implements Listener {

    private static EquipmentSelector equipmentSelector;
    private static HashMap<Screen, ItemStack> icons = new HashMap<>();

    private HashMap<UUID, Screen> uiOpen = new HashMap<>();
    private ItemStack backIcon;

    private ItemStack equipmentSelectorItem;

    private EquipmentSelector() {

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)))
            return;

        Player player = event.getPlayer();
        if (player.getEquipment().getItemInMainHand() == null) return;

        if (player.getEquipment().getItemInMainHand().isSimilar(getEquipmentSelectorItem())) {
            open(player);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!isOpen(player)) return;
        event.setCancelled(true);

        if (getBackIcon().isSimilar(event.getCurrentItem())) {
            open(player);
            return;
        }

        if (isOpen(player, Screen.MENU)) {
            for (Screen screen : Screen.values()) {
                if (screen.getIcon().isSimilar(event.getCurrentItem())) {
                    open(player, screen);
                    return;
                }
            }
            return;
        }

        Equipment equipment = Equipment.byItemStack(event.getCurrentItem());
        if (equipment != null) {
            if (equipment.isUnlocked(player.getUniqueId())) {
                player.getInventory().addItem(equipment.getItemStack());
            } else {
                player.sendMessage(TheForceMC.MAIN + "You have not unlocked " + TheForceMC.SECOND + equipment.getDisplayName());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        uiOpen.remove(player.getUniqueId());
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.DARK_BLUE + "Equipment Selector");

        inventory.addItem(Screen.RANGED.getIcon());
        inventory.addItem(Screen.MELEE.getIcon());
        inventory.addItem(Screen.FORCE_ABILITIES.getIcon());
        inventory.addItem(Screen.ARMOR.getIcon());
        inventory.addItem(Screen.CONSUMABLES.getIcon());

        player.openInventory(inventory);
        uiOpen.put(player.getUniqueId(), Screen.MENU);
    }

    public void openArmor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.DARK_BLUE + "Armor Categories");

        inventory.addItem(Screen.HELMETS.getIcon());
        inventory.addItem(Screen.CHESTPLATES.getIcon());
        inventory.addItem(Screen.LEGGINGS.getIcon());
        inventory.addItem(Screen.BOOTS.getIcon());
        inventory.addItem(getBackIcon());

        player.openInventory(inventory);
        uiOpen.put(player.getUniqueId(), Screen.ARMOR);
    }

    public void open(Player player, Screen screen) {
        if (screen.equals(Screen.MENU)) {
            open(player);
            return;
        }

        if (screen.equals(Screen.ARMOR)) {
            openArmor(player);
            return;
        }

        Battle battle = Battle.get(player);
        GunRestrictions gunRestrictions = null;
        if (battle != null) {
            for (Setting setting : battle.getSettings().values()) {
                if (setting instanceof GunRestrictions) {
                    gunRestrictions = (GunRestrictions) setting;
                    break;
                }
            }
        }

        List<Equipment> equipments;
        switch (screen) {
            case RANGED:
                equipments = new ArrayList<>(Gun.values(true));
                break;
            case MELEE:
                equipments = new ArrayList<>(Melee.values(true));
                break;
            case HELMETS:
            case CHESTPLATES:
            case LEGGINGS:
            case BOOTS:
                equipments = new ArrayList<>();
                for (Armor armor : Armor.values(true)) {
                    if (armor.getItemStack().getType().toString().contains(screen.toString().substring(0, 4))) {
                        equipments.add(armor);
                    }
                }
                break;
            default:
                equipments = new ArrayList<>();
        }

        List<ItemStack> itemstacks = new ArrayList<>();
        for (Equipment equipment : equipments) {
            if (!equipment.isEnabled()) continue;
            if (equipment.isAdminOnly() && !player.isOp()) continue;
            if (gunRestrictions != null && equipment instanceof Gun && !gunRestrictions.isAllowed((Gun) equipment))
                continue;
            itemstacks.add(equipment.getItemStack());
        }

        int size = 9;
        while (size < itemstacks.size() + 1) size += 9;

        Inventory inventory = Bukkit.createInventory(null, size, ChatColor.DARK_BLUE + screen.getName());

        for (ItemStack itemStack : itemstacks)
            inventory.addItem(itemStack);

        inventory.setItem(size - 1, getBackIcon());

        player.openInventory(inventory);
        uiOpen.put(player.getUniqueId(), screen);
    }

    public boolean isOpen(Player player, Screen screen) {
        if (!uiOpen.containsKey(player.getUniqueId())) return false;
        return uiOpen.get(player.getUniqueId()).equals(screen);
    }

    public boolean isOpen(Player player) {
        return uiOpen.containsKey(player.getUniqueId());
    }

    public ItemStack getEquipmentSelectorItem() {
        if (equipmentSelectorItem == null) equipmentSelectorItem = Icon.get(Material.CHEST, 0, "Equipment Selector");
        return equipmentSelectorItem;
    }

    public ItemStack getBackIcon() {
        if (backIcon == null) backIcon = Icon.get(Material.BARRIER, 0, "Back", "");
        return backIcon;
    }

    public Screen getOpen(Player player) {
        return uiOpen.getOrDefault(player.getUniqueId(), null);
    }

    public enum Screen {
        MENU,
        ARMOR, HELMETS, CHESTPLATES, LEGGINGS, BOOTS,
        RANGED, MELEE, FORCE_ABILITIES, CONSUMABLES;

        public ItemStack getIcon() {
            if (!icons.containsKey(this)) {
                String description = "Coming Soon...";
                switch (this) {
                    case RANGED:
                        description = "Ranged equipments";
                        break;
                    case MELEE:
                        description = "Melee equipments";
                        break;
                    case ARMOR:
                        description = "Armor equipments";
                        break;
                    case HELMETS:
                    case CHESTPLATES:
                    case LEGGINGS:
                    case BOOTS:
                        description = "";
                        break;
                }
                icons.put(this, Icon.get(this.getMaterial(), 0, this.getName(), description));
            }
            return icons.get(this);
        }

        public String getName() {
            StringBuilder name = new StringBuilder();
            boolean caps = true;
            for (String letter : this.toString().split("")) {
                if (letter.equals("_")) {
                    name.append(" ");
                    caps = true;
                    continue;
                }
                if (caps) {
                    name.append(letter.toUpperCase());
                    caps = false;
                    continue;
                }
                name.append(letter.toLowerCase());
            }
            return name.toString();
        }

        public Material getMaterial() {
            switch (this) {
                case RANGED:
                    return Material.GOLD_AXE;
                case MELEE:
                    return Material.SHEARS;
                case FORCE_ABILITIES:
                    return Material.CARROT_STICK;
                case ARMOR:
                case CHESTPLATES:
                    return Material.IRON_CHESTPLATE;
                case HELMETS:
                    return Material.IRON_HELMET;
                case LEGGINGS:
                    return Material.IRON_LEGGINGS;
                case BOOTS:
                    return Material.IRON_BOOTS;
                case CONSUMABLES:
                    return Material.SNOW_BALL;
            }
            return Material.BARRIER;
        }

    }

    public static EquipmentSelector getInstance() {
        if (equipmentSelector == null) equipmentSelector = new EquipmentSelector();
        return equipmentSelector;
    }
}