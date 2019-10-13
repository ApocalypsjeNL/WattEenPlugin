package dev.niekv.player.schedule;

import dev.niekv.player.PlayerDataManager;

public class AutoSaveScheduler implements Runnable {

    private final PlayerDataManager playerDataManager;

    public AutoSaveScheduler(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        this.playerDataManager.getPlugin().getServer().getOnlinePlayers().forEach(this.playerDataManager::savePlayer);
    }
}
