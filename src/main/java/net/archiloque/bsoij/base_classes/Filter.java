package net.archiloque.bsoij.base_classes;

import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

/**
 * A Criteria applied to a Field
 */
public class Filter<M extends SimpleModel, T> {

    @NotNull
    private final Criteria<T> criteria;

    @NotNull
    private final Field<M, T> field;

    public Filter(@NotNull Criteria<T> criteria, @NotNull Field<M, T> field) {
        this.criteria = criteria;
        this.field = field;
    }

    @NotNull
    public Criteria<T> getCriteria() {
        return criteria;
    }

    @NotNull
    public Field<M, T> getField() {
        return field;
    }
}
