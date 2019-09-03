package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import net.theforcemc.TheForceMC;
import net.theforcemc.equipment.melee.Melee;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hero implements Battle {

    public final static String NAME = "Hero";

    private Melee lightsaber = Melee.byID("blue_lightsaber");
    private List<Gun> guns = new ArrayList<>();
    private Random random = new Random();

    public Hero(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(Hero.NAME), started, party, settings);

        setSaveRestoreInventory(true);

        for (Gun gun : Gun.values()) {
            if (gun.isEnabled() && !gun.isAdminOnly()) {
                guns.add(gun);
            }
        }

        add(started);
        if (party != null)
            for (Player player : party.getPlayers())
                add(player);
    }

    @Override
    public void second() {
        if (!hasHero()) {
            Player newHero = getPlayers().get(random.nextInt(getPlayers().size()));
            PlayerHandler.clearInventory(newHero);
            newHero.getInventory().addItem(getLightsaber().getItemStack());

            for (Player player : getPlayers()) {
                player.sendMessage(TheForceMC.SECOND + newHero.getName() + TheForceMC.MAIN + " is the new Hero!");
            }
        }
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());
    }

    public boolean hasHero() {
        for (Player player : getPlayers()) {
            if (isHero(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHero(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().equals(Material.AIR)) continue;
            if (lightsaber.is(itemStack)) return true;
        }
        return false;
    }

    public Melee getLightsaber() {
        return lightsaber;
    }

    public Gun getRandomGun() {
        return guns.get(random.nextInt(guns.size()));
    }
}