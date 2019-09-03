package me.therealdan.tfmcba.battlelisteners;

import me.therealdan.battlearena.events.BattleCreateEvent;
import me.therealdan.battlearena.events.BattleDamageEvent;
import me.therealdan.battlearena.events.BattleDeathEvent;
import me.therealdan.battlearena.events.BattleRespawnEvent;
import me.therealdan.battlearena.util.PlayerHandler;
import me.therealdan.tfmcba.battles.Hero;
import net.theforcemc.TheForceMC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeroListener implements Listener {

    @EventHandler
    public void onCreate(BattleCreateEvent event) {
        if (!event.getBattleType().getName().equals(Hero.NAME)) return;

        new Hero(event.getArena(), event.getPlayer(), event.getParty(), event.getSettings());
        event.setCreated(true);
    }

    @EventHandler
    public void onDamage(BattleDamageEvent event) {
        if (!(event.getBattle() instanceof Hero)) return;
        Hero hero = (Hero) event.getBattle();

        if (!hero.isHero(event.getVictim()) && !hero.isHero(event.getAttacker())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(BattleDeathEvent event) {
        if (!(event.getBattle() instanceof Hero)) return;
        Hero hero = (Hero) event.getBattle();

        if (hero.isHero(event.getPlayer())) {
            for (Player player : hero.getPlayers()) {
                PlayerHandler.clearInventory(player);
                player.getInventory().addItem(hero.getRandomGun().getItemStack());
            }

            event.setBattleMessage(TheForceMC.MAIN + "Hero " + TheForceMC.SECOND + event.getPlayer().getName() + TheForceMC.MAIN + " has been slain!");
        }
    }

    @EventHandler
    public void onRespawn(BattleRespawnEvent event) {
        if (!(event.getBattle() instanceof Hero)) return;
        Hero hero = (Hero) event.getBattle();

        if (!hero.isHero(event.getPlayer())) {
            event.getPlayer().getInventory().addItem(hero.getRandomGun().getItemStack());
        }
    }
}