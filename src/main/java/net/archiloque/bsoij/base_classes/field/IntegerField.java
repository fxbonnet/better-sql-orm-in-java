package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class IntegerField<M extends SimpleModel> extends Field<M, Integer> {

    public IntegerField(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, Integer> getter,
            @NotNull BiConsumer<M, Integer> setter
    ) {
        super(tableName, columnName, getter, setter, ColumnType.Integer);
    }
}
