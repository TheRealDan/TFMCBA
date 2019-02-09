package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.*;
import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.tfmcba.battles.ffa.FFA;
import me.therealdan.tfmcba.battles.team.Team;
import me.therealdan.tfmcba.settings.Health;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BattleListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        switch (event.getBattleType().getName()) {
            default:
                return;
            case "FFA":
                event.getSettings().apply(new FFA(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings()));
                break;
            case "Team":
                event.getSettings().apply(new Team(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings()));
                break;
        }

        event.setCreated(true);
    }

    @EventHandler
    public void onStart(BattleStartEvent event) {

    }

    @EventHandler
    public void onFinish(BattleFinishEvent event) {

    }

    @EventHandler
    public void onJoin(BattleJoinEvent event) {
        for (Setting setting : event.getBattle().getSettings().values()) {
            if (setting instanceof Health) {
                Health health = (Health) setting;
                health.apply(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onLeave(BattleLeaveEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(20);
        player.setHealthScale(20);
    }

    @EventHandler
    public void onDamage(BattleDamageEvent event) {

    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {

    }

    @EventHandler
    public void onRespawn(BattleRespawnEvent event) {

    }
}