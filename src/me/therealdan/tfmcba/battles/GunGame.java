package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Setting;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import me.therealdan.tfmcba.settings.GunRestrictions;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GunGame implements Battle {

    public final static String NAME = "Gun Game";

    private List<Gun> gunsOrder = new ArrayList<>();

    public GunGame(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(NAME), started, party, settings);

        setSaveRestoreInventory(true);

        gunsOrder.add(Gun.byID("pistol"));
        gunsOrder.add(Gun.byID("blasterrifle"));
        gunsOrder.add(Gun.byID("heavyblaster"));
        gunsOrder.add(Gun.byID("assaultrifle"));
        gunsOrder.add(Gun.byID("sonicblaster"));
        gunsOrder.add(Gun.byID("dartgun"));
        gunsOrder.add(Gun.byID("flamethrower"));
        gunsOrder.add(Gun.byID("scatterrifle"));
        gunsOrder.add(Gun.byID("disruptorrifle"));
        gunsOrder.add(Gun.byID("repeaterrifle"));
        gunsOrder.add(Gun.byID("ionblaster"));
        gunsOrder.add(Gun.byID("beamrifle"));
        gunsOrder.add(Gun.byID("rotaryblastercannon"));
        gunsOrder.add(Gun.byID("missilelauncher"));

        for (Setting setting : settings.values()) {
            if (setting instanceof GunRestrictions) {
                GunRestrictions gunRestrictions = (GunRestrictions) setting;
                for (String gunID : gunRestrictions.getDisabled()) {
                    gunsOrder.remove(Gun.byID(gunID));
                }
                break;
            }
        }

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());

        PlayerHandler.clearInventory(player);
        player.getInventory().addItem(getNext(player).getItemStack());
    }

    public Gun getNext(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && Gun.byItemStack(itemStack) != null) {
                return Gun.byItemStack(itemStack);
            }
        }
        return getNext((Gun) null);
    }

    public Gun getNext(Gun gun) {
        if (gun != null) {
            boolean next = false;
            for (Gun each : gunsOrder) {
                if (next) return each;
                if (each == gun) next = true;
            }
            return gun;
        }
        return gunsOrder.get(0);
    }
}