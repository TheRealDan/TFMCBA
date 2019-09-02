package me.therealdan.tfmcba.battles.team;

import me.therealdan.battlearena.BattleArena;
import me.therealdan.battlearena.events.BattleLeaveEvent;
import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.battle.Battle;
import me.therealdan.battlearena.mechanics.battle.BattleType;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.party.Party;
import me.therealdan.tfmcba.TFMCBA;
import me.therealdan.tfmcba.listeners.EquipmentSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Team implements Battle {

    public final static String NAME = "Team";

    private HashSet<UUID> team1 = new HashSet<>();
    private HashSet<UUID> team2 = new HashSet<>();

    public Team(Arena arena, Player started, Party party, Settings settings) {
        init(arena, BattleType.byName(Team.NAME), started, party, settings);

        setSaveRestoreInventory(true);

        if (party != null)
            for (Player player : party.getPlayers())
                add(player, party.isTeam(player, 1));
    }

    @Override
    public void second() {
        if (getGraceTimeRemaining() <= 0) {
            for (Player player : getPlayers()) {
                player.getInventory().remove(EquipmentSelector.getInstance().getEquipmentSelectorItem());
            }
        }
    }

    @Override
    public void end(BattleLeaveEvent.Reason reason) {
        int team1Kills = getTotalKills(true);
        int team2Kills = getTotalKills(false);
        int mostKills = Math.max(team1Kills, team2Kills);
        String mostKillsTeam = team1Kills >= team2Kills ? "Team 1" : "Team 2";

        String battleMessage = null;
        if (mostKills > 0)
            battleMessage = BattleArena.SECOND + mostKillsTeam + BattleArena.MAIN + " got the most kills, with " + BattleArena.SECOND + mostKills + BattleArena.MAIN + " kills.";
        end(reason, battleMessage);
    }

    @Override
    public void add(Player player) {
        add(player, !(team1.size() > team2.size()));
    }

    public void add(Player player, boolean team1) {
        if (contains(player)) return;

        PlayerHandler.clearInventory(player);
        player.getInventory().addItem(EquipmentSelector.getInstance().getEquipmentSelectorItem());
        Bukkit.getScheduler().scheduleSyncDelayedTask(TFMCBA.getInstance(), () -> EquipmentSelector.getInstance().open(player), 20);

        add(player, BattleArena.SECOND + player.getName() + BattleArena.MAIN + " has joined " + BattleArena.SECOND + "Team " + (isTeam1(player) ? "1" : "2"));

        if (team1) {
            this.team1.add(player.getUniqueId());
        } else {
            this.team2.add(player.getUniqueId());
        }
    }

    @Override
    public void remove(Player player, BattleLeaveEvent.Reason reason) {
        this.team1.remove(player.getUniqueId());
        this.team2.remove(player.getUniqueId());

        Battle.super.remove(player, reason);
    }

    @Override
    public void respawn(Player player) {
        respawn(player, getRandomSpawnpoint(player));
    }

    public boolean isTeam1(Player player) {
        return team1.contains(player.getUniqueId());
    }

    @Override
    public boolean sameTeam(Player player, Player player1) {
        return isTeam1(player) == isTeam1(player1);
    }

    public int getTotalKills(boolean team1) {
        int totalKills = 0;
        for (Player player : team1 ? getTeam1Players() : getTeam2Players())
            totalKills += getKillCounter().getKills(player.getUniqueId());
        return totalKills;
    }

    public Location getRandomSpawnpoint(Player player) {
        return getRandomSpawnpoint(getArena().getLocations(isTeam1(player) ? 2 : 3));
    }

    public List<Player> getTeam1Players() {
        List<Player> players = new ArrayList<>();
        for (Player player : getPlayers())
            if (isTeam1(player))
                players.add(player);
        return players;
    }

    public List<Player> getTeam2Players() {
        List<Player> players = new ArrayList<>();
        for (Player player : getPlayers())
            if (!isTeam1(player))
                players.add(player);
        return players;
    }
}