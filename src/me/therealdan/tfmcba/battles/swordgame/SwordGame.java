package me.therealdan.tfmcba.battles.swordgame;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import net.theforcemc.equipment.melee.Melee;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SwordGame implements Battle {

    public static final String NAME = "Sword Game";

    private List<Melee> meleeOrder = new ArrayList<>();

    public SwordGame(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(NAME), started, party, settings);

        setSaveRestoreInventory(true);

        meleeOrder.add(Melee.byID("electroblade"));
        meleeOrder.add(Melee.byID("poison_knife"));
        meleeOrder.add(Melee.byID("warbalde"));
        meleeOrder.add(Melee.byID("cortosis_sword"));
        meleeOrder.add(Melee.byID("zhaboka"));
        meleeOrder.add(Melee.byID("electrostaff"));
        meleeOrder.add(Melee.byID("blue_lightsaber"));

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

    public Melee getNext(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && Melee.byItemStack(itemStack) != null) {
                return Melee.byItemStack(itemStack);
            }
        }
        return getNext((Melee) null);
    }

    public Melee getNext(Melee melee) {
        if (melee != null) {
            boolean next = false;
            for (Melee each : meleeOrder) {
                if (next) return each;
                if (each == melee) next = true;
            }
            return melee;
        }
        return meleeOrder.get(0);
    }
}