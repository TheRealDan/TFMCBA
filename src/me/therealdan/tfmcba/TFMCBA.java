package me.therealdan.tfmcba;

import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.SetupHandler;
import me.therealdan.battlearena.util.Icon;
import me.therealdan.tfmcba.battlelisteners.*;
import me.therealdan.tfmcba.battles.*;
import me.therealdan.tfmcba.commands.TFMCBACommand;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import me.therealdan.tfmcba.listeners.TrashCanListener;
import me.therealdan.tfmcba.setup.*;
import me.therealdan.tfmcba.statistics.Statistics;
import me.therealdan.tfmcba.statistics.StatisticsHandler;
import me.therealdan.tfmcba.statistics.StatisticsViewer;
import net.theforcemc.TheForceMC;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class TFMCBA extends JavaPlugin {

    private static TFMCBA tfmcba;

    @Override
    public void onEnable() {
        tfmcba = this;

        Statistics.load();

        BattleType.register(FFA.NAME, Icon.build(Material.IRON_SWORD, 0, false, TheForceMC.MAIN + FFA.NAME, "&7All against all"), new FFASetup());
        BattleType.register(TeamBattle.NAME, Icon.build(Material.IRON_CHESTPLATE, 0, false, TheForceMC.MAIN + TeamBattle.NAME, "&7Team against team"), new TeamBattleSetup());
        BattleType.register(GunGame.NAME, Icon.build(Material.GOLD_AXE, 0, false, TheForceMC.MAIN + GunGame.NAME, "&7Unlock better guns by getting kills"), new GunGameSetup());
        BattleType.register(SwordGame.NAME, Icon.build(Material.SHEARS, 0, false, TheForceMC.MAIN + SwordGame.NAME, "&7Unlock better swords by getting kills"), new SwordGameSetup());
        BattleType.register(Roulette.NAME, Icon.build(Material.GOLD_AXE, 0, false, TheForceMC.MAIN + Roulette.NAME, "&7One player gets a pistol, everyone else gets poison knives.", "&7If the player with the pistol gets a kill a random player is given the pistol.", "&7If a player with a knife kills the pistol holder, they become the pistol holder."), new RouletteSetup());
        BattleType.register(Scavenger.NAME, Icon.build(Material.IRON_INGOT, 0, false, TheForceMC.MAIN + Scavenger.NAME, "&7Everyone starts with a Pistol", "&7Every kill rewards a random item"), new ScavengerSetup());
        BattleType.register(MysteryWeapon.NAME, Icon.build(Material.GOLD_AXE, 0, false, TheForceMC.MAIN + MysteryWeapon.NAME, "&7Always respawn with a different weapon"), new MysteryWeaponSetup());

        SetupHandler.setDefault(BattleType.byName(FFA.NAME).getSetup());

        getServer().getPluginManager().registerEvents(StatisticsHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(StatisticsViewer.getInstance(), this);
        getServer().getPluginManager().registerEvents(EquipmentSelector.getInstance(), this);

        getServer().getPluginManager().registerEvents(new BattleListener(), this);
        getServer().getPluginManager().registerEvents(new FFAListener(), this);
        getServer().getPluginManager().registerEvents(new TeamBattleListener(), this);
        getServer().getPluginManager().registerEvents(new GunGameListener(), this);
        getServer().getPluginManager().registerEvents(new SwordGameListener(), this);
        getServer().getPluginManager().registerEvents(new RouletteListener(), this);

        getServer().getPluginManager().registerEvents(new TrashCanListener(), this);

        getCommand("TFMCBA").setExecutor(new TFMCBACommand());
    }

    @Override
    public void onDisable() {
        Statistics.unload();
    }

    public static TFMCBA getInstance() {
        return tfmcba;
    }
}