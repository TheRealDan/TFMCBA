package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.*;
import me.therealdan.battlearena.mechanics.lobby.Lobby;
import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.tfmcba.battles.ffa.FFA;
import me.therealdan.tfmcba.battles.team.Team;
import me.therealdan.tfmcba.settings.Health;
import net.theforcemc.equipment.shootable.flamethrower.FlamethrowerHandler;
import net.theforcemc.events.GunDamageEvent;
import net.theforcemc.events.GunShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

public class BattleListener implements Listener {

    private static BattleListener battleListener;

    private HashMap<UUID, UUID> lastPoisonDamage = new HashMap<>();
    private HashMap<UUID, UUID> lastFallDamage = new HashMap<>();

    private boolean canShootInLobby = false;

    public BattleListener() {
        battleListener = this;
    }

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
        if (event.getDamageCause().toString().contains("FIRE")) {
            Player attacker = Bukkit.getPlayer(FlamethrowerHandler.getLastFireDamage(event.getVictim().getUniqueId()));
            event.setAttacker(attacker);
        }

        if (event.getDamageCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (lastFallDamage.containsKey(event.getVictim().getUniqueId())) {
                event.setAttacker(Bukkit.getPlayer(lastFallDamage.get(event.getVictim().getUniqueId())));
                lastFallDamage.remove(event.getVictim().getUniqueId());
            }
        }

        if (event.getDamageCause().equals(EntityDamageEvent.DamageCause.POISON)) {
            if (lastPoisonDamage.containsKey(event.getVictim().getUniqueId())) {
                event.setAttacker(Bukkit.getPlayer(lastPoisonDamage.get(event.getVictim().getUniqueId())));
            }
        }
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {

    }

    @EventHandler
    public void onRespawn(BattleRespawnEvent event) {

    }

    @EventHandler
    public void onGunShoot(GunShootEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!canShootInLobby() && Lobby.getInstance().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGunDamage(GunDamageEvent event) {
        if (event.getGun().getID().equalsIgnoreCase("dartgun")) {
            lastPoisonDamage.put(event.getVictim().getUniqueId(), event.getAttacker().getUniqueId());
        }

        if (event.getGun().getKnockback() > 0) {
            lastFallDamage.put(event.getVictim().getUniqueId(), event.getAttacker().getUniqueId());
        }
    }

    public static void setCanShootInLobby(boolean canShootInLobby) {
        battleListener.canShootInLobby = canShootInLobby;
    }

    public static boolean canShootInLobby() {
        return battleListener.canShootInLobby;
    }
}