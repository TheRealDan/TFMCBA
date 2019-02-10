package me.therealdan.tfmcba.battles.settings;

import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.setup.Setting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Competitive extends Setting {

    public Competitive(boolean enabled) {
        super("Competitive", enabled);
    }

    @Override
    public void apply(Battle battle) {
        battle.setStatisticsTracking(isCompetitive());
    }

    @Override
    public boolean click(Player player, boolean shift, boolean left) {
        setCompetitive(!isCompetitive());
        return false;
    }

    @Override
    public boolean click(Player player, ItemStack icon, boolean shift, boolean left) {
        return false;
    }

    @Override
    public Material getMaterial() {
        return isCompetitive() ? Material.SHEARS : Material.WOODEN_DOOR;
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Enabled: " + (isCompetitive() ? "&aCompetitive" : "&cfalse"));
        if (isCompetitive()) description.add("&7Affects KDR rating");
        if (!isCompetitive()) description.add("&7Doesn't affect KDR rating");
        return description;
    }

    public void setCompetitive(boolean enabled) {
        set(enabled);
    }

    public boolean isCompetitive() {
        return (boolean) getValue();
    }

    @Override
    public Object clone() {
        return new Competitive(isCompetitive());
    }
}