package dev.niekv.player;

import java.util.UUID;

public class PlayerData {

    private int databaseId;
    private UUID uuid;
    private int deaths;
    private long totalOnlineTime;
    private long sessionLoginTime;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public int getDatabaseId() {
        return this.databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int incrementAndGetDeahts() {
        return ++this.deaths;
    }

    public void setTotalOnlineTime(long totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public long recalculateOnlineTime() {
         return this.totalOnlineTime + (System.currentTimeMillis() - this.sessionLoginTime);
    }

    public void initializeSessionLoginTime() {
        this.sessionLoginTime = System.currentTimeMillis();
    }
}
