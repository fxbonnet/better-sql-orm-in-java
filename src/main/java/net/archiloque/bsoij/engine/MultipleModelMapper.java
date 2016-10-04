package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.RuntimeSqlException;
import net.archiloque.bsoij.base_classes.TableAndModel;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.MultipleModel;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import net.archiloque.bsoij.base_classes.select.MultipleSelect;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Optional;

/**
 * Map a ResultSet into a Model.
 */
public final class MultipleModelMapper<S extends MultipleModel> extends AbstractModelMapper<S> {

    @NotNull
    private final MultipleSelect<S> select;

    public MultipleModelMapper(@NotNull MultipleSelect<S> select) {
        this.select = select;
    }

    @Override
    public S apply(ResultSet resultSet) {
        S model = select.instantiateModel();
        for (int fieldIndex = 0; fieldIndex < select.getFields().length; fieldIndex++) {
            Field field = select.getFields()[fieldIndex];
            // find the SimpleModel corresponding to the table of the field
            Optional<TableAndModel> tableAndModelOptional = Arrays.
                    stream(select.getTablesAndModels()).
                    filter(taM -> taM.getTableName().equals(field.getTableName())).
                    findFirst();
            if(! tableAndModelOptional.isPresent()) {
                throw new RuntimeSqlException("Unknown table [" + field.getTableName() + "]");
            }

            TableAndModel tableAndModel = tableAndModelOptional.get();
            // get the model by applying the getter to the MultipleModel instance
            SimpleModel simpleModelInstance = (SimpleModel) tableAndModel.getGetter().apply(model);
            // set the value of the filed in the SimpleModel
            setModelValue(resultSet, simpleModelInstance, field, "column_" + fieldIndex);
        }
        return model;
    }
}
