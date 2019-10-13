package dev.niekv.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final static String CHAT_FORMAT = ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE + ": " +
            ChatColor.GRAY + "%s";

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(PlayerChatListener.CHAT_FORMAT);
    }
}
