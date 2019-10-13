package dev.niekv.player;

import dev.niekv.WattEenPlugin;
import dev.niekv.player.schedule.AutoSaveScheduler;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();
    private final WattEenPlugin plugin;

    public PlayerDataManager(WattEenPlugin plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin,
                new AutoSaveScheduler(this), 0L, 20L * 60 * 5);
    }

    public Map<UUID, PlayerData> getPlayerDataCache() {
        return this.playerDataCache;
    }

    public WattEenPlugin getPlugin() {
        return this.plugin;
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataCache.get(player.getUniqueId());
    }

    public void loadPlayer(Player player, CompletableFuture<Void> onComplete) {
        this.plugin.getMySqlDataStorage().getConnection(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM player_data WHERE uuid=?")) {
                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    PlayerData playerData = new PlayerData(player.getUniqueId());
                    if(resultSet.next()) {
                        playerData.setDatabaseId(resultSet.getInt("id"));
                        playerData.setDeaths(resultSet.getInt("deaths"));
                        playerData.setTotalOnlineTime(resultSet.getInt("onlineTime"));
                    } else {
                        PreparedStatement preparedStatement1 = connection.prepareStatement(
                                "INSERT INTO player_data (id, uuid, deaths,  onlineTime) VALUES (NULL, ?, ?, ?)",
                                PreparedStatement.RETURN_GENERATED_KEYS);

                        preparedStatement1.setString(1, player.getUniqueId().toString());
                        preparedStatement1.setInt(2, 0);
                        preparedStatement1.setLong(3, 0);

                        int updatedRows = preparedStatement1.executeUpdate();
                        if(updatedRows > 0) {
                            ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();
                            if(resultSet1.next()) {
                                playerData.setDatabaseId(resultSet1.getInt("GENERATED_KEY"));
                                playerData.setDeaths(0);
                                playerData.setTotalOnlineTime(0);
                            }
                        } else {
                            throw new RuntimeException("Failed to create player data for " + player.getUniqueId());
                        }
                    }

                    playerData.initializeSessionLoginTime();

                    this.playerDataCache.put(player.getUniqueId(), playerData);

                    onComplete.complete(null);
                }
            }
        });
    }

    public void savePlayer(Player player) {
        final PlayerData localPlayerData = this.playerDataCache.get(player.getUniqueId());

        if(localPlayerData == null) {
            this.plugin.getLogger().warning("No playerdata found for " + player.getName() + "{" + player.getUniqueId() + "}");
            return;
        }

        this.savePlayer(localPlayerData);
    }

    public void savePlayer(final PlayerData playerData) {
        this.plugin.getMySqlDataStorage().getConnection(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE player_data SET deaths=?, onlineTime=? WHERE id=?")) {
                preparedStatement.setInt(1, playerData.getDeaths());
                preparedStatement.setLong(2, playerData.recalculateOnlineTime());
                preparedStatement.setInt(3, playerData.getDatabaseId());

                int changedRows = preparedStatement.executeUpdate();

                if(changedRows < 1) {
                    this.plugin.getLogger().warning("Something went wrong while saving player " + playerData.getUuid());
                }
            }
        });
    }
}
