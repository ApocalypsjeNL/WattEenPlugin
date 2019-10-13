package dev.niekv.listener;

import dev.niekv.WattEenPlugin;
import dev.niekv.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class JoinLeaveListener implements Listener {

    private static final String JOIN_MESSAGE = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "+" +
            ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "%s heeft het domein van " +
            ChatColor.DARK_GREEN + "Watt" + ChatColor.YELLOW + "EenServer" +
            ChatColor.GRAY + " betreden!";

    private static final String LEAVE_MESSAGE = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" +
            ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "%s heeft het domein van " +
            ChatColor.DARK_GREEN + "Watt" + ChatColor.YELLOW + "EenServer" +
            ChatColor.GRAY + " verlaten!";

    private final WattEenPlugin plugin;

    public JoinLeaveListener(WattEenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        completableFuture.whenComplete((v, e) -> {
            this.plugin.getServer().getScheduler().callSyncMethod(this.plugin, () -> {
                this.plugin.getScoreboardManager().initializeScoreboard(event.getPlayer());
                return null;
            });
        });

        this.plugin.getPlayerDataManager().loadPlayer(event.getPlayer(), completableFuture);

        event.setJoinMessage(String.format(JoinLeaveListener.JOIN_MESSAGE, event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerDataCache().remove(event.getPlayer().getUniqueId());

        this.plugin.getPlayerDataManager().savePlayer(playerData);

        event.setQuitMessage(String.format(JoinLeaveListener.LEAVE_MESSAGE, event.getPlayer().getName()));
        this.plugin.getScoreboardManager().cleanup(event.getPlayer());
    }
}
