package net.archiloque.bsoij.db_specific;

import net.archiloque.bsoij.schema.bean.Column;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface DbTranslator {

    @NotNull
    String escapeTableName(@NotNull String tableName);

    @NotNull
    String escapeColumnName(@NotNull String columnName);

    @NotNull
    String getColumnType(@NotNull Column.ColumnType columnType);

}
