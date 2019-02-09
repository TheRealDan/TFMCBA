package me.therealdan.tfmcba;

import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.SetupHandler;
import me.therealdan.battlearena.util.Icon;
import me.therealdan.tfmcba.battles.ffa.FFASetup;
import me.therealdan.tfmcba.commands.TFMCBACommand;
import me.therealdan.tfmcba.listeners.BattleListener;
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
        BattleType.register("Team", Icon.build(Material.IRON_CHESTPLATE, 0, false, TheForceMC.MAIN + "Team Battle", "&7Team against team"), new FFASetup());
        SetupHandler.setDefault(BattleType.byName("FFA").getSetup());

        getServer().getPluginManager().registerEvents(StatisticsHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(StatisticsViewer.getInstance(), this);
        getServer().getPluginManager().registerEvents(new BattleListener(), this);
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