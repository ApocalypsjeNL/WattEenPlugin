package dev.niekv.scoreboard;

import dev.niekv.WattEenPlugin;
import dev.niekv.player.PlayerData;
import dev.niekv.scoreboard.schedule.ScoreboardUpdateScheduler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager {

    private static final String OBJECTIVE_NAME = "playerdata";

    private final WattEenPlugin plugin;
    private final org.bukkit.scoreboard.ScoreboardManager bukkitScoreboardManager;

    private Map<UUID, Scoreboard> scoreboardMap = new HashMap<>();
    private List<UUID> ignoredScoreboard = new ArrayList<>();

    public ScoreboardManager(WattEenPlugin plugin) {
        this.plugin = plugin;

        this.bukkitScoreboardManager = plugin.getServer().getScoreboardManager();

        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new ScoreboardUpdateScheduler(this), 0L, 20L);
    }

    public WattEenPlugin getPlugin() {
        return this.plugin;
    }

    public Map<UUID, Scoreboard> getScoreboardMap() {
        return this.scoreboardMap;
    }

    public void initializeScoreboard(Player player) {
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        Scoreboard scoreboard = bukkitScoreboardManager.getNewScoreboard();
        scoreboardMap.put(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);

        this.renderScoreboard(scoreboard, playerData);
    }

    public void update(Player player) {
        this.renderScoreboard(this.scoreboardMap.get(player.getUniqueId()), this.plugin.getPlayerDataManager().getPlayerData(player));
    }

    public boolean toggleScoreboard(Player player) {
        if(this.ignoredScoreboard.contains(player.getUniqueId())) {
            this.ignoredScoreboard.remove(player.getUniqueId());
            this.initializeScoreboard(player);
            return true;
        } else {
            this.ignoredScoreboard.add(player.getUniqueId());
            this.cleanup(player);
            return false;
        }
    }

    private void renderScoreboard(Scoreboard scoreboard, PlayerData playerData) {
        if(scoreboard == null) {
            return;
        }

        Objective previousObjective;
        if((previousObjective = scoreboard.getObjective(ScoreboardManager.OBJECTIVE_NAME)) != null) {
            previousObjective.unregister();
        }

        Objective objective = scoreboard.registerNewObjective(ScoreboardManager.OBJECTIVE_NAME, "dummy", ChatColor.GREEN + "Player Stats:");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if(playerData == null) {
            Score score = objective.getScore(ChatColor.RED + "Failed to load data!");
            score.setScore(0);
            return;
        }

        Score spacer1 = objective.getScore(ChatColor.BLACK + "");
        spacer1.setScore(15);

        Score deaths = objective.getScore(ChatColor.DARK_AQUA + "Deaths: " + ChatColor.GREEN + playerData.getDeaths());
        deaths.setScore(14);

        Score onlineTime = objective.getScore(ChatColor.DARK_AQUA + "Time: " + ChatColor.GREEN + parseTime(playerData.recalculateOnlineTime()));
        onlineTime.setScore(13);
    }

    public void cleanup(Player player) {
        Scoreboard scoreboard = this.scoreboardMap.remove(player.getUniqueId());
        if(scoreboard == null) {
            this.plugin.getLogger().warning("No scoreboard loaded for " + player.getName() + ", so nothing to clean up");
            return;
        }

        Objective previousObjective;
        if((previousObjective = scoreboard.getObjective(ScoreboardManager.OBJECTIVE_NAME)) != null) {
            previousObjective.unregister();
        }
    }

    private String parseTime(long onlineTime) {
        return String.format("%d days %02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(onlineTime),
                TimeUnit.MILLISECONDS.toHours(onlineTime) % 24,
                TimeUnit.MILLISECONDS.toMinutes(onlineTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(onlineTime)),
                TimeUnit.MILLISECONDS.toSeconds(onlineTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(onlineTime)));
    }
}
