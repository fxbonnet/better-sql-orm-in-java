package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public class Field<M extends SimpleModel, T> {

    @NotNull
    private final String tableName;

    @NotNull
    private final String columnName;

    @NotNull
    private final Function<M, T> getter;

    @NotNull
    private final BiConsumer<M, T> setter;

    @NotNull
    private final ColumnType columnType;

    public Field(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, T> getter,
            @NotNull BiConsumer<M, T> setter,
            @NotNull ColumnType columnType) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
        this.columnType = columnType;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public String getColumnName() {
        return columnName;
    }

    @NotNull
    public Function<M, T> getGetter() {
        return getter;
    }

    @NotNull
    public BiConsumer<M, T> getSetter() {
        return setter;
    }

    @NotNull
    public ColumnType getColumnType() {
        return columnType;
    }
}
