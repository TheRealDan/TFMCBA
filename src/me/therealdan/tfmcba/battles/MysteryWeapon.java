package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.party.Party;
import net.theforcemc.equipment.Weapon;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MysteryWeapon implements Battle {

    public final static String NAME = "Mystery Weapon";

    private List<Weapon> weapons = new ArrayList<>();
    private Random random = new Random();

    public MysteryWeapon(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(MysteryWeapon.NAME), started, party, settings);

        setSaveRestoreInventory(true);

        for (Weapon weapon : Weapon.values()) {
            if (weapon.isEnabled() && !weapon.isAdminOnly()) {
                weapons.add(weapon);
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
    }

    public Weapon getRandomWeapon() {
        return weapons.get(random.nextInt(weapons.size()));
    }
}