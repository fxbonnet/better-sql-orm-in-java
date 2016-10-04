package net.archiloque.bsoij.base_classes.model;

import net.archiloque.bsoij.base_classes.field.Field;
import org.jetbrains.annotations.NotNull;

/**
 * Base classes for models
 */
public abstract class Model {

    @NotNull
    public abstract Field[] getFields();


}
