package dev.niekv.datastorage;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariDataSource;
import dev.niekv.WattEenPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MySqlDataStorage {

    private static final String THREAD_PREFIX = "WattEenPlugin-Database-Future-Thread";
    private final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2,
            new ThreadFactoryBuilder().setNameFormat(MySqlDataStorage.THREAD_PREFIX + "-%d").build());
    private final WattEenPlugin plugin;
    private HikariDataSource hikariDataSource;

    public MySqlDataStorage(WattEenPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect(String hostname, String username, String password, String database) {
        this.plugin.getLogger().info("Connecting to datastorage...");

        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":3306/" + database + "?useSSL=false");
        this.hikariDataSource.setUsername(username);
        this.hikariDataSource.setPassword(password);
        this.hikariDataSource.setMaximumPoolSize(2);
        this.hikariDataSource.addDataSourceProperty("ssl.mode", "disable");
        this.hikariDataSource.setPoolName("WattEenPlugin-Database-Pool");

        this.plugin.getLogger().info("Connected to datastorage MySQL.");

        try (Connection connection = this.hikariDataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS player_data (" +
                        "id INT(12) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "uuid VARCHAR(36) NOT NULL, " +
                        "deaths INT(12) NOT NULL DEFAULT 0," +
                        "onlineTime BIGINT(32) NOT NULL DEFAULT 0);");
            }
        } catch (SQLException e) {
            this.plugin.getLogger().info("Failed to crate tables!");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        this.threadPoolExecutor.shutdown();
        if (this.hikariDataSource != null) {
            this.hikariDataSource.close();
        }
    }

    public void getConnection(ConnectionLambdaInterface connectionInterface) {
        this.threadPoolExecutor.submit(() -> {
            try (Connection connection = this.hikariDataSource.getConnection()) {
                connectionInterface.handleConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
