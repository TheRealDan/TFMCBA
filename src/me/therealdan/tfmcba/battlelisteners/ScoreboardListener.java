package me.therealdan.tfmcba.battlelisteners;

import com.coloredcarrot.api.sidebar.Sidebar;
import me.therealdan.battlearena.events.*;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.statistics.KillCounter;
import net.theforcemc.TheForceMC;
import net.theforcemc.util.CustomScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScoreboardListener implements Listener {

    private HashMap<UUID, Sidebar> sidebars = new HashMap<>();
    private HashMap<UUID, CustomScoreboard> _scoreboards = new HashMap<>();

    @EventHandler
    public void onFinish(BattleFinishEvent event) {
        sidebars.remove(event.getBattle().getBattleID());
        _scoreboards.remove(event.getBattle().getBattleID());
    }

    @EventHandler
    public void onJoin(BattleJoinEvent event) {
        getScoreboard(event.getBattle()).updateScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onLeave(BattleLeaveEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        updateScoreboard(event.getBattle());
    }

    @EventHandler
    public void onRespawn(BattleRespawnEvent event) {
        updateScoreboard(event.getBattle());
    }

    private void updateScoreboard(Battle battle) {
        CustomScoreboard scoreboard = getScoreboard(battle);
        KillCounter killCounter = battle.getKillCounter();

        List<String> lines = new ArrayList<>();
        List<Player> players = battle.getPlayers();
        while (players.size() > 0) {
            Player top = null;
            for (Player player : players) {
                if (top == null) {
                    top = player;
                } else if (killCounter.getKDR(player.getUniqueId()) > killCounter.getKDR(top.getUniqueId())) {
                    top = player;
                }
            }

            lines.add(top.getName() + " - " + TheForceMC.MAIN + killCounter.getKills(top.getUniqueId()));
            players.remove(top);
        }
        scoreboard.updateScoreboard(lines);
    }

    private CustomScoreboard getScoreboard(Battle battle) {
        if (!_scoreboards.containsKey(battle.getBattleID()))
            _scoreboards.putIfAbsent(battle.getBattleID(), new CustomScoreboard("ba_" + battle.getBattleID().toString().substring(0, 8), battle.getArena().getName() + ChatColor.RED + " : " + TheForceMC.MAIN + battle.getBattleType().getName()));
        return _scoreboards.get(battle.getBattleID());
    }
}