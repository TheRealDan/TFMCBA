package me.therealdan.tfmcba.commands;

import me.therealdan.tfmcba.TFMCBA;
import me.therealdan.tfmcba.listeners.BattleListener;
import me.therealdan.tfmcba.statistics.StatisticsViewer;
import net.theforcemc.TheForceMC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFMCBACommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("Statistics") || args[0].equalsIgnoreCase("Stats")) {
                statistics(sender, target, args);
                return true;
            } else if (args[0].equalsIgnoreCase("CanShootInLobby") || args[0].equalsIgnoreCase("CSIL")) {
                canShootInLobby(sender);
                return true;
            }
        }

        sender.sendMessage(TheForceMC.MAIN + "/TFMCBA Statistics " + TheForceMC.SECOND + "Open Statistics Viewer");
        sender.sendMessage(TheForceMC.MAIN + "/TFMCBA CanShootInLobby/CSIL " + TheForceMC.SECOND + "Currently " + (BattleListener.canShootInLobby() ? "allowed" : "not allowed"));
        return true;
    }

    private void statistics(CommandSender sender, Player target, String[] args) {
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
        }

        if (target == null) {
            sender.sendMessage(TheForceMC.MAIN + "Invalid Player. Try " + TheForceMC.SECOND + "/TFMCBA Statistics [Player]");
            return;
        }

        StatisticsViewer.getInstance().open(target);
        if (sender != target) sender.sendMessage(TheForceMC.MAIN + "Opened Statistics Viewer for " + TheForceMC.SECOND + target.getName());
    }

    private void canShootInLobby(CommandSender sender) {
        BattleListener.setCanShootInLobby(!BattleListener.canShootInLobby());
        sender.sendMessage(TheForceMC.MAIN + "Shooting in lobby is " + TheForceMC.SECOND + (BattleListener.canShootInLobby() ? "now allowed" : "no longer allowed"));
    }
}