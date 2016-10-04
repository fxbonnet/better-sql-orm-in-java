package net.archiloque.bsoij.engine;

import net.archiloque.bsoij.RuntimeSqlException;
import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.Model;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractModelMapper<S extends Model> implements Function<ResultSet, S> {

    /**
     * Set a value of a model using information provided by a Field.
     * @param resultSet
     * @param model
     * @param field
     * @param columnName
     */
    void setModelValue(
            @NotNull ResultSet resultSet,
            @NotNull SimpleModel model,
            @NotNull Field field,
            @NotNull String columnName) {
        BiConsumer setter = field.getSetter();
        try {
            switch (field.getColumnType()) {
                case String:
                    setter.accept(model, resultSet.getString(columnName));
                    break;
                case Date:
                    setter.accept(model, resultSet.getDate(columnName));
                    break;
                case Integer:
                    setter.accept(model, resultSet.getInt(columnName));
                    break;
                case Long:
                    setter.accept(model, resultSet.getLong(columnName));
                    break;
                default:
                    throw new RuntimeException("Unknown type [" + field.getColumnType() + "]");
            }
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
