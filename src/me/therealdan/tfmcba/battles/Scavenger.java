package me.therealdan.tfmcba.battles;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import net.theforcemc.TheForceMC;
import net.theforcemc.equipment.Weapon;
import net.theforcemc.equipment.shootable.gun.Gun;
import org.bukkit.entity.Player;

import java.util.*;

public class Scavenger implements Battle {

    public final static String NAME = "Scavenger";

    private List<Weapon> weapons = new ArrayList<>();
    private Random random = new Random();

    private HashMap<UUID, HashMap<String, Long>> ammo = new HashMap<>();

    public Scavenger(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(Scavenger.NAME), started, party, settings);

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
    public void tick() {
        for (Player player : getPlayers()) {
            Gun gun = Gun.byItemStack(player.getEquipment().getItemInMainHand());
            if (gun == null) continue;
            

        }
    }

    @Override
    public void second() {
        for (Player player : getPlayers()) {
            if (!ammo.containsKey(player.getUniqueId())) ammo.put(player.getUniqueId(), new HashMap<>());
            for (String gunID : ammo.get(player.getUniqueId()).keySet()) {
                Gun gun = Gun.byID(gunID);
                if (remainingAmmo(player, gun) <= 0) {
                    player.getInventory().remove(gun.getItemStack());
                }
            }
        }
    }

    @Override
    public void add(Player player) {
        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined the " + BattleArena.SECOND + this.getBattleType().getName());

        PlayerHandler.clearInventory(player);
        giveWeapon(player);
    }

    public void fireBullet(Player player, Gun gun) {
        if (!this.ammo.containsKey(player.getUniqueId())) this.ammo.put(player.getUniqueId(), new HashMap<>());

        HashMap<String, Long> ammo = this.ammo.getOrDefault(player.getUniqueId(), new HashMap<>());
        ammo.put(gun.getID(), remainingAmmo(player, gun) - 1);
        this.ammo.put(player.getUniqueId(), ammo);
    }

    public void setRemainingAmmo(Player player, Gun gun, long remaining) {
        if (!this.ammo.containsKey(player.getUniqueId())) this.ammo.put(player.getUniqueId(), new HashMap<>());

        HashMap<String, Long> ammo = this.ammo.getOrDefault(player.getUniqueId(), new HashMap<>());
        ammo.put(gun.getID(), remaining);
        this.ammo.put(player.getUniqueId(), ammo);
    }

    public void giveWeapon(Player player) {
        Weapon weapon = getRandomWeapon();
        if (weapon instanceof Gun) {
            Gun gun = (Gun) weapon;
            long remaining = getRandomAmmo(gun);
            setRemainingAmmo(player, gun, remaining);
            player.sendMessage(TheForceMC.MAIN + "You've been given a " + TheForceMC.SECOND + gun.getDisplayName() + TheForceMC.MAIN + " with " + TheForceMC.SECOND + remaining + TheForceMC.MAIN + " ammo remaining");
        }
        player.getInventory().addItem(weapon.getItemStack());
    }

    public long remainingAmmo(Player player, Gun gun) {
        if (!ammo.containsKey(player.getUniqueId())) return 0;
        if (!ammo.get(player.getUniqueId()).containsKey(gun.getID())) return 0;

        return ammo.get(player.getUniqueId()).get(gun.getID());
    }

    public long getRandomAmmo(Gun gun) {
        double cluster = gun.getCluster();
        double reload = gun.getRecharge() / 1000D;
        double burst = gun.getBurst() / 20D;

        return (long) ((cluster * (double) gun.getBurst() / (reload + burst)) * (double) (2 + random.nextInt(6)));
    }

    public Weapon getRandomWeapon() {
        return weapons.get(random.nextInt(weapons.size()));
    }
}