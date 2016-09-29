package net.archiloque.better_sql_orm_in_java.base_classes.field;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class Field<T> {

    @NotNull
    private final String columnName;

    public Field(String columnName) {
        this.columnName = columnName;
    }

    @NotNull
    public String getColumnName() {
        return columnName;
    }
}
