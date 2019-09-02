package me.therealdan.tfmcba;

import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.SetupHandler;
import me.therealdan.battlearena.util.Icon;
import me.therealdan.tfmcba.battles.ffa.FFA;
import me.therealdan.tfmcba.battles.ffa.FFASetup;
import me.therealdan.tfmcba.battles.gungame.GunGame;
import me.therealdan.tfmcba.battles.gungame.GunGameSetup;
import me.therealdan.tfmcba.battles.swordgame.SwordGame;
import me.therealdan.tfmcba.battles.swordgame.SwordGameSetup;
import me.therealdan.tfmcba.battles.team.TeamBattle;
import me.therealdan.tfmcba.battles.team.TeamBattleSetup;
import me.therealdan.tfmcba.commands.TFMCBACommand;
import me.therealdan.tfmcba.listeners.*;
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
        SetupHandler.setDefault(BattleType.byName(FFA.NAME).getSetup());

        getServer().getPluginManager().registerEvents(StatisticsHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(StatisticsViewer.getInstance(), this);
        getServer().getPluginManager().registerEvents(EquipmentSelector.getInstance(), this);

        getServer().getPluginManager().registerEvents(new BattleListener(), this);
        getServer().getPluginManager().registerEvents(new FFAListener(), this);
        getServer().getPluginManager().registerEvents(new TeamBattleListener(), this);
        getServer().getPluginManager().registerEvents(new GunGameListener(), this);
        getServer().getPluginManager().registerEvents(new SwordGameListener(), this);

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