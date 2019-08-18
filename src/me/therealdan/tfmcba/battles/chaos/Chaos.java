package me.therealdan.tfmcba.battles.chaos;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.party.Party;
import net.theforcemc.equipment.Equipment;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chaos implements Battle {

    private static Random random = new Random();

    public Chaos(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName("Chaos"), started, party, settings);

        setSaveRestoreInventory(true);

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());

        giveRandomGun(player);
    }

    @Override
    public void second() {
        if (random.nextDouble() < 0.05) {
            Player playerA = getPlayers().get(random.nextInt(getPlayers().size()));
            Player playerB = getPlayers().get(random.nextInt(getPlayers().size()));
            if (playerA != playerB) swapPlayers(playerA, playerB);
        }
    }

    public void swapPlayers(Player playerA, Player playerB) {
        ItemStack[] contents = playerA.getInventory().getContents();
        ItemStack[] armor = playerA.getInventory().getArmorContents();
        playerA.getInventory().setContents(playerB.getInventory().getContents());
        playerA.getInventory().setArmorContents(playerB.getInventory().getArmorContents());
        playerB.getInventory().setContents(contents);
        playerB.getInventory().setArmorContents(armor);

        Location location = playerA.getLocation();
        for (Player target : getPlayers()) {
            target.hidePlayer(BattleArena.getInstance(), playerA);
            target.hidePlayer(BattleArena.getInstance(), playerB);
        }
        playerA.teleport(playerB);
        playerB.teleport(location);
        for (Player target : getPlayers()) {
            target.showPlayer(BattleArena.getInstance(), playerA);
            target.showPlayer(BattleArena.getInstance(), playerB);
        }
    }

    public void giveRandomGun(Player player) {
        List<Gun> guns = new ArrayList<>();
        for (Gun each : Gun.values()) {
            if (!each.getID().equals("admin")) {
                guns.add(each);
            }
        }

        player.getInventory().addItem(guns.get(random.nextInt(guns.size())).getItemStack());
    }

    public void giveRandomEquipment(Player player) {
        List<Equipment> equipment = new ArrayList<>();
        for (Equipment each : Equipment.values()) {
            if (!each.getID().equals("admin")) {
                equipment.add(each);
            }
        }

        player.getInventory().addItem(equipment.get(random.nextInt(equipment.size())).getItemStack());
    }
}