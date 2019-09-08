package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.events.BattleJoinEvent;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.tfmcba.battles.Scavenger;
import net.theforcemc.events.GunShootEvent;
import org.bukkit.entity.Player;
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
    public void onJoin(BattleJoinEvent event) {
        if (!(event.getBattle() instanceof Scavenger)) return;
        Scavenger scavenger = (Scavenger) event.getBattle();

        scavenger.giveWeapon(event.getPlayer());
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof Scavenger)) return;
        Scavenger scavenger = (Scavenger) event.getBattle();

        if (event.getKiller() != null) {
            scavenger.giveWeapon(event.getKiller());
        }
    }

    @EventHandler
    public void onGunShoot(GunShootEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        Battle battle = Battle.get(player);
        if (!(battle instanceof Scavenger)) return;
        Scavenger scavenger = (Scavenger) battle;

        scavenger.fireBullet(player, event.getGun());
    }
}