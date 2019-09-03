package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleRespawnEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.MysteryWeapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MysteryWeaponListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(MysteryWeapon.NAME)) return;

        new MysteryWeapon(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDeath(BattleRespawnEvent event) {
        if (!(event.getBattle() instanceof MysteryWeapon)) return;
        MysteryWeapon mysteryWeapon = (MysteryWeapon) event.getBattle();

        PlayerHandler.clearInventory(event.getPlayer());
        event.getPlayer().getInventory().addItem(mysteryWeapon.getRandomWeapon().getItemStack());
    }
}