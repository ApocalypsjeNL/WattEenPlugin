package dev.niekv;

import dev.niekv.datastorage.MySqlDataStorage;
import dev.niekv.listener.DeathListener;
import dev.niekv.listener.JoinLeaveListener;
import dev.niekv.listener.PlayerChatListener;
import dev.niekv.player.PlayerDataManager;
import dev.niekv.scoreboard.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WattEenPlugin extends JavaPlugin {

    private MySqlDataStorage mySqlDataStorage;
    private PlayerDataManager playerDataManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        this.getConfig().addDefault("mysql.hostname", "localhost");
        this.getConfig().addDefault("mysql.username", "root");
        this.getConfig().addDefault("mysql.password", "toor");
        this.getConfig().addDefault("mysql.database", "watteendatabase");

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.mySqlDataStorage = new MySqlDataStorage(this);

        this.mySqlDataStorage.connect(this.getConfig().getString("mysql.hostname", "localhost"),
                this.getConfig().getString("mysql.username", "root"),
                this.getConfig().getString("mysql.password", "toor"),
                this.getConfig().getString("mysql.database", "watteendatabase"));

        this.playerDataManager = new PlayerDataManager(this);
        this.scoreboardManager = new ScoreboardManager(this);

        this.getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
    }

    public MySqlDataStorage getMySqlDataStorage() {
        return this.mySqlDataStorage;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }
}
