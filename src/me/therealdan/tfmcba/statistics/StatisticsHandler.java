package me.therealdan.tfmcba.statistics;

import me.therealdan.battlearena.events.BattleJoinEvent;
import me.therealdan.battlearena.events.BattleLeaveEvent;
import me.therealdan.battlearena.mechanics.battle.Battle;
import net.theforcemc.equipment.shootable.gun.Gun;
import net.theforcemc.events.GunDamageEvent;
import net.theforcemc.events.GunEquipEvent;
import net.theforcemc.events.GunShootEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class StatisticsHandler implements Listener {

    private static StatisticsHandler statisticsHandler;

    private HashMap<UUID, Gun> mainHand = new HashMap<>();
    private HashMap<UUID, Gun> offHand = new HashMap<>();
    private HashMap<UUID, Long> mainHandStart = new HashMap<>();
    private HashMap<UUID, Long> offHandStart = new HashMap<>();

    private StatisticsHandler() {

    }

    @EventHandler
    public void onJoin(BattleJoinEvent event) {
        Player player = event.getPlayer();

        Gun mainHand = Gun.byEntity(player, true);
        if (mainHand != null) {
            this.mainHand.put(player.getUniqueId(), mainHand);
            this.mainHandStart.put(player.getUniqueId(), System.currentTimeMillis());
        }

        Gun offHand = Gun.byEntity(event.getPlayer(), false);
        if (offHand != null) {
            this.offHand.put(player.getUniqueId(), offHand);
            this.offHandStart.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onLeave(BattleLeaveEvent event) {
        Player player = event.getPlayer();

        Gun mainHand = Gun.byEntity(player, true);
        if (mainHand != null) {
            this.mainHand.remove(player.getUniqueId());
            long timepassed = System.currentTimeMillis() - this.mainHandStart.get(player.getUniqueId());
            Statistics.byPlayer(player).addTimeHeld(mainHand, timepassed);
        }

        Gun offHand = Gun.byEntity(player, false);
        if (offHand != null) {
            this.offHand.remove(player.getUniqueId());
            long timepassed = System.currentTimeMillis() - this.offHandStart.get(player.getUniqueId());
            Statistics.byPlayer(player).addTimeHeld(offHand, timepassed);
        }
    }

    @EventHandler
    public void onGunEquip(GunEquipEvent event) {
        Player player = event.getPlayer();

        if (Battle.get(player) == null) return;

        Gun previous = event.getPrevious();
        if (previous != null &&
                (event.isMainHand() ? this.mainHand : this.offHand).containsKey(player.getUniqueId()) &&
                (event.isMainHand() ? this.mainHand : this.offHand).get(player.getUniqueId()).equals(previous)) {
            long timepassed = System.currentTimeMillis() - (event.isMainHand() ? this.mainHandStart : this.offHandStart).get(player.getUniqueId());
            Statistics.byPlayer(player).addTimeHeld(previous, timepassed);
        }

        Gun gun = event.getGun();
        if (gun != null) {
            (event.isMainHand() ? this.mainHand : this.offHand).put(player.getUniqueId(), gun);
            (event.isMainHand() ? this.mainHandStart : this.offHandStart).put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onGunDamage(GunDamageEvent event) {
        if (event.isCancelled()) return;
        if (event.isDeflect()) return;
        if (!(event.getVictim() instanceof Player)) return;
        if (!(event.getAttacker() instanceof Player)) return;
        Player victim = (Player) event.getVictim();
        Player attacker = (Player) event.getAttacker();

        if (Battle.get(victim) == null) return;
        if (Battle.get(attacker) == null) return;

        // Damage dealt
        Statistics.byPlayer(attacker).addDamageDealt(event.getGun(), event.getDamage());

        // Kills
        if (victim.getHealth() - event.getFinalDamage() <= 0.0)
            Statistics.byPlayer(attacker).addKill(event.getGun());
    }

    @EventHandler
    public void onGunShoot(GunShootEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (Battle.get(player) == null) return;

        // Bullets fired
        Statistics.byPlayer(player).addBulletFired(event.getGun(), event.getGun().getCluster() * event.getGun().getBurst());
    }

    public static StatisticsHandler getInstance() {
        if (statisticsHandler == null) statisticsHandler = new StatisticsHandler();
        return statisticsHandler;
    }
}