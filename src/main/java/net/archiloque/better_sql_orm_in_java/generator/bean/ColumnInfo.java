package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.schema.bean.Column;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Information for a column
 */
public class ColumnInfo {

    @NotNull
    private final Column column;

    @NotNull
    private final ColumnTypeInfo columnTypeInfo;

    public ColumnInfo(@NotNull Column column, @NotNull ColumnTypeInfo columnTypeInfo) {
        this.column = column;
        this.columnTypeInfo = columnTypeInfo;
    }

    @NotNull
    public Column getColumn() {
        return column;
    }

    @NotNull
    public ColumnTypeInfo getColumnTypeInfo() {
        return columnTypeInfo;
    }

    public String getColumnFieldName(){
        return WordUtils.uncapitalize(WordUtils.capitalizeFully(column.getName(), '_')).replace("_", "");
    }

    public String getColumnConstantName(){
        return column.getName().toUpperCase();
    }


}
