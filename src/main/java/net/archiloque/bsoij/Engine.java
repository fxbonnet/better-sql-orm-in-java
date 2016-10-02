package net.archiloque.bsoij;

import net.archiloque.bsoij.db_specific.DbTranslator;
import net.archiloque.bsoij.db_specific.H2Translator;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Use a singleton.
 */
public class Engine {

    @NotNull
    private final String driverClass;

    @NotNull
    private final String connectionUrl;

    @NotNull
    private final String user;

    @NotNull
    private final String password;

    private Connection connection;

    private final DbTranslator dbTranslator;

    public Engine(@NotNull String driverClass, @NotNull String connectionUrl, @NotNull String user, @NotNull String password) {
        this.driverClass = driverClass;
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        dbTranslator = new H2Translator();
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        connection = DriverManager.
                getConnection(connectionUrl, user, password);
    }

    public void disconnect() throws SQLException {
        if(connection == null) {
            throw new RuntimeException("Can't disconnect before connecting");
        }
        connection.close();
    }

    @NotNull
    public Connection getConnection() {
        if(connection == null) {
            throw new RuntimeException("Can't disconnect before connecting");
        }
        return connection;
    }

    public DbTranslator getDbTranslator() {
        return dbTranslator;
    }
}
