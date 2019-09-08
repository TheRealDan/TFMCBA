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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Roulette implements Battle {

    public final static String NAME = "Roulette";
    private Random random = new Random();

    private UUID lastPistolHolder;

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
        if (isGracePeriodActive()) {

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

            if (!someoneHasPistol && getPlayers().size() > 0) {
                Player player = getRandomPlayer();
                PlayerHandler.clearInventory(player);
                player.getInventory().addItem(getPistol().getItemStack());
                lastPistolHolder = player.getUniqueId();
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

    public Player getRandomPlayer() {
        List<UUID> players = new ArrayList<>();
        for (Player player : getPlayers()) players.add(player.getUniqueId());
        players.remove(lastPistolHolder);

        return Bukkit.getPlayer(players.size() > 1 ? players.get(random.nextInt(players.size())) : players.get(0));
    }

    public Gun getPistol() {
        return Gun.byID("pistol");
    }

    public Melee getPoisonKnife() {
        return Melee.byID("poison_knife");
    }
}