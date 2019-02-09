package me.therealdan.tfmcba.statistics;

import net.theforcemc.mechanics.equipment.shootable.gun.Gun;
import net.theforcemc.util.YamlFile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Statistics {

    private static YamlFile yamlFile;
    private static HashMap<UUID, Statistics> statistics = new HashMap<>();

    private UUID uuid;
    private HashMap<String, Long> timeHeld = new HashMap<>();
    private HashMap<String, Long> bulletsFired = new HashMap<>();
    private HashMap<String, Double> damageDealt = new HashMap<>();
    private HashMap<String, Long> kills = new HashMap<>();

    private Statistics(UUID uuid) {
        this.uuid = uuid;

        if (getYamlFile().getData().contains("TimeHeld"))
            for (String gunID : getYamlFile().getData().getConfigurationSection("TimeHeld").getKeys(false))
                timeHeld.put(gunID, getYamlFile().getData().getLong("TimeHeld." + gunID + "." + uuid.toString()));
        if (getYamlFile().getData().contains("BulletsFired"))
            for (String gunID : getYamlFile().getData().getConfigurationSection("BulletsFired").getKeys(false))
                bulletsFired.put(gunID, getYamlFile().getData().getLong("BulletsFired." + gunID + "." + uuid.toString()));
        if (getYamlFile().getData().contains("DamageDealt"))
            for (String gunID : getYamlFile().getData().getConfigurationSection("DamageDealt").getKeys(false))
                damageDealt.put(gunID, getYamlFile().getData().getDouble("DamageDealt." + gunID + "." + uuid.toString()));
        if (getYamlFile().getData().contains("Kills"))
            for (String gunID : getYamlFile().getData().getConfigurationSection("Kills").getKeys(false))
                kills.put(gunID, getYamlFile().getData().getLong("Kills." + gunID + "." + uuid.toString()));

        statistics.put(uuid, this);
    }

    private void save() {
        for (String gunID : timeHeld.keySet())
            getYamlFile().getData().set("TimeHeld." + gunID + "." + getUUID().toString(), timeHeld.get(gunID));
        for (String gunID : bulletsFired.keySet())
            getYamlFile().getData().set("BulletsFired." + gunID + "." + getUUID().toString(), bulletsFired.get(gunID));
        for (String gunID : damageDealt.keySet())
            getYamlFile().getData().set("DamageDealt." + gunID + "." + getUUID().toString(), damageDealt.get(gunID));
        for (String gunID : kills.keySet())
            getYamlFile().getData().set("Kills." + gunID + "." + getUUID().toString(), kills.get(gunID));
    }

    public void addTimeHeld(Gun gun, long milliseconds) {
        timeHeld.put(gun.getID(), getBulletsFired(gun) + milliseconds);
    }

    public void addBulletFired(Gun gun, long bullets) {
        bulletsFired.put(gun.getID(), getBulletsFired(gun) + bullets);
    }

    public void addDamageDealt(Gun gun, double damage) {
        damageDealt.put(gun.getID(), getDamageDealt(gun) + damage);
    }

    public void addKill(Gun gun) {
        kills.put(gun.getID(), getKills(gun) + 1);
    }

    public long getTimeHeld(Gun gun) {
        if (!timeHeld.containsKey(gun.getID())) timeHeld.put(gun.getID(), 0L);
        return timeHeld.get(gun.getID());
    }

    public long getBulletsFired(Gun gun) {
        if (!bulletsFired.containsKey(gun.getID())) bulletsFired.put(gun.getID(), 0L);
        return bulletsFired.get(gun.getID());
    }

    public double getDamageDealt(Gun gun) {
        if (!damageDealt.containsKey(gun.getID())) damageDealt.put(gun.getID(), 0.0);
        return damageDealt.get(gun.getID());
    }

    public long getKills(Gun gun) {
        if (!kills.containsKey(gun.getID())) kills.put(gun.getID(), 0L);
        return kills.get(gun.getID());
    }

    public UUID getUUID() {
        return uuid;
    }

    public static void load() {
        statistics.clear();

        if (getYamlFile().getData().contains("Players"))
            for (String uuid : getYamlFile().getData().getConfigurationSection("Players").getKeys(false))
                new Statistics(UUID.fromString(uuid));
    }

    public static void unload() {
        for (Statistics statistics : values())
            statistics.save();

        getYamlFile().save();
    }

    public static Statistics byPlayer(Player player) {
        return byUUID(player.getUniqueId());
    }

    public static Statistics byUUID(UUID uuid) {
        if (!statistics.containsKey(uuid)) statistics.put(uuid, new Statistics(uuid));
        return statistics.get(uuid);
    }

    public static List<Statistics> values() {
        return new ArrayList<>(statistics.values());
    }

    private static YamlFile getYamlFile() {
        if (yamlFile == null) yamlFile = new YamlFile("data/statistics.yml");
        return yamlFile;
    }
}