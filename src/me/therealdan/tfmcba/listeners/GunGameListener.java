package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.GunGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GunGameListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(GunGame.NAME)) return;

        new GunGame(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof GunGame)) return;
        GunGame gunGame = (GunGame) event.getBattle();

        if (event.getKiller() != null) {
            PlayerHandler.clearInventory(event.getKiller());
            event.getKiller().getInventory().addItem(gunGame.getNext(event.getKiller()).getItemStack());
        }
    }
}