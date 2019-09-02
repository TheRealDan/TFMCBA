package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.*;
import me.therealdan.battlearena.mechanics.lobby.Lobby;
import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import me.therealdan.tfmcba.settings.GunRestrictions;
import me.therealdan.tfmcba.settings.Health;
import net.theforcemc.equipment.armor.ArmorHandler;
import net.theforcemc.equipment.shootable.flamethrower.FlamethrowerHandler;
import net.theforcemc.equipment.shootable.gun.Gun;
import net.theforcemc.events.GunDamageEvent;
import net.theforcemc.events.GunShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class BattleListener implements Listener {

    private static BattleListener battleListener;

    private HashMap<UUID, UUID> lastPoisonDamage = new HashMap<>();
    private HashMap<UUID, UUID> lastFallDamage = new HashMap<>();

    private HashMap<UUID, HashMap<Integer, ItemStack>> items = new HashMap<>();

    private boolean canShootInLobby = true;

    public BattleListener() {
        battleListener = this;
    }

    @EventHandler
    public void onJoin(BattleJoinEvent event) {
        for (Setting setting : event.getBattle().getSettings().values()) {
            if (setting instanceof Health) {
                Health health = (Health) setting;
                health.apply(event.getPlayer());
            }
            if (setting instanceof GunRestrictions && !event.getBattle().isSaveRestoreInventoryEnabled()) {
                Player player = event.getPlayer();
                GunRestrictions gunRestrictions = (GunRestrictions) setting;
                HashMap<Integer, ItemStack> items = new HashMap<>();
                int slot = 0;
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    Gun gun = Gun.byItemStack(itemStack);
                    if (gun != null) {
                        if (!gunRestrictions.isAllowed(gun)) {
                            items.put(slot, itemStack);
                            player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                        }
                    }
                    slot++;
                }
                this.items.put(player.getUniqueId(), items);
            }
        }
    }

    @EventHandler
    public void onLeave(BattleLeaveEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(20);
        player.setHealthScale(20);

        for (Setting setting : event.getBattle().getSettings().values()) {
            if (setting instanceof GunRestrictions && !event.getBattle().isSaveRestoreInventoryEnabled()) {
                HashMap<Integer, ItemStack> items = this.items.get(player.getUniqueId());
                for (int slot : items.keySet()) {
                    if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType().equals(Material.AIR)) {
                        player.getInventory().setItem(slot, items.get(slot));
                    } else {
                        player.getInventory().addItem(items.get(slot));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(BattleDamageEvent event) {
        if (EquipmentSelector.getInstance().isOpen(event.getVictim())) {
            event.setCancelled(true);
            return;
        }

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
        ArmorHandler.setJetpackFuel(event.getPlayer().getUniqueId(), Long.MAX_VALUE);
    }

    @EventHandler
    public void onRespawn(BattleRespawnEvent event) {
        ArmorHandler.setJetpackFuel(event.getPlayer().getUniqueId(), Long.MAX_VALUE);
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