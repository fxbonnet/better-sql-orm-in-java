package net.archiloque.bsoij;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 *
 */
public class RuntimeSqlException extends RuntimeException {

    public RuntimeSqlException(@NotNull String message) {
        super(message);
    }

    public RuntimeSqlException(@NotNull SQLException e) {
        super(e);
    }
}
