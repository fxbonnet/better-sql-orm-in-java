package net.archiloque.bsoij.base_classes.field;

import net.archiloque.bsoij.base_classes.ColumnType;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public abstract class StringField<M extends SimpleModel> extends Field<M, String> {

    public StringField(
            @NotNull String tableName,
            @NotNull String columnName,
            @NotNull Function<M, String> getter,
            @NotNull BiConsumer<M, String> setter) {
        super(tableName, columnName, getter, setter, ColumnType.String);
    }
}
