package me.therealdan.tfmcba.setup;

import me.therealdan.battlearena.mechanics.arena.Arena;
import me.therealdan.battlearena.mechanics.setup.Settings;
import me.therealdan.battlearena.mechanics.setup.Setup;
import me.therealdan.battlearena.mechanics.setup.settings.BattleDuration;
import me.therealdan.battlearena.mechanics.setup.settings.GracePeriod;
import me.therealdan.battlearena.mechanics.setup.settings.Map;
import me.therealdan.battlearena.mechanics.setup.settings.Open;
import me.therealdan.tfmcba.battles.Roulette;
import me.therealdan.tfmcba.settings.Competitive;
import me.therealdan.tfmcba.settings.Health;

public class RouletteSetup extends Setup {

    public RouletteSetup() {
        super(Roulette.NAME, new Settings(
                new Map(Arena.getFree()),
                new BattleDuration(180),
                new GracePeriod(0),
                new Health(20),
                new Competitive(true),
                new Open(true)
        ));
    }
}