package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.Roulette;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RouletteListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(Roulette.NAME)) return;

        new Roulette(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof Roulette)) return;
        Roulette roulette = (Roulette) event.getBattle();

        Player victim = event.getPlayer();
        Player killer = event.getKiller();

        if (roulette.hasPistol(killer)) {
            PlayerHandler.clearInventory(killer);
            killer.getInventory().addItem(roulette.getPoisonKnife().getItemStack());

        } else if (roulette.hasPoisonKnife(killer) && roulette.hasPistol(victim)) {
            PlayerHandler.clearInventory(killer);
            killer.getInventory().addItem(roulette.getPistol().getItemStack());

            PlayerHandler.clearInventory(victim);
            victim.getInventory().addItem(roulette.getPoisonKnife().getItemStack());
        }
    }
}