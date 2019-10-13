package dev.niekv.scoreboard.schedule;

import dev.niekv.scoreboard.ScoreboardManager;

public class ScoreboardUpdateScheduler implements Runnable {

    private final ScoreboardManager scoreboardManager;

    public ScoreboardUpdateScheduler(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public void run() {
        this.scoreboardManager.getPlugin().getServer().getOnlinePlayers().forEach(this.scoreboardManager::update);
    }
}
