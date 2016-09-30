package net.archiloque.bsoij.base_classes.field;

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
