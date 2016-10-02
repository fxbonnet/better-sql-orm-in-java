package net.archiloque.bsoij.db_specific;

import net.archiloque.bsoij.schema.bean.Column;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @TODO recode
 */
public class H2Translator implements DbTranslator {

    @Override
    @NotNull
    public String escapeTableName(String tableName){
        return "\"" + tableName + "\"";
    }

    @Override
    @NotNull
    public String escapeColumnName(String columnName) {
        return "\"" + columnName + "\"";
    }

    @Override
    @NotNull
    public String getColumnType(Column.ColumnType columnType) {
        switch (columnType) {
            case String:
                return "VARCHAR";
            case Date:
                return "DATE";
            case Integer:
                return "INT";
            default:
                throw new RuntimeException("Unknown type [" + columnType + "]");
        }
    }
}
