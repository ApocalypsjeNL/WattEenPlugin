package dev.niekv.listener;

import dev.niekv.WattEenPlugin;
import dev.niekv.player.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final WattEenPlugin plugin;

    public DeathListener(WattEenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(event.getEntity());
        if(playerData == null) {
            event.getEntity().kickPlayer("Failed to save deaths");
            return;
        }

        int deaths = playerData.incrementAndGetDeahts();


        TextComponent SEFA = new TextComponent("in de hemel");
        SEFA.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/watch?v=1IgtxOas6vc"));
        SEFA.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("What's SEFA doing here?").color(ChatColor.DARK_GRAY).create()));

        event.setDeathMessage(null);
        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(new ComponentBuilder("OEFF").color(ChatColor.RED)
                    .append(", ").color(ChatColor.GRAY).append(event.getEntity().getName()).color(ChatColor.DARK_RED)
                    .append(" is net gestorven ").color(ChatColor.GRAY).append(SEFA).reset().color(ChatColor.GRAY)
                    .append(". Zijn deathcount staat nu op ").color(ChatColor.GRAY).append(String.valueOf(deaths))
                    .color(ChatColor.YELLOW).create());
        });
    }
}
