package net.archiloque.better_sql_orm_in_java.generator.bean;

import net.archiloque.better_sql_orm_in_java.schema.bean.Column;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Information for a column
 */
public class ColumnInfo {

    private final Column column;

    private final ColumnTypeInfo columnTypeInfo;

    public ColumnInfo(Column column, ColumnTypeInfo columnTypeInfo) {
        this.column = column;
        this.columnTypeInfo = columnTypeInfo;
    }

    public Column getColumn() {
        return column;
    }

    public ColumnTypeInfo getColumnTypeInfo() {
        return columnTypeInfo;
    }

    public String getColumnFieldName(){
        return WordUtils.uncapitalize(WordUtils.capitalizeFully(column.getName(), '_')).replace("_", "");
    }
}
