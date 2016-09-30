package net.archiloque.bsoij;

import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Validate a Schema against a database.
 */
public class Validator {

    private final @NotNull Engine engine;
    private final @NotNull Schema schema;
    private final @NotNull
    Connection connection;

    public Validator(@NotNull Engine engine, Schema schema) {
        this.engine = engine;
        this.schema = schema;
        connection = engine.getConnection();
    }

    public void validate() throws SQLException, ValidationException {
        for (Model model : schema.getModels()) {
            String tableName = model.getTableName();
            ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null);
            if(tables.next()) {

            } else {
                throw new MissingTableException(tableName);
            }
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class MissingTableException extends ValidationException {

        private final @NotNull String tableName;

        public MissingTableException(@NotNull String tableName) {
            super("Table missing [" + tableName + "]");
            this.tableName = tableName;
        }
    }
}
