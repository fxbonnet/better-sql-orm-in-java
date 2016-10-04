package net.archiloque.bsoij.base_classes;

import net.archiloque.bsoij.base_classes.field.Field;
import net.archiloque.bsoij.base_classes.model.SimpleModel;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sort.
 */
public class Sort<M extends SimpleModel, T> {

    @NotNull
    private final Field<M, T> field;

    @NotNull
    private final Order order;

    public Sort(@NotNull Field<M, T> field, @NotNull Order order) {
        this.field = field;
        this.order = order;
    }

    @NotNull
    public Field<M, T> getField() {
        return field;
    }

    @NotNull
    public Order getOrder() {
        return order;
    }

    /**
     * For ordering
     */
    public enum Order {

        DESC, ASC

    }

    @Override
    public String toString() {
        return "Sort{" +
                "field=" + field +
                ", order=" + order +
                '}';
    }

}
