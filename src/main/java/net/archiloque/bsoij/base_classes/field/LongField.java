package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class LongField<M extends SimpleModel> extends Field<M, Long> {

    public LongField(@NotNull String tableName,
                     @NotNull String columnName,
                     @NotNull Function<M, Long> getter,
                     @NotNull BiConsumer<M, Long> setter) {
        super(tableName, columnName, getter, setter, ColumnType.Long);
    }
}
