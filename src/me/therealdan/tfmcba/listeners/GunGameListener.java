package me.therealdan.tfmcba.listeners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.gungame.GunGame;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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

        if (event.getKiller() != null) {
            Gun gun = null;
            for (ItemStack itemStack : event.getKiller().getInventory().getContents()) {
                if (itemStack != null && Gun.byItemStack(itemStack) != null) {
                    gun = Gun.byItemStack(itemStack);
                    break;
                }
            }

            if (gun != null) {
                if (gun.getID().equals("rotaryblastercannon")) gun = Gun.byID("missilelauncher");
                if (gun.getID().equals("beamrifle")) gun = Gun.byID("rotaryblastercannon");
                if (gun.getID().equals("ionblaster")) gun = Gun.byID("beamrifle");
                if (gun.getID().equals("repeaterrifle")) gun = Gun.byID("ionblaster");
                if (gun.getID().equals("disruptorrifle")) gun = Gun.byID("repeaterrifle");
                if (gun.getID().equals("scatterrifle")) gun = Gun.byID("disruptorrifle");
                if (gun.getID().equals("flamethrower")) gun = Gun.byID("scatterrifle");
                if (gun.getID().equals("dartgun")) gun = Gun.byID("flamethrower");
                if (gun.getID().equals("sonicblaster")) gun = Gun.byID("dartgun");
                if (gun.getID().equals("assaultrifle")) gun = Gun.byID("sonicblaster");
                if (gun.getID().equals("heavyblaster")) gun = Gun.byID("assaultrifle");
                if (gun.getID().equals("blasterrifle")) gun = Gun.byID("heavyblaster");
                if (gun.getID().equals("pistol")) gun = Gun.byID("blasterrifle");

                PlayerHandler.clearInventory(event.getKiller());
                event.getKiller().getInventory().addItem(gun.getItemStack());
            } else {
                PlayerHandler.clearInventory(event.getKiller());
                event.getKiller().getInventory().addItem(Gun.byID("pistol").getItemStack());
            }
        }
    }
}