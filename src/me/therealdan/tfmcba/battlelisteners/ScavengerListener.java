package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.tfmcba.battles.Scavenger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScavengerListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(Scavenger.NAME)) return;

        new Scavenger(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof Scavenger)) return;
        Scavenger scavenger = (Scavenger) event.getBattle();

        if (event.getKiller() != null) {
            event.getKiller().getInventory().addItem(scavenger.getRandomEquipment().getItemStack());
        }
    }
}