package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.tfmcba.battles.TeamBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamBattleListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(TeamBattle.NAME)) return;

        new TeamBattle(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }
}