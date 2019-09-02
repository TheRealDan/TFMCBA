package me.therealdan.tfmcba;

import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.SetupHandler;
import me.therealdan.battlearena.util.Icon;
import me.therealdan.tfmcba.battles.chaos.ChaosSetup;
import me.therealdan.tfmcba.battles.ffa.FFASetup;
import me.therealdan.tfmcba.battles.gungame.GunGameSetup;
import me.therealdan.tfmcba.battles.team.TeamSetup;
import me.therealdan.tfmcba.commands.TFMCBACommand;
import me.therealdan.tfmcba.listeners.BattleListener;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import me.therealdan.tfmcba.listeners.TrashCanListener;
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

        BattleType.register("FFA", Icon.build(Material.IRON_SWORD, 0, false, TheForceMC.MAIN + "FFA", "&7All against all"), new FFASetup());
        BattleType.register("Team", Icon.build(Material.IRON_CHESTPLATE, 0, false, TheForceMC.MAIN + "Team Battle", "&7Team against team"), new TeamSetup());
        BattleType.register("Gun Game", Icon.build(Material.GOLD_AXE, 0, false, TheForceMC.MAIN + "Gun Game", "&7Unlock better guns by getting kills"), new GunGameSetup());
        BattleType.register("Chaos", Icon.build(Material.TNT, 0, false, TheForceMC.MAIN + "Chaos", "&7Unlock better guns by getting kills"), new ChaosSetup());
        SetupHandler.setDefault(BattleType.byName("FFA").getSetup());

        getServer().getPluginManager().registerEvents(StatisticsHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(StatisticsViewer.getInstance(), this);
        getServer().getPluginManager().registerEvents(new BattleListener(), this);
        getServer().getPluginManager().registerEvents(new TrashCanListener(), this);
        getServer().getPluginManager().registerEvents(EquipmentSelector.getInstance(), this);

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