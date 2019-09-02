package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import net.theforcemc.equipment.melee.Melee;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Roulette implements Battle {

    public final static String NAME = "Roulette";
    private Random random = new Random();

    public Roulette(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(Roulette.NAME), started, party, settings);

        setSaveRestoreInventory(true);

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void second() {
        if (getGraceTimeRemaining() <= 0) {

            for (Player player : getPlayers()) {
                if (!hasPoisonKnife(player)) {
                    player.getInventory().addItem(getPoisonKnife().getItemStack());
                }
            }

        } else {

            boolean someoneHasPistol = false;
            for (Player player : getPlayers()) {
                if (hasPistol(player)) {
                    if (someoneHasPistol) {
                        PlayerHandler.clearInventory(player);
                        player.getInventory().addItem(getPoisonKnife().getItemStack());
                    } else {
                        someoneHasPistol = true;
                    }
                } else if (!hasPoisonKnife(player)) {
                    player.getInventory().addItem(getPoisonKnife().getItemStack());
                }
            }

            if (!someoneHasPistol) {
                Player player = getPlayers().get(random.nextInt(getPlayers().size()));
                PlayerHandler.clearInventory(player);
                player.getInventory().addItem(getPistol().getItemStack());
            }

        }
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());
    }

    public boolean hasPistol(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) continue;
            if (getPistol().is(itemStack)) return true;
        }
        return false;
    }

    public boolean hasPoisonKnife(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) continue;
            if (getPoisonKnife().is(itemStack)) return true;
        }
        return false;
    }

    public Gun getPistol() {
        return Gun.byID("pistol");
    }

    public Melee getPoisonKnife() {
        return Melee.byID("poison_knife");
    }
}