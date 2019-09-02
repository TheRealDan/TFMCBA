package me.therealdan.tfmcba.battles.ffa;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import org.bukkit.entity.Player;

public class FFA implements Battle {

    public FFA(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName("FFA"), started, party, settings);

        setSaveRestoreInventory(true);

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());

        PlayerHandler.clearInventory(player);
        player.getInventory().addItem(EquipmentSelector.getInstance().getEquipmentSelectorItem());
    }
}
