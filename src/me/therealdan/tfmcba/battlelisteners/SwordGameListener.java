package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.SwordGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SwordGameListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(SwordGame.NAME)) return;

        new SwordGame(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof SwordGame)) return;
        SwordGame swordGame = (SwordGame) event.getBattle();

        if (event.getKiller() != null) {
            PlayerHandler.clearInventory(event.getKiller());
            event.getKiller().getInventory().addItem(swordGame.getNext(event.getKiller()).getItemStack());
        }
    }
}