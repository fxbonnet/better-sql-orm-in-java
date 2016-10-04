package net.archiloque.bsoij.base_classes;

import net.archiloque.bsoij.base_classes.model.MultipleModel;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 *
 */
public class TableAndModel<M extends MultipleModel, S extends SimpleModel> {

    @NotNull
    private final String tableName;

    @NotNull
    private final Class<S> modelClass;

    @NotNull
    private final Function getter;

    public TableAndModel(
            @NotNull String tableName, 
            @NotNull Class<S> modelClass,
            @NotNull Function<M, S> getter) {
        this.tableName = tableName;
        this.modelClass = modelClass;
        this.getter = getter;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public Class<S> getModelClass() {
        return modelClass;
    }

    @NotNull
    public Function<? extends MultipleModel, S> getGetter() {
        return getter;
    }
}
