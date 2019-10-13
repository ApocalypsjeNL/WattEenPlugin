package dev.niekv.datastorage;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionLambdaInterface {

    void handleConnection(Connection connection) throws SQLException;
}
