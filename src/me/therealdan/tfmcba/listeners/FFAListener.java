package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.tfmcba.battles.FFA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FFAListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(FFA.NAME)) return;

        new FFA(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }
}