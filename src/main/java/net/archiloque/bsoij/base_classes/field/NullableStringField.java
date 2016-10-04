package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class NullableStringField<M extends SimpleModel> extends StringField<M> {

    public NullableStringField(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, String> getter,
            @NotNull BiConsumer<M, String> setter) {
        super(tableName, columnName, getter, setter);
    }
}
