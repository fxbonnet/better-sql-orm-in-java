package net.archiloque.bsoij.base_classes.model;

import net.archiloque.bsoij.base_classes.field.Field;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleModel extends Model {

    @NotNull
    public abstract String getTableName();

    @NotNull
    public abstract Field getPrimaryKeyField();

}
