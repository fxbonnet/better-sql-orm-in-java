package net.archiloque.bsoij;

import net.archiloque.bsoij.schema.bean.Column;
import net.archiloque.bsoij.schema.bean.Model;
import net.archiloque.bsoij.schema.bean.Schema;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Validate a Schema against a database.
 */
public class Validator {

    private static final String COLUMN_NAME_COLUMN_NAME = "COLUMN_NAME";

    @NotNull
    private final Engine engine;

    @NotNull
    private final Schema schema;

    @NotNull
    private final Connection connection;

    public Validator(@NotNull Engine engine, @NotNull Schema schema) {
        this.engine = engine;
        this.schema = schema;
        connection = engine.getConnection();
    }

    public void validate() throws SQLException, ValidationException {
        for (Model model : schema.getModels()) {
            String tableName = model.getTableName();

            // validate table existence
            try (ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null)) {
                if (! tables.next()) {
                    throw new MissingTableException(tableName);
                }
            }

            Map<String, Column> notFoundColumns = new HashMap<>();
            model.getColumns().forEach(column -> notFoundColumns.put(column.getName(), column));

            // validate columns
            try(ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, null)) {
                while(columns.next()) {
                    String columnName = columns.getString(COLUMN_NAME_COLUMN_NAME);
                    if(notFoundColumns.containsKey(columnName)) {
                        notFoundColumns.remove(columnName);
                    } else {
                        throw new UnknownColumnException(columnName, tableName);
                    }
                }
            }
            if(! notFoundColumns.isEmpty()){
                throw new MissingColumnException(notFoundColumns.keySet().iterator().next(), tableName);
            }
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class MissingTableException extends ValidationException {

        private final
        @NotNull
        String tableName;

        public MissingTableException(@NotNull String tableName) {
            super("Table missing [" + tableName + "]");
            this.tableName = tableName;
        }
    }

    public static class MissingColumnException extends ValidationException {

        private final
        @NotNull
        String tableName;

        private final
        @NotNull
        String columnName;

        public MissingColumnException(@NotNull String columnName, @NotNull String tableName) {
            super("Column missing [" + columnName + "] in table [" + tableName + "]");
            this.columnName = columnName;
            this.tableName = tableName;
        }
    }

    public static class UnknownColumnException extends ValidationException {

        private final
        @NotNull
        String tableName;

        private final
        @NotNull
        String columnName;

        public UnknownColumnException(@NotNull String columnName, @NotNull String tableName) {
            super("Unknown column [" + columnName + "] in table [" + tableName + "]");
            this.columnName = columnName;
            this.tableName = tableName;
        }
    }
}
