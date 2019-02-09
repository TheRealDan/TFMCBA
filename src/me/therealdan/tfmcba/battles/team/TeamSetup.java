package me.therealdan.tfmcba.battles.team;

import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.mechanics.setup.Setup;
import me.therealdan.battlearena.mechanics.setup.settings.BattleDuration;
import me.therealdan.battlearena.mechanics.setup.settings.GracePeriod;
import me.therealdan.battlearena.mechanics.setup.settings.Map;
import me.therealdan.battlearena.mechanics.setup.settings.Open;
import me.therealdan.tfmcba.settings.Health;

public class TeamSetup extends Setup {

    public TeamSetup() {
        super("Team", new Settings(
                new Map(Arena.getFree()),
                new Open(true),
                new BattleDuration(180),
                new GracePeriod(0),
                new Health(20)
        ));
    }
}