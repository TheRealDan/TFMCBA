package me.therealdan.tfmcba.battles.settings;

import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.setup.Setting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Health extends Setting {

    private HashMap<UUID, Double> health = new HashMap<>();

    public Health(double health) {
        super("Health", health);
    }

    @Override
    public void apply(Battle battle) {
        for (Player player : battle.getPlayers())
            apply(player);
    }

    public void apply(Player player) {
        player.setMaxHealth(getHealth(player.getUniqueId()));
        player.setHealthScale(20);
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public boolean click(Player player, boolean shift, boolean left) {
        double health = getHealth();
        health += left ? 1 : -1;
        if (health < 1) health = 1;
        setHealth(health);
        return false;
    }

    @Override
    public boolean click(Player player, ItemStack icon, boolean shift, boolean left) {
        return false;
    }

    @Override
    public Material getMaterial() {
        return Material.APPLE;
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Player health: &f" + getHealth());
//        description.add("&7(Shift click for advanced)");
        return description;
    }

    public void setHealth(double health) {
        set(health);
    }

    public double getHealth(UUID uuid) {
        if (health.containsKey(uuid)) return health.get(uuid);
        return getHealth();
    }

    public double getHealth() {
        return (double) getValue();
    }

    @Override
    public Object clone() {
        return new Health(getHealth());
    }
}