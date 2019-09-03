package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import me.therealdan.tfmcba.TFMCBA;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FFA implements Battle {

    public final static String NAME = "FFA";

    public FFA(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(FFA.NAME), started, party, settings);

        setSaveRestoreInventory(true);

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void second() {
        if (!isGracePeriodActive()) {
            for (Player player : getPlayers()) {
                player.getInventory().remove(EquipmentSelector.getInstance().getEquipmentSelectorItem());
            }
        }
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());

        PlayerHandler.clearInventory(player);
        player.getInventory().addItem(EquipmentSelector.getInstance().getEquipmentSelectorItem());
        Bukkit.getScheduler().scheduleSyncDelayedTask(TFMCBA.getInstance(), () -> EquipmentSelector.getInstance().open(player), 20);
    }
}
