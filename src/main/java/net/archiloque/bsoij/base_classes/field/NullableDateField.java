package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class NullableDateField<M extends SimpleModel> extends DateField<M> {

    public NullableDateField(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, Date> getter,
            @NotNull BiConsumer<M, Date> setter) {
        super(tableName, columnName, getter, setter);
    }
}
