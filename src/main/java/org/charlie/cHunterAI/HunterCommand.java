package org.charlie.cHunterAI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HunterCommand implements CommandExecutor {

    private final CHunterAI plugin;

    public HunterCommand(CHunterAI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        new HunterNPC(player.getLocation(), player);
        player.sendMessage("A hunter has been spawned to chase you!");
        return true;
    }
}
