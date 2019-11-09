package dev.niekv.command;

import dev.niekv.WattEenPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerStatsCommand implements CommandExecutor {

    private final WattEenPlugin plugin;

    public PlayerStatsCommand(WattEenPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String s, @Nonnull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (this.plugin.getScoreboardManager().toggleScoreboard(player)) {
                player.sendMessage(ChatColor.GREEN + "You enabled the scoreboard for the player statistics!");
            } else {
                player.sendMessage(ChatColor.YELLOW + "You disabled the scoreboard for the player statistics!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
        }
        return false;
    }
}
