package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import net.archiloque.bsoij.base_classes.select.SimpleSelect;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Map a ResultSet into a Model.
 */
public final class SimpleModelMapper<S extends SimpleModel> extends AbstractModelMapper<S> {

    @NotNull
    private final SimpleSelect<S> select;

    public SimpleModelMapper(@NotNull SimpleSelect<S> select) {
        this.select = select;
    }

    @Override
    public S apply(ResultSet resultSet) {
        S model = select.instantiateModel();
        for (int fieldIndex = 0; fieldIndex < select.getFields().length; fieldIndex++) {
            Field field = select.getFields()[fieldIndex];
            setModelValue(resultSet, model, field, "column_" + fieldIndex);
        }
        return model;
    }

}
