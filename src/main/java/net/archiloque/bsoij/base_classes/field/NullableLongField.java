package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class NullableLongField<M extends SimpleModel> extends LongField<M> {

    public NullableLongField(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, Long> getter,
            @NotNull BiConsumer<M, Long> setter) {
        super(tableName, columnName, getter, setter);
    }
}
