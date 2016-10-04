package net.archiloque.bsoij.db_specific;

import net.archiloque.bsoij.base_classes.ColumnType;
import org.jetbrains.annotations.NotNull;

/**
 * @TODO recode
 */
public class H2Translator implements DbTranslator {

    @Override
    @NotNull
    public String escapeTableName(String tableName) {
        return "\"" + tableName + "\"";
    }

    @Override
    @NotNull
    public String escapeColumnName(String columnName) {
        return "\"" + columnName + "\"";
    }

    @Override
    @NotNull
    public String getColumnType(ColumnType columnType) {
        switch (columnType) {
            case String:
                return "VARCHAR";
            case Date:
                return "DATE";
            case Integer:
                return "INT";
            case Long:
                return "BIGINT";
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }
}
